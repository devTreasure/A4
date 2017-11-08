package Client;

import java.io.DataInputStream;
import java.net.Socket;

public class CommandFactory {

   public static Command process(Socket socket) {
      return process(socket, "");
   }
   
	public static Command process(Socket socket, String directoryPath) {

		Command cmd = null;
		DataInputStream din = null;
		try {
			din = new DataInputStream(socket.getInputStream());
			int request_Typelength = 0;
			request_Typelength = din.readInt();
			byte[] request_Type = new byte[request_Typelength];
			din.readFully(request_Type);
			String str_request_type = new String(request_Type);
			System.out.println(str_request_type);

			if (ChunkServersRequestCommand.cmd.equals(str_request_type)) {
				ChunkServersRequestCommand chunkServersRequestCmd = new ChunkServersRequestCommand();
				chunkServersRequestCmd.pack(din);
				cmd = chunkServersRequestCmd;
			} else if (ChunkNodeFileInfoCommand.cmd.equals(str_request_type)) {
				ChunkNodeFileInfoCommand fileInfoRequestCmd = new ChunkNodeFileInfoCommand();
				fileInfoRequestCmd.pack(din);
				cmd = fileInfoRequestCmd;
			} else if (Response.cmd.equals(str_request_type)) {
				Response registerReponse = new Response();
				registerReponse.pack(din);
				cmd = registerReponse;
			} else if (ChunkNodeWentliveRequest.cmd.equals(str_request_type)) {
				ChunkNodeWentliveRequest chunkNodeAliveCmd = new ChunkNodeWentliveRequest();
				chunkNodeAliveCmd.pack(din);
				cmd = chunkNodeAliveCmd;
			} else if (FileInfoCommnad.cmd.equals(str_request_type)) {
				FileInfoCommnad controllerfileAndNodeInfo = new FileInfoCommnad();
				controllerfileAndNodeInfo.pack(din);
				cmd = controllerfileAndNodeInfo;
			} else if (ChunkFileReadCommand.cmd.equals(str_request_type)) {
				ChunkFileReadCommand chunk2ClienttWrite = new ChunkFileReadCommand();
				chunk2ClienttWrite.pack(din);
				cmd = chunk2ClienttWrite;
			} else if (ChunkWriteOperationsCommand.cmd.equals(str_request_type)) {
				ChunkWriteOperationsCommand chunkWriteOperationsCommand = new ChunkWriteOperationsCommand();
				chunkWriteOperationsCommand.pack(din);
				cmd = chunkWriteOperationsCommand;
			} else if (ChunkNodePollingCommand.cmd.equals(str_request_type)) {
				ChunkNodePollingCommand chunkNodePollingCmd = new ChunkNodePollingCommand();
				chunkNodePollingCmd.pack(din);
				cmd = chunkNodePollingCmd;
			} else if (ChunkWriteCommand.cmd.equals(str_request_type)) {
				ChunkWriteCommand chunkWriteCommand = new ChunkWriteCommand();
				chunkWriteCommand.directoryName = directoryPath;
				chunkWriteCommand.pack(din);
				cmd = chunkWriteCommand;
			} else {
				System.out.println("ERROR: UNKNOWN COMMAND. " + str_request_type);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// try {
			// din.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}

		return cmd;

	}

}
