package Controller;

public class FileInfo {

   public String fileName = "";
   public String[] chunkFileNames;
   
   public String chunkNodeIP = "";
   public int chunkNodePORT = -1;

   public FileInfo() {
   }

   public FileInfo(String fileName, String[] chunkFileName, String chunkNodeIP, int chunkNodePORT) {
      this.fileName = fileName;
      this.chunkFileNames = chunkFileName;
      this.chunkNodeIP = chunkNodeIP;
      this.chunkNodePORT = chunkNodePORT;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((chunkNodeIP == null) ? 0 : chunkNodeIP.hashCode());
      result = prime * result + chunkNodePORT;
      result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
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
      FileInfo other = (FileInfo) obj;
      if (chunkNodeIP == null) {
         if (other.chunkNodeIP != null)
            return false;
      } else if (!chunkNodeIP.equals(other.chunkNodeIP))
         return false;
      if (chunkNodePORT != other.chunkNodePORT)
         return false;
      if (fileName == null) {
         if (other.fileName != null)
            return false;
      } else if (!fileName.equals(other.fileName))
         return false;
      return true;
   }

	@Override
	public String toString() {
		return "FileInfo [ fileName= "+fileName + ", chunkFileNames=" + chunkFileNames + "chunkNodeIP= "+ chunkNodeIP +", port=" + chunkNodePORT + "]";
	}
}
