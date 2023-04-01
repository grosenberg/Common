package net.certiv.common.graph;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.stores.UniqueDeque;

public class EdgeSet<N extends Node<N, E>, E extends Edge<N, E>> {

	/** key=edge; value=distal node */
	private final LinkedHashMap<E, N> forward = new LinkedHashMap<>();
	/** key=distal node; value=edge */
	private final LinkedHashMap<N, E> reverse = new LinkedHashMap<>();

	private final Sense dir;

	public EdgeSet(Sense dir) {
		this.dir = dir;
	}

	public void add(E edge) {
		N distal = edge.other(dir);
		forward.put(edge, distal);
		reverse.put(distal, edge);
	}

	public boolean remove(E edge) {
		N n = forward.remove(edge);
		reverse.remove(edge.other(dir));
		return n != null;
	}

	public void clear() {
		forward.clear();
	}

	/**
	 * Returns {@code true} if any edge in this set contains the given node as its distal
	 * node.
	 */
	public boolean isAdjacent(N node) {
		return reverse.containsKey(node);
	}

	/**
	 * Returns the set of adjacent nodes qualified by the given filter, or all adjacent
	 * nodes if the filter is {@code null}.
	 */
	public UniqueDeque<N> adjacent(Predicate<? super N> filter) {
		if (filter == null) {
			return new UniqueDeque<>(reverse.keySet()).unmodifiable();
		}

		UniqueDeque<N> result = new UniqueDeque<>();
		for (N n : reverse.keySet()) {
			if (filter.test(n)) {
				result.add(n);
			}
		}
		return result.unmodifiable();
	}

	/**
	 * Returns the set of edges qualified by the given filter on the set of edges, or all
	 * edges if the filter is {@code null}.
	 */
	public UniqueDeque<E> edges(Predicate<? super E> filter) {
		if (filter == null) {
			return new UniqueDeque<>(forward.keySet()).unmodifiable();
		}

		UniqueDeque<E> result = new UniqueDeque<>();
		for (E e : forward.keySet()) {
			if (filter.test(e)) {
				result.add(e);
			}
		}
		return result.unmodifiable();
	}
}
