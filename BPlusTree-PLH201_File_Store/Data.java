import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Contains our data. It is of fixed byte array size for writing to or reading to the data file
 * @author sk
 *
 */
public class Data {
	private int storageByteOffset; // this node is stored at byte index storageByteOffset in the data file. We must calculate the datapage this corresponds to in order to read or write it

	private int data1;

	
	private boolean dirty;
	
	public Data() {
		this.data1 = 0;

	}
	public Data(int data1) {
		this.data1 = data1;

	}
	
	public boolean isDirty() {
		return this.dirty;
	}
	public void setDirty() {
		this.dirty = true;
	}
	public void setStorageByteOffset(int storageByteOffset) {
		this.storageByteOffset = storageByteOffset;
	}
	public int getStorageByteOffset() {
		return this.storageByteOffset;
	}
	
	@Override
	public String toString() {
		
		return "data1: "+data1;
	}
	

	/* takes a Data class, and transforms it to an array of bytes 
	  we can't store it as is to the file. We must calculate the data page based on storageByteIndex, load the datapage, replace
	  the part starting from storageByteIndex, and then store the data page back to the file
	  */ 
	
	
	protected byte[] toByteArray() throws IOException {
		// .....
		// .....
		byte[] byteArray = new byte[4]; // 4: demo size of our data. This should be some constant
		Data result = new Data();
		result = StorageCache.getInstance().retrieveData(getStorageByteOffset());
		ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
		DataOutputStream dos = new DataOutputStream(bos);
		dos.write(result.data1);
		byteArray = bos.toByteArray();
		// ..... do stuff
		// ..... do stuff
		
		return byteArray;
		
	}

	
	/* 
	 this takes a byte array of fixed size, and transforms it to a Data class instance
	 it takes the format we store our Data (as specified in toByteArray()) and constructs the Data
	 We need as parameter the storageByteIndex in order to set it
	 */
	protected Data fromByteArray(byte[] byteArray, int storageByteOffset) throws IOException {
		Data result = new Data(1); // 1,2,3,4 will be your data extracted from the byte array
		result.setStorageByteOffset(storageByteOffset);
		ByteBuffer bb =	ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN);
		bb.position(0);
		result.data1 = bb.getInt();
	

		// ..... do stuff
		// ..... do stuff
		
		return result;
	}
}
