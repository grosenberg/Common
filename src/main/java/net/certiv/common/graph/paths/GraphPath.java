package net.certiv.common.graph.paths;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Maths;
import net.certiv.common.util.Strings;

/**
 * Collects a path of edges tracing from a head node to some set of terminal nodes.
 *
 * <pre>
 *       D-E
 *      /   \
 *  A--B--C--F--G--H
 *         \
 *          I--J--K
 * </pre>
 */
public class GraphPath<N extends Node<N, E>, E extends Edge<N, E>> {

	public static final String DKEY_PREFIX = "DKEY-";
	public static final GraphException ERR_DKEY = GraphEx.of("No weighted distance key/value for edge: %s");

	/** path edges */
	private final UniqueList<E> edges = new UniqueList<>();
	/** path terminals */
	private final UniqueList<N> terminals = new UniqueList<>();

	/** Index key=edges */
	private final LinkedHashSet<E> index = new LinkedHashSet<>();
	/** Index key=begin node; value=edges */
	private final LinkedHashList<N, E> idxBeg = new LinkedHashList<>();
	/** Index key=end node; value=edges */
	private final LinkedHashList<N, E> idxEnd = new LinkedHashList<>();

	/** Path weight property key: for min total weighted distance */
	private final String key;

	/**
	 * Make a pseudo random property key for use in storing the min total weighted
	 * distance on each edge in this path.
	 *
	 * @param bound the positive upper bound
	 * @param width the resultant string width
	 * @return a next positive pseudorandom number
	 * @throws IllegalArgumentException if parameters are not positive
	 * @see Maths#nextRandomFilled(int, int)
	 */
	public static String makeDKey(int bound, int width) {
		return DKEY_PREFIX + Maths.nextRandomFilled(bound, width);
	}

	/** Construct an instance with a random path weight property key. */
	public GraphPath() {
		this(makeDKey(999, 4));
	}

	/** Construct an instance with the given path weight property key. */
	public GraphPath(String key) {
		this.key = key;
	}

	/** @return the weighted distance property key */
	public String getDistanceKey() {
		return key;
	}

	/** @return head node (begin node of first edge) of this path */
	public N head() {
		return !edges.isEmpty() ? edges.peek().beg() : null;
	}

	/** @return all nodes included within this path */
	public UniqueList<N> nodes() {
		UniqueList<N> nodes = new UniqueList<>(idxBeg.keys());
		nodes.addAll(idxEnd.keys());
		return nodes.unmodifiable();
	}

	/** @return all edges included within this path */
	public UniqueList<E> edges() {
		return new UniqueList<>(edges).unmodifiable();
	}

	/**
	 * Reduces this path by:
	 * <ol>
	 * <li>removing all self-cycles
	 * </ol>
	 */
	public void reduce() {
		edges().forEach(e -> { if (e.cyclic()) remove(e); });
	}

	/**
	 * Finds the next edges in this path for the given direction from the given node.
	 *
	 * @param dir  a sense direction
	 * @param node the target node
	 * @return the connecting edges
	 */
	public UniqueList<E> adjacent(Sense dir, N node) {
		switch (dir) {
			case IN:
				return new UniqueList<>(idxBeg.get(node)).unmodifiable();

			case OUT:
				return new UniqueList<>(idxEnd.get(node)).unmodifiable();

			case BOTH:
			default:
				UniqueList<E> b = new UniqueList<>(idxBeg.get(node));
				b.addAll(idxEnd.get(node));
				return b.unmodifiable();
		}
	}

	/**
	 * Add an identified path terminal node.
	 *
	 * @param node path terminal node
	 */
	public void addTerminal(N node) {
		terminals.addLast(node);
	}

	/**
	 * Remove a path terminal node.
	 *
	 * @param node path terminal node
	 */
	public boolean removeTerminal(N node) {
		return terminals.remove(node);
	}

	/**
	 * Return the path terminal nodes.
	 *
	 * @return the path terminal nodes
	 */
	public UniqueList<N> terminals() {
		return new UniqueList<>(terminals).unmodifiable();
	}

	/**
	 * Finds the first shortest path from the given node to the head of this path.
	 * <p>
	 * Walks this path backwards, seeking the first found minimum weighted path to the
	 * head.
	 * <p>
	 * Somewhat related to the approach presented in {@code "Effcient Top-k
	 * Shortest-Path Distance Queries on Large Networks
	 * by Pruned Landmark Labeling", Akiba et al.}
	 *
	 * @return first shortest path edge list, ordered head to target
	 */
	public LinkedList<E> shortestPathTo(N target) {
		LinkedList<E> minPath = new LinkedList<>();

		HashSet<N> settled = new HashSet<>();
		settled.add(target);

		E parent = minParent(settled, target);
		while (parent != null) {
			minPath.add(parent);
			parent = minParent(settled, parent.beg());
		}
		Collections.reverse(minPath);
		return minPath;
	}

	/**
	 * @param settled traversed nodes
	 * @param node    end node of parental edges to inspect
	 * @return minimum weight parental edge
	 */
	private E minParent(HashSet<N> settled, N node) {
		LinkedList<E> edges = idxEnd.get(node);
		if (edges == null || edges.isEmpty()) return null;

		E minEdge = null;
		double minWeight = Double.POSITIVE_INFINITY;

		for (E edge : edges) {
			N beg = edge.beg();
			if (!edge.cyclic() && index.contains(edge) && !settled.contains(beg)) {
				Assert.isTrue(edge.has(key));
				settled.add(beg);
				double weight = edge.get(key);
				if (weight < minWeight) {
					minEdge = edge;
					minWeight = weight;
				}
			}
		}
		return minEdge;
	}

	// ---- Delegates -----------------

	/**
	 * Add the given edge to the beginning of this path.
	 *
	 * @param edge the edge to add
	 */
	public void addFirst(E edge) {
		if (!index.contains(edge)) {
			edges.addFirst(edge);
			index.add(edge);
			idxBeg.put(edge.beg(), edge);
			idxEnd.put(edge.end(), edge);
			updateMinWeight(edge);
			adjTerminals(edge.end());
		}
	}

	/**
	 * Add the given edge to the end of this path.
	 *
	 * @param edge the edge to add
	 */
	public void addLast(E edge) {
		if (!index.contains(edge)) {
			edges.addLast(edge);
			index.add(edge);
			idxBeg.put(edge.beg(), edge);
			idxEnd.put(edge.end(), edge);
			updateMinWeight(edge);
			adjTerminals(edge.end());
		}
	}

	/**
	 * Add all of the edges between the given nodes to the end of this path.
	 *
	 * @param beg a begin node
	 * @param end an end node
	 */
	public void addAll(N beg, N end) {
		for (E edge : beg.to(end)) {
			addLast(edge);
		}
	}

	/**
	 * Add all of the given edges to the end of this path.
	 *
	 * @param edges the edges to add
	 */
	public void addAll(Collection<? extends E> edges) {
		edges.forEach(e -> addLast(e));
	}

	private void adjTerminals(N node) {
		node.adjacent(Sense.IN, n -> contains(n)).forEach(n -> removeTerminal(n));
		if (!hasNextInPath(node)) addTerminal(node);
	}

	private double updateMinWeight(E edge) {
		double w = edge.weight();
		w = supra(edge.beg()) + w;
		edge.put(key, w);
		return w;
	}

	/** @return the min edge weight of any in-path parent edge, or 0 */
	private double supra(N beg) {
		UniqueList<E> edges = beg.edges(Sense.IN, e -> !e.cyclic() && contains(e));
		if (edges.isEmpty()) return 0.0;

		double min = Double.POSITIVE_INFINITY;
		for (E edge : edges) {
			Double val = edge.get(key);
			Assert.notNull(ERR_DKEY.on(edge), val);
			min = Math.min(min, val);
		}
		return min;
	}

	public E peekFirst() {
		return edges.peekFirst();
	}

	public E peekLast() {
		return edges.peekLast();
	}

	public boolean remove(E edge) {
		if (!index.contains(edge)) return false;
		edges.remove(edge);
		index.remove(edge);
		idxBeg.remove(edge.beg(), edge);
		idxEnd.remove(edge.end(), edge);

		terminals.remove(edge.beg());
		terminals.remove(edge.end());
		edges.forEach(e -> { if (!hasNextInPath(e.end())) addTerminal(e.end()); });

		return true;
	}

	public boolean remove(Collection<? extends E> edges) {
		return edges.stream().allMatch(e -> remove(e));
	}

	public E removeFirst() {
		E e = edges.get(0);
		if (e != null) remove(e);
		return e;
	}

	public E removeLast() {
		E e = edges.get(edges.size() - 1);
		if (e != null) remove(e);
		return e;
	}

	/**
	 * Determine whether the given node has a next node in this path. Nominally used to
	 * determine if the given node should be recorded as a terminal or to verify if a
	 * terminal is correctly identified.
	 *
	 * @param node the node to check
	 * @return {@code true} if this path has any in-path next node
	 */
	public boolean hasNextInPath(N node) {
		return !node.adjacent(Sense.OUT, nxt -> contains(nxt)).isEmpty();
	}

	/**
	 * Returns whether this path contains the given edge.
	 *
	 * @param edge the edge to check
	 * @return {@code true} if this path contains the given edge
	 */
	public boolean contains(E edge) {
		return index.contains(edge);
	}

	/**
	 * Returns whether this path contains the given node.
	 *
	 * @param node the node to check
	 * @return {@code true} if this path contains the given node
	 */
	public boolean contains(N node) {
		return containsBeg(node) || containsEnd(node);
	}

	/**
	 * Returns whether this path contains an edge that begins with the given node.
	 *
	 * @param node the node to check
	 * @return {@code true} if this path contains an edge that begins with the given node
	 */
	public boolean containsBeg(N node) {
		return idxBeg.containsKey(node);
	}

	/**
	 * Returns whether this path contains an edge that ends with the given node.
	 *
	 * @param node the node to check
	 * @return {@code true} if this path contains an edge that ends with the given node
	 */
	public boolean containsEnd(N node) {
		return idxEnd.containsKey(node);
	}

	/**
	 * Returns whether the given node is a path terminal node.
	 *
	 * @param node the node to check
	 * @return {@code true} if the given node is a path terminal node
	 */

	public boolean containsTerminal(N node) {
		return terminals.contains(node);
	}

	/**
	 * Returns whether this path contains all of the given nodes.
	 *
	 * @param edges the edges to check
	 * @return {@code true} if this path contains the given nodes
	 */
	public boolean contains(Collection<? extends E> edges) {
		return index.containsAll(edges);
	}

	/**
	 * Determine if this path shares any edges in common with the given path.
	 *
	 * @param path another path
	 * @return {@code true} if the paths intersect
	 */
	public boolean intersects(GraphPath<N, E> path) {
		return edges.stream().anyMatch(e -> path.contains(e));
	}

	/**
	 * Retains only the edges in this path that are contained in the given edge
	 * collection.
	 *
	 * @param edges the edges to be retained
	 * @return {@code true} if this path changed
	 */
	public boolean retainAll(Collection<? extends E> edges) {
		UniqueList<E> delta = edges().dup();
		delta.removeAll(edges);
		if (delta.isEmpty()) return false;
		delta.forEach(e -> remove(e));
		return true;
	}

	public boolean valid() {
		return head() != null;
	}

	public boolean isEmpty() {
		return edges.isEmpty();
	}

	/**
	 * Removes the instance specific weighted distance property keys from all included
	 * edges and clears the path and indexes.
	 */
	public void clear() {
		edges().forEach(n -> n.put(key, null));
		edges.clear();
		terminals.clear();
		index.clear();
		idxBeg.clear();
		idxEnd.clear();
	}

	/**
	 * Returns the number of edges in this path.
	 *
	 * @return the number of elements in this edges
	 */
	public int size() {
		return edges.size();
	}

	public Iterator<E> iterator() {
		return edges.iterator();
	}

	public Iterator<E> descendingIterator() {
		return edges.descendingIterator();
	}

	public Stream<E> stream() {
		return edges.stream();
	}

	public GraphPath<N, E> dup() {
		GraphPath<N, E> cp = new GraphPath<>(key);
		cp.addAll(edges);
		terminals.forEach(t -> cp.addTerminal(t));
		return cp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(edges);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GraphPath<?, ?> other = (GraphPath<?, ?>) obj;
		return Objects.equals(edges, other.edges);
	}

	@Override
	public String toString() {
		N head = head();
		return String.format("%s %s", head != null ? head : Strings.EMPTY, edges);
	}
}
