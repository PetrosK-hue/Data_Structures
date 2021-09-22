package organi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import organi.Node;

public class SerialFile {
	

	private static final int MAX_KEY = 1000000;
	public int rec_size = 32;
	public int page_size =  128;
	public int key;
	public Comparator<? super Node> key2;
	public int numberOfKeys = 10000;
	
	public Node rec;
	Random rand = new Random();
	ArrayList <Integer> helping_buf4Keys = new ArrayList<Integer>(10000); 
	ArrayList <Node> nodesList = new ArrayList<Node>(10000);
	
	
	public int CreateSerialFile_A(String filename) throws NoSuchAlgorithmException, IOException {
		
		FileOutputStream fos = new FileOutputStream(filename);
		ByteArrayOutputStream hbuf = new ByteArrayOutputStream( );
		int disk_counter = 0;
		
		byte[] rec_buffer =  new byte[rec_size];
		byte[] page_buffer = new byte[page_size];
		int random_key;
		String random_str ;
		
		while( helping_buf4Keys.size() < 10000) {
			
			disk_counter++;
		
			for(int i = 0 ; i < 4 ; i++ ) {
				
				random_key = rand.nextInt(MAX_KEY) + 1;
				while(helping_buf4Keys.contains(random_key) == true)
				{
					random_key = rand.nextInt(MAX_KEY) + 1;
				}
		
				helping_buf4Keys.add(random_key);
				random_str = getAlphaNumericString(28);
				//rec = new Node(random_key, random_bytes );
				
				ByteBuffer bb = ByteBuffer.allocate(rec_size);
				bb.order(ByteOrder.BIG_ENDIAN);
				bb.putInt(random_key);
				bb.put(random_str.getBytes(StandardCharsets.US_ASCII));
				rec_buffer = bb.array();	
				hbuf.write(rec_buffer);	
						

		}
			page_buffer = hbuf.toByteArray();
			hbuf.reset();
			disk_counter++;
			fos.write(page_buffer);
		}
	
	
		return disk_counter;
	}
	
	
	public int CreateSerialFile_B() throws IOException {
		int disk_access = 0;
		
		FileInputStream fis = new FileInputStream("SerialFile_A.bin");
		
		FileOutputStream fos1 = new FileOutputStream("SerialFile_B.bin");
		FileOutputStream fos2 = new FileOutputStream("SerialFile_B_info.bin");
		byte[] key = new byte[4];
		byte[] rest_info = new byte[28];
		int pagenum = 0 ;
		int counter2=0;
		
		for(int i = 0; i < 2500; i++ ) {
			
			for(int j = 0; j < 4; j++) {
				int calc = (i*128)+(j*32);
				fis.getChannel().position(calc);
				fis.read(key);
				fis.read(rest_info);
			

				byte[] page = ByteBuffer.allocate(4).putInt(pagenum).array();

				fos1.write(key);
				fos1.write(page);
				fos2.write(rest_info);
				disk_access = disk_access + 3 ;
				counter2++;
			}
			disk_access++;
			pagenum++;
		}
		
		return disk_access;
	}
	
	
	public int Search_serialA() throws IOException {
		int disk_access = 0;
		int j, pos = 0 ;
		int key;
		RandomAccessFile raf = new RandomAccessFile("SerialFile_A.bin", "rw" );	
		boolean search = false;
		
		for(int i = 0; i < 20; i++) {
			
			pos = 0;
			int rand_key = rand.nextInt(10000) + 1;
			int search_key = helping_buf4Keys.get(rand_key);
			
			while(search == false) {
					
					
					raf.seek(pos);
					pos = pos + 32;
					key = raf.readInt();
					
					if(key == search_key) {
						search = true;
						pos = pos/128;
					}
				}
			disk_access = disk_access + pos;
			search = false;
			}
		
		
		return disk_access/20;
	}
		
	
	public int Search_serialB() throws IOException {
		int disk_access = 0;
		int j, pos = 0 ;
		int key;
		byte[] page = new byte[4];
		int pagenum;
		RandomAccessFile raf = new RandomAccessFile("SerialFile_B.bin", "rw" );
		RandomAccessFile raf2 = new RandomAccessFile("SerialFile_B_info.bin", "rw" );
		boolean search = false;
		
		
		for(int i = 0; i < 20; i++) {
			
			pos = 0;
			int rand_key = rand.nextInt(10000) + 1;
			int search_key = helping_buf4Keys.get(rand_key);
			
			while(search == false) {
					
					
					raf.seek(pos);
					pos = pos + 8;
					key = raf.readInt();
					
					if(key == search_key) {
						search = true;
						raf.read(page);
						DataInputStream dis = new DataInputStream(new ByteArrayInputStream(page));
						pagenum = dis.readInt();
						
						raf2.seek(pagenum*128 );
						
						pos = (pos/128) + 1;
					}
				}
			
			disk_access = disk_access + pos;
			search = false;
			}
		
		
		return disk_access/20;
		
	}
	
	
	public int ExternalSerialFile_A() throws IOException {
		
		byte[] h_buffer =  new byte[28];
		byte[] file_buffer = new byte[330000];
	
		
		RandomAccessFile raf = new RandomAccessFile("SerialFile_A.bin", "rw" );
		FileOutputStream fos = new FileOutputStream("ExternalFile_A.bin");
		ByteArrayOutputStream hbuf = new ByteArrayOutputStream();
		int disk_counter = 0;
	
		int key;
		String str ;
		
	
	
		
		for(int i = 0; i < 2500; i++ ) {
			
			for(int j = 0; j < 4; j++) {
				
				int calc = (i*128)+(j*32);
				raf.seek(calc);
				key = raf.readInt();
				
				raf.read(h_buffer);
				ByteBuffer info1 = ByteBuffer.wrap(h_buffer);
				str = StandardCharsets.UTF_8.decode(info1).toString();
				
				rec = new Node(key, str);
				nodesList.add(rec);
			
				nodesList.sort(key2);
				disk_counter++;
			}
		}
			//long cap = getBytesFromList(nodesList);
			//System.out.println("Length of List:" + cap);
			file_buffer = serialize(nodesList);
			fos.write(file_buffer);
			
			//calculate manually the disk accesses
			disk_counter = disk_counter/4;
			disk_counter = disk_counter + 2500;		
		
		
		return disk_counter;
	}
	
	
	
	public int ExternalSerialFile_B() throws IOException {
		
		byte[] file_buffer = new byte[330000];
		int disk_counter = 0;
		int key;
		int calc = 0 ;
		RandomAccessFile raf = new RandomAccessFile("SerialFile_B.bin", "rw" );
		FileOutputStream fos = new FileOutputStream("ExternalFile_B.bin");
	
		while(true){
			try{
		
				raf.seek(calc);
				key = raf.readInt();                                                   
				calc = calc + 8;
				disk_counter++;
				
				rec = new Node(key, null);
				nodesList.add(rec);
				nodesList.sort(key2);
				
			} catch(EOFException e){
			      break;
			   }
		}
		//long cap = getBytesFromList(nodesList);
		//System.out.println("Length of List:" + cap);
		file_buffer = serialize(nodesList);
		fos.write(file_buffer);
		
		
		//calculate manually the disk accesses
		disk_counter = disk_counter/16;
		disk_counter = disk_counter + 2500;	
			
		
		
		return disk_counter;
					
	}
	
	
	
	
	public int BinarySearch_Α() throws IOException {
		RandomAccessFile raf = new RandomAccessFile("ExternalFile_A.bin", "rw" );
		int key, disk_access = 0;
		byte[] page = new byte[4];
		int pagenum;
		int rand_key = rand.nextInt(10000) + 1;
		int search_key = helping_buf4Keys.get(rand_key);
		
		int i =2;
		long pos =  (raf.length()/2) ;
		raf.seek(pos);
		int read_int = raf.readInt();
		disk_access = BinarySearch_ΑA(5, disk_access, search_key, read_int, raf, i);
		
		
		
		return disk_access;
	
	}
	
	
	private int BinarySearch_ΑA(long pos, int disk_access, int search_key, int read_int, RandomAccessFile raf, int i) throws IOException {
	
		try {
			if(read_int == search_key ) {
			return disk_access;
			}
	
			if(read_int < search_key ) {
				pos = pos + pos/i;
				i = i + 2;
				raf.seek(pos);
				read_int = raf.readInt();
				disk_access = BinarySearch_ΑA(pos, disk_access, search_key, read_int, raf, i);
			}
			if(read_int > search_key ) {
				pos = pos - pos/i;
				i = i + 2;
				raf.seek(pos);
				read_int = raf.readInt();
				disk_access = BinarySearch_ΑA(pos, disk_access, search_key, read_int, raf, i);
			}
		}catch(EOFException e) {
			System.out.println("Search Failed.");
			return disk_access;
		}
		
	return disk_access;
	
	}
	
	public int BinarySearch_B() throws IOException {
		RandomAccessFile raf = new RandomAccessFile("ExternalFile_B.bin", "rw" );
		int key, disk_access = 0;
		byte[] page = new byte[4];
		int pagenum;
		int rand_key = rand.nextInt(10000) + 1;
		int search_key = helping_buf4Keys.get(rand_key);
		
		int i =2;
		long pos =  (raf.length()/2) ;
		raf.seek(pos);
		int read_int = raf.readInt();
		disk_access = BinarySearch_BB(5, disk_access, search_key, read_int, raf, i);
		
		
		return disk_access;
	
	}
	
	
	private int BinarySearch_BB(long pos, int disk_access, int search_key, int read_int, RandomAccessFile raf, int i) throws IOException {
	
		try {
			if(read_int == search_key ) {
			return disk_access;
			}
	
			if(read_int < search_key ) {
				pos = pos + pos/i;
				i = i + 2;
				raf.seek(pos);
				read_int = raf.readInt();
				disk_access = BinarySearch_ΑA(pos, disk_access, search_key, read_int, raf, i);
			}
			if(read_int > search_key ) {
				pos = pos - pos/i;
				i = i + 2;
				raf.seek(pos);
				read_int = raf.readInt();
				disk_access = BinarySearch_ΑA(pos, disk_access, search_key, read_int, raf, i);
			}
		}catch(EOFException e) {
			System.out.println("Search Failed.");
			return disk_access;
		}
		
	return disk_access;
	
	}


	//Method for serialize/deserialize
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
		}
	
	public static Object deserialize(byte[] data) throws IOException,   ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
		}

	//Method to get a random String of length "length"

		public static String getAlphaNumericString(int length) 
		{ 
	  
			// chose a Character random from this String 
			String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					            + "0123456789"
					            + "abcdefghijklmnopqrstuvxyz"; 

			// create StringBuffer size of AlphaNumericString 
			StringBuilder sb = new StringBuilder(length); 

			for (int i = 0; i < length; i++) { 

			    // generate a random number between 
			    // 0 to AlphaNumericString variable length 
			    int index 
				= (int)(AlphaNumericString.length() 
					* Math.random()); 

			    // add Character one by one in end of sb 
			    sb.append(AlphaNumericString 
					  .charAt(index)); 
			} 

			return sb.toString(); 
		} 
	
	//Method to get Bytes from an ArrayList
		public static long getBytesFromList(ArrayList<Node> list) throws IOException {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream out = new ObjectOutputStream(baos);
		    out.writeObject(list);
		    out.close();
		    return baos.toByteArray().length;
		}
		
		
		
		
		
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		SerialFile sf = new SerialFile();
		
		int counter = sf.CreateSerialFile_A("SerialFile_A.bin");
		System.out.println("Counter of Organization ( File A ) :" + counter);
		System.out.println("\n");
		
		counter = sf.CreateSerialFile_B();
		System.out.println("Counter of Organization ( File B ) :" + counter);
		System.out.println("\n");
		
		
		counter = sf.Search_serialA();
		System.out.println("Counter Of The Average of 20 Searches (File A) :" + counter);
		System.out.println("\n");
		
		counter = sf.Search_serialB();
		System.out.println("Counter Of The Average of 20 Searches (File b) :" + counter);
		System.out.println("\n");
		
		counter = sf.ExternalSerialFile_A();
		System.out.println("Counter of External Sorting ( File A ) :" + counter);
		System.out.println("\n");
		
		counter = sf.ExternalSerialFile_B();
		System.out.println("Counter of External Sorting ( File B ) :" + counter);
		System.out.println("\n");
		
		
		//counter = sf.BinarySearch_Α();
		//System.out.println("Counter of Binary Search ( File A ) :" + counter);
		//System.out.println("\n");
		
		//counter = sf.BinarySearch_B();
		//System.out.println("Counter of Binary Search ( File A ) :" + counter);
		//System.out.println("\n");
	}
	

}
