import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

class BTreeLeafNode<TKey extends Comparable<TKey>, TValue> extends BTreeNode<TKey> {
	protected final static int LEAFORDER = 29;
	protected final static int DATA_PAGE_SIZE = 256;
	// private Object[] values;
	// CHANGE FOR STORING ON FILE
	private Integer[] values; // integers pointing to byte offset in data file

	public BTreeLeafNode() {
		this.keys = new Integer[LEAFORDER + 1];
		this.values = new Integer[LEAFORDER + 1];
	}

	@SuppressWarnings("unchecked")
	public int getValue(int index) {
		return (int) this.values[index];
	}

	public void setValue(int index, Integer nextFreeDatafileByteOffset) {
		this.values[index] = nextFreeDatafileByteOffset;
		setDirty(); // we changed a value, so this node is dirty and must be flushed to disk
	}

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.LeafNode;
	}

	@Override
	public int search(TKey key) {
		for (int i = 0; i < this.getKeyCount(); ++i) {
			int cmp = this.getKey(i).compareTo(key);
			if (cmp == 0) {
				return i;
			} else if (cmp > 0) {
				return -1;
			}
		}

		return -1;
	}

	/* The codes below are used to support insertion operation */

	public void insertKey(TKey key, int nextFreeDatafileByteOffset) {
		int index = 0;
		while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
			++index;
		this.insertAt(index, key, nextFreeDatafileByteOffset);
	}

	private void insertAt(int index, TKey key, int nextFreeDatafileByteOffset) {
		// move space for the new key
		for (int i = this.getKeyCount() - 1; i >= index; --i) {
			this.setKey(i + 1, this.getKey(i));
			this.setValue(i + 1, this.getValue(i));
		}

		// insert new key and value
		this.setKey(index, key);
		this.setValue(index, nextFreeDatafileByteOffset);
		// setDirty() will be called in setKey/setValue
		++this.keyCount;
	}

	/**
	 * When splits a leaf node, the middle key is kept on new node and be pushed to
	 * parent node.
	 */
	@Override
	protected BTreeNode<TKey> split() {
		int midIndex = this.getKeyCount() / 2;
		try {
			BTreeLeafNode newRNode = StorageCache.getInstance().newLeafNode();
			for (int i = midIndex; i < this.getKeyCount(); ++i) {
				newRNode.setKey(i - midIndex, this.getKey(i));
				newRNode.setValue(i - midIndex, this.getValue(i));
				this.setKey(i, null);
				this.setValue(i, null);
			}
			newRNode.keyCount = this.getKeyCount() - midIndex;
			this.keyCount = midIndex;
			setDirty();// just to make sure
			return newRNode;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected BTreeNode<TKey> pushUpKey(TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightNode) {
		throw new UnsupportedOperationException();
	}

	/* The codes below are used to support deletion operation */

	public boolean delete(TKey key) {
		int index = this.search(key);
		if (index == -1)
			return false;

		this.deleteAt(index);
		return true;
	}

	private void deleteAt(int index) {
		int i = index;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setValue(i, this.getValue(i + 1));
		}

		this.setKey(i, null);
		this.setValue(i, null);
		--this.keyCount;

		// setDirty will be called through setKey/setValue
	}

	@Override
	protected void processChildrenTransfer(BTreeNode<TKey> borrower, BTreeNode<TKey> lender, int borrowIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected BTreeNode<TKey> processChildrenFusion(BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Notice that the key sunk from parent is be abandoned.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void fusionWithSibling(TKey sinkKey, BTreeNode<TKey> rightSibling) {
		BTreeLeafNode<TKey, TValue> siblingLeaf = (BTreeLeafNode<TKey, TValue>) rightSibling;

		int j = this.getKeyCount();
		for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
			this.setKey(j + i, siblingLeaf.getKey(i));
			this.setValue(j + i, siblingLeaf.getValue(i));
		}
		this.keyCount += siblingLeaf.getKeyCount();

		this.setRightSibling(siblingLeaf.getRightSibling());
		if (siblingLeaf.getRightSibling() != null)
			siblingLeaf.getRightSibling().setLeftSibling(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected TKey transferFromSibling(TKey sinkKey, BTreeNode<TKey> sibling, int borrowIndex) {
		BTreeLeafNode<TKey, TValue> siblingNode = (BTreeLeafNode<TKey, TValue>) sibling;

		this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValue(borrowIndex));
		siblingNode.deleteAt(borrowIndex);
		// setDirty will be called through setKey/setValue in deleteAt
		return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
	}

	@SuppressWarnings("unchecked")
	protected byte[] toByteArray() {
		// very similar to BTreeInnerNode. Instead of pointers to children (offset to
		// our data pages in our node file), we have pointers
		// to data (byte offset to our data file)
		BTreeLeafNode<TKey, TValue> newRNode = new BTreeLeafNode<TKey, TValue>();
		try {
			newRNode = (BTreeLeafNode<TKey, TValue>) StorageCache.getInstance().retrieveNode(getStorageDataPage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] byteArray = new byte[256];
		// 4 bytes for marking this as a inner node (e.g. an int with value = 1 for
		// inner node and 2 for leaf node)
		TreeNodeType nodetype = newRNode.getNodeType();
		int ntype = 0;
		if (nodetype == TreeNodeType.InnerNode) {
			ntype = 1;
		} else if (nodetype == TreeNodeType.LeafNode) {
			ntype = 2;
		}
		ByteBuffer bb = ByteBuffer.allocate(DATA_PAGE_SIZE);
		// 4 bytes for marking this as a inner node
		bb.putInt(ntype);
		// 4 bytes for left sibling
		if (newRNode.leftSibling == null) {
			bb.putInt(-1);
		} else {
			bb.putInt(newRNode.leftSibling);
		}
		// 4 bytes for right sibling
		if (newRNode.rightSibling == null) {
			bb.putInt(-1);
		} else {
			bb.putInt(newRNode.rightSibling);
		}
		// 4 bytes for parent
		if (newRNode.parentNode == null) {
			bb.putInt(-1);
		} else {
			bb.putInt(newRNode.parentNode);
		}
		// 4 bytes for the number of keys
		bb.putInt(newRNode.getKeyCount());

		for (int i = 0; i < newRNode.getKeyCount(); i++) {
			if (newRNode.keys[i] != null) {
				// key
				bb.putInt(newRNode.keys[i]);
			}
		}
		for (int i = 0; i < newRNode.getKeyCount(); i++) {
			if (newRNode.values[i] != null) {
				// Children Offset
				// System.out.println("Value " + newRNode.values[i]);
				bb.putInt(newRNode.values[i]);
			}
		}

		byteArray = bb.array();
		return byteArray;

	}

	@SuppressWarnings("unchecked")
	protected BTreeLeafNode<TKey, TValue> fromByteArray(byte[] byteArray, int dataPageOffset) {
		// this takes a byte array of fixed size, and transforms it to a BTreeLeafNode
		// it takes the format we store our node (as specified in toByteArray()) and
		// constructs the BTreeLeafNode
		// We need as parameter the dataPageOffset in order to set it
		ByteBuffer bb = ByteBuffer.wrap(byteArray);

		BTreeLeafNode<TKey, TValue> result = new BTreeLeafNode<TKey, TValue>();
		result.setStorageDataPage(dataPageOffset);
		if (bb.getInt(4) < 0) {
			result.leftSibling = null;
		} else {
			result.leftSibling = bb.getInt(4);
		}
		if (bb.getInt(8) < 0) {
			result.rightSibling = null;
		} else {
			result.rightSibling = bb.getInt(8);
		}

		if (bb.getInt(12) < 0) {
			result.parentNode = null;
		} else {
			result.parentNode = bb.getInt(12);
		}

		int keycounter = bb.getInt(16);
		result.setKeyCount(keycounter);

		for (int i = 0; i < keycounter; i++) {
			if ((Integer) bb.getInt(20 + (4 * i)) != null) {
				result.keys[i] = bb.getInt(20 + (4 * i));
			}

		}

		for (int i = 0; i < keycounter; i++) {
			if ((Integer) bb.getInt(20 + (keycounter * 4) + (4 * i)) != null) {
				result.values[i] = bb.getInt(20 + (keycounter * 4) + (4 * i));
			}

		}
		return result;

	}

	public int searchRange(TKey lowvalu, TKey highval) {
		for (int i = 0; i < this.getKeyCount(); ++i) {
			int cmp_low = this.getKey(i).compareTo(lowvalu);
			int cmp_high = this.getKey(i).compareTo(highval);

			if (cmp_low > 0 && cmp_high > 0) {
				return i;
			}	
		}
		return -1;
	}

}
