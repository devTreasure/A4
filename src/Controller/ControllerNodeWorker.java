package Controller;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Client.Command;
import Client.CommandFactory;
import Client.TCPSender;


public class ControllerNodeWorker implements Runnable {

   private ServerSocket serverSocket;
   private ControllerNode node;
   private TCPSender tcpSender = new TCPSender();

   public ControllerNodeWorker(ServerSocket sc, ControllerNode node) {
      this.serverSocket = sc;
      this.node = node;
   }

   @Override
   public void run() {
      System.out.println("Controlll node  receiver thread started");

      while (ControllerNode.continueOperation) {
         Socket socket = null;
         Command request = null;
         try {
            socket = serverSocket.accept();
            System.out.println("Controlle node socket accepted");
            //Get the message
            request = CommandFactory.process(socket);
            
            try {
               Command response = node.notify(request);
               tcpSender.sendData(socket, response.unpack());
            } catch (Exception e) {
               e.printStackTrace();
            }
            
            
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            try {
               if (socket != null) {
                  socket.close();
                  socket = null;
               }
            } catch (IOException e) {
               System.out.println("Error while closing resources: " + e.getMessage());
            }
         }
         
         //Notify the Node with received command
//         try {
//            node.notify(message);
//         } catch (Exception e) {
//            e.printStackTrace();
//         }
      }
   }

   public ControllerNode getNode() {
      return node;
   }

   public void setNode(ControllerNode node) {
      this.node = node;
   }
}