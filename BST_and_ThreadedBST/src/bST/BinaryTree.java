package bST;


import java.util.ArrayList;
import java.util.Random;

public class BinaryTree {

	public int data[][];
	public int secondaryRoot = 0;
	public int LeftOrRight = 0;
	Random rand = new Random();
	ArrayList<Integer> helping_buf4Keys = new ArrayList<Integer>(100000);

	public void init() {

		int val;
		data = new int[100002][3];
		for (int k = 0; k < 100002; k++) {

			for (int l = 0; l < 2; l++) {
				data[k][l] = -1;
			}
			data[k][2] = k;
		}

		for (int i = 0; i < 100000; i++) {

			val = rand.nextInt(1000000) + 1;
			while (helping_buf4Keys.contains(val) == true) {
				val = rand.nextInt(1000000) + 1;
			}
			helping_buf4Keys.add(val);

			callInsertNode(val);
		}
		return;

	}

	public int callInsertNode(int value) {
		int counter = 0;
		int nodeIndex = 0;
		counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, counter);
		return counter;
	}

	public int insertNode(int value, int nodeIndex, int LeftOrRight, int secondaryRoot, int counter) {
		
		if (data[nodeIndex][0] == -1) {
			counter++;
			data[nodeIndex][0] = value;
			if (LeftOrRight > 0) {
				data[secondaryRoot][LeftOrRight] = nodeIndex;
				data[nodeIndex][1] = -1;
				data[nodeIndex][2] = -1;
				secondaryRoot = 0;
				LeftOrRight = 0;
				return counter;
			}
			data[nodeIndex][1] = -1;
			data[nodeIndex][2] = -1;
			return counter;
		}
		if (value > data[nodeIndex][0]) {
			counter++;
			LeftOrRight = 2;
			secondaryRoot = nodeIndex;

			if (data[nodeIndex][2] != -1) {
				counter++;
				nodeIndex = data[nodeIndex][2];
			} else {
				do {
					nodeIndex = nodeIndex + 1;

				} while (data[nodeIndex][0] > 0);
			}
			counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, counter);
		}

		if (value < data[nodeIndex][0]) {
			counter++;
			LeftOrRight = 1;
			secondaryRoot = nodeIndex;

			if (data[nodeIndex][1] != -1) {
				counter++;
				nodeIndex = data[nodeIndex][1];

			} else {
				do {
					nodeIndex = nodeIndex + 1;
				} while (data[nodeIndex][0] > 0);
			}
			counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, counter);
		}
		return counter;
	}

	public int callSearchNode(int value, int nodeIndex, int counter) {
		counter = 0;
		nodeIndex = 0;
		for (int i = 0; i < 100; i++) {
			nodeIndex = 0;
			int rkey = rand.nextInt(99999) + 1;
			value = helping_buf4Keys.get(rkey);
			
			counter = searchNode(value, nodeIndex, counter);
		}
		return counter / 100;
	}

	public int searchNode(int value, int nodeIndex, int counter) {
		
		if (value == data[nodeIndex][0]) {
			counter++;
		} else if (value > data[nodeIndex][0]) {
			counter = counter +2;
			nodeIndex = data[nodeIndex][2];
			return searchNode(value, nodeIndex, counter);
		} else if (value < data[nodeIndex][0]) {
			counter = counter +3;
			nodeIndex = data[nodeIndex][1];
			return searchNode(value, nodeIndex, counter);
		}
		return counter;
	}

	public int callPrintRange(int counter, int k) {
		counter = 0;
		int nodeIndex = 0;
		for (int i = 0; i < 100; i++) {
			int rkey = rand.nextInt(10000) + 1;
			int low = helping_buf4Keys.get(rkey);
			int high = low + k;
			counter = printRange(nodeIndex, low, high, counter);
			System.out.print("|");
		}
		return counter/100;
	}

	public int printRange(int nodeIndex, int low, int high, int counter) {
		counter++;
		if (nodeIndex == -1) {
			return counter;
		}
		counter++;
		if (high < data[nodeIndex][0]) {
			counter = printRange(data[nodeIndex][1], low, high, counter);
		} else if (low > data[nodeIndex][0]) {
			counter++;
			counter = printRange(data[nodeIndex][2], low, high, counter);

		} else {
			counter++;
			counter = printRange(data[nodeIndex][1], low, high, counter);
			// UNCOMMENT TO SHOW VALUES
			//System.out.print(" " + data[nodeIndex][0]);
			counter = printRange(data[nodeIndex][2], low, high, counter);

		}
		return counter;
	}

	public int getNonExistingKey() {
		int val = 0;
		val = rand.nextInt(1000000) + 1;
		while (helping_buf4Keys.contains(val) == true) {
			val = rand.nextInt(1000000) + 1;
		}
		helping_buf4Keys.add(val);
		return val;
	}
}
