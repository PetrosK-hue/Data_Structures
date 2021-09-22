package bST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ThreadBST_MAIN {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		int nodeInd = 0;
		int val = 0;
		int counter = 0;

		ThreadBST tBST = new ThreadBST();
		System.out.println("############################################");
		System.out.println("  ");
		System.out.println("	  PROGRAM STARTED      ");
		System.out.println("");
		System.out.println("      INITIALING BINARY TREE... ");
		System.out.println("             THREADED  ");
		System.out.println("############################################");

		tBST.init();
		
		System.out.println("      INSERT A NEW NODE:    ");
		System.out.println("############################################");
		System.out.println("");
		
		val = tBST.getNonExistingKey();
		counter = tBST.callInsertNode(val);
		
		System.out.println("     COMPARISONS COUNT:" + counter);
		System.out.println("");
		System.out.println("############################################");
		System.out.println("");

		System.out.println("############################################");
		System.out.println("");
		System.out.println("   STARTING 100 SUCCESSFUS SEARCHES	");
		System.out.println("	 CALCULATING MEDIAN VALUE 	");
		System.out.println("           OF COMPARISONS...  	");
		System.out.println("									");

		counter = tBST.callSearchNode(val, nodeInd, counter);
		
		System.out.println("############################################");
		System.out.print("");
		System.out.println("              RESULT:" + counter);
		System.out.print("");
		System.out.println("############################################");
		
		System.out.println("");
		System.out.println("############################################");
		System.out.println("");
		System.out.println(" STARTING AN IN RANGE SEARCH	  ");
		System.out.println("  BETWEEN OF 2 VALUES. 	");
		System.out.println("            	");
		System.out.println("( (uncomment //System.out.println() to print values) )");
		System.out.println("");
		System.out.println("############################################");

		counter = tBST.callPrintRange(counter, 1000); //K=1000;
		
		System.out.println("");
		System.out.println("############################################");
		System.out.print("");
		System.out.println("              RESULT:" + counter);
		System.out.print("");
		System.out.println("############################################");

		System.out.println(" ");
		System.out.println("############################################");
		System.out.println("         PROGRAM FINISHED          ");
		System.out.println("############################################");
	}

}
