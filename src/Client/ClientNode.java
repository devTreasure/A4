package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ChunkServer.ChunkServer;

public class ClientNode implements Node {

   public static final String EXIT_COMMAND = "exit";
   public static final String WRITE_COMMAND = "write";
   public static final String READ_COMMAND = "read";
   public static final String THREE_SERVERS = "3servers";
   public static String ServerToClientPATH = "NOT_SET";
   public static boolean continueOperations = true;

   public String clientNodeIP = "";
   public int clientNodePORT = -1;

   public String controllerNodeIP = "";
   public int controllerNodePORT = -1;
   private List<ChunkServer> chunkServers = new ArrayList<>();
   private TCPSender sender = new TCPSender();
   public int totalChunks = 0;

   public void initializeClientNode() throws IOException {

      System.out.println("Initializing client Node ...");
      ServerSocket serversocket = new ServerSocket(0);
      this.clientNodePORT = serversocket.getLocalPort();
      InetAddress ip = InetAddress.getLocalHost();
      this.clientNodeIP = (ip.getHostAddress()).trim();

      ReceiverWorker receiverWorker = new ReceiverWorker(serversocket, this);
      Thread receiverWorkerThread = new Thread(receiverWorker);
      receiverWorkerThread.start();
      System.out.println(this);

      System.out.println("Client Node started ...");

   }

   @Override
   public String toString() {
      return "ClientNode [nodeIP=" + this.clientNodeIP + ", nodePort=" + clientNodePORT + ", "
            + " controlleNodeIP = " + controllerNodeIP + ", controllerNodePORT= "
            + controllerNodePORT + "]";
   }

   public static void main(String[] args) throws Exception {

      System.out.println("Please pass the IP--SPACE--PORT number of the Controller Node");
      int controllerNodePORT = 0;
      String controllerIP = "";
      int nodeId = 0;

      if (args.length < 3) {
         System.out.println("Exa: java A4.ClientNode <Controller NODE_IP> <Controller NODE_PORT> <FILE_STORAGE_PATH>");
         System.exit(0);
      }

      try {
         controllerIP = args[0];
         controllerNodePORT = Integer.parseInt(args[1]);
         ServerToClientPATH = args[2];
         File fileStoragePath = new File(ServerToClientPATH);
         if(fileStoragePath.exists()) {
            System.out.println("Directiry exists.");
         } else {
            fileStoragePath.mkdirs();
            if(fileStoragePath.exists() && fileStoragePath.isDirectory()) {
               System.out.println("Filestorage created at:" + fileStoragePath.getAbsolutePath());
            } else {
               System.out.println("ERROR: file can not be created or not a directory.");
               System.exit(0);
            }
         }
         
         // if (args.length == 3)
         // nodeId = Integer.parseInt(args[2]);
      } catch (Exception e) {
         System.out.println("Error: Please provide numneric argument.");
         System.exit(0);
      }

      ClientNode clientnode = null;
      clientnode = new ClientNode();
      clientnode.controllerNodeIP = controllerIP;
      clientnode.controllerNodePORT = controllerNodePORT;

      clientnode.initializeClientNode();

      while (ClientNode.continueOperations) {
         System.out.println("Commands: \n write<SPACE>FILE_ABSOLUTE_PATH \n read \n 3servers");

         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String inputStr = br.readLine();
         System.out.println("Received command is:" + inputStr);

         if (EXIT_COMMAND.equalsIgnoreCase(inputStr)) {
            System.out.println("Exiting.");
            ClientNode.continueOperations = false;
         } else if (inputStr.startsWith(WRITE_COMMAND)) {
            clientnode.sendFileToChunkNode(inputStr.split(" ")[1]);
         } else if (READ_COMMAND.equalsIgnoreCase(inputStr)) {
            System.out.println("************ READ  OPS ***********");
            System.out.println("Provide file name to read");
            BufferedReader brs = new BufferedReader(new InputStreamReader(System.in));
            String inputFileStr = brs.readLine();
            System.out.println(inputFileStr);
            clientnode.readFileFromChunkServer(inputFileStr);
         } else if (THREE_SERVERS.equalsIgnoreCase(inputStr)) {
            System.out.println("3servers operation is performed");
            clientnode.return3AvailableChunkServers();
         }

      }

   }

   private void sendFileToChunkNode(String filePath) throws Exception {
      System.out.println(" Writing file to chunk server. " + filePath);
      File file = new File(filePath);
      System.out.println("   1. Spliting file.");
      List<File> chunks = splitFile(file);
      System.out.println("   2. Ask for chunk nodes to controller.");
      return3AvailableChunkServers();
      System.out.println("   3. Writing to first chunk server.");
      writeFiletoChunkNode(file, chunks);
   }

   public void readFileFromChunkServer(String filename) throws UnknownHostException, IOException {
      // TODO Auto-generated method stub
      System.out.println("File to be read is :" + filename);

      FileInfoCommnad controllerFileInfo = new FileInfoCommnad(filename);
      Command resp = sender.sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, controllerFileInfo.unpack(), ServerToClientPATH);
      Response response = (Response) resp;
      if(response==null || !response.isSuccess()) {
         System.out.println("FAILURE: message:" + response);
         return;
      }
      
      ArrayList<File> fileToBeMerged=new ArrayList<File>();
      String message = response.getMessage();

      System.out.println("Chunknode detail: " + message);
      String[] strChunkNodeInfo = message.split(":");
      if(strChunkNodeInfo == null || !(strChunkNodeInfo.length > 2)) {
         System.out.println("FAILURE: invalid chunkNode details from controller." + response);
         return;
      }
      
      String IP = strChunkNodeInfo[0].toString();
      int PORT = Integer.parseInt(strChunkNodeInfo[1].toString());
      String[] chunkFiles = strChunkNodeInfo[2].split(",");
      fileToBeMerged.clear();
      boolean allChunnkReceivedSuccessfully = true;
      for (String chunkFileName : chunkFiles) {
         ChunkFileReadCommand chunkFileReadCommand = new ChunkFileReadCommand(IP, PORT, this.clientNodeIP, this.clientNodePORT, chunkFileName);
         Command filedataResp = sender.sendAndReceiveData(IP, PORT, chunkFileReadCommand.unpack(), ServerToClientPATH);
         if(filedataResp instanceof ChunkWriteOperationsCommand) {
            ChunkWriteOperationsCommand filedata = (ChunkWriteOperationsCommand) filedataResp;
            System.out.println("Chunnk written at: " + filedata.chunk.getAbsolutePath());
            fileToBeMerged.add(filedata.chunk);
         }
         if(filedataResp instanceof Response) {
            Response fileData = (Response) filedataResp;
            if(!fileData.isSuccess() && fileData.getMessage().startsWith("Tempered:")) {
               System.out.println("Chunk is tempred.: " + chunkFileName);
               System.out.println(fileData);
               
               //Recover the tempered chunk from replica.
               ChunkReplicaRequest reqForNewChunk = new ChunkReplicaRequest("", -1, chunkFileName);
               Command chunkReplicaServers = sender.sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, reqForNewChunk.unpack(), ServerToClientPATH);
               Response chunkReplicaServersResp = (Response) chunkReplicaServers;
               if(chunkReplicaServersResp.isSuccess()) {
                  String data = chunkReplicaServersResp.getMessage();
                  System.out.println("Chunnk servers found. " + data);
                  String[] replicatedNodes = data.split(",");
//                     HashSet<ChunkServer> replicatedNodes = new HashSet<ChunkServer>();
//                     replicatedNodes.add(new ChunkServer(servers[0], Integer.parseInt(servers[1])));
                  String anothrIP = "";
                  int anotherPort = -1;
                  for (String eachServer : replicatedNodes) {
                     String[] split = eachServer.split(":");
                     anothrIP = split[0];
                     String anotherPORTStr = split[1];
                     anotherPort = Integer.parseInt(anotherPORTStr);
                     if(anothrIP.equals(IP) && anotherPort == PORT) {
                        continue;
                     } else {
                        break;
                     }
                  }
                  if(anotherPort != -1) {
                     try {
                        System.out.println("Requesting replica from: " + anothrIP + ":" +  anotherPort);
                        ChunkFileReadCommand chunkFileReplicaReadCommand = new ChunkFileReadCommand("", -1, "", -1, chunkFileName);
                        Command replicaReadResp = sender.sendAndReceiveData(anothrIP, anotherPort, chunkFileReplicaReadCommand.unpack(), ServerToClientPATH);
                        System.out.println(replicaReadResp);
                        if(replicaReadResp instanceof ChunkWriteOperationsCommand) {
//                           ChunkWriteOperationsCommand filedata = (ChunkWriteOperationsCommand) filedataResp;
//                           System.out.println("Replicat written at:" + filedata.chunk.getAbsolutePath());
                           allChunnkReceivedSuccessfully = true;
                        } else if (replicaReadResp instanceof Response) {
                           System.out.println(replicaReadResp);
                           allChunnkReceivedSuccessfully = ((Response) replicaReadResp).isSuccess();
                        }
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
                  
               }
               
            }
            
         }
      }
      // Files are copied on the first chink server
      // now replicte the same files on another server.
      FileSplit objfs = new FileSplit();
      File mergerdFile = new File(ServerToClientPATH, filename);
      objfs.mergeFiles(fileToBeMerged, mergerdFile);
      System.out.println("File merged at:" + mergerdFile.getAbsolutePath());

      if (allChunnkReceivedSuccessfully) {
         System.out.println("Merge successful");
      } else {
         System.out.println("Some of the chunks are tempred with. Merge may not be successful..");
      }
      
   }


private void reportContollerAboutFaultyChunkandLocateAndResotreTheFile() {
	// TODO Auto-generated method stub
	
}

private List<File> splitFile(File inputFile) throws Exception {
      FileSplit fileSplit = new FileSplit();
      List<File> chunks = fileSplit.splitFile(inputFile);
      return chunks;
   }

   /**
    * Write chunk and target chunk server to first chunk serevr. 
    * Example: Chunk servers returned are A, B, C and File Chunks are FC1, FC2, FC3
    * 
    * Commands will be: (Chunk, TargetChunkServer) (FC1, [B,C]) sent o nchunk node A
    * 
    * Chink server will check replication node details present then write file there.
    * 
    * @param file
    * @throws IOException 
    * @throws UnknownHostException 
    * 
    */
   public void writeFiletoChunkNode(File file, List<File> chunks) throws UnknownHostException, IOException {
      if (chunkServers.isEmpty()) {
         System.out.println("Can not find any chunk server.");
      } else {
         ChunkServer toChunkServer = chunkServers.get(0);

         String replicationNodes = "";
         if(chunkServers.size() == 1) {
            System.out.println("Replication nodes NOT available.");
         } else {
            
            //first node is self node. Should not replicate
            for (int i = 1; i < chunkServers.size(); i++) {
               ChunkServer eachChunkServer = chunkServers.get(i);
               replicationNodes += "," + eachChunkServer.IP()+":" + eachChunkServer.PORT();
            }
            replicationNodes = replicationNodes.replaceFirst(",", "");
            System.out.println("Replication nodes :" + replicationNodes);
         }
         
         for (File eachChunk : chunks) {
            ChunkWriteCommand command = 
                new ChunkWriteCommand(
                   toChunkServer, file.getName(), eachChunk.getName(), eachChunk, replicationNodes);
            sender.sendAndReceiveData(toChunkServer.IP(), toChunkServer.PORT(), command.unpack());
         }
      }
   }

   private void return3AvailableChunkServers() throws Exception {
      ChunkServersRequestCommand cmd =
            new ChunkServersRequestCommand(this.controllerNodeIP, this.controllerNodePORT, 7);
      Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP,
            this.controllerNodePORT, cmd.unpack());
      Response response = (Response) resp;
      System.out.println(response.getMessage());
      if (!response.isSuccess()) {
         throw new RuntimeException("No Chunk Node Available");
      } else {
         String[] strchunkNodes = response.getMessage().split(",");
         for (String eachValue : strchunkNodes) {
            if (eachValue != null && !eachValue.trim().isEmpty()) {
               String[] data = eachValue.split(":");
               ChunkServer srvr = new ChunkServer(data[0], Integer.parseInt(data[1]));
               System.out.println("Chunk Server: " + srvr);
               chunkServers.add(srvr);
            }
         }
      }
   }

   @Override
   public Command notify(Command command) throws Exception {
      System.out.println("Received command >> " + command);
      if (command instanceof ChunkServersRequestCommand) {
         // 1. Register request (Check for id collision)
         System.out.println("Client node -- notify() method");

         // return ChunkServersRequestCommand((ChunkServersRequestCommand)
         // command);
      }
      if (command instanceof ChunkWriteOperationsCommand) {

         // else if(command instanceof ReturnRandomNodeCommand) {
         // 2. Give me random node to resolve the successor
         return FileWriteChunkToClientOPS((ChunkWriteOperationsCommand) command);
         // }

      }

      return null;
   }

   public ArrayList<File> filex = new ArrayList<File>();

   private Command FileWriteChunkToClientOPS(ChunkWriteOperationsCommand command)
         throws IOException {

      // TODO Auto-generated method stub
      System.out.println("---" + command);
      File f = new File(ServerToClientPATH);
      String[] files = f.list();
      if (files.length == this.totalChunks) {
         for (String string : files) {
            File fs = new File(ServerToClientPATH + string);
            filex.add(fs);
         }

         FileSplit s = new FileSplit();
         s.mergeFiles(filex, new File(ServerToClientPATH + command.fileName));
      }

      return new Response(true, "File received on client side from chunk server.");

   }
}


