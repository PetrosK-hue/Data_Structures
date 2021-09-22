package org.tuc.arraysearchnode.countersearchboth;

import java.util.Random;

import org.tuc.Node;
import org.tuc.SearchDataStructure;
import org.tuc.counter.MultiCounter;

/**
 * A class searching sequentially in an array of Nodes based on a int key.
 * Includes functionality to count number of statements and iterations for each search.
 * @author sk
 *
 */
public class ArraySearch implements SearchDataStructure {
	private Node data[];
	
	/**
	 * Constructor. Given newData must be sorted!
	 * @param newData
	 */
	public ArraySearch(Node newData[]) {
		this.data = newData;
	}
	
	@Override
	public Node search(int key) {
		if (data == null) {
			return null;
		}
		int arrayIndex = 0;
		for (arrayIndex=0; MultiCounter.increaseCounter(1) &&  arrayIndex < data.length; arrayIndex++) {
			MultiCounter.increaseCounter(2);
			if (MultiCounter.increaseCounter(1) && data[arrayIndex].getKey() == key) {
				return data[arrayIndex];
			}
		}
		return null;
	}

	@Override
	public int getRandomKey() {
		Random random = new Random();
		return data[random.nextInt(data.length)].getKey();
	}
}
