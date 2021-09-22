import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Menu {
	public Random rand = new Random();
	public int Num = 100000; // 10^5 keys
	public int Maxnum = 1000000;
	Scanner myObj = new Scanner(System.in);
	ArrayList<Integer> helping_buf4Keys = new ArrayList<Integer>(Num);

	public void deleteFile() {
		// Demo with Data as data
		File myObj1 = new File("NodeFile.bin");
		File myObj2 = new File("DataFile.bin");
		myObj1.delete();
		myObj2.delete();
	}
	
//////     INIT TREE //////////////
//////////////////////////////////////////////////////////////////////////////////////////////
	public BTree<Integer, Data> BTreeInit() throws IOException {
		BTree<Integer, Data> tree = new BTree<Integer, Data>();
		for (int i = 0; i < Num-1; i++) {

			int randnum = rand.nextInt(Maxnum - 1) + 1;
			int randdata = rand.nextInt(1000) + 1;
			helping_buf4Keys.add(randnum);
			tree.insert(Integer.valueOf(randnum), new Data(randdata));

		}
		System.out.println("--------------------------- ");
		System.out.println(" Initialization Finished!");
		System.out.println("--------------------------- ");
		return tree;
	}
	
//////   INSERT TREE //////////////
//////////////////////////////////////////////////////////////////////
	public void MenuInsert(BTree<Integer, Data> tree) throws IOException {
		System.out.println("--------------------------- ");
		System.out.println("INSERTING 20 KEYS INTO B+-TREE. ");
		System.out.println("--------------------------- ");
		int counter = 0;
		for (int i = 0; i < 20; i++) {
			int newval = getNonExistingKey();
			int randdata = rand.nextInt(1000) + 1;
			counter = counter + tree.CustomInsert(Integer.valueOf(newval), new Data(randdata));
		}
		System.out.println("--------------------------- ");
		System.out.println("INSERTIONS HAVE FINISHED!!! ");
		System.out.println("--------------------------- ");
		System.out.println("		 ");
		System.out.println("AVERAGE COUNTER OF INSERTIONS IS:  " + counter / 20);
		System.out.println("		 ");

	}
//////  SEARCH TREE //////////////
//////////////////////////////////////////////////////////////////////////////
	public void MenuSearch(BTree<Integer, Data> tree) {
		System.out.println("--------------------------- ");
		System.out.println("SEARCHING 20 KEYS INTO B+-TREE. ");
		System.out.println("--------------------------- ");
		int counter = 0;
		for (int i = 0; i < 20; i++) {
			int newval = getAnExistingKey();
			counter = counter + tree.CustomSearch(Integer.valueOf(newval)) + 1;
			
		}
		System.out.println("--------------------------- ");
		System.out.println("SEARCHS HAVE FINISHED!!! ");
		System.out.println("--------------------------- ");
		System.out.println("		 ");
		System.out.println("AVERAGE COUNTER OF SEARCHES IS:  " + counter / 20);
		System.out.println("		 ");

	}
////// SEARCH IN RANGE IN TREE //////////////
//////////////////////////////////////////////////////////////////////////////	
	public void MenuSearchInRange(BTree<Integer, Data> tree) {
		System.out.println("--------------------------- ");
		System.out.println("SEARCHING IN RANGE 20 TIMES INTO B+-TREE. ");
		System.out.println("--------------------------- ");
		int counter = 0;
		int K =10; //////// value of K
		for (int i = 0; i < 20; i++) {
			
			int newval = getAnExistingKey();
			int highval = newval + K;
			counter = counter + tree.CustomSearchRange(Integer.valueOf(newval) ,Integer.valueOf(highval)) + 1;
			
		}
		System.out.println("--------------------------- ");
		System.out.println("SEARCHS HAVE FINISHED!!! ");
		System.out.println("--------------------------- ");
		System.out.println("		 ");
		System.out.println("AVERAGE COUNTER OF SEARCHES IS:  " + counter / 20);
		System.out.println("		 ");
		
	}
//////  DELETE FROM  TREE //////////////
//////////////////////////////////////////////////////////////////////////////
	public void MenuDelete(BTree<Integer, Data> tree) throws IOException {
		System.out.println("--------------------------- ");
		System.out.println("DELETING 20 KEYS FROM B+-TREE. ");
		System.out.println("--------------------------- ");
		int counter = 0;
		for (int i = 0; i < 20; i++) {
			int newval = getAnExistingKey();
			counter = counter + tree.CustomDelete(Integer.valueOf(newval)) + 2;
			
		}
		System.out.println("--------------------------- ");
		System.out.println("DELETIONS HAVE FINISHED!!! ");
		System.out.println("--------------------------- ");
		System.out.println("		 ");
		System.out.println("COUNTER OF DELETIONS IS:  " + counter / 20);
		System.out.println("		 ");
	}
	
	public int getAnExistingKey() {
		 int random = rand.nextInt(Num - 1) ;
		 int val = helping_buf4Keys.get(random);
		 return val;
		 
	}
	// System.out.println(tree.search(10));
	public int getNonExistingKey() {
		int val = 0;
		val = rand.nextInt(Maxnum - 1) + 1;
		while (helping_buf4Keys.contains(val) == true) {
			val = rand.nextInt(Maxnum - 1) + 1;
		}
		helping_buf4Keys.add(val);
		return val;
	}



	

}
