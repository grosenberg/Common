package net.certiv.common.graph;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.TreeMapSet;
import net.certiv.common.stores.UniqueList;

/**
 * Default EdgeSet.
 */
public class EdgeSet<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements IEdgeSet<I, N, E> {

	/** 1:1 map: key=edge; value=distal node */
	private final TreeMap<E, N> forward;
	/** 1:n map: key=distal node; value=edge */
	private final TreeMapSet<N, E> reverse;

	private final Sense dir;
	private N node;

	/**
	 * Construct a default edge set. Relies on the natural comparable ordering of nodes
	 * and edges.
	 *
	 * @param dir edge set direction
	 */
	public EdgeSet(Sense dir) {
		this(dir, null, null);
	}

	/**
	 * Construct a default edge set. Ordering of nodes and edges is dependent on the given
	 * comparators. A {@code null} comparator is equivalent to a natural ordering
	 * comparator.
	 *
	 * @param dir   edge set direction
	 * @param edges edge comparator
	 * @param nodes node comparator
	 */
	public EdgeSet(Sense dir, Comparator<? super E> edges, Comparator<? super N> nodes) {
		this.dir = dir;
		forward = new TreeMap<>(edges);
		reverse = new TreeMapSet<>(nodes, edges);
	}

	public Sense sense() {
		return dir;
	}

	@Override
	public N owner() {
		return node;
	}

	@Override
	public void setOwner(N node) {
		this.node = node;
	}

	@Override
	public void add(E edge) {
		N distal = edge.other(dir);
		forward.put(edge, distal);
		reverse.put(distal, edge);
	}

	@Override
	public boolean remove(E edge) {
		N n = forward.remove(edge);
		if (n != null) return reverse.remove(n, edge);
		return false;
	}

	@Override
	public boolean isAdjacent(N node) {
		return reverse.containsKey(node);
	}

	@Override
	public UniqueList<N> adjacent() {
		return new UniqueList<>(reverse.keys()).unmodifiable();
	}

	@Override
	public UniqueList<N> adjacent(Predicate<? super N> filter) {
		if (filter == null) return adjacent();
		return reverse.keys().stream() //
				.filter(filter) //
				.collect(Collectors.toCollection(UniqueList::new)) //
				.unmodifiable();
	}

	@Override
	public boolean hasEdge(E edge) {
		return forward.containsKey(edge);
	}

	@Override
	public UniqueList<E> edges() {
		return new UniqueList<>(forward.keySet()).unmodifiable();
	}

	@Override
	public UniqueList<E> edges(N node) {
		Set<E> edges = reverse.get(node);
		return edges != null ? new UniqueList<>(edges) : new UniqueList<>();
	}

	@Override
	public UniqueList<E> edges(Predicate<? super E> filter) {
		if (filter == null) return edges();
		return edges().stream() //
				.filter(filter) //
				.collect(Collectors.toCollection(UniqueList::new)) //
				.unmodifiable();
	}

	@Override
	public int size() {
		return forward.size();
	}

	@Override
	public void clear() {
		forward.clear();
		reverse.clear();
	}

	@Override
	public String toString() {
		return String.format("%s %s", dir, forward.keySet());
	}
}
