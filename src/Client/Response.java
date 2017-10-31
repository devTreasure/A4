package Client;



import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Response implements Command {

   public static final String cmd = "CMD_Reponse";
   private boolean success;
   private String message;

   public Response(boolean success, String message) {
      this.success = success;
      this.message = message!=null ? message : "";
   }

   public Response() {
   }

   public byte[] unpack() {
      byte[] marshalledBytes = null;
      ByteArrayOutputStream baOutputStream = null;
      DataOutputStream dout = null;

      try {
         baOutputStream = new ByteArrayOutputStream();
         dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
         dout.writeInt(cmd.length());
         dout.write(cmd.getBytes());
         dout.writeBoolean(success);
         dout.writeInt(message.length());
         dout.write(message.getBytes());
         dout.flush();
         marshalledBytes = baOutputStream.toByteArray();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            baOutputStream.close();
            dout.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return marshalledBytes;
   }

   public void pack(DataInputStream din) {
      try {
         success = din.readBoolean();
         message = readString(din);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private String readString(DataInputStream din) throws IOException {
      int IP_length = din.readInt();
      byte[] IP_address = new byte[IP_length];
      din.readFully(IP_address);
      return new String(IP_address);
   }

   @Override
   public String toString() {
      return "Reponse [success=" + success + ", message=" + message + "]";
   }

   public boolean isSuccess() {
      return success;
   }

   public String getMessage() {
      return message;
   }
   
}
