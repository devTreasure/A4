package Client;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkNodeFileStoreCommand implements Command {
	public static final String cmd = "CMD_CHUNK_NODE_FILE_STORE_COMMAND";

	public String ipAddress;
	public int port;
	public String chunkIP;
	public int chunkPORT;

	public ChunkNodeFileStoreCommand() {
	}

	public ChunkNodeFileStoreCommand(String ipAddress, int port,String ChunkipAddress, int Chunkport) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.chunkIP=ChunkipAddress;
		this.chunkPORT=Chunkport;
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
		return "ChunkServersFileStoreCommand [cmd=" + cmd + ", ipAddress=" + chunkIP + ", port=" + chunkPORT + "]";
	}

}
