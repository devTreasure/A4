package Client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileResponse implements Command {

   public static final String cmd = "CMD_FILE_Reponse";
   private boolean success;
   private String fileName;
   private byte[] file;

   public FileResponse(String filename,byte [] file) {
      this.file = file;
      this.fileName = fileName!=null ? fileName : "";
   }

   public FileResponse() {
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
         dout.writeInt(fileName.length());
         dout.write(fileName.getBytes());
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
         fileName = readString(din);
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

  
   
}
