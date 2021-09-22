import java.io.IOException;

/**
 * Original at https://github.com/linli2016/BPlusTree A B+ tree Since the
 * structures and behaviors between internal node and external node are
 * different, so there are two different classes for each kind of node.
 * 
 * @param <TKey>   the data type of the key
 * @param <TValue> the data type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> {
	private BTreeNode<TKey> root;

	private Integer nextFreeDatafileByteOffset = 0; // for this assignment, we only create new, empty files. We keep
													// here the next free byteoffset in our file

	public BTree() throws IOException {
		// this.root = new BTreeLeafNode<TKey, TValue>();
		// CHANGE FOR STORING ON FILE
		this.root = StorageCache.getInstance().newLeafNode();
		StorageCache.getInstance().flush();
	}

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 * 
	 * @throws IOException
	 */
	public void insert(TKey key, TValue value) throws IOException {
		// CHANGE FOR STORING ON FILE
		nextFreeDatafileByteOffset = StorageCache.getInstance().newData((Data) value, nextFreeDatafileByteOffset);

		// CHANGE FOR STORING ON FILE
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		StorageCache.getInstance().retrieveNode(leaf.getStorageDataPage());
		leaf.insertKey(key, nextFreeDatafileByteOffset);

		if (leaf.isOverflow()) {
			BTreeNode<TKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n;
		}

		// CHANGE FOR STORING ON FILE
		StorageCache.getInstance().flush();
	}

	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public Integer search(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}

	/**
	 * Delete a key and its associated value from the tree.
	 * 
	 * @throws IOException
	 */
	public void delete(TKey key) throws IOException {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);

		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n;
		}
		// CHANGE FOR STORING ON FILE
		// StorageCache.getInstance().flush();
	}

	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		BTreeNode<TKey> node;
		try {
			node = StorageCache.getInstance().retrieveNode(this.root.getStorageDataPage());
			while (node.getNodeType() == TreeNodeType.InnerNode) {
				node = ((BTreeInnerNode<TKey>) node).getChild(node.search(key));

			}

			return (BTreeLeafNode<TKey, TValue>) node;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	////////////////////////////////////////////////////////////////////////////
	/////////////// EXTRA FUNTIONS FOR 3RD ASSIGN ////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public int CustomInsert(TKey key, TValue value) throws IOException {
		int counter = 0;
		StorageCache.getInstance().ResetCount();
		insert(key, value);
		counter = StorageCache.getInstance().getAccessCount();
		return counter;
	}

	public int CustomSearch(TKey key) {
		int counter = 0;
		StorageCache.getInstance().ResetCount();
		search(key);
		counter = StorageCache.getInstance().getAccessCount();
		return counter;
	}

	public int CustomDelete(TKey key) throws IOException {
		int counter = 0;
		StorageCache.getInstance().ResetCount();
		delete(key);
		counter = StorageCache.getInstance().getAccessCount();
		return counter;
	}

	public int CustomSearchRange(TKey lowvalue, TKey highvalue) {
		int counter = 0;
		int index = -1;
		StorageCache.getInstance().ResetCount();
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(lowvalue);
		while (index ==-1) {
			index = leaf.searchRange(lowvalue, highvalue);
			if(index ==-1) {
			try {
				//GET THE RIGHT SIBLING TILL HIGH VALUE MEETS A BIGGER KEY
				leaf = (BTreeLeafNode<TKey, TValue>) StorageCache.getInstance().retrieveNode(leaf.rightSibling);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

		counter = StorageCache.getInstance().getAccessCount();
		return counter;
	}
}
