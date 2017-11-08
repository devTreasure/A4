package ChunkServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;

public class Hello {
	
	public static void main(String[] args) throws IOException {
//	File file = new File("D:\\destination");
//		
//	File filespace = new File("D:");
//	
//	long totalSpace = filespace.getTotalSpace(); //total disk space in bytes.
//	long usableSpace = filespace.getUsableSpace(); ///unallocated / free disk space in bytes.
//	long freeSpace = filespace.getFreeSpace(); //unallocated / free disk space in bytes.
//	
//	System.out.println("Total Space (mb) : " + totalSpace /1024 /1024 );
//	System.out.println("Total usable Space (mb) : " + totalSpace /1024 /1024 );
//	System.out.println("Total free Space (mb) : " + totalSpace /1024 /1024 );
//	
//		if(file.isDirectory()){
//
//		    if(file.list().length>0){
//		    	
//		    	String[] filelists=file.list();
//		    	for (String string : filelists) {
//		    		System.out.println(string);
//				}
//
//		        System.out.println("Directory is not empty!");
//
//		    }else{
//
//		        System.out.println("Directory is empty!");
//
//		    }
//
//		}else{
//
//		    System.out.println("This is not a directory");
//
//		}
	   
        Matcher matcher = ChunkNode.FILE_NAME_PATETRN.matcher("logs2808_FullGC_part.log_chunk.001");
       
        System.out.println(matcher.matches());
        System.out.println(matcher.group(0));
        System.out.println(matcher.group(1));
       
        String input = "ab|cd";
        String[] split = input.split("\\|");
        System.out.println(split[0]);
        System.out.println(split[1]);
        
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.add(new File("D:\\Temp\\chunkServer\\toclient\\logs2808_FullGC_part.log_chunk.002"));
        arrayList.add(new File("D:\\Temp\\chunkServer\\toclient\\logs2808_FullGC_part.log_chunk.001"));
        
        for (File file : arrayList) {
           System.out.println(file);
        }
        
        Collections.sort(arrayList);
        
        for (File file : arrayList) {
           System.out.println(file);
        }
        
	}

}
