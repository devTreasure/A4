package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

	private Socket socket;
	private DataOutputStream dout;

	public TCPSender() throws IOException {
		this.socket = socket;
		
	}

	public void sendData(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}

	public void sendMessage(Socket socket) throws IOException {
		Socket sc=null;
		try {
			
			sc= socket;
			dout = new DataOutputStream(sc.getOutputStream());			
			int randomN = 191;
			dout.writeInt(randomN);
			dout.flush();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dout != null)
				dout.close();
		     	sc.close();
		}
	}

}