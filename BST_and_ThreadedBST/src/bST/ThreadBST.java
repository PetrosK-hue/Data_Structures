package bST;


import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ThreadBST {

	public int data[][];
	public int secondaryRoot = 0;
	public int LeftOrRight = 0;
	public int LeftThread = -1;
	public int RightThread = -1;
	public int N = 100000;
	public int maxN = 1000000;
	Scanner myObj = new Scanner(System.in);
	Random rand = new Random();
	ArrayList<Integer> helping_buf4Keys = new ArrayList<Integer>(N);

	public void init() {
		int val;
		data = new int[N+2][5];

		for (int k = 0; k < N+2; k++) {
			for (int l = 0; l < 5; l++) {
				data[k][l] = -1;
			}
			data[k][2] = k;
		}

		for (int i = 0; i < N; i++) {

			val = getNonExistingKey();
			helping_buf4Keys.add(val);
			callInsertNode(val);
		}
		return;
	}

	public int callInsertNode(int value) {
		int nodeIndex = 0;
		int counter = 0;
		counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, RightThread, LeftThread, counter);
		return counter;

	}

	private int insertNode(int value, int nodeIndex, int leftOrRight, int secondaryRoot, int RightThread,
			int LeftThread, int counter) {

		int helpIndex = 0;
		if (data[nodeIndex][0] == -1) {
			data[nodeIndex][0] = value;
			if (LeftOrRight > 0) {
				data[secondaryRoot][LeftOrRight] = nodeIndex;
				if (LeftOrRight == 1) {
					data[secondaryRoot][3] = -1;
				} else if (LeftOrRight == 2) {
					data[secondaryRoot][4] = -1;
				}
				if (LeftThread != -1) {
					data[nodeIndex][1] = LeftThread;
					data[nodeIndex][3] = 1;
				} else {
					data[nodeIndex][1] = -1;
				}
				if (RightThread != -1) {
					data[nodeIndex][2] = RightThread;
					data[nodeIndex][4] = 1;
				} else {
					data[nodeIndex][2] = -1;
				}
				secondaryRoot = 0;
				LeftOrRight = 0;
				RightThread = -1;
				LeftThread = -1;
				return counter;
			}
			data[nodeIndex][1] = -1;
			data[nodeIndex][2] = -1;
			return counter;
		}
		helpIndex = nodeIndex;
		//////////////// FOR RIGHT INSERTION //////////////////////////////
		if (value > data[nodeIndex][0]) {
			counter++;
			LeftOrRight = 2;
			secondaryRoot = nodeIndex;

			if (data[nodeIndex][2] != -1 && data[nodeIndex][4] == -1) {
				counter++;
				nodeIndex = data[nodeIndex][2];
				LeftThread = helpIndex;

			} else {
				counter++;
				LeftThread = nodeIndex;
				do {
					nodeIndex = nodeIndex + 1;

				} while (data[nodeIndex][0] > 0);
			}
			counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, RightThread, LeftThread, counter);
		}

		//////////////// FOR LEFT INSERTION //////////////////////////////
		if (value < data[nodeIndex][0]) {
			counter++;
			LeftOrRight = 1;
			secondaryRoot = nodeIndex;

			if (data[nodeIndex][1] != -1 && data[nodeIndex][3] == -1) {
				counter++;
				nodeIndex = data[nodeIndex][1];
				RightThread = helpIndex;

			} else {
				counter++;
				RightThread = nodeIndex;
				do {
					nodeIndex = nodeIndex + 1;
				} while (data[nodeIndex][0] > 0);
			}
			counter = insertNode(value, nodeIndex, LeftOrRight, secondaryRoot, RightThread, LeftThread, counter);

		}
		return counter;
	}

	public int callSearchNode(int value, int nodeIndex, int counter) {
		counter = 0;
		for (int i = 0; i < 100; i++) {
			nodeIndex = 0;
			int rkey = rand.nextInt(N-1) + 1;
			value = helping_buf4Keys.get(rkey);
			counter = searchNode(value, nodeIndex, counter);
		}
		return counter / 100;
	}

	public int searchNode(int value, int nodeIndex, int counter) {
		
		if (value == data[nodeIndex][0]) {
			counter++;
		} else if (value > data[nodeIndex][0]) {
			counter = counter + 2;
			nodeIndex = data[nodeIndex][2];
			return searchNode(value, nodeIndex, counter);
		} else if (value < data[nodeIndex][0]) {
			counter = counter + 3;
			nodeIndex = data[nodeIndex][1];
			return searchNode(value, nodeIndex, counter);
		}

		return counter;
	}

	public int callPrintRange(int counter, int k) {
		counter = 0;
		int nodeIndex = 0;
		for (int i = 0; i < 100; i++) {
			int rkey = rand.nextInt(99999) + 1;
			int low = helping_buf4Keys.get(rkey);
			int high = low + k;
			counter = printRange(nodeIndex, low, high, counter);
			System.out.print("|");
		}
		return counter / 100;

	}

	public int printRange(int nodeIndex, int low, int high, int counter) {
		if (nodeIndex == -1) {
			counter++;
			return counter;
		}
		counter++;
		if (high < data[nodeIndex][0]) {
			counter++;
			if (data[nodeIndex][3] == -1) {
				counter = printRange(data[nodeIndex][1], low, high, counter);
			} else {
				return counter;
			}
		 counter++;
		} else if (low > data[nodeIndex][0]) {
			counter++;
			if (data[nodeIndex][4] == -1) {
				counter = printRange(data[nodeIndex][2], low, high, counter);
			} else {
				return counter;
			}
		} else {

			if (data[nodeIndex][3] == -1) {
				counter++;
				counter = printRange(data[nodeIndex][1], low, high, counter);
			}

			//System.out.print(" " + data[nodeIndex][0]);

			//// To prevent second print of a threaded Node
			if (data[nodeIndex][4] != -1) {
				counter++;
				return counter;
			}
			counter = printRange(data[nodeIndex][2], low, high, counter);
			

		}
		return counter;
	}

	public int getNonExistingKey() {
		int val = 0;
		val = rand.nextInt(maxN -1 ) + 1;
		while (helping_buf4Keys.contains(val) == true) {
			val = rand.nextInt(maxN -1) + 1;
		}
		helping_buf4Keys.add(val);
		return val;
	}

}
