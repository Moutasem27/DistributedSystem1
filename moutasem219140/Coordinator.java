package com.mycompany.moutasem219140;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class Coordinator {
    
    private static List<nodeconnection> nodeConnections = new ArrayList<>();
 static CoordinatorGUI c_GUI;
    private static class nodeconnection {
          DataInputStream input;
        DataOutputStream output;
        
        Socket socket;
        String nodeName;
      
        boolean connected;

        nodeconnection(Socket socket, String nodeName) {
            this.socket = socket;
            this.nodeName = nodeName;
            
            this.connected = true;
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("error whilecreating input/output streams for " + nodeName + ": " + e.getMessage());
            }
        }
    }

        
           
              
   public static void main(String[] args) {
        try {
            c_GUI = new CoordinatorGUI();
            c_GUI.setVisible(true);
            ServerSocket serverSocket = new ServerSocket(2002);
            System.out.println("Coordinator is on. ");
     c_GUI.appendTextArea("Coordinator is on");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String nodeName = input.readUTF();
                
                System.out.println(nodeName + " has connected.");
         c_GUI.appendTextArea(nodeName + " has connected");
                nodeconnection nodeConnection = new nodeconnection(socket, nodeName);
                
                nodeConnections.add(nodeConnection);

                Thread receiveThread = new Thread(() -> receiveMessages(nodeConnection));
                
                receiveThread.start();

                
            }
        } catch (IOException e) {
            System.out.println("there'serror in the coordinator: " + e.getMessage());
            c_GUI.appendTextArea("there'serror in the coordinator: "+ e.getMessage());
        }
    }

    
    private static void receiveMessages(nodeconnection nodeConnection) {
        try {
            String nodeName = nodeConnection.nodeName;
            
            DataInputStream input = nodeConnection.input;
            while (true) {
                String message = input.readUTF();
                
                System.out.println(nodeName + " tells :- " + message);
                 c_GUI.appendTextArea(nodeName + " tells :- " + message);

                if (message.equals("leave")) {
                    nodeConnection.connected = false;
                    removeNode(nodeConnection);
                    break;
                } else if (message.equals("join")) {
                    nodeConnection.connected = true;
                    System.out.println(nodeName + " has reconnected.");
                     c_GUI.appendTextArea(nodeName + " has reconnected.");
                    sendRingNodesList(nodeConnection);
                } else {
                    displayMessage(nodeName + " tells :- " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("error receiving messages from " + nodeConnection.nodeName + ": " + e.getMessage());
            
            removeNode(nodeConnection);
        }
    }

   
     private static void displayMessage(String message) {
        for (nodeconnection connection : nodeConnections) {
            try {
                if (connection.connected) {
                    
                    connection.output.writeUTF(message);
                    connection.output.flush();
                }
            } catch (IOException e) {
                System.out.println("error displaying message: " + e.getMessage());
                c_GUI.appendTextArea("error displaying message: " + e.getMessage());
                // her it removes the node from the ring when an error occurs
                removeNode(connection);
            }
        }
    }
 
     private static void removeNode(nodeconnection nodeConnection) {
        nodeConnections.remove(nodeConnection);
        System.out.println(nodeConnection.nodeName + " has disconnected.");
         c_GUI.appendTextArea(nodeConnection.nodeName + " has disconnected.");
    }
       private static void sendRingNodesList(nodeconnection nodeConnection) {
        try {
           
            DataOutputStream output = nodeConnection.output;
            for (nodeconnection connection : nodeConnections) {
                if (connection != nodeConnection) {
                    output.writeUTF("Node " + connection.nodeName + " is already in the ring.");
                     c_GUI.appendTextArea("Node " + connection.nodeName + " is already in the ring.");
                    output.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("error while sending nodes list: " + e.getMessage());
            c_GUI.appendTextArea("Error sending ring nodes list: " + e.getMessage());
        }
    }
} 
 