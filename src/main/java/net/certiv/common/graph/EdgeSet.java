package net.certiv.common.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.certiv.common.graph.Edge.Sense;

public class EdgeSet<N extends Node<N, E>, E extends Edge<N, E>> {

	// key=edge; value=distal node
	private final LinkedHashMap<E, N> edgeSet = new LinkedHashMap<>();
	// key=distal node; value=edge
	private final LinkedHashMap<N, E> reverse = new LinkedHashMap<>();

	private final Sense dir;

	public EdgeSet(Sense dir) {
		this.dir = dir;
	}

	public void add(E edge) {
		edgeSet.put(edge, edge.other(dir));
		reverse.put(edge.other(dir), edge);
	}

	public boolean remove(E edge) {
		N n = edgeSet.remove(edge);
		reverse.remove(edge.other(dir));
		return n != null;
	}

	public void clear() {
		edgeSet.clear();
	}

	/**
	 * Returns {@code true} if any edge in this set contains the given node as its distal
	 * node.
	 */
	public boolean isAdjacent(N node) {
		return edgeSet.containsValue(node);
	}

	/**
	 * Returns the set of adjacent nodes qualified by the given filter, or all adjacent
	 * nodes if the filter is {@code null}.
	 */
	public Set<N> adjacent(Predicate<? super N> filter) {
		if (filter == null) {
			return Collections.unmodifiableSet(reverse.keySet());
		}

		LinkedHashSet<N> result = new LinkedHashSet<>();
		for (N n : reverse.keySet()) {
			if (filter.test(n)) {
				result.add(n);
			}
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns the set of edges qualified by the given filter on the set of edges, or all
	 * edges if the filter is {@code null}.
	 */
	public Set<E> edges(Predicate<? super E> filter) {
		if (filter == null) {
			return Collections.unmodifiableSet(edgeSet.keySet());
		}

		LinkedHashSet<E> result = new LinkedHashSet<>();
		for (E e : edgeSet.keySet()) {
			if (filter.test(e)) {
				result.add(e);
			}
		}
		return Collections.unmodifiableSet(result);
	}
}
