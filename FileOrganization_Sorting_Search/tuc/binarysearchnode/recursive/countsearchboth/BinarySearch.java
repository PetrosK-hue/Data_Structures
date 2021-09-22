package org.tuc.binarysearchnode.recursive.countsearchboth;

import java.util.Random;

import org.tuc.Node;
import org.tuc.SearchDataStructure;
import org.tuc.counter.MultiCounter;

/**
 * A class implementing the binary search algorithm on an array of Nodes, using recursion.
 * Includes functionality to count number of statements and recursion levels for each search.
 * Based on https://www.geeksforgeeks.org/binary-search/
 * @author sk
 *
 */
public class BinarySearch implements SearchDataStructure  {
	private Node data[];

	/**
	 * Constructor. Given newData must be sorted!
	 * @param newData
	 */
	public BinarySearch(Node newData[]) {
		this.data = newData;
	}
	
	/**
	 * Given newData must be sorted!
	 * @param newData
	 */
	public void setData(Node newData[]) {
		this.data = newData;
	}
	
	@Override
	public Node search(int key) {
		if (data == null) {
			return null;
		}
		return doSearch(0, data.length - 1, key);
	}
	

	/**
	 * <p>
	 * Helper method that searches in data array, from index leftIndex to index rightIndex. 
	 * Uses recursion
	 * </p>
	 * 
	 * @param leftIndex   The left index of the data array
	 * @param rightIndex  The right index of the data array
	 * @param key         The key to search
	 * @return            The node containing the key if found
	 */
	private Node doSearch(int leftIndex, int rightIndex, int key) 
    { 
		MultiCounter.increaseCounter(2);

		
        if (MultiCounter.increaseCounter(1) && rightIndex >= leftIndex) { 
        	MultiCounter.increaseCounter(1);
            int mid = leftIndex + (rightIndex - leftIndex) / 2; 
  
            // If the element is present at the 
            // middle itself 
            if (MultiCounter.increaseCounter(1) && data[mid].getKey() == key) 
                return data[mid]; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            
            if (MultiCounter.increaseCounter(1) && data[mid].getKey() > key) 
                return doSearch(leftIndex, mid - 1, key); 
  
            // Else the element can only be present 
            // in right subarray 
            return doSearch(mid + 1, rightIndex, key); 
        } 
  
        // We reach here when element is not present 
        // in array 
        return null; 
    }

	@Override
	public int getRandomKey() {
		Random random = new Random();
		return data[random.nextInt(data.length)].getKey();
	} 
}
