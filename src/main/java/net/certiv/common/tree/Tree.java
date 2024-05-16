package net.certiv.common.tree;

import java.util.Comparator;

/**
 * {@link Tree} provides the root container for a single connected, undirected, acyclic
 * N-ary graph of nodes. Each node is implemented as a {@link TreeNode} that further
 * functions as a typed data value container.
 *
 * @param <T> tree node captive data type
 */
public class Tree<T> extends Forest<T> {

	/**
	 * Construct a tree instance with a {@link TreeNode} sort order determined by the
	 * natural sort order of the type T.
	 */
	public Tree() {
		this((Comparator<T>) null);
	}

	/**
	 * Construct a tree instance utilizing the given comparator to define the
	 * {@link TreeNode} sort order.
	 *
	 * @param comp node sort order comparator
	 */
	public Tree(Comparator<T> comp) {
		super(comp);
		setLimit(1);
	}

	/**
	 * Copy constructor. Constructs a tree instance derived from the given tree.
	 *
	 * @param tree instance to copy
	 */
	public Tree(Tree<T> tree) {
		this(tree.comparator());
		tree.terminals().stream().forEach(t -> install(t.path()));
	}
}
