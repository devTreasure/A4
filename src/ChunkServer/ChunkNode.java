package ChunkServer;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.print.DocFlavor.CHAR_ARRAY;

import Client.ChunkServersRequestCommand;
import Client.Command;
import Client.Node;
import Client.Response;
import Client.TCPSender;
import Client.ChunkNodeFileInfoCommand;
import Client.ChunkNodeFileStoreCommand;
import Client.ChunkNodePollingCommand;
import Client.ChunkNodeWentliveRequest;

public class ChunkNode implements Node {

	public String controllerNodeIP;
	public int controllerNodePORT;
	
	public String chunkNodeIP;
	public int chunkrNodePORT;
	
	//Tis collection has the guids for the files being added for the first time
	
	private Hashtable<String,String> chunkServerFileIntegretyCheckSumCollection = new Hashtable<String,String>();

	public static final String EXIT_COMMAND = "exit";
	public static final String WRITE_COMMAND = "write";
	public static final String READ_COMMAND = "read";
	public String str_getChunkServer_Request = "GET_3_CHUNK_SERVERS";
	public String chunkserverNodeName;
	public ServerSocket serverSocket;
	public String filePATH="D:\\chunkStorage";
	public  ArrayList<String> fileCollection = new ArrayList<String>();
	
	public String str_SUCC_REQUEST = "SUCC_REQUEST";
	public String str_RANDOM_REQUEST = "RANDOM_NODE_REQUEST";
	public String str_RANDOM_RESPONSE = "RANDOM_NODE_RESPONSE";
	public String str_REG_REQUEST = "REG_REQUEST";
	
	public String str_MAJOR_HEARTBEAT_REQUEST = "MAJOR_HB";
	public String str_MINOR_HEARTBEAT_REQUEST = "MINOR_HB";

	public ChunkNode() {

		this.chunkServerStatistics();
	}
	
	public Command storetheFile(ChunkNodeFileStoreCommand command)
	{
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList =null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {
		    StringBuilder builder = new StringBuilder();
			filesList=fmonitor.getAllfilesInfoOnChunkServer();
			String fileString ="";
			 for (int i = 0; i < filesList.size(); i++) {
				builder.append(filesList.get(i).trim()).append(":");
			}
			return new Response(true, builder.toString());
		}
		else
		{

		return new Response(true, "Nofiles");
		}
	}
	
	public Command collectfilesInfo(ChunkNodeFileInfoCommand command) {
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList =null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {
		    StringBuilder builder = new StringBuilder();
			filesList=fmonitor.getAllfilesInfoOnChunkServer();
			String fileString ="";
			 for (int i = 0; i < filesList.size(); i++) {
				builder.append(filesList.get(i).trim()).append(":");
			}
			return new Response(true, builder.toString());
		}
		else
		{

		return new Response(true, "Nofiles");
		}
	}

	
	public void sendtheHealthchekSingnalToCunkServer() {
		//
	}
	
	public void chunkServerStatistics()
	{
		this.fileCollection.clear();
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList =null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {
		
			filesList=fmonitor.getAllfilesInfoOnChunkServer();
			this.fileCollection=filesList;
		}
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
	 
	private void intializeChunkNode() throws Exception {
		// TODO Auto-generated method stub
		
		//1.Chunk Node is alive 
		ServerSocket sc = new ServerSocket(0);

		System.out.println("Resolved Host name is :");

		System.out.println(sc.getInetAddress().getLocalHost().getHostAddress());

		System.out.println(InetAddress.getLocalHost().getHostName());

		this.chunkNodeIP = InetAddress.getLocalHost().getHostAddress();

		this.chunkrNodePORT = sc.getLocalPort();

		this.serverSocket = sc;
		
		//2. Send the controller 
		
		sendtheChunkNodeinfotoController();
				
		ChunkNodeWorker chunkNodeWorker = new ChunkNodeWorker(sc, this);

		Thread t = new Thread(chunkNodeWorker);

		t.start();

		Chunkpulse30Sec p30= new Chunkpulse30Sec(sc, this);
		
		Thread t30sec = new Thread(p30);
		
		t30sec.start();
		

		Chunkpulse300Sec p300= new Chunkpulse300Sec(sc, this);
		
		Thread t300sec = new Thread(p300);
		
		t300sec.start();
		
		
		System.out.println(" Chunk node is hoasted at : " + this.chunkNodeIP + "  " + " Listenning port : "+ sc.getLocalPort());

		
		
		
	}

	public void sendtheChunkNodeinfotoController() throws Exception {
		// TODO Auto-generated method stub
		ChunkNodeWentliveRequest livereq = new ChunkNodeWentliveRequest(this.controllerNodeIP,this.controllerNodePORT,this.chunkNodeIP,this.chunkrNodePORT);
		Command resp=new TCPSender().sendAndReceiveData(this.controllerNodeIP,this.controllerNodePORT, livereq.unpack());
		Response response = (Response) resp;
	    System.out.println(response.getMessage());
	}


	
	public void sendchunkkinfoToCOntroller() throws NoSuchAlgorithmException, IOException
	{
		//Update the file lists to check if there are any new Files
		
		//it will return File collection arraylist with file names
		
		//How to maintain checksum for the all files for the first time and compare here it with method
		
		
		chunkServerStatistics();
		
		
		for(int i=0;i<this.fileCollection.size();i++)
		{
			String generatedCHeckSumID="";
			
		   TemperingUtil temperU= new TemperingUtil();
		   
		   temperU.generateChecksum(this.fileCollection.get(i));
		  
			ChunkNodeFileInfoCommand cmd = new ChunkNodeFileInfoCommand(this.controllerNodeIP, this.controllerNodePORT,this.chunkNodeIP,this.chunkrNodePORT,fileCollection.get(i), temperU.checkSumID);

			Command resp =new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, cmd.unpack());
		
		    Response response = (Response) resp;
		    
		    System.out.println(response.getMessage());
			
		}
	
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Enter Controller node IP-SPACE-PORT");

		String[] strSplit = null;
		
		int controllerNodePORT = 0;
		String controllerIP = "";


		if (args.length < 2) {
			System.out.println("Exa: java A2.Node <Controller NODE IP> <Controller NODE PORT>");
			System.exit(0);
		}

		try {
			controllerIP = args[0];
			InetAddress ipaddress = InetAddress.getLocalHost();//InetAddress.getByName(strIP);
			System.out.println("IP address: " + ipaddress.getHostAddress());
			controllerNodePORT = Integer.parseInt(args[1]);
			
		} catch (Exception e) {
			System.out.println("Error: " +e.getMessage());
			System.exit(0);
		}



		ChunkNode chunkNode = new ChunkNode();
		
		chunkNode.controllerNodeIP=controllerIP;
		chunkNode.controllerNodePORT=controllerNodePORT;
		
		chunkNode.intializeChunkNode();
	

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


	@Override
	public Command notify(Command command) throws Exception {
		// TODO Auto-generated method stub
		
		
		if(command instanceof ChunkNodeFileInfoCommand)
		{
			return collectfilesInfo((ChunkNodeFileInfoCommand) command);
		}
	
		return null;
	}



}
