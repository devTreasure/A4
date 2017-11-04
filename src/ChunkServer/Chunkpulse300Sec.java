package ChunkServer;

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
            System.out.println("Pulse 300 sec");
            //TODO: Implement this notification
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }

   public void sendMajorHeartbeat() {

   }

}
