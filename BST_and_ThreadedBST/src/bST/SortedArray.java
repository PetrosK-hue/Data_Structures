package bST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SortedArray {
	public int data[];
	Random rand = new Random();
	ArrayList<Integer> helping_buf4Keys = new ArrayList<Integer>(10000);

	public void initArray() {
		data = new int[10000];
		int val = 0;
		for (int i = 0; i < 10000; i++) {
			val = rand.nextInt(1000000) + 1;
			while (helping_buf4Keys.contains(val) == true) {
				val = rand.nextInt(1000000) + 1;
			}
			helping_buf4Keys.add(val);

			data[i] = val;
		}
		Arrays.sort(data);
		return;
	}

	public int callSearchArray(int value, int counter) {
		counter = 0;
		for (int i = 0; i < 100; i++) {

			int rkey = rand.nextInt(10000) + 1;
			value = helping_buf4Keys.get(rkey);

			counter = binarySearch(data, 0, data.length - 1, value, counter);
		}
		return counter / 100;
	}

	public int binarySearch(int arr[], int start, int arrayLength, int value, int counter) {
		int mid = start + (arrayLength - start) / 2;
		if (arrayLength <= start) {
			return counter;
		}

		if (arr[mid] == value) {
			counter++;
			return counter;
		} else if (arr[mid] > value) {
			counter= counter +2;
			counter = binarySearch(arr, start, mid - 1, value, counter);
		} else {
			counter= counter +3;;
			counter = binarySearch(arr, mid + 1, arrayLength, value, counter);
		}
		return counter;	
	}

	public int callPrintRange(int counter, int k) {
		counter = 0;
		for (int i = 0; i < 100; i++) {
			int rkey = rand.nextInt(10000) + 1;
			int low = helping_buf4Keys.get(rkey);
			int high = low + k;
			counter = printRange(data, 0, data.length - 1, low, high, counter);
			//System.out.println("|");
		}
		return counter / 100;
	}

	public int printRange(int arr[], int start, int arrayLength, int low, int high, int counter) {
		if (arrayLength >= start) {
			int mid = start + (arrayLength - start) / 2;

			if (high < arr[mid]) {
				counter = counter + 1;
				counter = printRange(arr, start, mid - 1, low, high, counter);
			} else if (low > arr[mid]) {
				counter = counter + 2;
				counter = printRange(arr, mid + 1, arrayLength, low, high, counter);
			} else {
				counter = counter + 3;
				counter = printRange(arr, start, mid - 1, low, high, counter);
				//System.out.print(" " + arr[mid]);
				counter = printRange(arr, mid + 1, arrayLength, low, high, counter);
			}

		}

		return counter;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

		SortedArray sArray = new SortedArray();
		int counter = 0;
		int val = 0;

		sArray.initArray();

		counter = sArray.callSearchArray(val, counter);
		System.out.println("  ");
		System.out.println("SEARCH RESULT: " + counter);
		System.out.println("  ");
		//System.out.print(" Found Numbers: ");

		counter = sArray.callPrintRange(counter, 1000); // K = 1000;
		System.out.println("  ");
		System.out.println("  ");
		System.out.println("PRINT RANGE RESULT: " + counter);
	}

}
