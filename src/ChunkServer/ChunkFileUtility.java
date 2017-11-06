package ChunkServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class ChunkFileUtility {

	public static String addSha1AndWriteChunk(byte[] b, File file) throws Exception {

		String sha1ID = TemperingUtil.generateChecksumOfAllBytes(b);

		FileOutputStream fos = new FileOutputStream(file);
		byte[] sha1Bytes = sha1ID.getBytes();
		System.out.println(sha1Bytes.length);
		System.out.println(b.length);
		fos.write(sha1Bytes);
		fos.write(b);
		System.out.println(file.length());
		fos.close();

		return sha1ID;
	}

	public static String readSha1(String path) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(path, "r");

		// 160 bit sha has 20 bytes - 40 HEX digits numerical value
		// So it translates into 40 byte string.
		raf.seek(0);
		byte[] shaBytes = new byte[40];
		raf.readFully(shaBytes);
		String storedSha1Id = new String(shaBytes);
		System.out.println("storedSha1Id :" + storedSha1Id);
		return storedSha1Id;

	}

	public static byte[] fileContentWithoutSha1(String path) throws Exception {
		byte[] data = null;
		RandomAccessFile raf = null;

		try {
			File f = new File(path);
			System.out.println("Rading file: " + f.getAbsolutePath() + ", Exists:[" + f.exists() + "]");

			// 160 bit sha has 20 bytes - 40 HEX digits numerical value
			// So it translates into 40 byte string.
			
			// raf.seek(0);
			// byte[] shaBytes = new byte[40];
			// raf.readFully(shaBytes);
			// String storedSha1Id = new String(shaBytes);
			
			raf = new RandomAccessFile(path, "r");
			long contentLength = f.length() - 40;
			System.out.println("File content length" + f.length() + ", will be readinf minus 40 bytes:" + contentLength);
			data = new byte[(int) contentLength];
			raf.seek(40);
			raf.read(data, 0, data.length);
		} catch (Exception e) {
			raf.close();
		}
		
		return data;
	}
	


   public static String join(String[] str, String separator) {
      String retval = "";
      for (String s : str) {
         retval += separator + s;
      }
      return retval.replaceFirst(separator, "");
   }


}
