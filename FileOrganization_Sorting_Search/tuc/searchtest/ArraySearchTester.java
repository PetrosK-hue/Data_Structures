package org.tuc.searchtest;

import org.tuc.Node;
import org.tuc.SearchDataStructure;
import org.tuc.arraysearchnode.countersearchboth.ArraySearch;

/**
 * <p>
 * Test runs for Binary Search. Uses random integers as keys between 0 (inclusive) and
 * MAX_INT_NUMBER (exclusive).
 * </p>
 * <p>
 * Fills a data set with number of nodes given by each value in the NUMBER_OF_NODES
 * array, and executes a search for SUCCESS_SEARCHES keys that are in the data set,
 * and FAILURE_SEARCHES keys that are not in the data set.
 * </p>
 * <p>
 * In this version, the org.tuc.counter.MultiCoutnerSingleton is used to count both 
 * the number of statements and the number of levels traversed for each search. See
 * {@link org.tuc.counter.MultiCounter MultiCounterSingleton}
 * </p>
 * 
 * @author sk
 *
 */
public class ArraySearchTester extends Tester {

	public ArraySearchTester(int[] numberOfNodesPerTest, int failureSearches, int successSearches, int minIntNumber,
			int maxIntNumber) {
		super(numberOfNodesPerTest, failureSearches, successSearches, minIntNumber, maxIntNumber);
	}

	@Override
	protected SearchDataStructure getSearchDataStructure(int numberOfNodes) {
		Node[] randomNumbers = this.getRandomNumberNodes(numberOfNodes);

		return new ArraySearch(randomNumbers);
	}

}
