package org.tuc;

/**
 * An interface describing a data structure for searching for nodes based on an int key
 * @author sk
 *
 */
public interface SearchDataStructure {
	/**
	 * Search for a node with given key and returns it if found.
	 * @param key
	 * @return the node with given key
	 */
	public Node search(int key);
	
	/**
	 * 
	 * @return a random Key in the data structure. Used only for testing
	 */
	public int getRandomKey();
}
