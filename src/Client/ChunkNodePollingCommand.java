
package Client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkNodePollingCommand implements Command {
	
	
	public static final String cmd = "CMD_CHUNK_NODE_POLLING_INFO";

	public String ipAddress;
	public int port;
	public String chunkIP;
	public int chunkPORT;
	public String fileNames;

	public ChunkNodePollingCommand() {
	}

	public ChunkNodePollingCommand(String ipAddress, int port,String fileNames) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.fileNames=fileNames;
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
			dout.writeInt(fileNames.length());
			dout.write(fileNames.getBytes());
	
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
			fileNames =readString(din);

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
		return "chunkNodePollingCommand [cmd=" + cmd +"]";
	}


}
