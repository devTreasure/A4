package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ClientNode implements Node {

	public static final String EXIT_COMMAND = "exit";
	public static final String WRITE_COMMAND = "write";
	public static final String READ_COMMAND = "read";
	public static final String THREE_SERVERS = "3servers";

	public String clientNodeIP = "";
	public int clientNodePORT = -1;
    public String chunkNodeIP="";
    public int chunkNodePORT=-1;
    
	public String controllerNodeIP = "";
	public int controllerNodePORT = -1;

	public String str_getChunkServer_Request = "GET_3_CHUNK_SERVERS";

	public void initializeClientNode() throws IOException {
		
		System.out.println("Initializing client Node ...");

		ServerSocket serversocket = new ServerSocket(0);

		// ServerSocket serversocket = new ServerSocket(47000);

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
		return "Node [nodeIP=" + this.clientNodeIP + ", nodePort=" + clientNodePORT + ", " + " controlleNodeIP = " + controllerNodeIP + ", controllerNodePORT= " + controllerNodePORT + "]";
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Enter \"start\" to start the Node ");

		System.out.println("Please pass the IP --SPACE--  PORT number of the Controller Node");

		int controllerNodePORT = 0;
		String controllerIP = "";
		int nodeId = 0;

		if (args.length < 2) {
			System.out.println("Exa: java A2.Node <Controller NODE IP> <Controller NODE PORT>");
			System.exit(0);
		}

		try {
			// controllerIP = args[0];
			String strIP = "";
			strIP = args[0];
			InetAddress ipaddress = InetAddress.getLocalHost();//InetAddress.getByName(strIP);
			System.out.println("IP address: " + ipaddress.getHostAddress());
			controllerIP = ipaddress.getHostAddress();
			controllerNodePORT = Integer.parseInt(args[1]);
			if (args.length == 3)
				nodeId = Integer.parseInt(args[2]);
		} catch (Exception e) {
			System.out.println("Error: Please provide numneric argument.");
			System.exit(0);
		}

		boolean continueOperations = true;

		ClientNode clientnode = null;

		clientnode = new ClientNode();

		clientnode.controllerNodeIP = controllerIP;

		clientnode.controllerNodePORT = controllerNodePORT;

		clientnode.initializeClientNode();

		while (continueOperations) {
			System.out.println("Commands: \n write \n read \n 3servers");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String inputStr = br.readLine();

			System.out.println("Received command is:" + inputStr);

			if (EXIT_COMMAND.equalsIgnoreCase(inputStr)) {

				System.out.println("Exiting.");

				continueOperations = false;

			} else if (WRITE_COMMAND.equalsIgnoreCase(inputStr)) {
				clientnode.writeFiletoChunkNode();
				System.out.println("Write operation is performed");

			} else if (READ_COMMAND.equalsIgnoreCase(inputStr)) {
				System.out.println("read operation is performed");

			} else if (THREE_SERVERS.equalsIgnoreCase(inputStr)) {

				System.out.println("3servers operation is performed");
				clientnode.return3AvailableChunkServers(clientnode);

			}

		}

	}

	public void writeFiletoChunkNode() {
		FileWriteOperationCommand writeOps=new FileWriteOperationCommand();
		
	}

	private void return3AvailableChunkServers(ClientNode clientnode) throws Exception {
		
		ChunkServersRequestCommand cmd = new ChunkServersRequestCommand(this.controllerNodeIP, this.controllerNodePORT,7);

		Command resp =new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, cmd.unpack());
	
	    Response response = (Response) resp;
	    
	    System.out.println(response.getMessage());
	    
	      if (!response.isSuccess()) {
	         throw new RuntimeException("No Chunk Node Available");
	      }
	      else
	      {
	    	  String[] strchunkNode=  response.getMessage().split(":");
	    	  this.chunkNodeIP=strchunkNode[0].toString();
	    	  this.chunkNodePORT= Integer.parseInt(strchunkNode[1].toString());
	    	  System.out.println("###Available Chunk Nonde###");
	    	  System.out.println(this.chunkNodeIP + ":::"+this.chunkNodePORT);
	      }
	}

	@Override
	public Command notify(Command command) throws Exception {
		System.out.println("Received command >> " + command);
		if (command instanceof ChunkServersRequestCommand) {
			// 1. Register request (Check for id collision)
			 System.out.println("Client node -- notify() method");
			
			// return ChunkServersRequestCommand((ChunkServersRequestCommand) command);
		}
		// else if(command instanceof ReturnRandomNodeCommand) {
		// 2. Give me random node to resolve the successor
		// return randomStaringNode((ReturnRandomNodeCommand) command);
		// }
		return null;
	}
}
