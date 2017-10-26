package Client;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPRecevier implements Runnable {

	private Socket socket;
	private DataInputStream din;
	private ServerSocket serversocket;

	public void TCPReceiver(Socket socket) throws IOException {
		
		this.socket = socket;
		
		din = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		Socket socket = null;
		DataInputStream din = null;
		// read first data
		try {

			socket = this.serversocket.accept();

			System.out.println(".....Connection has established....");
			System.out.println("...................................");

			din = new DataInputStream(socket.getInputStream());

			while (din.available() > 0) // We don't want to read when available bytes are zero, to
										// avoid EOF exception
			{

				int nodeNameLength = din.readInt();
				
				byte[] nodeNameBytes = new byte[nodeNameLength];
				
				din.readFully(nodeNameBytes);
				
				String nodeName = new String(nodeNameBytes);

				int sentMessagesCounter = din.readInt();
				double sumOfPayload = din.readDouble();

				int receivedMessagesCounter = din.readInt();
				double receivedsumOfPayload = din.readDouble();

				// Data data = new Data(nodeName, sentMessagesCounter, sumOfPayload,
				// receivedMessagesCounter, receivedsumOfPayload);
				// collator.dataCollection.add(data);
			}

			din.close();

			socket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (din != null)
					din.close();
				if (socket != null)
					socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}