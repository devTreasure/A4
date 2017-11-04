package Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileSplit {
	
//	public int globalpartCounter = 0;
	public ArrayList<File> files = new ArrayList<File>();
	public String strfileName = "";

	public List<File> splitFile(File f) throws IOException {
		if(f==null || !f.exists()) {
			System.out.println("File does not exist. " + f);
			return Collections.emptyList();
		}
		
		this.files.clear();

		int partCounter = 1;

		// I like to name parts from 001, 002, 003, ...
		// you can change it to 0 if you want 000, 001, ...

		int sizeOfFiles = 1024 * 64;// 1MB
		byte[] buffer = new byte[sizeOfFiles];

		String fileName = f.getName();
		this.strfileName = fileName;

		// try-with-resources to ensure closing stream
		try (FileInputStream fis = new FileInputStream(f); BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;

			while ((bytesAmount = bis.read(buffer)) > 0) {
				// write each chunk of data into separate file with different
				// number in name
				String filePartName = String.format("%s.%03d", fileName + "_chunk", partCounter++);
				File newFile = new File(f.getParent(), filePartName);
				files.add(newFile);
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, bytesAmount);
				}
			}
//			globalpartCounter = partCounter;
		}
		return Collections.unmodifiableList(files);
	}

	public void mergeFiles(List<File> files, File into) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(into); BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
			}
		}
	}

	public static void main(String[] args) throws IOException {

		FileSplit objSplit = new FileSplit();

		objSplit.splitFile(new File("D:\\destination\\test.txt"));

		objSplit.mergeFiles(objSplit.files, new File("D:\\destination2\\" + objSplit.strfileName));
	}
}