package net.certiv.common.tree;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IllegalArgsEx;

/**
 * Represents a single, connected, undirected acyclic path within a single tree existing
 * within a {@link Forest}.
 * <p>
 * A path represents a view of the selected forest nodes, nominally starting at a tree
 * root and continuing in ascending order. The path is backed by the forest, so changes to
 * the path are reflected in the forest, and vice-versa.
 * <p>
 * No protection is provided against changes that affect the continuitiy of the path or
 * proper structure of the forest.
 */
public class TreePath<T> implements Iterable<TreeNode<T>> {

	/** Path representing this path. */
	private final LinkedList<TreeNode<T>> path = new LinkedList<>();
	/** Values missing in the progressive construction of this path. */
	private final LinkedList<T> missing = new LinkedList<>();

	/**
	 * Creates a new, empty {@link TreePath}.
	 */
	public TreePath() {}

	/**
	 * Creates a new {@link TreePath} containing a single node.
	 *
	 * @param node root node
	 * @throws IllegalArgumentException if the given node is {@code null}
	 */
	public TreePath(TreeNode<T> node) {
		this();
		Assert.notNull(node);
		path.add(node);
	}

	/**
	 * Constructs a {@link TreePath} from the given consecutive sequence of nodes.
	 *
	 * @param path nodes defining path
	 * @throws IllegalArgumentException if {@code path} is {@code null}, empty, or
	 *                                  contains a {@code null} value
	 */
	public TreePath(List<TreeNode<T>> path) {
		Assert.notNull(path);
		if (path.isEmpty()) throw IllegalArgsEx.of("Path cannot be empty.");
		path.addAll(path);
	}

	/**
	 * Creates a new path containing the given parent path and given terminal path node.
	 *
	 * @param parent   parent path; may be {@code null}
	 * @param terminal path terminal node
	 * @throws IllegalArgumentException if the parent or terminal is {@code null}
	 */
	public TreePath(TreePath<T> parent, TreeNode<T> terminal) {
		Assert.notNull(parent, terminal);
		TreePath<T> path = new TreePath<>();
		path.addAll(path.nodes());
		path.addLast(terminal);
	}

	/**
	 * Returns an unmodifiable list copy of the path nodes.
	 *
	 * @return path nodes
	 */
	public List<TreeNode<T>> nodes() {
		return List.copyOf(path);
	}

	/** Returns this path as a corresponding list of the contained data values. */
	public LinkedList<T> values() {
		return path.stream() //
				.map(n -> n.get()) //
				.collect(Collectors.toCollection(LinkedList<T>::new));
	}

	/**
	 * Returns a new path containing all but the terminal node of this path. Returns an
	 * empty path if this path contains fewer than two nodes.
	 *
	 * @return parent path
	 */
	public TreePath<T> parentPath() {
		int end = size() - 1;
		if (end < 1) return new TreePath<>();
		return new TreePath<>(path.subList(0, end));
	}

	/**
	 * Returns {@code true} if the given forest path is a descendant of this
	 * {@link TreePath}.
	 *
	 * @param other another forest path
	 * @return {@code true} if the given forest path is a descendant of this path
	 */
	public boolean isDescendant(TreePath<T> other) {
		if (other == this) return true;

		if (other != null) {
			int a = size();
			int b = other.size();

			// Cannot be a descendant if it has fewer path nodes.
			if (b < a) return false;

			while (b-- > a) {
				other = other.parentPath();
			}
			return equals(other);
		}
		return false;
	}

	public boolean contains(Object o) {
		return path.contains(o);
	}

	/**
	 * Retrieves, but does not remove, the first path node.
	 *
	 * @return first path node, or {@code null} if this path is empty
	 */
	public TreeNode<T> base() {
		return path.peekFirst();
	}

	/**
	 * Returns the path node at the given index. The node is not removed.
	 *
	 * @param idx node index within this path
	 * @return selected node
	 * @throws IllegalArgumentException if the index is outside the range of this path
	 */
	public TreeNode<T> get(int idx) {
		return path.get(idx);
	}

	/**
	 * Returns, without removing, the last element of this path.
	 *
	 * @return the last element in the path
	 */
	public TreeNode<T> terminal() {
		return path.peekLast();
	}

	/**
	 * Inserts the given node at the start of this path.
	 *
	 * @param base node to insert at the path head
	 */
	public void addFirst(TreeNode<T> base) {
		path.addFirst(base);
	}

	/**
	 * Appends the given node to the end of this path.
	 *
	 * @param terminal node to append to the path tail
	 */
	public void addLast(TreeNode<T> terminal) {
		path.addLast(terminal);
	}

	public boolean addAll(Collection<? extends TreeNode<T>> c) {
		return path.addAll(c);
	}

	public void push(TreeNode<T> e) {
		path.push(e);
	}

	public TreeNode<T> pop() {
		return path.pop();
	}

	public TreeNode<T> removeFirst() {
		return path.removeFirst();
	}

	public TreeNode<T> removeLast() {
		return path.removeLast();
	}

	public Stream<TreeNode<T>> stream() {
		return path.stream();
	}

	/**
	 * @see Iterable#forEach(Consumer)
	 */
	@Override
	public void forEach(Consumer<? super TreeNode<T>> action) {
		path.forEach(action);
	}

	/**
	 * @see AbstractSequentialList#iterator()
	 */
	@Override
	public Iterator<TreeNode<T>> iterator() {
		return path.iterator();
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}

	/**
	 * Returns the path length (size).
	 *
	 * @return the number of elements in the path
	 */
	public int size() {
		return path.size();
	}

	/**
	 * Returns whether this path is complete. A complete path occurs where the full
	 * sequence of {@link Forest#find} value terms maps to an existing, connected sequence
	 * of forest nodes. Any missing value terms, as collected in the progressive
	 * construction of this path, can be retrieved using {@link #missing()}.
	 *
	 * @return {@code true} if this path is complete
	 */
	public boolean complete() {
		return missing.isEmpty();
	}

	/**
	 * Returns the missing value terms, as collected in the progressive construction of
	 * this path.
	 *
	 * @return missing value terms
	 */
	public List<T> missing() {
		return missing;
	}

	void skipped(T value) {
		if (value != null) missing.add(value);
	}

	void skipped(Collection<T> values) {
		if (values != null && !values.isEmpty()) {
			missing.addAll(values);
		}
	}

	public void clear() {
		path.clear();
		missing.clear();
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TreePath)) return false;
		TreePath<?> other = (TreePath<?>) obj;
		return Objects.equals(path, other.path);
	}

	@Override
	public String toString() {
		return path.toString();
	}

	// /**
	// * Appends the given terminal node to the end of this path. Equivalent to
	// * {@link #addLast}.
	// *
	// * @param terminal node to append to this path
	// * @return {@code true}; consistent with {@link Collection#add}
	// */
	// public boolean append(TreeNode<T> terminal) {
	// Assert.notNull(terminal);
	// TreeNode<T> node = new TreeNode<>(terminal.forest(), terminal.get());
	// TreeNode<T> prev = path.peekLast();
	// if (prev != null) {
	// node.setParent(prev);
	// prev.addChild(node);
	// }
	// return path.add(node);
	// }
}
