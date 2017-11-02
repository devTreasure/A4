package ChunkServer;

import java.io.File;
import java.io.IOException;

public class Hello {
	
	public static void main(String[] args) throws IOException {
	File file = new File("D:\\destination");
		
	File filespace = new File("D:");
	
	long totalSpace = filespace.getTotalSpace(); //total disk space in bytes.
	long usableSpace = filespace.getUsableSpace(); ///unallocated / free disk space in bytes.
	long freeSpace = filespace.getFreeSpace(); //unallocated / free disk space in bytes.
	
	System.out.println("Total Space (mb) : " + totalSpace /1024 /1024 );
	System.out.println("Total usable Space (mb) : " + totalSpace /1024 /1024 );
	System.out.println("Total free Space (mb) : " + totalSpace /1024 /1024 );
	
		if(file.isDirectory()){

		    if(file.list().length>0){
		    	
		    	String[] filelists=file.list();
		    	for (String string : filelists) {
		    		System.out.println(string);
				}

		        System.out.println("Directory is not empty!");

		    }else{

		        System.out.println("Directory is empty!");

		    }

		}else{

		    System.out.println("This is not a directory");

		}
	}

}
