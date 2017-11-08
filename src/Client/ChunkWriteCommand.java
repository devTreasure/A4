package Client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ChunkServer.ChunkFileUtility;
import ChunkServer.ChunkServer;

public class ChunkWriteCommand implements Command {

   public static final String cmd = "CMD_CHUNK_WRITE_COMMAND";
   public String directoryName = "Please SpecIfy directory name";
   private ChunkServer target;
   private String fileName;
   private String chunkName;
   private File chunk;
   private String replicationNodes;
   private boolean isReplicatingFile;

   public ChunkWriteCommand(ChunkServer target, String fileName, String chunkName, File chunk, String replicationNodes, boolean isReplicatingFile) {
      this.target = target;
      this.fileName = fileName;
      this.chunkName = chunkName;
      this.chunk = chunk;
      this.replicationNodes = replicationNodes;
      this.isReplicatingFile = isReplicatingFile;
   }

   public ChunkWriteCommand() {}

   @Override
   public byte[] unpack() {
      byte[] marshalledBytes = null;
      ByteArrayOutputStream baOutputStream = null;
      DataOutputStream dout = null;
      byte[] fileBytes = null;

      try {
         fileBytes = Files.readAllBytes(chunk.toPath());

         baOutputStream = new ByteArrayOutputStream();
         dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
         dout.writeInt(cmd.length());
         dout.write(cmd.getBytes());

         dout.writeInt(target.IP().length());
         dout.write(target.IP().getBytes());
         dout.writeInt(target.PORT());

         dout.writeInt(fileName.length());
         dout.write(fileName.getBytes());

         dout.writeInt(chunkName.length());
         dout.write(chunkName.getBytes());
         
         dout.writeInt(replicationNodes.length());
         dout.write(replicationNodes.getBytes());
         
         dout.writeBoolean(isReplicatingFile);

         dout.writeInt(fileBytes.length);
         dout.write(fileBytes);
         
         dout.flush();
         marshalledBytes = baOutputStream.toByteArray();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            fileBytes = null;
            baOutputStream.close();
            dout.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return marshalledBytes;
   }

   @Override
   public void pack(DataInputStream din) {
      try {
         String ipAddress = readString(din);
         int port = din.readInt();
         this.target = new ChunkServer(ipAddress, port);

         this.fileName = readString(din);
         this.chunkName = readString(din);
         this.replicationNodes = readString(din);
         this.isReplicatingFile = din.readBoolean();

         int fileBytesSize = din.readInt();
         byte[] fileBytes = new byte[fileBytesSize];
         din.readFully(fileBytes);
         writeChunkFile(directoryName, fileBytes);
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
      return "ChunkWriteCommand [directoryName=" + directoryName + ", target=" + target
            + ", fileName=" + fileName + ", chunkName=" + chunkName + ", chunk=" + chunk
            + ", replicationNodes=" + replicationNodes + ", isReplicatingFile=" + isReplicatingFile
            + "]";
   }

   public void writeChunkFile(String directoryName, byte[] fileBytes) {
      try {
         chunk = new File(directoryName, chunkName);
         if(isReplicatingFile) {
            ChunkFileUtility.writeChunk(fileBytes, chunk);
         } else {
            ChunkFileUtility.addSha1AndWriteChunk(fileBytes, chunk);
         }
         System.out.println("Chunk writen: " + chunk.getAbsolutePath());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public ChunkServer getTarget() {
      return target;
   }

   public String getFileName() {
      return fileName;
   }

   public String getChunkName() {
      return chunkName;
   }

   public File getChunk() {
      return chunk;
   }

   public String getReplicationNodes() {
      return replicationNodes;
   }

   public boolean isReplicatingFile() {
      return isReplicatingFile;
   }

}
