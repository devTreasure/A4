package Client;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileWriteOperationCommand implements Command {
	
	
	public static final String cmd = "CMD_FILE_WRITE_OPS";

	public String ipAddress;
	public int port;
	public String chunkIP;
	public int chunkPORT;
	public String fileName;
	public String checksumID;

	public FileWriteOperationCommand() {
	}

	public FileWriteOperationCommand(String ipAddress, int port,String ChunkipAddress, int Chunkport,String fileName,String ChecksumID) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.chunkIP=ChunkipAddress;
		this.chunkPORT=Chunkport;
		this.fileName=fileName;
		this.checksumID=ChecksumID;
	}

	public byte[] unpack() {

		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = null;
		DataOutputStream dout = null;

		try {
			baOutputStream = new ByteArrayOutputStream();
			dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
			dout.writeInt(cmd.length());
			dout.write(cmd.getBytes());
			dout.writeInt(ipAddress.length());
			dout.write(ipAddress.getBytes());
			dout.writeInt(port);
			dout.writeInt(chunkIP.length());
			dout.write(chunkIP.getBytes());
			dout.writeInt(chunkPORT);
			dout.writeInt(fileName.length());
			dout.write(fileName.getBytes());
			dout.writeInt(checksumID.length());
			dout.write(checksumID.getBytes());
			dout.flush();
			marshalledBytes = baOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baOutputStream.close();
				dout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return marshalledBytes;
	}

	public void pack(DataInputStream din) {
		try {

			ipAddress = readString(din);
			port = din.readInt();
			chunkIP=readString(din);
			chunkPORT= din.readInt();
			fileName= readString(din);
			checksumID=readString(din);

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
		return "FileWriteOperationCommand [cmd=" + cmd + ", ipAddress=" + chunkIP + ", port=" + chunkPORT + "]";
	}


}
