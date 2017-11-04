package ChunkServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;

import Client.ChunkServersRequestCommand;
import Client.Command;
import Client.Response;
import Client.TCPSender;
import Client.ChunkNodeFileInfoCommand;
import Client.ChunkNodePollingCommand;

public class Chunkpulse30Sec  implements Runnable{
	
	   private ServerSocket serverSocket;
	   private ChunkNode node;
	   public boolean continueFlag = true;
	  

	   public Chunkpulse30Sec(ServerSocket sc, ChunkNode node) {
	      this.serverSocket = sc;
	      this.node = node;
	   }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep(30000);
				
				node.sendchunkkinfoToCOntroller();

				System.out.println("Pulse 30 sec");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
