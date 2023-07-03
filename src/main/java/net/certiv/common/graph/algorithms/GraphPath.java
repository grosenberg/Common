package net.certiv.common.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Node;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;

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

	/** Path weight property key */
	private final String key;

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
	 * Reduces the contained path by:
	 * <ol>
	 * <li>remove self-cycles
	 * </ol>
	 */
	public void reduce() {
		edges().forEach(e -> { if (e.cyclic()) remove(e); });
	}

	/**
	 * Determine if the given path shares any edges in common with this path.
	 *
	 * @param path another path
	 * @return {@code true} if the paths intersect
	 */
	public boolean intersect(GraphPath<N, E> path) {
		return edges().dup().removeAll(path.edges());
	}

	/**
	 * Finds the next edges in this path for the given direction from the given node.
	 *
	 * @param dir  a sense direction
	 * @param node the target node
	 * @return the connecting edges
	 */
	public UniqueList<E> query(Sense dir, N node) {
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
	 * @return fist shortest path edges, ordered head to target
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

	public void add(int idx, E e) {
		if (!index.contains(e)) {
			edges.add(idx, e);
			index.add(e);
			idxBeg.put(e.beg(), e);
			idxEnd.put(e.end(), e);
		}
	}

	public void addFirst(E e) {
		if (!index.contains(e)) {
			edges.addFirst(e);
			index.add(e);
			idxBeg.put(e.beg(), e);
			idxEnd.put(e.end(), e);
		}
	}

	public void addLast(E e) {
		if (!index.contains(e)) {
			edges.addLast(e);
			index.add(e);
			idxBeg.put(e.beg(), e);
			idxEnd.put(e.end(), e);
		}
	}

	public void addAll(Collection<? extends E> c) {
		c.forEach(e -> addLast(e));
	}

	public void addAll(int idx, Collection<? extends E> c) {
		if (idx == edges.size()) {
			addAll(c);
		} else {
			List<E> l = new ArrayList<>(c);
			for (int x = 0; x < c.size() + idx; x++) {
				add(x + idx, l.get(idx));
			}
		}
	}

	public E get(int idx) {
		return edges.get(idx);
	}

	public E peekFirst() {
		return edges.peekFirst();
	}

	public E peekLast() {
		return edges.peekLast();
	}

	public List<E> subList(int from, int to) {
		return edges().subList(from, to);
	}

	public E set(int idx, E e) {
		E prior = edges.set(idx, e);
		if (prior != null && !prior.equals(e)) {
			index.remove(prior);
			idxBeg.remove(prior.beg(), prior);
			idxEnd.remove(prior.end(), prior);

			index.add(e);
			idxBeg.put(e.beg(), e);
			idxEnd.put(e.end(), e);
		}
		return prior;
	}

	public boolean remove(E e) {
		if (!index.contains(e)) return false;
		edges.remove(e);
		index.remove(e);
		idxBeg.remove(e.beg(), e);
		idxEnd.remove(e.end(), e);
		return true;
	}

	public E remove(int idx) {
		E e = edges.get(idx);
		remove(e);
		return e;
	}

	public E removeFirst() {
		return remove(0);
	}

	public E removeLast() {
		return remove(edges.size() - 1);
	}

	public boolean removeAll(Collection<? extends E> c) {
		return c.stream().allMatch(e -> remove(e));
	}

	public boolean contains(E e) {
		return index.contains(e);
	}

	public boolean contains(N n) {
		return containsBeg(n) || containsEnd(n);
	}

	public boolean containsBeg(N n) {
		return idxBeg.containsKey(n);
	}

	public boolean containsEnd(N n) {
		return idxEnd.containsKey(n);
	}

	public boolean containsTerminal(N n) {
		return terminals.contains(n);
	}

	public boolean containsAll(Collection<? extends E> c) {
		return index.containsAll(c);
	}

	public boolean retainAll(Collection<? extends E> c) {
		return edges.retainAll(c);
	}

	public int indexOf(E e) {
		return edges.indexOf(e);
	}

	public int lastIndexOf(E e) {
		return edges.lastIndexOf(e);
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
		return edges.toString();
	}
}
