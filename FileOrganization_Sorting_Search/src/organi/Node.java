package organi;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Node implements Comparable<Node>,Serializable {

	
	int key;
	String info;
	public byte[] buffer = new byte[32];
	
	
	public Node(int item, String random_str) {
		this.key = item;
		this.info = random_str;
		
	}

	public  byte[] getArray() {
		return this.buffer;
	}
	
	public int getKey() {
		return this.key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public Node (byte[] buffer) {
		
		ByteBuffer key1 = ByteBuffer.wrap(buffer, 0, 3);
		ByteBuffer info1 = ByteBuffer.wrap(buffer, 4, 32);
		
		this.key = key1.getInt();
		this.info = StandardCharsets.UTF_8.decode(info1).toString();
	}

	@Override
	public int compareTo(Node otherNode) {
		if (this.getKey() == otherNode.getKey())
			return 0; // this == otherNode
		else if (this.getKey() > otherNode.getKey())
			return 1; // this > otherNode
		else
			return -1; // this < otherNode
	}
	
	
	
	
}
