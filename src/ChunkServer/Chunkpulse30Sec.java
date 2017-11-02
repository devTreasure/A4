package ChunkServer;

import java.net.ServerSocket;

import Client.ChunkServersRequestCommand;
import Client.Command;
import Client.Response;
import Client.TCPSender;
import Client.chunkNodeFileInfoCommand;
import Client.chunkNodePollingCommand;

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
				chunkNodePollingCommand cmd = new chunkNodePollingCommand(this.node.controllerNodeIP, this.node.controllerNodePORT,"thisfile");

				Command resp =new TCPSender().sendAndReceiveData(this.node.controllerNodeIP, this.node.controllerNodePORT, cmd.unpack());
			
			    Response response = (Response) resp;
			    
			    System.out.println(response.getMessage());

				System.out.println("Pulse 30 sec");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
