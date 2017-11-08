package Client;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;


public class TCPSender {

   public Command sendAndReceiveData(String hostIp, int hostPort, byte[] data) throws UnknownHostException, IOException {
      return sendAndReceiveData(hostIp, hostPort, data, "");
   }
   
   public Command sendAndReceiveData(String hostIp, int hostPort, byte[] data, String localPath) throws UnknownHostException, IOException {
      Socket socket = null;
      BufferedOutputStream dout = null;
      Command response = null;
      try {
         socket = new Socket(hostIp, hostPort);

         // write data
         dout = new BufferedOutputStream(socket.getOutputStream());
         dout.write(data);
         dout.flush();
         // read and parse response
         if(localPath.equals("")) {
            response = CommandFactory.process(socket);            
         } else {
            response = CommandFactory.process(socket, localPath);
         }
      } 
      catch(ConnectException e)
      {
    	  System.out.println("Connection forcibaly closed or shut down might have happned on remote machine...");
      }
      catch(Exception e)
      {
    	  System.out.println(e.getMessage());
      }
      
      finally {
         try {
            dout.close();
            socket.close();
         }
         catch (ConnectException e) {
             // TODO Auto-generated catch block
        	 System.out.println(e.getMessage());
        	 System.out.println("Connection forcibaly closed or shut down might have happned on remote machine...");
          }
         catch(NullPointerException e)
         {
        	 System.out.println(e.getMessage());
         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }

      return response;
   }

   public void sendData(String hostIp, int hostPort, byte[] data) throws Exception {
      Socket socket = null;
      try {
         socket = new Socket(hostIp, hostPort);
         sendData(socket, data);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         socket.close();
      }
   }

   public void sendData(Socket socket, byte[] data) throws Exception {
      BufferedOutputStream dout = null;
      try {
         dout = new BufferedOutputStream(socket.getOutputStream());
         dout.write(data);
      } 
      catch(ConnectException e)
      {
    	  System.out.println("Connection forcibaly closed or shut down might have happned on remote machine...");
      }
      
      catch (IOException e) {
         e.printStackTrace();
      } finally {
         dout.close();
      }

   }

}
