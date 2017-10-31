package Client;


import java.io.DataInputStream;

public interface Command {

   void pack(DataInputStream din);

   byte[] unpack();

}
