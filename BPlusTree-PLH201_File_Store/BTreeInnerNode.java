import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

class BTreeInnerNode<TKey extends Comparable<TKey>> extends BTreeNode<TKey> {
	protected final static int INNERORDER = 29;
	protected final static int DATA_PAGE_SIZE = 256;
	// protected Object[] children;
	// CHANGE FOR STORING ON FILE
	protected Integer[] children;

	public BTreeInnerNode() {
		this.keys = new Integer[INNERORDER + 1];
		// this.children = new Object[INNERORDER + 2];
		// CHANGE FOR STORING ON FILE
		this.children = new Integer[INNERORDER + 2];
	}

	@SuppressWarnings("unchecked")
	public BTreeNode<TKey> getChild(int index) {
//		return (BTreeNode<TKey>)this.children[index];
		// CHANGE FOR STORING ON FILE
		try {

			return (BTreeNode<TKey>) StorageCache.getInstance().retrieveNode(this.children[index]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void setChild(int index, BTreeNode<TKey> child) {
//		this.children[index] = child;
		// CHANGE FOR STORING ON FILE
		if (child != null) {
			this.children[index] = child.getStorageDataPage();

			child.setParent(this);

			setDirty();
		}else {
			this.children[index] = null;
		}
	}

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.InnerNode;
	}

	@Override
	public int search(TKey key) {
		int index = 0;
		for (index = 0; index < this.getKeyCount(); ++index) {
			int cmp = this.getKey(index).compareTo(key);
			if (cmp == 0) {
				return index + 1;
			} else if (cmp > 0) {
				return index;
			}
		}

		return index;
	}

	/* The codes below are used to support insertion operation */

	private void insertAt(int index, TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		// move space for the new key
		for (int i = this.getKeyCount() + 1; i > index; --i) {
			this.setChild(i, this.getChild(i - 1));
		}
		for (int i = this.getKeyCount(); i > index; --i) {
			this.setKey(i, this.getKey(i - 1));
		}

		// insert the new key
		this.setKey(index, key);
		this.setChild(index, leftChild);
		this.setChild(index + 1, rightChild);
		this.keyCount += 1;

	}

	/**
	 * When splits a internal node, the middle key is kicked out and be pushed to
	 * parent node.
	 */
	@Override
	protected BTreeNode<TKey> split() {
		int midIndex = this.getKeyCount() / 2;

		BTreeInnerNode<TKey> newRNode;
		try {
			newRNode = StorageCache.getInstance().newInnerNode();
			for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {
				newRNode.setKey(i - midIndex - 1, this.getKey(i));
				this.setKey(i, null);
			}
			for (int i = midIndex + 1; i <= this.getKeyCount(); ++i) {
				newRNode.setChild(i - midIndex - 1, this.getChild(i));
				newRNode.getChild(i - midIndex - 1).setParent(newRNode);
				this.setChild(i, null);
			}
			this.setKey(midIndex, null);
			newRNode.keyCount = this.getKeyCount() - midIndex - 1;
			this.keyCount = midIndex;
			setDirty();
			return newRNode;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	protected BTreeNode<TKey> pushUpKey(TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightNode) {
		// find the target position of the new key
		int index = this.search(key);

		// insert the new key
		this.insertAt(index, key, leftChild, rightNode);

		// check whether current node need to be split
		if (this.isOverflow()) {
			return this.dealOverflow();
		} else {
			return this.getParent() == null ? this : null;
		}
	}

	/* The codes below are used to support delete operation */

	private void deleteAt(int index) {
		int i = 0;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setChild(i + 1, this.getChild(i + 2));
		}
		this.setKey(i, null);
		this.setChild(i + 1, null);
		--this.keyCount;
		setDirty();
	}

	@Override
	protected void processChildrenTransfer(BTreeNode<TKey> borrower, BTreeNode<TKey> lender, int borrowIndex) {
		int borrowerChildIndex = 0;
		while (borrowerChildIndex < this.getKeyCount() + 1 && this.getChild(borrowerChildIndex) != borrower)
			++borrowerChildIndex;

		if (borrowIndex == 0) {
			// borrow a key from right sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex), lender, borrowIndex);
			this.setKey(borrowerChildIndex, upKey);
		} else {
			// borrow a key from left sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex - 1), lender, borrowIndex);
			this.setKey(borrowerChildIndex - 1, upKey);
		}
	}

	@Override
	protected BTreeNode<TKey> processChildrenFusion(BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) {
		int index = 0;
		while (index < this.getKeyCount() && this.getChild(index) != leftChild)
			++index;
		TKey sinkKey = this.getKey(index);

		// merge two children and the sink key into the left child node
		leftChild.fusionWithSibling(sinkKey, rightChild);

		// remove the sink key, keep the left child and abandon the right child
		this.deleteAt(index);

		// check whether need to propagate borrow or fusion to parent
		if (this.isUnderflow()) {
			if (this.getParent() == null) {
				// current node is root, only remove keys or delete the whole root node
				if (this.getKeyCount() == 0) {
					leftChild.setParent(null);
					return leftChild;
				} else {
					return null;
				}
			}

			return this.dealUnderflow();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fusionWithSibling(TKey sinkKey, BTreeNode<TKey> rightSibling) {
		BTreeInnerNode<TKey> rightSiblingNode = (BTreeInnerNode<TKey>) rightSibling;

		int j = this.getKeyCount();
		this.setKey(j++, sinkKey);

		for (int i = 0; i < rightSiblingNode.getKeyCount(); ++i) {
			this.setKey(j + i, rightSiblingNode.getKey(i));
		}
		for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; ++i) {
			this.setChild(j + i, rightSiblingNode.getChild(i));
		}
		this.keyCount += 1 + rightSiblingNode.getKeyCount();

		this.setRightSibling(rightSiblingNode.getRightSibling());
		if (rightSiblingNode.getRightSibling() != null)
			rightSiblingNode.getRightSibling().setLeftSibling(this);
	}

	@Override
	protected TKey transferFromSibling(TKey sinkKey, BTreeNode<TKey> sibling, int borrowIndex) {
		BTreeInnerNode<TKey> siblingNode = (BTreeInnerNode<TKey>) sibling;

		TKey upKey = null;
		if (borrowIndex == 0) {
			// borrow the first key from right sibling, append it to tail
			int index = this.getKeyCount();
			this.setKey(index, sinkKey);
			this.setChild(index + 1, siblingNode.getChild(borrowIndex));
			this.keyCount += 1;

			upKey = siblingNode.getKey(0);
			siblingNode.deleteAt(borrowIndex);
		} else {
			// borrow the last key from left sibling, insert it to head
			this.insertAt(0, sinkKey, siblingNode.getChild(borrowIndex + 1), this.getChild(0));
			upKey = siblingNode.getKey(borrowIndex);
			siblingNode.deleteAt(borrowIndex);
		}
		setDirty();
		return upKey;
	}

	@SuppressWarnings("unchecked")
	protected byte[] toByteArray() {

		// must include the index of the data page to the left sibling (int == 4 bytes),
		// to the right sibling,
		// to the parent node, the number of keys (keyCount), the type of node (inner
		// node/leaf node) and the list of keys and list of children (each key 4 byte
		// int, each children 4 byte int pointing to the a data page offeset)
		// We do not need the isDirty flag and the storageDataPage
		// so we need
		BTreeInnerNode<TKey> newRNode = new BTreeInnerNode<TKey>();
		try {
			newRNode = (BTreeInnerNode<TKey>) StorageCache.getInstance().retrieveNode(getStorageDataPage());
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
		for (int i = 0; i <= newRNode.getKeyCount(); i++) {
			if (newRNode.children[i] != null) {
				// Children Offset
				bb.putInt(newRNode.children[i]);
			}
		}

		byteArray = bb.array();
		return byteArray;

	}

	@SuppressWarnings("unchecked")
	protected BTreeInnerNode<TKey> fromByteArray(byte[] byteArray, int dataPageOffset) {
		// this takes a byte array of fixed size, and transforms it to a BTreeInnerNode
		// it takes the format we store our node (as specified in
		// BTreeInnerNode.toByteArray()) and constructs the BTreeInnerNode
		// We need as parameter the dataPageOffset in order to set it
		ByteBuffer bb = ByteBuffer.wrap(byteArray);
		int nodetype = bb.getInt(0);

		BTreeInnerNode<TKey> result = new BTreeInnerNode<TKey>();
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

		for (int i = 0; i <= keycounter; i++) {
			if ((Integer) bb.getInt(20 + (keycounter * 4) + (4 * i)) != null) {
				result.children[i] = bb.getInt(20 + (keycounter * 4) + (4 * i));
			}

		}

		return result;
	}

}