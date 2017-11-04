package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class CommandFactory {

	public static Command process(Socket socket) {

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
			} else if (ChunkNodePollingCommand.cmd.equals(str_request_type)) {
				ChunkNodePollingCommand chunkNodePollingCmd = new ChunkNodePollingCommand();
				chunkNodePollingCmd.pack(din);
				cmd = chunkNodePollingCmd;
			} else if(ChunkWriteCommand.cmd.equals(str_request_type)) {
				ChunkWriteCommand chunkWriteCommand = new ChunkWriteCommand();
				chunkWriteCommand.pack(din);
				cmd = chunkWriteCommand;
			} else {
				System.out.println("ERROR: UNKNOWN COMMAND. " + str_request_type);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			try {
//				din.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}

		return cmd;

	}

}
