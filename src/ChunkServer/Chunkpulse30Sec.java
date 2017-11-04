package ChunkServer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Chunkpulse30Sec implements Runnable {

   private ChunkNode node;
   public boolean continueFlag = true;

   public Chunkpulse30Sec(ChunkNode node) {
      this.node = node;
   }

   @Override
   public void run() {
      // TODO Auto-generated method stub
      while (true) {
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
