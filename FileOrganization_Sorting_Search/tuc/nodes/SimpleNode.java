package org.tuc.nodes;

import org.tuc.Node;

/**
 * A class implementing interface Node.
 * Implements the compareTo method of the Comparable interface that Node extends 
 * @author sk
 *
 */
public class SimpleNode implements Node {
	/**
	 * The key of the node
	 */
	private int key;

	/**
	 * The class constructor
	 * 
	 * @param item the value for the key of the node
	 */
	public SimpleNode(int item) {
		key = item;
	}

	/**
	 * Returns the key of the Node.
	 * 
	 * @return the key
	 */
	public int getKey() {
		return key;
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
