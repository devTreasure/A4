package Client;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ChunkServer.ChunkServer;

public class ChunkWriteOperationsCommand implements Command {
	
	
	public static final String cmd = "CMD_CHUNK_WRITE_OPS";

	public String IP;
	public int PORT ;
	public  String fileName;
	public String chunkName;
	public File chunk;

	public ChunkWriteOperationsCommand(String IP,int PORT, String fileName, String chunkName, File chunk) {
		super();
		this.IP = IP;
		this.PORT = PORT;
		this.fileName = fileName;
		this.chunkName = chunkName;
		this.chunk = chunk;
	}

	public ChunkWriteOperationsCommand() {
	}

	@Override
	public byte[] unpack() {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = null;
		DataOutputStream dout = null;

		byte[] fileBytes = new byte[64 * 1000];
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		try {
			fis = new FileInputStream(chunk);
			bis = new BufferedInputStream(fis);
			bis.read(fileBytes);

			baOutputStream = new ByteArrayOutputStream();
			dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
			
			dout.writeInt(cmd.length());
			dout.write(cmd.getBytes());
			
			dout.writeInt(IP.length());
			dout.write(IP.getBytes());
			dout.writeInt(PORT);
			
			dout.writeInt(fileName.length());
			dout.write(fileName.getBytes());
			
			dout.writeInt(chunkName.length());
			dout.write(chunkName.getBytes());
			
			dout.writeInt(fileBytes.length);
			dout.write(fileBytes);
			
			dout.flush();
			
			marshalledBytes = baOutputStream.toByteArray();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				bis.close();
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
			//this.target = new ChunkServer(ipAddress, port);			
			this.fileName = readString(din);
			this.chunkName = readString(din);			
			int fileBytesSize = din.readInt();
			byte[] fileBytes = new byte[fileBytesSize];
			din.readFully(fileBytes);
			String name = "D:\\Temp\\chunkServerToClient\\" + chunkName;
			FileOutputStream fileOutputStream = new FileOutputStream(name);
			fileOutputStream.write(fileBytes);
			fileOutputStream.close();
			System.out.println("Chunk writen: " + name);
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
		return "ChunkWriteOperationsCommand [target=" + IP + ", fileName=" + fileName + ", chunkName=" + chunkName + "]";
	}
	
	

}
