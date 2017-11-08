package ChunkServer;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;

public class FileMonitor {

	public Hashtable<String, String> filesHTCollection = new Hashtable<String, String>();
	public boolean hasFiles = false;

	public String FILE_PATH = "NOT_SPECIFIED";
	public static String DRIVE_PATH = "D:";

	public ArrayList<String> fileslist = new ArrayList<String>();

	public FileMonitor(String directoryPath) {
	   this.FILE_PATH = directoryPath;
	}

	public boolean dofileExists() {
		File file = new File(FILE_PATH);

		if (file.list().length > 0) {

			this.hasFiles = true;

		} else {

			this.hasFiles = false;

		}

		return this.hasFiles;

	}

	public ArrayList<String> getAllfilesInfoOnChunkServer() {

		File file = new File(FILE_PATH);

		if (file.list().length > 0) {

			if (file.isDirectory()) {

				if (file.list().length > 0) {

					this.hasFiles = true;

					this.fileslist.clear();

					String[] filelists = file.list();

					for (String string : filelists) {

						System.out.println(string);

						this.fileslist.add(string);
					}
				}
			}

		}

		return this.fileslist;
	}

	public void fileExists() {

		File file = new File(FILE_PATH);

		File filespace = new File(DRIVE_PATH);

		long totalSpace = filespace.getTotalSpace(); // total disk space in bytes.
		long usableSpace = filespace.getUsableSpace(); /// unallocated / free disk space in bytes.
		long freeSpace = filespace.getFreeSpace(); // unallocated / free disk space in bytes.

		System.out.println("Total Space (mb) : " + totalSpace / 1024 / 1024);
		System.out.println("Total usable Space (mb) : " + totalSpace / 1024 / 1024);
		System.out.println("Total free Space (mb) : " + totalSpace / 1024 / 1024);

	}

	public String performFileMonitoring(String fileName) throws NoSuchAlgorithmException, IOException {

		String checkSumID = "";

		TemperingUtil temperinhObj = new TemperingUtil();
		temperinhObj.generateChecksum(fileName);

		return temperinhObj.checkSumID;

	}

}
