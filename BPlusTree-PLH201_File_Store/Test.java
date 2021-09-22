import java.io.IOException;


public class Test {

	public static void main(String[] args) throws IOException {
		Menu m = new Menu();
		BTree<Integer, Data> tree = new BTree<Integer, Data>();
		System.out.println("---------------------------");
		System.out.println("PROGRAM B+-TREE STARTED...");
		System.out.println("---------------------------");
		
		//DELETING FILES OLD FILES
		m.deleteFile();
		
		//INITIALIZATION OF BTREE (10^5 KEYS)
		tree = m.BTreeInit();
		
		//INSERTION OF 20 KEYS AND RETURNING COUNTER
		m.MenuInsert(tree);
		
		//SEARCHING OF 20 RANDOM KEYS AND RETURNING COUNTER
		m.MenuSearch(tree);
		
		//SEARCHING KEYS IN RANGE AND RETURNING COUNTER
		m.MenuSearchInRange(tree);
		
		//DELETING 20 KEYS AND RETURNING COUNTER
		m.MenuDelete(tree);
		System.out.println("PROGRAM HAS FINISHED SUCCESSFULLY!");
		System.out.println(" 	 GOODBYE!");

	}

}
