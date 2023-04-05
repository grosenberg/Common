package net.certiv.common.graph;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;

/**
 * Default embedded EdgeSet.
 */
public class EdgeSet<N extends Node<N, E>, E extends Edge<N, E>> implements IEdgeSet<N, E> {

	/** 1:1 map: key=edge; value=distal node */
	private final LinkedHashMap<E, N> forward = new LinkedHashMap<>();
	/** 1:n map: key=distal node; value=edge */
	private final LinkedHashList<N, E> reverse = new LinkedHashList<>();

	private final Sense dir;

	public EdgeSet(Sense dir) {
		this.dir = dir;
		reverse.setUniqueValued(true);
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
		if (n != null) reverse.remove(n, edge);
		return n != null;
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
		LinkedList<E> edges = reverse.get(node);
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
