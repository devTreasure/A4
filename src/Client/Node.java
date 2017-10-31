package Client;


public interface Node {
	   Command notify(Command command) throws Exception;
	}