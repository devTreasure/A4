package ChunkServer;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.print.DocFlavor.CHAR_ARRAY;

import Client.ChunkServersRequestCommand;
import Client.Command;
import Client.Response;

public class ChunkNode implements Runnable {

	public String controllerNodeIP;
	public int controllerNodePORT;
	public static final String EXIT_COMMAND = "exit";
	public static final String WRITE_COMMAND = "write";
	public static final String READ_COMMAND = "read";
	public String str_getChunkServer_Request = "GET_3_CHUNK_SERVERS";
	public String chunkserverNodeName;
	public ServerSocket serverSocket;
	// public Map<Integer, RingNodes> ringNodes = null;
	// public MiddleWare objMiddleware;
	public String str_SUCC_REQUEST = "SUCC_REQUEST";
	public String str_RANDOM_REQUEST = "RANDOM_NODE_REQUEST";
	public String str_RANDOM_RESPONSE = "RANDOM_NODE_RESPONSE";
	public String str_REG_REQUEST = "REG_REQUEST";
	public String str_MAJOR_HEARTBEAT_REQUEST = "MAJOR_HB";
	public String str_MINOR_HEARTBEAT_REQUEST = "MINOR_HB";

	public ChunkNode() {

		// ringNodes = new HashMap<Integer, RingNodes>();
		// this.objMiddleware = new MiddleWare(this);

	}

	public void sendtheHealthchekSingnalToCunkServer() {
		//
	}

	public void collecttheAvailbleChunkServers() {
		// collect the available chunk servers
	}

	public void get3AavailableChunkServers() {

	}
	
	public Command registerNode(ChunkServersRequestCommand command) throws Exception {
		 
	      Response response = null;
	      if(command==null) {
	         response = new Response(true, "success Bhavin !!");  
	      }
	      
	      return response;
	 }
	 
	private void intializeChunkNode() throws IOException {
		// TODO Auto-generated method stub
		ServerSocket sc = new ServerSocket(0);

		System.out.println("Resolved Host name is :");

		System.out.println(sc.getInetAddress().getLocalHost().getHostAddress());

		System.out.println(InetAddress.getLocalHost().getHostName());

		this.controllerNodeIP = InetAddress.getLocalHost().getHostAddress();

		this.controllerNodePORT = sc.getLocalPort();

		this.serverSocket = sc;

		System.out.println(" Chunk node is hoasted at : " + this.controllerNodeIP + "  " + " Listenning port : "+ sc.getLocalPort());

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Enter register tot register new node");

		String[] strSplit = null;

		// collatorNode.collatorIP = strIP;
		// collatorNode.collatorPORT = nodePort;

		// collatorNode.initializeCollatorNode(collatorNode.collatorPORT);
		// Thread thread = new Thread(collatorNode);
		// thread.start();

		ChunkNode chunkNode = new ChunkNode();

		chunkNode.intializeChunkNode();

		Thread t = new Thread(chunkNode);

		t.start();

		boolean continueOperations = true;

		while (continueOperations) {

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String exitStr = br.readLine();

			System.out.println("Received command is:" + exitStr);

			if (EXIT_COMMAND.equalsIgnoreCase(exitStr)) {

				System.out.println("Exiting.");

				continueOperations = false;

			} else if (WRITE_COMMAND.equalsIgnoreCase("write")) {
				System.out.println("Write operation is performed");
				//return3AvailableChunkServers(controllerNode);

			} else if (READ_COMMAND.equalsIgnoreCase("read")) {
				System.out.println("read operation is performed");

			}

			else if ("pull-traffic-summary".equalsIgnoreCase(exitStr)) {
				// collatorNode.trafficSummary();
			}
		}

		System.out.println("Bye.");
	}

	public static void return3AvailableChunkServers(ChunkNode chunkNode) {
		// TODO Auto-generated method stub
		chunkNode.get3AavailableChunkServers();
	}

	@Override
	public void run() {

		try {
			receiveMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void receiveMessage() throws IOException {

		System.out.println("Started Discovery node receiver thread;");
		DataInputStream din = null;
		DataOutputStream dout = null;
		Socket socket;

		while (true) {

			socket = serverSocket.accept();

			System.out.println("..: return 3AvailableChunkServers-Request Socket acceepted :...");

			try {

				din = new DataInputStream(socket.getInputStream());

				dout = new DataOutputStream(socket.getOutputStream());

				// int number = din.readInt();
				int requestIdentifierLength = din.readInt();

				byte[] identifierBytes = new byte[requestIdentifierLength];

				din.readFully(identifierBytes);

				String strID = new String(identifierBytes);

				int nodeID = 0;

				int nodeListenningPort = 0;

				if (strID.equalsIgnoreCase(str_getChunkServer_Request)) {
					System.out.println("Chunk server request recevied on controller");
				}

			}

			catch (Exception ex) {

			}
		}
	}
}
