package ChunkServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TemperingUtil {
	
	public String checkSumID ="";
	
	public void generateChecksum(String strFile) throws NoSuchAlgorithmException, IOException
	{
		
		this.checkSumID = "";
		
		String datafile = "D:\\destination\\"+ strFile;
		
		System.out.println("Checksum being generated for :"+strFile);
		
		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(datafile);
		byte[] dataBytes = new byte[8192];  //chunk would be of 8 KB

		int nread = 0;

		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;

		byte[] mdbytes = md.digest();

		// convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		this.checkSumID=sb.toString();
	}
	

	public static void main(String args[]) throws Exception {

		String datafile = "D:\\INSTLOG.TXT";

		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(datafile);
		byte[] dataBytes = new byte[8192];  //chunk would be of 8 KB

		int nread = 0;

		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;

		byte[] mdbytes = md.digest();

		// convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		System.out.println("Digest(in hex format):: " + sb.toString());

	}
}