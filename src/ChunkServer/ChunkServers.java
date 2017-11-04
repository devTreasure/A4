package ChunkServer;

public class ChunkServers {
	
	private String IP;
	private int PORT;
	
	public ChunkServers(String ipAddress, int port2) {
	   this.IP=ipAddress;
	   this.PORT=port2;
	}

	public String IP() {
		return IP;
	}

	public void IP(String iP) {
		IP = iP;
	}

	public int PORT() {
		return PORT;
	}

	public void PORT(int pORT) {
		PORT = pORT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IP == null) ? 0 : IP.hashCode());
		result = prime * result + PORT;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkServers other = (ChunkServers) obj;
		if (IP == null) {
			if (other.IP != null)
				return false;
		} else if (!IP.equals(other.IP))
			return false;
		if (PORT != other.PORT)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChunkServers [IP=" + IP + ", PORT=" + PORT + "]";
	}

}
