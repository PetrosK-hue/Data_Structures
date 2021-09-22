package org.tuc;

/**
 * A node in a data structure with int keys
 * @author sk
 *
 */
public interface Node extends Comparable<Node> {
	/**
	 * Returns the key of this node
	 * @return the key of the node
	 */
	public int getKey();
}
