package ChunkServer;

import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;

public class Chunkpulse300Sec implements Runnable {

   private ChunkNode node;
   public boolean continueFlag = true;

   public Chunkpulse300Sec(ChunkNode node) {
      this.node = node;
   }

   @Override
   public void run() {
      while (true) {
         try {
            Thread.sleep(300000);
            System.out.println("MAJOR Pulse 300 sec");
            try {
				node.sendNewFileInfoToController();
			} 
            catch (ConnectException e)
            {
            	  System.out.println(e.getMessage());
            }
          
            catch (NoSuchAlgorithmException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //TODO: Implement this notification
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }


}
