package org.example;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static final int PORT = 8181;


    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket("127.0.0.1", PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            String userName = "";

            Scanner scanner = new Scanner(System.in);

            String firstMess = in.readLine();
            System.out.println(firstMess);

            while (!firstMess.equals("Dobrodosao!")){
                String userName2 = scanner.nextLine();
                out.println(userName2);
                userName = userName2;
                firstMess = in.readLine();
//                if(firstMess.equals("Dobrodosao!") || firstMess.equals("Vase korisnicko ime je zauzeto, unesi novo!"))
                System.out.println(firstMess);
            }

            String messageHistory = in.readLine();
            while (messageHistory != null && !messageHistory.isEmpty()){
                System.out.println(messageHistory);
                messageHistory = in.readLine();
            }

            BufferedReader finalIn = in;
            Thread thread = new Thread(() -> {
                try {
                    while(true){
                        if (finalIn.ready())
                            System.out.println(finalIn.readLine());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            thread.start();


            while (true){
                String messageToSend = scanner.nextLine();
                out.println(sendMassage(messageToSend, userName));
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

    private static String sendMassage(String messageToSend, String userName) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        return "<" + formattedDateTime + "> " + "<" + userName + "> " + ":" + "<" + messageToSend + ">";
    }
}
