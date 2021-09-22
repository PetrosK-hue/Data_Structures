package org.tuc.nodes;

/**
 * A class extending SimpleNode that adds an additional member variable for a student name
 * @author sk
 *
 */
public class StudentNode extends SimpleNode {
	private String name;
	public StudentNode(int key, String name) {
		super(key);
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
