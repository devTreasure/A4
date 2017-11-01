package ChunkServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Client.ClientNode;
import Client.Command;
import Client.CommandFactory;
import Client.Node;
import Client.TCPSender;

public class ChunkNodeWorker  implements Runnable{
	
	

	   private ServerSocket serverSocket;
	   private ChunkNode node;
	   public boolean continueFlag = true;
	   private TCPSender tcpSender = new TCPSender();

	   public ChunkNodeWorker(ServerSocket sc, ChunkNode node) {
	      this.serverSocket = sc;
	      this.node = node;
	   }

	   @Override
	   public void run() {
	      System.out.println("Started ChunkNodeWorker thread....");

	      while (continueFlag) {
	         Socket socket = null;
	         Command request = null;
	         try {
	            socket = serverSocket.accept();
	            
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

	      }
	   }

	   public boolean isContinueFlag() {
	      return continueFlag;
	   }

	   public void setContinueFlag(boolean continueFlag) {
	      this.continueFlag = continueFlag;
	   }

	   public Node getNode() {
	      return node;
	   }

	   public void setNode(ChunkNode node) {
	      this.node = node;
	   }


}
