package org.tuc.searchtest;

import java.util.Arrays;

import org.tuc.Node;
import org.tuc.nodes.StudentNode;

/**
 * Our class to conduct tests using BinarySearch and ArraySearch implementations of the SearchDataStructure interface
 * Various test options are defined as static member variables
 * @author sk
 *
 */
public class SearchTests {
	// some constants used in the test runs
	
	/**
	 * The maximum generated number used as key for the nodes
	 */
	static int MAX_INT_NUMBER = 100000000;
	
	/**
	 * The minimum generated number used as key for the nodes
	 */	
	static int MIN_INT_NUMBER = -100000000;
	
	/**
	 * The amount of successful search, i.e. searches for keys that are in the data set
	 */
	static int SUCCESS_SEARCHES = 100;
	
	
	/**
	 * The amount of failure search, i.e. searches for keys that are not in the data set
	 */
	static int FAILURE_SEARCHES = 100;
	
	/**
	 * The test is run for different amount of keys in the data set, given by this array 
	 */
	static int NUMBER_OF_NODES_PER_TEST_BINARY[] = { 20, 50, 100, 200, 1000, 2500, 5000, 10000, 20000, 100000, 1000000, 10000000 };
	static int NUMBER_OF_NODES_PER_TEST_ARRAY[] = { 20, 50, 100, 200, 1000, 2500, 5000, 10000, 100000 };
	
	public static void main(String[] args)  {

		BinarySearchTester testerBinarySearch = new BinarySearchTester(NUMBER_OF_NODES_PER_TEST_BINARY, FAILURE_SEARCHES, SUCCESS_SEARCHES, MIN_INT_NUMBER, MAX_INT_NUMBER);
		ArraySearchTester testerArraySearch = new ArraySearchTester(NUMBER_OF_NODES_PER_TEST_ARRAY, FAILURE_SEARCHES, SUCCESS_SEARCHES, MIN_INT_NUMBER, MAX_INT_NUMBER);
		
		try {
			System.out.println("START TESTS BINARY SEARCH");
			testerBinarySearch.doTest();
			System.out.println("");
			System.out.println("START TESTS ARRAY SEARCH");
			testerArraySearch.doTest();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		
		
		// "real life" application
		/*
		Node[] students = new Node[1000];
		students[0] = new StudentNode(2000030099, "Stefanos Karasavvidis");
		students[1] = new StudentNode(1998030100, "Stefanos Karasavvidis");
		students[2] = new StudentNode(2010030099, "Stefanos Karasavvidis");
		students[3] = new StudentNode(1970030099, "Stefanos Karasavvidis");
		.... 
		students[999] = new StudentNode(1992030099, "Stefanos Karasavvidis");
		....
		 
		Arrays.sort(students);
		BinarySearch binarySearch = new BinarySearch(students);
		
		// use it for finding a Student by id number
		binarySearch.search(1998030100);
		*/
		
	}
}
