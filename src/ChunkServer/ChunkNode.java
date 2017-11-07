package ChunkServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import Client.ChunkNodeFileInfoCommand;
import Client.ChunkNodeFileStoreCommand;
import Client.ChunkNodeWentliveRequest;
import Client.ChunkFileReadCommand;
import Client.ChunkServersRequestCommand;
import Client.ChunkWriteCommand;
import Client.ChunkWriteOperationsCommand;
import Client.Command;
import Client.Node;
import Client.Response;
import Client.TCPSender;

public class ChunkNode implements Node {

	public static Pattern FILE_NAME_PATETRN = Pattern.compile("(.*)_chunk.*");
	public ArrayList<String> newfiles = new ArrayList<String>();
	public String controllerNodeIP;
	public int controllerNodePORT;

	public String chunkNodeIP;
	public int chunkrNodePORT;

	// Tis collection has the guids for the files being added for the first time

	private Hashtable<String, String> chunkServerFileIntegretyCheckSumCollection = new Hashtable<String, String>();
	private Hashtable<String, Integer> fileSpiltInfoInfoCollection = new Hashtable<String, Integer>();

	public static final String EXIT_COMMAND = "exit";
	public static final String WRITE_COMMAND = "write";
	public static final String READ_COMMAND = "read";
	public String str_getChunkServer_Request = "GET_3_CHUNK_SERVERS";
	public String chunkserverNodeName;
	public ServerSocket serverSocket;
	public static String filePATH = "D:\\Temp\\chunkServer\\out";
	public ArrayList<String> fileCollection = new ArrayList<String>();

	public String str_SUCC_REQUEST = "SUCC_REQUEST";
	public String str_RANDOM_REQUEST = "RANDOM_NODE_REQUEST";
	public String str_RANDOM_RESPONSE = "RANDOM_NODE_RESPONSE";
	public String str_REG_REQUEST = "REG_REQUEST";

	public String str_MAJOR_HEARTBEAT_REQUEST = "MAJOR_HB";
	public String str_MINOR_HEARTBEAT_REQUEST = "MINOR_HB";
	private TCPSender sender = new TCPSender();

	public ChunkNode() {

		this.chunkServerStatistics();
	}

	public Command storetheFile(ChunkNodeFileStoreCommand command) {
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList = null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {
			StringBuilder builder = new StringBuilder();
			filesList = fmonitor.getAllfilesInfoOnChunkServer();
			String fileString = "";
			for (int i = 0; i < filesList.size(); i++) {
				builder.append(filesList.get(i).trim()).append(":");
			}
			return new Response(true, builder.toString());
		} else {

			return new Response(true, "Nofiles");
		}
	}

	public Command collectfilesInfo(ChunkNodeFileInfoCommand command) {
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList = null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {
			StringBuilder builder = new StringBuilder();
			filesList = fmonitor.getAllfilesInfoOnChunkServer();
			String fileString = "";
			for (int i = 0; i < filesList.size(); i++) {
				builder.append(filesList.get(i).trim()).append(":");
			}
			return new Response(true, builder.toString());
		} else {

			return new Response(true, "Nofiles");
		}
	}

	public void sendtheHealthchekSingnalToCunkServer() {
		//
	}

	public void chunkServerStatistics() {
		this.fileCollection.clear();
		FileMonitor fmonitor = new FileMonitor();
		ArrayList<String> filesList = null;
		boolean hasfiles = fmonitor.dofileExists();

		if (hasfiles) {

			filesList = fmonitor.getAllfilesInfoOnChunkServer();
			this.fileCollection = filesList;
		}
	}

	public void get3AavailableChunkServers() {

	}

	public Command registerNode(ChunkServersRequestCommand command) throws Exception {

		Response response = null;
		if (command == null) {
			response = new Response(true, "success Bhavin !!");
		}

		return response;
	}

	private void intializeChunkNode() throws Exception {
		// 1.Chunk Node is alive
		ServerSocket sc = new ServerSocket(0);
		System.out.println("Resolved Host name is :");
		System.out.println(sc.getInetAddress().getLocalHost().getHostAddress());
		System.out.println(InetAddress.getLocalHost().getHostName());

		this.chunkNodeIP = InetAddress.getLocalHost().getHostAddress();
		this.chunkrNodePORT = sc.getLocalPort();
		this.serverSocket = sc;

		// 2. Start worker thread
		ChunkNodeWorker chunkNodeWorker = new ChunkNodeWorker(sc, this);
		Thread t = new Thread(chunkNodeWorker);
		t.start();

		// 3. start Minor pulse thread
		Chunkpulse30Sec p30 = new Chunkpulse30Sec(this);
		Thread t30sec = new Thread(p30);
		t30sec.start();

		// 4. start Major pulse thread
		Chunkpulse300Sec p300 = new Chunkpulse300Sec(this);
		Thread t300sec = new Thread(p300);
		t300sec.start();

		System.out.println(
				" Chunk node is hoasted at : " + this.chunkNodeIP + "  " + " Listenning port : " + sc.getLocalPort());

		// 5. Send the controller
		sendtheChunkNodeinfotoController();
	}

	public void sendtheChunkNodeinfotoController() throws Exception {
		// TODO Auto-generated method stub
		ChunkNodeWentliveRequest livereq = new ChunkNodeWentliveRequest(this.controllerNodeIP, this.controllerNodePORT,
				this.chunkNodeIP, this.chunkrNodePORT);
		Command resp = sender.sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT, livereq.unpack());
		Response response = (Response) resp;
		System.out.println(response.getMessage());
	}

	public void sendNewFileInfoToController() throws NoSuchAlgorithmException, IOException {
		String allFileData = "";
		HashMap<String, Set<String>> fileInfo = new HashMap<String, Set<String>>();
		if (this.newfiles.size() > 0) {

			for (String eachChunkFileName : newfiles) {
				Matcher matcher = FILE_NAME_PATETRN.matcher(eachChunkFileName);
				String fileName = null;
				if (matcher.matches()) {
					fileName = matcher.group(1);
					Set<String> chunks = fileInfo.get(fileName);
					if (chunks == null) {
						chunks = new HashSet<String>();
						fileInfo.put(fileName, chunks);
					}
					chunks.add(eachChunkFileName);
				}

			}

			// f1|f1c1:f1c2 :f1c3 ? f2|f2c1:f2c2 ?
			for (String key : fileInfo.keySet()) {
				Set<String> chunks = fileInfo.get(key);
				String[] chunksAsArray = chunks.toArray(new String[0]);
				String eachFileData = key + "|" + ChunkFileUtility.join(chunksAsArray, ":");
				allFileData = eachFileData + "?";
			}

			ChunkNodeFileInfoCommand cmd = new ChunkNodeFileInfoCommand("MAJOR_HEART_BEAT", 0, this.chunkNodeIP,
					this.chunkrNodePORT, allFileData, 0);
			Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT,
					cmd.unpack());
			Response response = (Response) resp;
			System.out.println("*****MAJOR HEAR BEAT*******");
			System.out.println(response.getMessage());
			System.out.println("*****MAJOR HEAR BEAT*******");

			this.newfiles.clear();

		} else {
			ChunkNodeFileInfoCommand cmd = new ChunkNodeFileInfoCommand("MAJOR_HEART_BEAT", 0, this.chunkNodeIP,
					this.chunkrNodePORT, allFileData, 0);
			Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT,cmd.unpack());

		}
	}

	public void sendchunkkinfoToCOntroller() throws NoSuchAlgorithmException, IOException {
		// Update the file lists to check if there are any new Files

		// it will return File collection arraylist with file names

		// How to maintain checksum for the all files for the first time and
		// compare here it with
		// method

		try {
			HashMap<String, Set<String>> fileInfo = new HashMap<String, Set<String>>();

			chunkServerStatistics();
			for (String eachChunkFileName : fileCollection) {
				Matcher matcher = FILE_NAME_PATETRN.matcher(eachChunkFileName);
				String fileName = null;
				if (matcher.matches()) {
					fileName = matcher.group(1);
					Set<String> chunks = fileInfo.get(fileName);
					if (chunks == null) {
						chunks = new HashSet<String>();
						fileInfo.put(fileName, chunks);
					}
					chunks.add(eachChunkFileName);
				}

			}

			String allFileData = "";
			// f1|f1c1:f1c2 :f1c3 ? f2|f2c1:f2c2 ?
			for (String key : fileInfo.keySet()) {
				Set<String> chunks = fileInfo.get(key);
				String[] chunksAsArray = chunks.toArray(new String[0]);
				String eachFileData = key + "|" + ChunkFileUtility.join(chunksAsArray, ":");
				allFileData = eachFileData + "?";
			}

			// if (fileSpiltInfoInfoCollection.size() > 0) {
			// // for (int i = 0; i < this.fileSpiltInfoInfoCollection.size(); i++) {
			// Set<String> keys = fileSpiltInfoInfoCollection.keySet();
			// for (String string : keys) {
			// fileSpiltInfoInfoCollection.get(string);
			ChunkNodeFileInfoCommand cmd = new ChunkNodeFileInfoCommand("", 0, this.chunkNodeIP, this.chunkrNodePORT,
					allFileData, 0);
			Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP, this.controllerNodePORT,
					cmd.unpack());
			Response response = (Response) resp;

			System.out.println(response.getMessage());
			// }
			// }
			String generatedCHeckSumID = "";

			// TemperingUtil temperU = new TemperingUtil();

			// temperU.generateChecksum(this.fileCollection.get(i));
			/*
			 * ChunkNodeFileInfoCommand cmd = new
			 * ChunkNodeFileInfoCommand(this.controllerNodeIP, this.controllerNodePORT,
			 * this.chunkNodeIP, this.chunkrNodePORT,null , temperU.checkSumID);
			 * 
			 * 
			 */

			// ChunkNodeFileInfoCommand cmd = new
			// ChunkNodeFileInfoCommand(this.controllerNodeIP,this.controllerNodePORT,
			// this.chunkNodeIP, this.chunkrNodePORT,null , temperU.checkSumID);
			// Command resp = new TCPSender().sendAndReceiveData(this.controllerNodeIP,
			// this.controllerNodePORT, cmd.unpack());

			// Response response = (Response) resp;

			// System.out.println(response.getMessage());

			// }
		}

		catch (ConnectException e) {
			System.out.println("Connection forcibaly closed or shut down might have happned on remote machine...");
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());

		} catch (Exception e) {
			System.out.println("Connection forcibaly closed or shut down might have happned on remote machine...");
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
			InetAddress ipaddress = InetAddress.getLocalHost();// InetAddress.getByName(strIP);
			System.out.println("IP address: " + ipaddress.getHostAddress());
			controllerNodePORT = Integer.parseInt(args[1]);

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(0);
		}

		ChunkNode chunkNode = new ChunkNode();

		chunkNode.controllerNodeIP = controllerIP;
		chunkNode.controllerNodePORT = controllerNodePORT;

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
				// return3AvailableChunkServers(controllerNode);

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

		if (command instanceof ChunkNodeFileInfoCommand) {
			return collectfilesInfo((ChunkNodeFileInfoCommand) command);
		} else if (command instanceof ChunkWriteCommand) {
			return chunkReceived((ChunkWriteCommand) command);
		} else if (command instanceof ChunkFileReadCommand) {
			return ChunkWriteToClient((ChunkFileReadCommand) command);
		}

		return new Response(true, "Nothing");
	}

	private Command ChunkWriteToClient(ChunkFileReadCommand command) {
		System.out.println("Inside chunk2ClienttWrite method");
		File file = new File("D:\\Temp\\chunkServer\\out", command.fileName);
		if (!file.exists()) {
			return new Response(false, "File not found." + file.getAbsolutePath());
		}

		boolean isFileTemperd = false;
		try {
			// Check whether file has been tempered or not.
			String sha1FromFile = ChunkFileUtility.readSha1(file.getAbsolutePath());
			byte[] fileBytes = ChunkFileUtility.fileContentWithoutSha1(file);
			String newSha1 = TemperingUtil.generateChecksumOfAllBytes(fileBytes);
			if (!sha1FromFile.equals(newSha1)) {
				isFileTemperd = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Command writeCommand = null;
		if (isFileTemperd) {
			writeCommand = new Response(false, "Tempered:"+command.fileName+":"+command.ipAddress+":"+command.port + ":"+file.getAbsolutePath());
		} else {
			writeCommand = new ChunkWriteOperationsCommand(command.ipAddress, command.port, command.fileName,
					file.getName(), file);
		}
		System.out.println("data to sent : " + command.clientIP + ":" + command.clientPORT);
		return writeCommand;

	}

	private Command chunkReceived(ChunkWriteCommand command) {
		System.out.println("---" + command);

		String fileName = command.getFileName();
		String chunkName = command.getChunkName();
		String newFile = "";
		newFile = fileName + " : " + chunkName;
		newfiles.add(chunkName);
		// TODO NoOfChunks not sent -- check
		// fileSpiltInfoInfoCollection.put(fileName, 0);
		return new Response(true, "File received.");
	}

}
