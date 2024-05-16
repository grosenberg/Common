package net.certiv.common.tree;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import net.certiv.common.annotations.VisibleForTesting;
import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.sparse.SparseSet;
import net.certiv.common.tree.Forest.Policy;

public class TreeNode<T> implements Comparable<TreeNode<T>> {

	private static final String ERR_CYCLIC = "Cannot create a cyclic reference.";

	/** Node counter */
	private static final Counter Cntr = new Counter(-1);

	public final long num;

	private Forest<T> forest;
	private TreeNode<T> parent;
	private final SparseSet<TreeNode<T>> children;
	private T data;

	private DotStyle style;

	/**
	 * Construct a tree node with no current data value.
	 * <p>
	 * Parent node reference will be set when the node is added to a tree, forest, or
	 * path; will be {@code null} iff this is a root node.
	 *
	 * @param forest containing element
	 */
	public TreeNode(Forest<T> forest) {
		Assert.notNull(forest);
		this.forest = forest;

		num = Cntr.inc();
		children = new SparseSet<>();
	}

	/**
	 * Construct a tree node with the given data value.
	 * <p>
	 * Parent node reference will be set when the node is added to a tree, forest, or
	 * path; will be {@code null} iff this is a root node.
	 *
	 * @param forest containing forest
	 * @param data   contained value
	 */
	public TreeNode(Forest<T> forest, T data) {
		this(forest);
		this.data = data;
	}

	public Forest<T> forest() {
		return forest;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public TreeNode<T> parent() {
		return parent;
	}

	/**
	 * Set the parent node.
	 *
	 * @param parent parent node
	 * @throws IllegalArgumentException if parent and child are the same
	 */
	public void setParent(TreeNode<T> parent) {
		if (equals(parent)) {
			throw new IllegalArgumentException(ERR_CYCLIC);
		}
		this.parent = parent;
	}

	public SparseSet<TreeNode<T>> children() {
		return children;
	}

	public int size() {
		return children.size();
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public void install(TreePath<T> path, LinkedList<T> queue) {
		path.addLast(this);
		if (!queue.isEmpty()) {
			T first = queue.pop();
			TreeNode<T> node = getChild(first);
			if (node == null) {
				node = new TreeNode<>(forest, first);
				addChild(node);
			}
			node.install(path, queue);
		}
	}

	public void install(TreePath<T> path, LinkedList<T> queue, BiPredicate<? super T, ? super T> match) {
		Assert.notNull(match);
		path.addLast(this);
		if (!queue.isEmpty()) {
			T first = queue.pop();
			TreeNode<T> node = getChild(first, match);
			if (node == null) {
				node = new TreeNode<>(forest, first);
				addChild(node);
			}
			node.install(path, queue, match);
		}
	}

	public void addChildren(List<TreeNode<T>> children) {
		children.forEach(c -> addChild(c));
	}

	/**
	 * Add a child node.
	 *
	 * @param child node to add to child set
	 * @throws IllegalArgumentException if parent and child are the same
	 */
	public void addChild(TreeNode<T> child) {
		if (equals(child)) {
			throw new IllegalArgumentException(ERR_CYCLIC);
		}
		child.setParent(this);
		children.add(child);
	}

	public boolean removeChild(TreeNode<T> node) {
		return children.remove(node);
	}

	public LinkedList<TreeNode<T>> removeChildren() {
		LinkedList<TreeNode<T>> c = new LinkedList<>(children);
		children.clear();
		return c;
	}

	public TreeNode<T> getChild(T value) {
		return children.stream() //
				.filter(c -> c.has(value)) //
				.findFirst().orElse(null);
	}

	public TreeNode<T> getChild(T value, BiPredicate<? super T, ? super T> match) {
		Assert.notNull(match);
		return children.stream() //
				.filter(c -> match.test(c.get(), value)) //
				.findFirst().orElse(null);
	}

	public TreePath<T> findPath(TreePath<T> path, Policy policy, LinkedList<T> queue,
			BiPredicate<? super T, ? super T> match) {

		if (!queue.isEmpty()) {
			T next = queue.pop();
			TreeNode<T> node = getChild(next, match);

			switch (policy) {
				case EXACT:
					if (node == null) {
						path.skipped(next);
						path.skipped(queue);
					}
					break;

				case FLEX:
					while (node == null) {
						path.skipped(next);
						if (queue.isEmpty()) break;
						next = queue.pop();
						node = getChild(next, match);
					}
					break;
			}

			if (node != null) {
				path.addLast(node);
				path = node.findPath(path, policy, queue, match);
			}
		}
		return path;
	}

	public TreePath<T> findPath(TreePath<T> path, Policy policy, LinkedList<T> queue) {
		if (!queue.isEmpty()) {
			T next = queue.pop();
			TreeNode<T> node = getChild(next);

			switch (policy) {
				case EXACT:
					if (node == null) {
						path.skipped(next);
						path.skipped(queue);
					}
					break;

				case FLEX:
					while (node == null) {
						path.skipped(next);
						if (queue.isEmpty()) break;
						next = queue.pop();
						node = getChild(next);
					}
					break;
			}

			if (node != null) {
				path.addLast(node);
				path = node.findPath(path, policy, queue);
			}
		}
		return path;
	}

	/**
	 * Return {@code true} if this node has the given data value.
	 *
	 * @param value another data value
	 * @return {@code true} if this node has the given data value
	 */
	public boolean has(T value) {
		return Objects.equals(this.data, value);
	}

	/**
	 * Returns the data value held by this node.
	 *
	 * @return data value
	 */
	public T get() {
		return data;
	}

	/**
	 * Set the data value held by this node.
	 * <p>
	 * TODO: verify: If this node exists in a forest, altering the data value will not
	 * adjust the sorted order of node within the forest. Remove and re-add the node to
	 * correct?
	 *
	 * @param data data value
	 * @return prior data value
	 */
	public T set(T data) {
		T prior = this.data;
		this.data = data;
		return prior;
	}

	/**
	 * Returns the level of this node, representing the number of levels that exist above
	 * this node. The root level is defined as {@code level 0}.
	 *
	 * @return node level
	 */
	public int level() {
		int lvl = 0;
		for (TreeNode<T> p = parent(); p != null; p = parent()) {
			lvl++;
		}
		return lvl;
	}

	/**
	 * Returns a {@link TreePath} containing the path of nodes existing from a root node,
	 * inclusive, to this node, inclusive.
	 *
	 * @return root path to this node
	 */
	public TreePath<T> path() {
		TreePath<T> path = new TreePath<>();
		path.push(this);
		for (TreeNode<T> p = parent(); p != null; p = p.parent()) {
			path.push(p);
		}
		return path;
	}

	// --------------------------------

	/** Return the forest node instance display name. */
	public String label() {
		String label = (String) style().get(DotAttr.LABEL);
		if (label != null) return label;
		return String.format("%d: %s", num, data);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		style().put(DotAttr.LABEL, name);
	}

	/**
	 * Returns the {@code DotStyle} store for this node. Creates and adds an
	 * {@code ON#NODES} default category {@code DotStyle} store, if a store does not
	 * exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle style() {
		if (style == null) {
			style = new DotStyle(ON.NODES);
		}
		return style;
	}

	// --------------------------------

	public void clear() {
		parent = null;
		children.clear();
	}

	public void dispose() {
		clear();
		forest = null;
		data = null;
		style = null;
	}

	@VisibleForTesting
	static void reset() {
		Cntr.set(-1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(TreeNode<T> o) {
		if (this == o) return 0;

		int v = 0;
		Comparator<T> comp = forest.comparator();
		if (comp != null) v = Objects.compare(data, o.data, comp);
		if (v != 0) return v;

		if (data instanceof Comparable) v = ((Comparable<T>) data).compareTo(o.data);
		if (v != 0) return v;

		if (data == null && data != null) return -1;
		if (data != null && data == null) return 1;

		if (num < o.num) return -1;
		if (num > o.num) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, num);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TreeNode)) return false;
		TreeNode<?> other = (TreeNode<?>) obj;
		return Objects.equals(data, other.data) && num == other.num;
	}

	@Override
	public String toString() {
		if (data == null) return String.format("Node %d", num);
		return String.format("Node %d: %s", num, data);
	}
}
