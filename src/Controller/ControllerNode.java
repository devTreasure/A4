package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import ChunkServer.ChunkServers;
import Client.ChunkNodeFileInfoCommand;
import Client.ChunkNodeWentliveRequest;
import Client.ChunkServersRequestCommand;
import Client.Command;
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

	private Set<ChunkServers> chunkServerCollection = new HashSet<ChunkServers>();
	private Hashtable<String, String> chunkServerFileInfoCollection = new Hashtable<String, String>();

	public ControllerNode() {

	}

	public void sendtheHealthchekSingnalToCunkServer() {
		//
	}

	public void collecttheAvailbleChunkServers() {
		// collect the available chunk servers
	}

	public Command returnTheChunkServer() {
		Set<ChunkServers> nodes = new HashSet<ChunkServers>();
		
		int chunkServersCount = chunkServerCollection.size();
		if(chunkServersCount == 0) {
			return new Response(false, "No chunk servers available");
		} else if (chunkServersCount > 1 && chunkServersCount <= 3) {
			nodes.addAll(chunkServerCollection);
		} else {
			ArrayList<ChunkServers> chunkNodesAsList = new ArrayList<ChunkServers>(chunkServerCollection);
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
		for (ChunkServers eachChunkServer : nodes) {
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
		chunkServerCollection.add(new ChunkServers(command.chunkIP, command.chunkPORT));
		System.out.println("Collection size :" + chunkServerCollection.size());
		return new Response(true, "Chunk node is added");
	}

	public Command collectchunkNodeFileDetails(ChunkNodeFileInfoCommand command) {

		
		
		System.out.println(command.fileName + ":" + command.checksumID);
		chunkServerFileInfoCollection.put(command.fileName, command.checksumID);
		System.out.println("File Collection size :" + chunkServerFileInfoCollection.size());

		return new Response(true, "chunk file info recevied by controller");
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

			if (EXIT_COMMAND.equalsIgnoreCase(exitStr)) {
				ControllerNode.continueOperation = false;
				System.out.println("Exiting.");
			} else if (WRITE_COMMAND.equalsIgnoreCase("write")) {
				System.out.println("Write operation is performed");
				return3AvailableChunkServers(controllerNode);
			} else if (READ_COMMAND.equalsIgnoreCase("read")) {
				System.out.println("read operation is performed");
			} else if ("pull-traffic-summary".equalsIgnoreCase(exitStr)) {
				// collatorNode.trafficSummary();
			}
		}

		System.out.println("Bye.");
	}

	public static void return3AvailableChunkServers(ControllerNode controllerNode) {
		controllerNode.returnTheChunkServer();
	}
	
	@Override
	public Command notify(Command command) throws Exception {
		// TODO Auto-generated method stub
		if (command instanceof ChunkServersRequestCommand) {
			return returnTheChunkServer();
		}

		if (command instanceof ChunkNodeWentliveRequest) {
			return addChunkinfo2Collection((ChunkNodeWentliveRequest) command);
		}

		if (command instanceof ChunkNodeFileInfoCommand) {
			return collectchunkNodeFileDetails((ChunkNodeFileInfoCommand) command);
		}

		/*
		 * Command response = new NodeDetails("", -1, -1, true, "Nothing"); if
		 * (command instanceof ChunkServersRequestCommand) {
		 * ResolveSuccessorInFingerTableMessage asm =
		 * (ResolveSuccessorInFingerTableMessage) command; response =
		 * resolveTragetNode(asm.id); } else if (command instanceof
		 * SetMeAsSuccessor) { SetMeAsSuccessor msg = (SetMeAsSuccessor)
		 * command; response = successorChanged(msg); } else if (command
		 * instanceof SetMeAsPredecessor) { SetMeAsPredecessor msg =
		 * (SetMeAsPredecessor) command; response = predecessorChanged(msg); }
		 * else if (command instanceof GetSuccessor) { // GetSuccessor msg =
		 * (GetSuccessor) command; response = getSuccessor(); } else if (command
		 * instanceof UpdateFingerTable) { // GetSuccessor msg = (GetSuccessor)
		 * command; response = updateFingerTable(); } else if(command instanceof
		 * PredecessorDetail) { response = predecessor; }
		 */
		// System.out.println("Response: " + response);
		// return response;
		return null;
	}

}
