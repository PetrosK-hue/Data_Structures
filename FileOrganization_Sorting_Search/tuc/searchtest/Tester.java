package org.tuc.searchtest;

import java.util.Random;

import org.tuc.Node;
import org.tuc.nodes.SimpleNode;
import org.tuc.SearchDataStructure;
import org.tuc.counter.MultiCounter;

/**
 * <p>
 * Test runs for Classes of SearchDataStructure for multiple amount of test date.
 * It needs the number of nodes to create for each test run, how many searches for keys contained in the structure
 * to conduct, how many searches for keys not contained in the structure to conduct, and the range of keys to use for the random keys that will be generated. 
 * </p>
 * <p>
 * The class is an abstract class. Instances must implement the getSearchDataStructure method, which should return
 * an instance of SearchDataStructure with the given amount of test Nodes.
 * </p>
 * <p>
 * The doTest method conducts the tests and prints out the results.
 * </p>
 * <p>
 * The org.tuc.counter.MultiCoutnerSingleton is used to count both 
 * the number of statements and the number of levels traversed for each search. See
 * {@link org.tuc.counter.MultiCounter MultiCounterSingleton}
 * </p>
 * 
 * @author sk
 *
 */
public abstract class Tester {
	private int[] numberOfNodesPerTest; // array of number of input elements for each test
	private int failureSearches; // how many failed searches should be conducted to compute the mean values for the counters
	private int successSearches; // how many success searches should be conducted to compute the mean values for the counters
	private int minIntNumber; // the lower bound of the generated keys to use
	private int maxIntNumber; // the upper bound of the generated keys to use
	
	/**
	 * Constructor of class
	 * 
	 * @param numberOfNodesPerTest array of number of input elements for each test
	 * @param failureSearches how many failed searches should be conducted to compute the mean values for the counters
	 * @param successSearches how many success searches should be conducted to compute the mean values for the counters
	 * @param minIntNumber the lower bound of the generated keys to use
	 * @param maxIntNumber the upper bound of the generated keys to use
	 */
	public Tester(int[] numberOfNodesPerTest, int failureSearches, int successSearches, int minIntNumber, int maxIntNumber) {
		this.numberOfNodesPerTest = numberOfNodesPerTest;
		this.failureSearches = failureSearches;
		this.successSearches = successSearches;
		this.minIntNumber = minIntNumber;
		this.maxIntNumber = maxIntNumber;
	}
	
	/**
	 * Creates numberOfNodes nodes, with keys in the range between minIntNumber and maxIntNumber.
	 * For simplicity, it does not ensure that the keys are unique
	 * @param numberOfNodes Number of nodes to create
	 * @return
	 */
	protected Node[] getRandomNumberNodes(int numberOfNodes) {
		Node[] randomNumbers = new Node[numberOfNodes];
		Random random = new Random();
		for (int countRandom = 0; countRandom < numberOfNodes; countRandom++) {
			// random number between minIntNumber and maxIntNumber (exclusive)
			randomNumbers[countRandom] = new SimpleNode(random.nextInt(maxIntNumber-minIntNumber) + minIntNumber);
		}
		return randomNumbers;
	}
	
	/**
	 * pick randomly successSearches of the generated random numbers and perform a search on the data set.
	 * Make sure that all of them are inside the data set.
	 * Count number total number of statements and traversed levels
	 * @param searchDataStructure The search data structure upon which the search will be conducted
	 * @return
	 * @throws Exception In case a key is used, that is supposed to be within the data set, is not found
	 */
	private int[] testSuccessSearch(SearchDataStructure searchDataStructure) throws Exception {
		int totalStatementCountSearchSuccess = 0;
		int totalLevelCountSearchSuccess = 0;

		Node searchResult;
		int keyToSearch;
		
		for (int countTests = 0; countTests < successSearches; countTests++) {
			// random.nextInt(upperBound) returns random int between 0 (inclusive) and upperBound (exclusive)
			keyToSearch = searchDataStructure.getRandomKey();
			MultiCounter.resetCounter(1);
			MultiCounter.resetCounter(2);
			searchResult = searchDataStructure.search(keyToSearch);
			totalStatementCountSearchSuccess = totalStatementCountSearchSuccess + MultiCounter.getCount(1);
			totalLevelCountSearchSuccess = totalLevelCountSearchSuccess + MultiCounter.getCount(2);
			if (searchResult == null) { // this should never happen, as we search for keys that are in the data set
				throw new Exception("Existing number (" + keyToSearch + ") not found in test run " + (countTests + 1));
			}
			// System.out.println("Computed valued for " + searchResult.key + " is " +
			// searchResult.compute());
		}
		return new int[] {totalStatementCountSearchSuccess,totalLevelCountSearchSuccess};
	}

	/**
	 * pick randomly failureSearches numbers that are not in the data set, and do a search.
	 * Generate a random number in our number range, and do a search. If a node is not found, we have a key that is not in the data set, and thus can be used to make another search.
	 * Count number total number of statements and traversed levels
	 * @param searchDataStructure The search data structure upon which the search will be conducted
	 * @return
	 */
	private int[] testFailureSearch(SearchDataStructure searchDataStructure) {
		int totalStatementCountSearchFailure = 0;
		int totalLevelCountSearchFailure = 0;		
		Node searchResult;
		int keyToSearch;
		int countFailures = 0;
		Random random = new Random();

		while (countFailures < failureSearches) {
			keyToSearch = random.nextInt(maxIntNumber-minIntNumber) + minIntNumber;
			searchResult = searchDataStructure.search(keyToSearch);
			if (searchResult == null) {
				countFailures++;
				MultiCounter.resetCounter(1);
				MultiCounter.resetCounter(2);
				searchResult = searchDataStructure.search(keyToSearch);
				totalStatementCountSearchFailure = totalStatementCountSearchFailure + MultiCounter.getCount(1);
				totalLevelCountSearchFailure = totalLevelCountSearchFailure + MultiCounter.getCount(2);
			}
		}
		return new int[] {totalStatementCountSearchFailure,totalLevelCountSearchFailure};
	}	
	
	/**
	 * Abstract method that should be implemented by subclasses, to create a search data structure with given number of nodes
	 * @param numberOfNodes
	 * @return
	 */
	protected abstract SearchDataStructure getSearchDataStructure(int numberOfNodes);
	
	/**
	 * This method conducts the actual tests, as defined by the parameters of the constructor
	 * @throws Exception
	 */
	public void doTest() throws Exception {
		SearchDataStructure searchDataStructure; // this will hold our BinarySearch instance for each run
		int numberOfNodes; // the number of nodes that will be inserted into the data set on each run

		long totalTestStartTimeNano, totalTestEndTimeNano;
		long failureTestStartTimeNano, failureTestEndTimeNano;
		
		int totalStatementCountSearchSuccess, totalStatementCountSearchFailure;
		int totalLevelCountSearchSuccess, totalLevelCountSearchFailure;

		// heading for results
		System.out.println("maxIntNumber: "+maxIntNumber);
		System.out.println("minIntNumber: "+minIntNumber);
		System.out.println("successSearches: "+successSearches);
		System.out.println("failureSearches: "+failureSearches);
		System.out.println("**************************************");
		System.out.println("Beware!: The total test time includes also the time to generate numbers and find existing and non existing numbers to search,***");
		System.out.println("which skews the time data. A closer to reality time data is the failure time data");
		System.out.println("**************************************");
		System.out.println("Number of nodes | Mean statements success | Mean statements failure | Mean levels success | Mean levels failure | Total test time (ms) | Failure test time (ms)");
		
		for (int countRuns = 0; countRuns < numberOfNodesPerTest.length; countRuns++) {
			// initialize start time of this test run
			totalTestStartTimeNano = System.nanoTime();
			
			numberOfNodes = numberOfNodesPerTest[countRuns];

			searchDataStructure = this.getSearchDataStructure(numberOfNodes);
			
			// test success searches
			int testResultSuccess[] = this.testSuccessSearch(searchDataStructure);
			totalStatementCountSearchSuccess = testResultSuccess[0];
			totalLevelCountSearchSuccess = testResultSuccess[1];
			
			// test failure searches
			failureTestStartTimeNano = System.nanoTime();
			int testResultFailure[] = this.testFailureSearch(searchDataStructure);
			totalStatementCountSearchFailure = testResultFailure[0];
			totalLevelCountSearchFailure = testResultFailure[1];
			failureTestEndTimeNano = System.nanoTime();

			
			totalTestEndTimeNano = System.nanoTime();
			
			// 1st argument, decimal with 15 character width
			// 2nd argument, float, with 23 character width and 2 decimal point precision
			// 3rd argument, float, with 23 character width and 2 decimal point precision
			// 4th argument, float, with 19 character width and 2 decimal point precision
			// 5th argument, float, with 19 character width and 2 decimal point precision
			// 6th argument, float, with 20 character width and 3 decimal point precision
			System.out.printf("%15d | %23.2f | %23.2f | %19.2f | %19.2f | %20.3f | %22.3f \n", 
					numberOfNodes, 
					((float) totalStatementCountSearchSuccess / successSearches),
					((float) totalStatementCountSearchFailure / failureSearches),
					((float) totalLevelCountSearchSuccess / successSearches),
					((float) totalLevelCountSearchFailure / failureSearches),
					((float) (double) (totalTestEndTimeNano - totalTestStartTimeNano) / 1000000),				
					((float) (double) (failureTestEndTimeNano - failureTestStartTimeNano) / 1000000)					
					);

		}
	}
}
