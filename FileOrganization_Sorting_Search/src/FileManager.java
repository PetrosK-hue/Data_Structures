import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class FileManager {

	
	//For file handling
	public String fileName;
	public int filePos;
	public int pageNum;
	public int page_size = 128;
	
	
	public byte[] FileHandling(String fileName) throws IOException
	 {
	RandomAccessFile file = new RandomAccessFile(fileName, "rw");
	
	filePos =(int) file.getFilePointer();
	int pageNum= (int) file.length()/128;
	String info ="Pages:"+pageNum+" Current Position:"+filePos;
	byte[] bufinfo = info.getBytes();
	return bufinfo;
	 }
	 
	public int CreateFile(int filePos, int pageNum) throws FileNotFoundException, IOException {
	//Create File
	File file = new File("newFile.txt");
    
	// to fill with random bytes.
    try (FileOutputStream out = new FileOutputStream(file)) {
    	byte[] chars = new byte[page_size];
        chars[127] = '\n';
        out.write(chars);
        return 1;
    }
   
	}
	
	public int OpenFile(String filename) throws FileNotFoundException {
		
		File file = new File(filename);    
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(filename);
			return  1;
	    }else
	    {
	    	FileOutputStream fis = new FileOutputStream(filename);
	    }
		
		return 0;
		}
		
	public int ReadBlock(String filename, int pageNum) throws IOException {

		try (FileInputStream fis = new FileInputStream(filename)){
			ByteBuffer bytes = ByteBuffer.allocateDirect(page_size);
			fis.getChannel().read(bytes, (pageNum-1)*128);
		}catch(IllegalArgumentException e) {
			System.out.println("Wrong Block Choice.");
		}
			//fis.close();
			return 1;
		}
	
	public int ReadNextBlock(String filename, int pageNum) throws IOException {
		pageNum++;
		ReadBlock(filename, pageNum);
		return 1;
	}
	
	public int ReadPrevBlock(String filename, int pageNum) throws IOException {
		if(pageNum>=1) {
			pageNum--;
		}
		ReadBlock(filename, pageNum);
		return 1;	
	}
	
	public int WriteBlock(String filename, int filePos,byte buffer[]) throws IOException {

		try (RandomAccessFile file = new RandomAccessFile(filename, "rw");){
			file.seek(filePos*128);
			file.write(buffer);
			//file.close();
			return 1;
		}}
	
	public int WriteNextBlock(String filename, int filePos, byte buffer[]) throws IOException {
		filePos = filePos+128;
		WriteBlock(filename, filePos,buffer);
		return 1;
	}
	
	public int AppendBlock(String filename,byte buffer[]) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename, true);
		fos.write(buffer);
		return 1;
	}
	
	public int deleteBlock(String filename, int pageNum) throws FileNotFoundException, IOException {
		
		try (RandomAccessFile  fis = new RandomAccessFile (filename, "rw")){
			byte[] b = new byte[128];
			int len = (int) fis.length();
			fis.seek(len-128);
			fis.read(b);
			
			if((pageNum-1)*128 == len-128) {
				fis.write(null, (pageNum-1)*128, 128);
				
			}else {
				fis.write(b, (pageNum-1)*128, 128);
			}
			return 1;
	}
	}
	
	public int CloseFile(String filename, byte[] bufinfo) throws FileNotFoundException, IOException {
		try (RandomAccessFile  fis = new RandomAccessFile (filename, "rw")){
			fis.seek(128);
			fis.write(bufinfo);
			fis.close();
			return 1;
		}
		
	}

	}
	


    


	