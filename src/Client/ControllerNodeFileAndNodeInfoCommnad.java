package Client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ControllerNodeFileAndNodeInfoCommnad implements Command {
	public static final String cmd = "CMD_CONTROLLER_NODE_FILE_INFO";
	public String fileName;
	

	public ControllerNodeFileAndNodeInfoCommnad() {
	}

	public ControllerNodeFileAndNodeInfoCommnad(String strfileName) {
		super();
		this.fileName = strfileName;

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
			dout.writeInt(fileName.length());
			dout.write(fileName.getBytes());
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

			fileName = readString(din);

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
		return "ControllerNodeFileAndNodeInfoCommnad [cmd=" + cmd + ", fileName=" + fileName  + "]";
	}

}
