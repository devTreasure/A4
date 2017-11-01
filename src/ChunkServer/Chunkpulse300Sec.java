package ChunkServer;

import java.net.ServerSocket;


public class Chunkpulse300Sec  implements Runnable{
	   private ServerSocket serverSocket;
	   private ChunkNode node;
	   public boolean continueFlag = true;
	  

	   public Chunkpulse300Sec(ServerSocket sc, ChunkNode node) {
	      this.serverSocket = sc;
	      this.node = node;
	   }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep(300000);
				
				System.out.println("Pulse 300 sec");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void sendMajorHeartbeat() {
		
	}

}
