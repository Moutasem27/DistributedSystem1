package com.mycompany.moutasem219140;

import java.io.*;
import java.net.*;

import java.util.Scanner;

public class SecondNode {
     public static String RecieverNodeName;
    
    private static DatagramSocket privateMessageSocket;
    public static String nodeName;
   
    public static final int priv_msg_port = 3002; 
 
    public static void main(String[] args) {
        try {
            
            String serverHostname = "localhost";
            int serverPort = 2002;
            Socket socket = new Socket(serverHostname, serverPort);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
         
Scanner scanner = new Scanner(System.in);


            nodeName = "node2";
            output.writeUTF(nodeName);
            

            Thread receiveThread = new Thread(() -> receiveMessages(input));
            receiveThread.start();

            // we are starting the private message server here......
            privateMessageSocket = new DatagramSocket(priv_msg_port);
            Thread privateMessageThread = new Thread(() -> handlePrivateMessages());
            privateMessageThread.start();

            while (true) {
                String message = scanner.nextLine();

                if (message.equals("leave")) {
                    output.writeUTF(message);
                    break;
                } else if (message.startsWith("private.")) {
                    char identifier = '.';
                    char identifier2= ',';
                    int index = message.indexOf(identifier);
                    int index2 = message.indexOf(identifier2);
                    RecieverNodeName =(message.substring(index +1,index2 ));
                  
                    sendPrivateMessage(message.substring(14)); 
                    
                    
                    

                } else {
                    output.writeUTF(message);
                }
            }
        } catch (IOException e) {
            System.out.println("error in the node: " + e.getMessage());
        }
    }

    private static void receiveMessages(DataInputStream input) {
        try {
            while (true) {
                String message = input.readUTF();
                System.out.println(message);
            }
        } catch (EOFException e) {
            System.out.println("connection has been closed. terminating the node.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("errorwhile receiving messages: " + e.getMessage());
        }
    }

    private static void handlePrivateMessages() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            while (true) {
                privateMessageSocket.receive(packet);
                String encryptedMessage = new String(packet.getData(), 0, packet.getLength());
            String decryptedMessage = decryptMessage(encryptedMessage);
            
            System.out.println("Private message received: " + decryptedMessage);
            }
        } catch (IOException e) {
            System.out.println("err handling private messages: " + e.getMessage());
        }
    }

    private static void sendPrivateMessage(String message) {
        try {
            String encryptedMessage = encryptMessage(message);
           
        byte[] buffer = encryptedMessage.getBytes();
           
            InetAddress otherNodeAddress = InetAddress.getByName("localhost"); 
              int otherNodePort;
        
        switch (RecieverNodeName) {
            case "node1":
                otherNodePort = 3001;
                break;
            case "node3":
                otherNodePort = 3003;
                break;
            default:
                otherNodePort = 3004;
                break;
        }
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, otherNodeAddress, otherNodePort);
            privateMessageSocket.send(packet);
        } catch (IOException e) {
            System.out.println("error sending private message: " + e.getMessage());
        }
    }
    
    private static String encryptMessage(String message) {
    StringBuilder encryptedMessage = new StringBuilder();

    for (int i = 0; i < message.length(); i++) {
        char c = message.charAt(i);
       
        c++;
        encryptedMessage.append(c);
    }

    return encryptedMessage.toString();
}

private static String decryptMessage(String encryptedMessage) {
    StringBuilder decryptedMessage = new StringBuilder();

    for (int i = 0; i < encryptedMessage.length(); i++) {
        char c = encryptedMessage.charAt(i);
       
        c--;
        decryptedMessage.append(c);
    }

    return decryptedMessage.toString();
}
    
}