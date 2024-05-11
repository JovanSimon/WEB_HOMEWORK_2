package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;

public class ServerThread implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            out.println("Unesite vase korisnicko ime: ");

            while (true) {
                boolean usernameFound = false;
                String userName = in.readLine();

                Iterator<String> iterator = Main.users.iterator();
                while (iterator.hasNext()) {
                    String item = iterator.next();
                    if (userName.equals(item)) {
                        out.println("Vase korisnicko ime je zauzeto, unesi novo!");
                        usernameFound = true;
                        break;
                    }
                }

                if (!usernameFound) {
                    out.println("Dobrodosao!");
                    Main.users.add(userName);
                    Main.notifyAllExceptLast(userName);
                    break;
                }
            }

            int lowerBound;
            if(Main.messages.size() < 100)
                lowerBound = 1;
            else
                lowerBound = Main.messages.size() - 100;

            StringBuilder stringBuilder = new StringBuilder();

            for(int i = lowerBound; i <= Main.messages.size(); i++){
                stringBuilder.append(Main.messages.get(i));
                stringBuilder.append("\n");
            }

            out.println(stringBuilder);

            while (true){
                String newMessage = in.readLine();
                maintain();
                newMessage = cenzure(newMessage);
                Main.messages.put(Main.INDEX_OF_MESSAGE.incrementAndGet(), newMessage);
                System.out.println(newMessage);
                Main.notifyAll(newMessage);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyY(String message){
        out.println(message);
    }

    private String cenzure(String newMessage) {
        String[] toCenzure = newMessage.split(":");

        String finalToCenzure = toCenzure[toCenzure.length - 1].substring(1, toCenzure[toCenzure.length - 1].length() - 1);

        String[] finalToCenzureSplitted = finalToCenzure.split("\\s+");

        for (int i = 0; i < finalToCenzureSplitted.length; i++) {
            for (String cenWord : Main.cenzurisane) {
                if (cenWord.equals(finalToCenzureSplitted[i])) {
                    char firstChar = finalToCenzureSplitted[i].charAt(0);
                    char lastChar = finalToCenzureSplitted[i].charAt(finalToCenzureSplitted[i].length() - 1);
                    String replacement = firstChar + new String(new char[finalToCenzureSplitted[i].length() - 2]).replace('\0', '*') + lastChar;
                    finalToCenzureSplitted[i] = replacement;
                }
            }
        }

        StringBuilder resultBuilder = new StringBuilder();
        for (String word : finalToCenzureSplitted) {
            resultBuilder.append(word).append(" ");
        }
        String result = resultBuilder.toString().trim();

        return newMessage.replace(finalToCenzure, result);
    }


    private void maintain() {
        if(Main.messages.size() < 100)
            return;

        int idxToDelete = Main.INDEX_OF_MESSAGE.get() - 100;

        Main.messages.remove(idxToDelete);
    }

}
