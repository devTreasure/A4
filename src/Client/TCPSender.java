package Client;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


public class TCPSender {

   public Command sendAndReceiveData(String hostIp, int hostPort, byte[] data) {
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
         response = CommandFactory.process(socket);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            dout.close();
            socket.close();
         } catch (Exception e) {
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
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         dout.close();
      }

   }

}
