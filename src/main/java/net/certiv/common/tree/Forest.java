package net.certiv.common.tree;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.sparse.SparseSet;

/**
 * {@link Forest} provides the root container for a collection of mutually disconnected,
 * individually connected, undirected, acyclic N-ary graphs of nodes. Each node is
 * implemented as a {@link TreeNode} that further functions as a typed data value
 * container.
 * <p>
 * Supported operations over a forest include:
 *
 * <pre>
 * has		examine current node data, returning true if criteria matches
 * get		return current node data
 * hasChild	examine children, returning true if criteria matches
 * getChild	examine children, returning node that matches criteria
 * contains	examine subtree, inclusive, returning true if criteria matches
 * find		examine subtree, inclusive, returning node
 * hasPath	examine subtree, inclusive, returning true if path exists
 * findPath	examine subtree, inclusive, returning path
 * </pre>
 *
 * @param <T> tree node captive data type
 */
public class Forest<T> {

	protected static final String ERR_WRONG_FOREST = "Node does not belong to this forest.";
	protected static final String ERR_BOUND1 = "Forest must be permitted at least one root.";
	protected static final String ERR_BOUND2 = "Cannot reduce forest size; remove root(s) before setting constraint.";
	protected static final String ERR_BOUND3 = "Forest is constrained to [0..%s] roots.";

	public enum Policy {
		/** Order is exact/required; ommissions in the remainder permitted. */
		EXACT,
		/** Order requires first element; ommissions in the remainder permitted. */
		FLEX;
	}

	/** Node children value-specific comparator */
	private final Comparator<T> comp;
	/** Root node count limit; default is {@link Integer#MAX_VALUE} */
	private int limit = Integer.MAX_VALUE;
	/** Root node styles */
	private DotStyle style;

	/** Root nodes: limited to [0..n]. */
	private final SparseSet<TreeNode<T>> roots = new SparseSet<>() {

		@Override
		public boolean add(TreeNode<T> node) {
			chkLimit(size() + 1, limit);
			return super.add(node);
		}

		@Override
		public boolean addAll(Collection<? extends TreeNode<T>> nodes) {
			chkLimit(size() + nodes.size(), limit);
			return super.addAll(nodes);
		}
	};

	public Forest() {
		this((Comparator<T>) null);
	}

	public Forest(Comparator<T> comp) {
		this.comp = comp;
	}

	public Forest(Forest<T> forest) {
		this(forest.comparator());
		forest.terminals().stream().forEach(t -> install(t.path()));
	}

	public Comparator<T> comparator() {
		return comp;
	}

	/**
	 * Returns a new set of {@code 0..1} {@link TreeNode} roots for a {@link Tree}, or
	 * {@code 0..n} {@link TreeNode} roots for a {@link Forest},
	 *
	 * @return root set
	 */
	public SparseSet<TreeNode<T>> roots() {
		return new SparseSet<>(roots);
	}

	/**
	 * Directly adds a node to the root set of this forest.
	 *
	 * @param root new root node
	 * @return {@code true} if the root node was added
	 */
	public boolean addRoot(TreeNode<T> root) {
		return roots.add(root);
	}

	/**
	 * Returns the constrained maximum number of forest roots. Defaults is
	 * {@link Integer#MAX_VALUE}.
	 *
	 * @return maximum number of forest roots
	 */
	public int limit() {
		return limit;
	}

	/**
	 * Sets the maximum number of roots allowed in this forest. Set to {@code 1} to define
	 * a forest constrained to a single tree.
	 * <p>
	 * Any operation on this forest that attempts to increase the number of roots beyond
	 * the limit set here will result in an {@link IndexOutOfBoundsException}.
	 *
	 * @param limit maximum number of forest roots
	 * @throws IndexOutOfBoundsException if the limit being set is less than 1 or attempts
	 *                                   to reduce the existing number of roots
	 */
	public void setLimit(int limit) {
		chkLimit(limit, limit);
		this.limit = limit;
	}

	private void chkLimit(int desired, int max) {
		if (desired < 1) throw new IndexOutOfBoundsException(ERR_BOUND1);
		if (desired < roots.size()) throw new IndexOutOfBoundsException(ERR_BOUND2);
		if (desired > max) throw new IndexOutOfBoundsException(ERR_BOUND3.formatted(max));
	}

	/**
	 * Returns a complete set of leaf terminals. Each leaf terminal node will have no
	 * children.
	 *
	 * @return tree terminal nodes
	 */
	public SparseSet<TreeNode<T>> terminals() {
		return dfsStream() //
				.filter(n -> !n.hasChildren()) //
				.collect(Collectors.toCollection(SparseSet<TreeNode<T>>::new));
	}

	/**
	 * Returns a complete set of tree terminal paths. Each path begins at a tree root and
	 * ends with a terminal node.
	 *
	 * @return terminal paths
	 */
	public LinkedList<TreePath<T>> paths() {
		return terminals().stream() //
				.map(t -> t.path()) //
				.collect(Collectors.toCollection(LinkedList<TreePath<T>>::new));
	}

	/**
	 * Returns a complete set of tree sub-paths where each sub-path terminal node matches
	 * the given criteria. The sub-paths may be overlapping, but are otherwise unique.
	 *
	 * @param match criteria for identifying path terminal nodes
	 * @return tree sub-paths
	 */
	public LinkedList<TreePath<T>> paths(Predicate<? super TreeNode<T>> match) {
		return dfsStream() //
				.filter(match) //
				.map(t -> t.path()) //
				.collect(Collectors.toCollection(LinkedList<TreePath<T>>::new));
	}

	// --------------------------------

	/**
	 * Installs a rooted, ordered node sequence that corresponds to the given path. New
	 * nodes are created and added to the tree as necessary.
	 *
	 * @param path tree path
	 * @return installed path
	 */
	public TreePath<T> install(TreePath<T> path) {
		return install(path.values());
	}

	public void installAll(Collection<TreePath<T>> paths) {
		paths.forEach(p -> install(p));
	}

	/**
	 * Installs a rooted, ordered node sequence that corresponds to the given ordered
	 * collection of values. New nodes are created and added to the tree as necessary to
	 * satisfy an equality correspondence between node values.
	 * <p>
	 * TODO: change signature to SequencedCollection (Java 21)
	 *
	 * @param values ordered values defining a rooted node sequence
	 * @return path representing the values
	 */
	public TreePath<T> install(Collection<T> values) {
		TreeUtil.chkSequenced(values);

		TreePath<T> path = new TreePath<>();
		if (values == null || values.isEmpty()) return path;

		LinkedList<T> queue = new LinkedList<>(values);
		T first = queue.pop();

		TreeNode<T> node = find(t -> t.equals(first));
		if (node == null) {
			node = new TreeNode<>(this, first);
			roots.add(node);
		}
		node.install(path, queue);
		return path;
	}

	/**
	 * Installs a rooted, ordered node sequence that corresponds to the given ordered
	 * collection of values. New nodes are created and added to the tree as necessary to
	 * satisfy an equivalence correspondence between node values as defined by the given
	 * predicate.
	 * <p>
	 * The predicate provides a custom comparison mechanism for defining when an existing
	 * node, having an existing value, would be equivalent to a candidate new node, having
	 * another value.
	 * <p>
	 * Where equivalent path nodes exist within the tree, the actual node values will not
	 * be changed to the corresponding {@code values} instance. Validate the desired path
	 * node values in the returned {@link TreePath}, representing the found install path.
	 * <p>
	 * TODO: change signature to SequencedCollection (Java 21)
	 *
	 * <pre>{@code
	 *	// provided <T>.id() defines equivalence
	 *	Collection<T> values = ???;
	 *	install(values, (t, u) -> t.id().equals(u.id()));
	 * }</pre>
	 *
	 * @param values ordered values defining a rooted node sequence
	 * @param match  node match criteria defining data value equivalence
	 * @return path representing the values
	 */
	public TreePath<T> install(Collection<T> values, BiPredicate<? super T, ? super T> match) {
		TreeUtil.chkSequenced(values);

		TreePath<T> path = new TreePath<>();
		if (values == null || values.isEmpty()) return path;

		LinkedList<T> queue = new LinkedList<>(values);
		T first = queue.pop();

		TreeNode<T> node = getChild(t -> match.test(t, first));
		if (node == null) {
			node = new TreeNode<>(this, first);
			roots.add(node);
		}
		node.install(path, queue, match);
		return path;
	}

	/**
	 * Changes the given node to contain the given data value. Removes and re-adds the
	 * changed node to correct child ordering that may depend on the data value.
	 *
	 * @param node  tree node to change
	 * @param value new data value
	 * @return the prior data value held by the node
	 */
	public T adjNodeValue(TreeNode<T> node, T value) {
		if (this != node.forest()) throw IllegalArgsEx.of(ERR_WRONG_FOREST);

		TreeNode<T> parent = node.parent();
		T prior;
		if (parent == null) {
			roots.remove(node);
			prior = node.set(value);
			roots.add(node);

		} else {
			parent.removeChild(node);
			prior = node.set(value);
			parent.addChild(node);
		}
		return prior;
	}

	/**
	 * Determine whether any existing child node has data that matches the given criteria.
	 * <p>
	 * The criteria can implement any function on {@code <? super T>} that returns a
	 * {@code boolean}.
	 *
	 * <pre>{@code
	 *	T data = ???;
	 *	hasChild(t -> t.equals(data));
	 * }</pre>
	 *
	 * @param match data match criteria
	 * @return {@code true} if a matched node exists
	 */
	public boolean hasChild(Predicate<? super T> match) {
		Assert.notNull(match);
		return roots().stream().anyMatch(n -> match.test(n.get()));
	}

	/**
	 * Finds the first tree root node containing data that matches the given criteria.
	 * <p>
	 * The criteria can implement any function on {@code <? super T>} that returns a
	 * {@code boolean}.
	 *
	 * @param match data match criteria
	 * @return found tree root node or {@code null} if not found
	 */
	public TreeNode<T> getChild(Predicate<? super T> match) {
		Assert.notNull(match);
		return roots().stream().filter(n -> match.test(n.get())).findFirst().orElse(null);
	}

	/**
	 * Determine whether any existing tree node has data that matches the given criteria.
	 * Progressively examines each subtree in root set; search terminates on first match.
	 * The criteria can use any boolean function implemented by {@code <? super T>}.
	 *
	 * <pre>{@code
	 *	T data = ???;
	 *	contains(t -> t.equals(data));
	 * }</pre>
	 *
	 * @param match data match criteria
	 * @return {@code true} if a matched node exists
	 */
	public boolean contains(Predicate<? super T> match) {
		return find(match) != null;
	}

	/**
	 * Returns the first node whose data matches the given criteria. Performs a
	 * depth-first search over the existing nodes, terminating on the first match.
	 * <p>
	 * The criteria can implement any function on {@code <? super T>} that returns a
	 * {@code boolean}.
	 *
	 * <pre>{@code
	 *	T data = ???;
	 *	return get(t -> t.equals(data));
	 * }</pre>
	 *
	 * @param match data match criteria
	 * @return first matched node
	 */
	public TreeNode<T> find(Predicate<? super T> match) {
		Assert.notNull(match);
		return dfsStream().filter(n -> match.test(n.get())).findFirst().orElse(null);
	}

	/**
	 * Returns the first node whose data matches the given criteria in the subtree under
	 * the given start node, inclusive. Performs a depth-first search over the existing
	 * nodes, terminating on the first match.
	 * <p>
	 * The criteria can implement any function on {@code <? super T>} that returns a
	 * {@code boolean}.
	 *
	 * <pre>{@code
	 *  TreeNode<T> node = ???
	 *	T data = ???;
	 *	return get(node, t -> t.equals(data));
	 * }</pre>
	 *
	 * @param start search start node
	 * @param match data match criteria
	 * @return first matched node
	 */
	public TreeNode<T> find(TreeNode<T> start, Predicate<? super T> match) {
		Assert.notNull(start, match);
		return dfsStream(start).filter(n -> match.test(n.get())).findFirst().orElse(null);
	}

	/**
	 * Returns the first node whose data matches the given criteria in the subtree under
	 * the given start node, inclusive. Performs a breadth-first beginning with the given
	 * start node and terminating on the first match found, no nodes remain, or the search
	 * depth level delta exceeds the given number of tree levels.
	 * <p>
	 * The criteria can implement any function on {@code <? super T>} that returns a
	 * {@code boolean}.
	 *
	 * <pre>{@code
	 * delta -1: unlimited levels
	 * delta +0: start node only
	 * delta +1: start and one level of children
	 * delta +n: start and n levels of children
	 * }</pre>
	 *
	 * @param start search start node
	 * @param delta number of levels, relative to the start node level, to search; set to
	 *              {@code <= 0} for unlimited
	 * @param match data match criteria
	 * @return first matched node
	 */
	public TreeNode<T> find(TreeNode<T> start, int delta, Predicate<? super T> match) {
		Assert.notNull(start, match);
		final LinkedList<TreeNode<T>> queue = new LinkedList<>();
		queue.add(start);
		int end = start.level() + delta;
		while (!queue.isEmpty()) {
			TreeNode<T> head = queue.pop();
			if (delta > 0 && end <= head.level()) return null;
			if (match.test(head.get())) return head;
			queue.addAll(head.children());
		}
		return null;
	}

	/**
	 * Determines whether a rooted path, as represented by the given values, exists in
	 * this tree.
	 *
	 * @param values root referenced sequence of tree node data values
	 * @return path {@code true} if the path exists
	 */
	public boolean hasPath(List<T> values) {
		TreePath<T> path = findPath(Policy.EXACT, values);
		return !path.isEmpty();
	}

	/**
	 * Return a {@link TreePath} representing the given values, starting from a root node
	 * and then as found subject to {@link Policy#EXACT}.
	 *
	 * @param values sequence of tree node values
	 * @return path found
	 */
	public TreePath<T> findPath(List<T> values) {
		return findPath(Policy.EXACT, values);
	}

	/**
	 * Return a {@link TreePath} corresponding to the given sequence of values, starting
	 * from a root node and then as found subject to the given {@link Policy}.
	 * <p>
	 * TODO: change signature to SequencedCollection (Java 21)
	 *
	 * @param policy selection policy
	 * @param values sequence of tree node values
	 * @return path found
	 */
	public TreePath<T> findPath(Policy policy, Collection<T> values) {
		TreeUtil.chkSequenced(values);

		TreePath<T> path = new TreePath<>();
		if (values != null && !values.isEmpty()) {
			LinkedList<T> queue = new LinkedList<>(values);
			T root = queue.pop();
			TreeNode<T> node = getChild(d -> d.equals(root));
			if (node != null) {
				path.addLast(node);
				path = node.findPath(path, policy, queue);
			} else {
				path.skipped(values);
			}
		}
		return path;
	}

	/**
	 * Determines whether a rooted path, corresponding to the given values, exists in this
	 * tree.
	 * <p>
	 * TODO: change signature to SequencedCollection (Java 21)
	 *
	 * @param values root referenced sequence of data values
	 * @param match  criteria for matching a <T> node with a <U> value
	 * @return path {@code true} if the path exists
	 */
	public boolean hasPath(Collection<T> values, BiPredicate<? super T, ? super T> match) {
		TreeUtil.chkSequenced(values);
		TreePath<T> path = findPath(Policy.EXACT, values, match);
		return !path.isEmpty();
	}

	/**
	 * Return a {@link TreePath} corresponding to the given sequence of values, starting
	 * from a root node and then as found subject to the given {@link Policy}.
	 * <p>
	 * TODO: change signature to SequencedCollection (Java 21)
	 *
	 * <pre>{@code
	 *	Forest<FmtKeyOp> f = ...;	// t=>FmtKeyOp
	 *	List<FmtKey> ks = ...;		// u=>FmtKey
	 *	TreePath<FmtKeyOp> p = tree.findPath(Policy.EXACT, ks, (t, u) -> t.key().equals(u));
	 * }</pre>
	 *
	 * @param policy selection policy
	 * @param values sequence of values
	 * @param match  criteria for matching a <T> node with a <U> value
	 * @return path found
	 */
	public TreePath<T> findPath(Policy policy, Collection<T> values,
			BiPredicate<? super T, ? super T> match) {

		TreeUtil.chkSequenced(values);
		Assert.notNull(match);

		TreePath<T> path = new TreePath<>();
		if (values != null && !values.isEmpty()) {
			LinkedList<T> queue = new LinkedList<>(values);
			T root = queue.pop();
			TreeNode<T> node = getChild(t -> match.test(t, root));

			if (node != null) {
				path.addLast(node);
				path = node.findPath(path, policy, queue, match);
			} else {
				path.skipped(values);
			}
		}
		return path;
	}

	/**
	 * Perform a depth-first tree walk to apply the given visitor function to each node in
	 * this tree. The visitor must return {@code true} to continue the walk, or
	 * {@code false} to terminate.
	 *
	 * @param visitor visitor function; returns {@code true} to continue or {@code false}
	 *                to terminate the walk
	 */
	public void dfsWalk(Function<TreeNode<T>, Boolean> visitor) {
		// int cnt = 0;
		// dfsStream().filter(n -> out(cnt, n)).allMatch(visitor::apply);

		dfsStream().allMatch(visitor::apply);
	}

	// private boolean out(int cnt, TreeNode<T> n) {
	// System.out.println("Stream [%d] %s".formatted(cnt++, n));
	// return true;
	// }

	/**
	 * Perform a depth-first tree walk to apply the given visitor function to each node in
	 * this tree starting with the given node. The visitor must return {@code true} to
	 * continue the walk, or {@code false} to terminate.
	 *
	 * @param visitor visitor function; returns {@code true} to continue or {@code false}
	 *                to terminate the walk
	 */
	public void dfsWalk(Function<TreeNode<T>, Boolean> visitor, TreeNode<T> start) {
		dfsStream(start).allMatch(visitor::apply);
	}

	/**
	 * Returns a depth first tree node stream.
	 *
	 * @return tree node stream
	 */
	public Stream<TreeNode<T>> dfsStream() {
		return stream(dfsIterator());
	}

	/**
	 * Returns a depth first tree node stream starting with the given node.
	 *
	 * @param start search start node
	 * @return tree node stream
	 */
	public Stream<TreeNode<T>> dfsStream(TreeNode<T> start) {
		return stream(dfsIterator(start));
	}

	/**
	 * Perform a breadth-first tree walk to apply the given visitor function to each node
	 * in this tree. The visitor must return {@code true} to continue the walk, or
	 * {@code false} to terminate.
	 *
	 * @param visitor visitor function; returns {@code true} to continue or {@code false}
	 *                to terminate the walk
	 */
	public void bfsWalk(Function<TreeNode<T>, Boolean> visitor) {
		bfsStream().allMatch(visitor::apply);
	}

	/**
	 * Returns a breadth first tree node stream.
	 *
	 * @return tree node stream
	 */
	public Stream<TreeNode<T>> bfsStream() {
		return stream(bfsIterator());
	}

	/**
	 * Returns a breadth first tree node stream starting with the given node.
	 *
	 * @param start search start node
	 * @return tree node stream
	 */
	public Stream<TreeNode<T>> bfsStream(TreeNode<T> start) {
		return stream(bfsIterator(start));
	}

	/**
	 * Returns a depth first search iterator through all of the tree nodes.
	 *
	 * @return dfs iterator
	 */
	private Iterator<TreeNode<T>> dfsIterator() {
		return dfsIterator(roots());
	}

	/**
	 * Returns a depth first search iterator through the tree nodes starting with the
	 * given node.
	 *
	 * @param start search start node
	 * @return dfs iterator
	 */
	private Iterator<TreeNode<T>> dfsIterator(TreeNode<T> start) {
		return dfsIterator(List.of(start));
	}

	/**
	 * Returns a breadth first search iterator through all of the tree nodes.
	 *
	 * @return bfs iterator
	 */
	private Iterator<TreeNode<T>> bfsIterator() {
		return bfsIterator(roots());
	}

	/**
	 * Returns a breadth first search iterator through the tree nodes starting with the
	 * given node.
	 *
	 * @param start search start node
	 * @return bfs iterator
	 */
	private Iterator<TreeNode<T>> bfsIterator(TreeNode<T> start) {
		return bfsIterator(List.of(start));
	}

	private Iterator<TreeNode<T>> dfsIterator(Collection<TreeNode<T>> nodes) {
		final LinkedList<TreeNode<T>> queue = new LinkedList<>();
		queue.addAll(nodes);

		return new Iterator<>() {

			public boolean hasNext() {
				return !queue.isEmpty();
			}

			public TreeNode<T> next() {
				TreeNode<T> node = queue.pop();
				queue.addAll(0, node.children());
				return node;
			}
		};
	}

	private Iterator<TreeNode<T>> bfsIterator(Collection<TreeNode<T>> nodes) {
		final LinkedList<TreeNode<T>> queue = new LinkedList<>();
		queue.addAll(nodes);

		return new Iterator<>() {

			public boolean hasNext() {
				return !queue.isEmpty();
			}

			public TreeNode<T> next() {
				TreeNode<T> node = queue.pop();
				queue.addAll(node.children());
				return node;
			}
		};
	}

	/**
	 * Create a tree node stream from the given iterator.
	 *
	 * @param iterator stream order defining iterator
	 * @return tree node stream
	 */
	private Stream<TreeNode<T>> stream(Iterator<TreeNode<T>> iterator) {
		Iterable<TreeNode<T>> iterable = () -> iterator;
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	// --------------------------------

	/**
	 * Returns {@code true} if this tree contains no nodes.
	 *
	 * @return {@code true} if this tree is empty
	 */

	public boolean isEmpty() {
		return roots().isEmpty();
	}

	/**
	 * Return the size (node count) of this tree.
	 *
	 * @return count of contained nodes
	 */

	public int size() {
		return (int) dfsStream().count();
	}

	/**
	 * Return the size (node count) of the subtree starting with the given node,
	 * inclusive.
	 *
	 * @param node subtree apex node
	 * @return count of contained nodes
	 */
	public int size(TreeNode<T> node) {
		if (node == null) return 0;
		return (int) dfsStream(node).count();
	}

	// --------------------------------

	/** Return the tree instance display name. */

	public String label() {
		return (String) style().get(DotAttr.LABEL);
	}

	/**
	 * Sets a new tree instance display name.
	 *
	 * @param name new tree instance display name
	 */

	public void setLabel(String name) {
		if (name == null) {
			name = getClass().getSimpleName();
		}
		style().put(DotAttr.LABEL, name);
	}

	/**
	 * Returns the {@code DotStyle} store for this tree. Creates and adds an
	 * {@code ON#GRAPHS} default category {@code DotStyle} store, if a store does not
	 * exist. Also defines sane default styling values for this tree.
	 *
	 * @return the dot style store
	 */

	public DotStyle style() {
		if (style == null) {
			style = new DotStyle(ON.GRAPHS);
			style.putIfAbsent(DotAttr.LABEL, ON.GRAPHS, getClass().getSimpleName());
			style.putIfAbsent(DotAttr.FONTCOLOR, ON.GRAPHS, DotStyle.BLACK);
			style.putIfAbsent(DotAttr.FONTNAME, ON.GRAPHS, DotStyle.FONTS);
			style.putIfAbsent(DotAttr.FONTSIZE, ON.GRAPHS, 14);

			style.putIfAbsent(DotAttr.FONTCOLOR, ON.CLUSTERS, DotStyle.BLACK);
			style.putIfAbsent(DotAttr.FONTNAME, ON.CLUSTERS, DotStyle.FONTS);
			style.putIfAbsent(DotAttr.FONTSIZE, ON.CLUSTERS, 12);

			style.putIfAbsent(DotAttr.FONTCOLOR, ON.NODES, DotStyle.BLACK);
			style.putIfAbsent(DotAttr.FONTNAME, ON.NODES, DotStyle.FONTS);
			style.putIfAbsent(DotAttr.FONTSIZE, ON.NODES, 10);

			style.putIfAbsent(DotAttr.FONTCOLOR, ON.EDGES, DotStyle.BLACK);
			style.putIfAbsent(DotAttr.FONTNAME, ON.EDGES, DotStyle.FONTS);
			style.putIfAbsent(DotAttr.FONTSIZE, ON.EDGES, 10);
		}
		return style;
	}

	// --------------------------------

	public void clear() {
		roots.clear();
	}

	// @VisibleForTesting
	// void reset() {
	// TreeNode.reset();
	// }
}
