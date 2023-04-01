package net.certiv.common.graph;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueDeque;

public class EdgeSet<N extends Node<N, E>, E extends Edge<N, E>> {

	/** 1:1 map: key=edge; value=distal node */
	private final LinkedHashMap<E, N> forward = new LinkedHashMap<>();
	/** 1:n map: key=distal node; value=edge */
	private final LinkedHashList<N, E> reverse = new LinkedHashList<>();

	private final Sense dir;

	public EdgeSet(Sense dir) {
		this.dir = dir;
		reverse.setUniqueValued(true);
	}

	public void add(E edge) {
		N distal = edge.other(dir);
		forward.put(edge, distal);
		reverse.put(distal, edge);
	}

	public boolean remove(E edge) {
		N n = forward.remove(edge);
		if (n != null) reverse.remove(n, edge);
		return n != null;
	}

	/**
	 * Returns {@code true} if any edge in this set contains the given node as its distal
	 * node.
	 */
	public boolean isAdjacent(N node) {
		return reverse.containsKey(node);
	}

	public UniqueDeque<N> adjacent() {
		return new UniqueDeque<>(reverse.keys()).unmodifiable();
	}

	/**
	 * Returns the set of adjacent nodes qualified by the given filter, or all adjacent
	 * nodes if the filter is {@code null}.
	 */
	public UniqueDeque<N> adjacent(Predicate<? super N> filter) {
		if (filter == null) return adjacent();
		return reverse.keys().stream() //
				.filter(filter) //
				.collect(Collectors.toCollection(UniqueDeque::new)) //
				.unmodifiable();
	}

	public boolean hasEdge(E edge) {
		return forward.containsKey(edge);
	}

	public UniqueDeque<E> edges() {
		return new UniqueDeque<>(forward.keySet()).unmodifiable();
	}

	public LinkedList<E> edges(N node) {
		LinkedList<E> edges = reverse.get(node);
		return edges != null ? edges : new LinkedList<>();
	}

	/**
	 * Returns the set of edges qualified by the given filter on the set of edges, or all
	 * edges if the filter is {@code null}.
	 */
	public UniqueDeque<E> edges(Predicate<? super E> filter) {
		if (filter == null) return edges();
		return edges().stream() //
				.filter(filter) //
				.collect(Collectors.toCollection(UniqueDeque::new)) //
				.unmodifiable();
	}

	public void clear() {
		forward.clear();
		reverse.clear();
	}

	@Override
	public String toString() {
		return String.format("%s %s", dir, forward.keySet());
	}
}
