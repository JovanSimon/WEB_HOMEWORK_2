package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static final int PORT = 8181;
    public static List<String> users = new CopyOnWriteArrayList<>();
    public static Map<Integer, String> messages = new ConcurrentHashMap<>();
    public static List<String> cenzurisane = Arrays.asList("kreten", "debil", "idiot");
    public static AtomicInteger INDEX_OF_MESSAGE = new AtomicInteger(0);
    private static List<ServerThread> klijenti = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) {
        messages.put(1,"PRVA");
        messages.put(2,"Druga");
        messages.put(3,"Treca");
        messages.put(4,"Cetvrta");
        messages.put(5,"Peta");

        try {
            ServerSocket serverSocket = new ServerSocket(Main.PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket);
                klijenti.add(serverThread);
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void notifyAllExceptLast(String userName){
        for(int i = 0; i < klijenti.size() - 1; i++){
            klijenti.get(i).notifyY("Chat-u se prikljucio " + userName);
        }
    }

    public static void notifyAll(String message){
        for(ServerThread serverThread : klijenti){
            serverThread.notifyY(message);
        }
    }
}