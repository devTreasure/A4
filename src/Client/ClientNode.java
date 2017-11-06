package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import ChunkServer.ChunkServer;

public class ClientNode implements Node {

   public static final String EXIT_COMMAND = "exit";
   public static final String WRITE_COMMAND = "write";
   public static final String READ_COMMAND = "read";
   public static final String THREE_SERVERS = "3servers";
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

      if (args.length < 2) {
         System.out.println("Exa: java A2.Node <Controller NODE IP> <Controller NODE PORT>");
         System.exit(0);
      }

      try {
         controllerIP = args[0];
         controllerNodePORT = Integer.parseInt(args[1]);
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

   public void readFileFromChunkServer(String filename) {
      // TODO Auto-generated method stub
      System.out.println("File to be read is :" + filename);

      FileInfoCommnad controllerFileInfo = new FileInfoCommnad(filename);
      Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, controllerFileInfo.unpack());
      Response response = (Response) resp;

      if (response != null) {
         String message = response.getMessage();
         String[] strChunkNodeInfo = message.split(":");

         System.out.println(strChunkNodeInfo[0].toString() + ":" + strChunkNodeInfo[1].toString());

         String IP = strChunkNodeInfo[0].toString();
         int PORT = Integer.parseInt(strChunkNodeInfo[1].toString());
         String[] chunkFiles = strChunkNodeInfo[2].split(",");

         for (String chunkFileName : chunkFiles) {
            ChunkFileReadCommand chunk2Clientinfo = new ChunkFileReadCommand(IP, PORT, this.clientNodeIP, this.clientNodePORT, chunkFileName);
            Command resps = new TCPSender().sendAndReceiveData(IP, PORT, chunk2Clientinfo.unpack());
            System.out.println(resps);
         }
         //MERGE files

      } else {

         System.out.println("ControllerNodeFileAndNodeInfoCommnad issue");
      }
   }


   private List<File> splitFile(File inputFile) throws Exception {
      FileSplit fileSplit = new FileSplit();
      List<File> chunks = fileSplit.splitFile(inputFile);
      return chunks;
   }

   /**
    * Write chunk and target chunk server to first chunk serevr. Example: Chunk servers returned are
    * A, B, C File Chunnks FC1, FC2, FC3
    * 
    * Commands will be: (Chunk, TargetChunkServer) (FC1, A) and send it ot A (FC2, B) and send it ot
    * A (FC3, C) and send it ot A
    * 
    * Chink server will check if i am not the target(using IP + PORT) then write to that target.
    * 
    * @param file
    * 
    */
   public void writeFiletoChunkNode(File file, List<File> chunks) {
      if (chunkServers.isEmpty()) {
         System.out.println("Can not find any chunk server.");
      } else {
         int counter = 0;
         int max = chunkServers.size();
         ChunkServer toChunkServer = chunkServers.get(0);

         for (File eachChunk : chunks) {
            ChunkWriteCommand command = new ChunkWriteCommand(toChunkServer, file.getName(),
                  eachChunk.getName(), eachChunk);
            sender.sendAndReceiveData(toChunkServer.IP(), toChunkServer.PORT(), command.unpack());
            if (counter < max - 1) {
               counter++;
            }
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
      File f = new File("D:\\Temp\\chunkServerToClient");
      String[] files = f.list();
      if (files.length == this.totalChunks) {
         for (String string : files) {
            File fs = new File("D:\\Temp\\chunkServerToClient\\" + string);
            filex.add(fs);
         }

         FileSplit s = new FileSplit();
         s.mergeFiles(filex, new File("D:\\Temp\\chunkServerToClient\\" + command.fileName));
      }

      return new Response(true, "File received on client side from chunk server.");

   }
}


