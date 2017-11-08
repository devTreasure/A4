package Client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkReplicaRequest implements Command {

   public static final String cmd = "CMD_CHUNK_SERVERS_REQUEST_FOR_CHUNKFILE_CMD";

   public String ipAddress;
   public int port;
   public String chunkFileName;

   public ChunkReplicaRequest() {
   }

   public ChunkReplicaRequest(String ipAddress, int port, String chunkFileName) {
        super();
        this.ipAddress = ipAddress;
        this.port = port;
        this.chunkFileName = chunkFileName;
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
             dout.writeInt(ipAddress.length());
             dout.write(ipAddress.getBytes());
             dout.writeInt(port);
             dout.writeInt(chunkFileName.length());
             dout.write(chunkFileName.getBytes());
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
             ipAddress = readString(din);
             port = din.readInt();
             chunkFileName = readString(din);
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
      return "ChunkServersRequestForChunkFileCommand [ipAddress=" + ipAddress + ", port=" + port
            + ", chunkFileName=" + chunkFileName + "]";
   }
   
}
