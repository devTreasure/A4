package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import ChunkServer.ChunkFileUtility;
import ChunkServer.ChunkServer;
import Client.ChunkNodeFileInfoCommand;
import Client.ChunkNodeWentliveRequest;
import Client.ChunkServersRequestCommand;
import Client.ChunkReplicaRequest;
import Client.Command;
import Client.FileInfoCommnad;
import Client.Node;
import Client.Response;

public class ControllerNode implements Node {

	public String controllerNodeIP;
	public int controllerNodePORT;
	public static boolean continueOperation = true;
	
	public static final String EXIT_COMMAND = "exit";
	public static final String WRITE_COMMAND = "write";
	public static final String READ_COMMAND = "read";
	public String str_getChunkServer_Request = "GET_3_CHUNK_SERVERS";
	public String discoveryNodeName;
	public ServerSocket serverSocket;

	public String str_SUCC_REQUEST = "SUCC_REQUEST";
	public String str_RANDOM_REQUEST = "RANDOM_NODE_REQUEST";
	public String str_RANDOM_RESPONSE = "RANDOM_NODE_RESPONSE";
	public String str_REG_REQUEST = "REG_REQUEST";
	public String str_MAJOR_HEARTBEAT_REQUEST = "MAJOR_HB";
	public String str_MINOR_HEARTBEAT_REQUEST = "MINOR_HB";
	private ControllerNodeWorker controllerReceiverWorker;

	private Set<ChunkServer> chunkServerCollection = new HashSet<ChunkServer>();
	private Hashtable<String, Integer> chunkServerFileInfoCollection = new Hashtable<String, Integer>();
	private Hashtable<String, FileInfo> fileInfoCollection = new Hashtable<String, FileInfo>();
	private Hashtable<String, ArrayList<String>> fileSpiltInfoInfoCollection = new Hashtable<String, ArrayList<String>>();
	private HashMap<String, FileInfo> fileInfoMap = new HashMap<String, FileInfo>();
	private HashMap<String, Set<ChunkServer>> chunkFileInfoMap = new HashMap<String, Set<ChunkServer>>();
	
	public ControllerNode() {

	}

	public void sendtheHealthchekSingnalToCunkServer() {
		//
	}

	public void collecttheAvailbleChunkServers() {
		// collect the available chunk servers
	}

	public Command returnTheChunkServer() {
		Set<ChunkServer> nodes = new HashSet<ChunkServer>();
		
		int chunkServersCount = chunkServerCollection.size();
		if(chunkServersCount == 0) {
			return new Response(false, "No chunk servers available");
		} else if (chunkServersCount > 1 && chunkServersCount <= 3) {
			nodes.addAll(chunkServerCollection);
		} else {
			ArrayList<ChunkServer> chunkNodesAsList = new ArrayList<ChunkServer>(chunkServerCollection);
			Random random = new Random();
			for(int i=0;i < 10; i++) {
				int nextInt = random.nextInt(chunkServersCount);
				nodes.add(chunkNodesAsList.get(nextInt));
				if(nodes.size()==3) {
					break;
				}
			}
		}
		
		String message = ""; 
		for (ChunkServer eachChunkServer : nodes) {
			message += eachChunkServer.IP() + ":" + String.valueOf(eachChunkServer.PORT());
			message += ",";
		}
		
		Response response = null;
		if(message.trim().isEmpty()) {
			response = new Response(false, "No chunk servers available");
		} else {
			response = new Response(true, message);
		}
		
		return response;
	}

	public Command addChunkinfo2Collection(ChunkNodeWentliveRequest command) {
		System.out.println(command.chunkIP + ":" + command.chunkPORT);
		chunkServerCollection.add(new ChunkServer(command.chunkIP, command.chunkPORT));
		System.out.println("Collection size :" + chunkServerCollection.size());
		return new Response(true, "Chunk node is added");
	}

	
	
   public Command collectchunkNodeFileDetails(ChunkNodeFileInfoCommand command) {

      if (command != null && command.allFileData.length() > 0) {

         System.out.println(command.allFileData + ":" + command.checksumID);

         //// f1|f1c1:f1c2:f1c3 ? f2|f2c1:f2c2 ?
         String[] fileDetails = command.allFileData.split("\\?");
         for (String eachFileDetail : fileDetails) {
            String[] fileDetail = eachFileDetail.split("\\|");
            String fileName = fileDetail[0];
            String[] chunks = fileDetail[1].split(":");
            FileInfo fileInfo = new FileInfo(fileName, chunks, command.chunkIP, command.chunkPORT);

            if (command.ipAddress.equalsIgnoreCase("MAJOR_HEART_BEAT")) {
               System.out.println("******MAJOR HEART BEAT***********");
               System.out.println(fileInfo);
               System.out.println("******END MAJOR HEART BEAT***********");
               fileInfoMap.put(fileName, fileInfo);
            } else {
               System.out.println(fileInfo);
               fileInfoMap.put(fileName, fileInfo);
            }
            
            if(chunks!=null && chunks.length > 0) {
               for (String eachChunkName : chunks) {
                  Set<ChunkServer> servers = chunkFileInfoMap.get(eachChunkName);
                  if(servers==null) {
                     servers = new HashSet<>();
                     chunkFileInfoMap.put(eachChunkName, servers);
                  }
                  servers.add(new ChunkServer(command.chunkIP, command.chunkPORT));
               }
            }
            
         }
         return new Response(true, "chunk file info recevied by controller");
      } else {
         if (command.ipAddress.equalsIgnoreCase("MAJOR_HEART_BEAT")) {
            System.out.println("******MAJOR HEART BEAT***********");
            System.out.println("No new files being added recntly on Chunkserver");

         } else {
            System.out.println("No new files being added recntly on Chunkserver");
         }

         return new Response(true, "*** No new files ADDED to chunk Server ***");
      }

   }

	private void intializeControllerNode() throws IOException {

		ServerSocket sc = new ServerSocket(63120);
		System.out.println("Resolved Host name is :");
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		System.out.println(InetAddress.getLocalHost().getHostName());
		this.controllerNodeIP = InetAddress.getLocalHost().getHostAddress();
		this.controllerNodePORT = sc.getLocalPort();
		this.serverSocket = sc;
		System.out.println(" Controller node is hoasted at : " + this.controllerNodeIP + "  " + " Listenning port : " + sc.getLocalPort());
		
		controllerReceiverWorker = new ControllerNodeWorker(sc, this);
		Thread t = new Thread(controllerReceiverWorker);
		t.start();
		System.out.println("Controller worker started.");
	}

	public void filesmaintaindbythisChunkServer() {

	}

	public static void main(String[] args) throws IOException {

		ControllerNode controllerNode = new ControllerNode();
		controllerNode.intializeControllerNode();

		while (ControllerNode.continueOperation) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String exitStr = br.readLine();
			System.out.println("Received command is:" + exitStr);
			System.out.println("NO READ WRITE OPS ON CONTROLLER TERMINAL");

		}

		System.out.println("Bye.");
	}

	public static void return3AvailableChunkServers(ControllerNode controllerNode) {
		controllerNode.returnTheChunkServer();
	}
	
	public Command collectchunkNodeFileDetailsforclientRequest(FileInfoCommnad command)
	{
		String requestedFile = command.fileName;
		if(requestedFile==null || requestedFile.trim().isEmpty()) {
		   return new Response(false, "filename is mandatory field.");
		}
		
		FileInfo fileInfo = fileInfoMap.get(requestedFile);
		if(fileInfo==null) {
             return new Response(false, "No information found for file.");
          } else {
             String chunkFileNames = ChunkFileUtility.join(fileInfo.chunkFileNames, ",");
             return new Response(true, fileInfo.chunkNodeIP +":"+String.valueOf(fileInfo.chunkNodePORT) +":"+ chunkFileNames);
          }
	}

   @Override
   public Command notify(Command command) throws Exception {
      // TODO Auto-generated method stub
      if (command instanceof ChunkServersRequestCommand) {
         return returnTheChunkServer();
      } else if (command instanceof ChunkNodeWentliveRequest) {
         return addChunkinfo2Collection((ChunkNodeWentliveRequest) command);
      } else if (command instanceof ChunkNodeFileInfoCommand) {
         return collectchunkNodeFileDetails((ChunkNodeFileInfoCommand) command);
      } else if (command instanceof FileInfoCommnad) {
         return collectchunkNodeFileDetailsforclientRequest((FileInfoCommnad) command);
      } else if (command instanceof ChunkReplicaRequest) {
         return retunChunkServerrsForChunkFile((ChunkReplicaRequest) command);
      } else {
         return new Response(false, "Command not recognized by controller.");
      }
   }

   private Command retunChunkServerrsForChunkFile(ChunkReplicaRequest command) {
      Set<ChunkServer> set = chunkFileInfoMap.get(command.chunkFileName);
      if(set == null || set.size()==0) {
         return new Response(false, "No chunk servers found.");
      } else {
         String message = "";
         for (ChunkServer eachChunkServer : set) {
            message += "," + eachChunkServer.IP() + ":" + String.valueOf(eachChunkServer.PORT());
         }
         message = message.replaceFirst(",", "");
         return new Response(true, message);
      }
   }

}
