package net.certiv.common.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.stores.props.Props;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> extends Props
		implements ITransform<N, E> {

	public static final String GRAPH_NAME = "GraphName";

	static final Counter CTR = new Counter();

	/** Unique numerical graph identifier */
	public final long _gid;

	/** All graph nodes. */
	private final LinkedHashSet<N> nodes = new LinkedHashSet<>();

	/** Graph modification control lock. */
	private final ReentrantLock lock = new ReentrantLock();

	/** Construct a graph. */
	public Graph() {
		super();
		_gid = CTR.getAndIncrement();
	}

	/** Construct a graph with the given display name. */
	public Graph(String name) {
		this();
		put(GRAPH_NAME, name);
	}

	/** Return a simple display name for this graph instance. */
	public String name() {
		Object name = get(GRAPH_NAME);
		if (name == null || name.toString().isBlank()) return String.valueOf(_gid);
		return name.toString();
	}

	/** Return a unique display name for this graph instance. */
	public String uniqueName() {
		String name = name();
		String gid = String.valueOf(_gid);
		if (name.equals(gid)) return gid;
		return String.format("%s(%s)", name, gid);
	}

	/** Lock access to this graph. */
	public void lock() {
		lock.lock();
	}

	/** Unlock access to this graph. */
	public void unlock() {
		lock.unlock();
	}

	/**
	 * Creates a new edge instance with the given terminal nodes.
	 *
	 * @param beg the begin terminal node
	 * @param end the end terminal node
	 * @return a new edge otherwise unassociated with the graph
	 */
	protected abstract E createEdge(N beg, N end);

	/**
	 * Creates a new edge instance with the given terminal nodes. Adds the edge, including
	 * the terminal nodes, to the graph.
	 *
	 * @param beg the begin terminal node
	 * @param end the end terminal node
	 * @return a new edge newly associated with the graph
	 */
	public E createAndAddEdge(N beg, N end) {
		Assert.notNull(beg, end);
		E edge = createEdge(beg, end);
		addEdge(edge); // add edge, including nodes, to graph
		return edge;
	}

	/**
	 * Returns an immutable list of the current graph root nodes. Dynamically collected by
	 * examining all nodes currently in the graph.
	 * <p>
	 * Roots are nominally defined as all graph nodes having no inbound edges
	 * ({@code Sense.IN}). Where no such node exists, the first constructed node in the
	 * graph is somewhat arbitrarily chosen as the single root node.
	 */
	public UniqueList<N> getRoots() {
		UniqueList<N> roots = new UniqueList<>();
		for (N node : nodes) {
			if (node.isRoot()) {
				roots.add(node);
			}
		}
		if (roots.isEmpty() && !nodes.isEmpty()) roots.add(nodes.iterator().next());
		return roots.unmodifiable();
	}

	/** Returns {@code true} if the graph contains the given node. */
	public boolean contains(N node) {
		return nodes.contains(node);
	}

	/** Returns the size of the graph. Equivalent to the node count. */
	public int size() {
		return nodes.size();
	}

	/** Returns {@code true} if the graph contains the given edge. */
	public boolean contains(E edge) {
		if (!edge.valid()) return false;
		return getEdges(edge.beg(), edge.end()).stream().anyMatch(e -> e.equals(edge));
	}

	/**
	 * Returns a copy of the current graph node set.
	 * <p>
	 * The value list is unmodifiable.
	 */
	public UniqueList<N> getNodes() {
		return new UniqueList<>(nodes).unmodifiable();
	}

	/**
	 * Removes all edges directly connecting from the given source node to the given
	 * destination node and that satisfy the given filter predicate. All selected edges
	 * are removed if the filter is {@code null}.
	 *
	 * @param src    a source node
	 * @param dst    a destination node
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if the selected edges were removed
	 */
	/**
	 * Returns a copy of the current graph node set constrained by the given filter. If
	 * the filter is {@code null}, all nodes are included.
	 * <p>
	 * The value list is unmodifiable.
	 *
	 * @param filter a predicate returning {@code true} to select for inclusion
	 * @return node subset as defined by the filter
	 */
	public UniqueList<N> getNodes(Predicate<? super N> filter) {
		if (filter == null) return getNodes();
		return nodes.stream() //
				.filter(filter) //
				.collect(Collectors.toCollection(UniqueList::new)) //
				.unmodifiable();
	}

	/** Returns {@code true} if any edge exists between the given nodes. */
	public boolean hasEdge(N src, N dst) {
		Assert.notNull(src, dst);
		return src.isAdjacent(dst);
	}

	/**
	 * Returns the unique set of all edges in the graph.
	 *
	 * @return the unique set of existing edges
	 */
	public UniqueList<E> getEdges() {
		UniqueList<E> edges = new UniqueList<>();
		for (N node : nodes) {
			edges.addAll(node.edges());
		}
		return edges;
	}

	/**
	 * Returns the unique set of edges existing between the given nodes.
	 *
	 * @param src a source node
	 * @param dst a destination node
	 * @return the edges existing between the given nodes
	 */
	public UniqueList<E> getEdges(N src, N dst) {
		Assert.notNull(src, dst);
		return src.edges(Sense.BOTH, e -> e.between(src, dst));
	}

	/**
	 * Primary graph tree constuction entry point. Adds the given edge to the graph.
	 * <p>
	 * Internally performs all operations necessary to create the edge terminal nodes, if
	 * not prior existing in the graph, and to incorporate the edge and terminal nodes
	 * into the graph.
	 * <p>
	 * Note: discretely constucting a node or edge alone does not add it to the graph.
	 *
	 * <pre>
	 * <code>
	 * 	public DemoEdge createEdge(String parent, String child) {
	 * 		return createEdge(createNode(parent), createNode(child));
	 * 	}
	 *
	 * 	public DemoEdge createEdge(DemoNode parent, DemoNode child) {
	 * 		DemoEdge edge = new DemoEdge(parent, child);
	 * 		addEdge(edge);
	 * 		return edge;
	 * 	}
	 *
	 * 	public DemoNode createNode(String name) {
	 * 		DemoNode node = getNode(name);
	 * 		if (node != null) return node;
	 *
	 * 		node = new DemoNode(this, name);
	 * 		return node;
	 * 	}
	 * </code>
	 * </pre>
	 *
	 * @param edge a graph edge
	 * @returns {@code true} if either terminal node was not already present in the graph
	 */
	public boolean addEdge(E edge) {
		Assert.notNull(GraphEx.of("Invalid edge"), edge, edge.beg(), edge.end());
		edge.beg().add(edge, Sense.OUT);
		edge.end().add(edge, Sense.IN);
		boolean ok = _add(edge.beg());
		ok |= _add(edge.end());
		return ok;
	}

	/**
	 * Internal use only. Adds the node to the graph node list.
	 *
	 * @param node the node to add
	 * @return {@code true} if not already present
	 * @see Graph#addEdge(Edge)
	 */
	private boolean _add(N node) {
		return nodes.add(node);
	}

	/**
	 * Internal use only. Removes the node from the graph node list.
	 *
	 * @param node the node to remove
	 * @return {@code true} if removed
	 * @see Graph#addEdge(Edge)
	 */
	private boolean _remove(N node) {
		return nodes.remove(node);
	}

	@Override
	public boolean removeNode(N node) {
		lock();
		try {
			Assert.notNull(node);
			if (!nodes.contains(node)) return false;

			boolean ok = node.edges().stream().allMatch(e -> removeEdge(e, true));
			node.clear();
			return ok;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean removeEdge(E edge, boolean clear) {
		lock();
		try {
			Assert.notNull(edge);

			N beg = edge.beg();
			N end = edge.end();

			boolean ok = edge.remove(clear);
			if (beg.adjacent().isEmpty()) _remove(beg);
			if (end.adjacent().isEmpty()) _remove(end);
			return ok;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean removeEdges(Collection<? extends E> edges, boolean clear) {
		lock();
		try {
			Assert.notNull(edges);
			return edges.stream().allMatch(e -> removeEdge(e, clear));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter) {
		lock();
		try {
			Assert.notNull(edge);
			if (filter != null && !filter.test(edge)) return false;
			return removeEdge(edge, clear);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean removeEdges(N src, N dst, boolean clear) {
		lock();
		try {
			Assert.notNull(src, dst);
			UniqueList<E> edges = src.to(dst);
			if (edges.isEmpty()) return false;
			return edges.stream().allMatch(e -> removeEdge(e, clear));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean removeEdgesIf(N src, N dst, boolean clear, Predicate<? super E> filter) {
		lock();
		try {
			Assert.notNull(src, dst);
			UniqueList<E> edges = src.to(dst);
			if (edges.isEmpty()) return false;
			if (filter == null) return edges.stream().allMatch(e -> removeEdge(e, clear));
			return edges.stream().filter(filter).allMatch(e -> removeEdge(e, clear));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean transfer(E edge, N beg) {
		lock();
		try {
			Assert.notNull(edge, beg);
			if (edge.beg().equals(beg)) return false;
			return move(edge, beg, edge.end(), false);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean transfer(Collection<? extends E> edges, N beg) {
		lock();
		try {
			Assert.notNull(edges, beg);
			return edges.stream().allMatch(e -> transfer(e, beg));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean move(E edge, N beg, N end) {
		lock();
		try {
			return move(edge, beg, end, false);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean move(E edge, N beg, N end, boolean cyclic) {
		lock();
		try {
			Assert.notNull(edge, beg, end);

			if (edge.beg().equals(beg) && edge.end().equals(end)) return false;
			if (beg.equals(edge.end()) && !cyclic) { // impermissble self cycle
				removeEdge(edge, true);
				return false;
			}

			removeEdge(edge, false);
			edge.setBeg(beg);
			edge.setEnd(end);
			addEdge(edge);

			return true;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean move(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		lock();
		try {
			Assert.notNull(edges);
			return edges.stream().allMatch(e -> move(e, beg, end, cyclic));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reterminate(E edge, N end) {
		lock();
		try {
			return reterminate(edge, end, false);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reterminate(E edge, N end, boolean cycles) {
		lock();
		try {
			Assert.notNull(edge, end);
			if (edge.end().equals(end)) return false;
			return move(edge, edge.beg(), end, cycles);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reterminate(Collection<? extends E> edges, N end) {
		lock();
		try {
			return reterminate(edges, end, false);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reterminate(Collection<? extends E> edges, N end, boolean cycles) {
		lock();
		try {
			Assert.notNull(edges, end);
			return edges.stream().allMatch(e -> reterminate(e, end, cycles));

		} finally {
			unlock();
		}
	}

	@Override
	public boolean consolidateEdges(Collection<? extends N> sources, N target) {
		lock();
		try {
			Assert.notNull(sources, target);
			Set<N> nodes = new LinkedHashSet<>(sources);
			nodes.remove(target);
			boolean ok = true;
			for (N node : nodes) {
				// convert [D,G] => [E,H] to [D,G] => B
				UniqueList<E> in = node.edges(Sense.IN);
				ok &= reterminate(in, target);

				// convert [E,H] => [F,I] to B => [F,I]
				UniqueList<E> out = node.edges(Sense.OUT);
				ok &= transfer(out, target);
			}
			return ok;

		} finally {
			unlock();
		}
	}

	/**
	 * Replicates the given edge. Override, if needed, to adjust edge decorations. The
	 * returned replica edge is unassociated with the graph.
	 *
	 * @param edge the exemplar edge
	 * @return a replica of the given edge otherwise unassociated with the graph
	 */
	public E replicateEdge(E edge) {
		lock();
		try {
			E repl = createEdge(edge.beg(), edge.end());
			repl.putAllIfAbsent(edge.properties());
			return repl;

		} finally {
			unlock();
		}
	}

	/**
	 * Replicates the given edge and changes the edge terminals to the given nodes.
	 * Override, if needed, to adjust edge decorations that are dependent on the changed
	 * terminals. The returned replica edge is unassociated with the graph.
	 *
	 * @param edge the exemplar edge
	 * @param beg  the begin terminal node name
	 * @param end  the end terminal node name
	 * @return a replica of the given edge otherwise unassociated with the graph
	 */
	public E replicateEdge(E edge, N beg, N end) {
		lock();
		try {
			E repl = replicateEdge(edge);
			repl.setBeg(beg);
			repl.setEnd(end);
			return repl;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean replicateEdges(N node, Collection<? extends N> targets) {
		lock();
		try {
			return replicateEdges(node, targets, false);

		} finally {
			unlock();
		}
	}

	@Override
	public boolean replicateEdges(N node, Collection<? extends N> targets, boolean remove) {
		lock();
		try {
			Set<? extends N> tgts = new LinkedHashSet<>(targets);
			tgts.remove(node);

			UniqueList<E> in = node.edges(Sense.IN);
			UniqueList<E> out = node.edges(Sense.OUT);

			boolean ok = true;
			for (N tgt : tgts) {
				// for edges ? => node, create ? => targets
				for (E edge : in) {
					E repl = replicateEdge(edge, edge.beg(), tgt);
					ok &= addEdge(repl);
				}

				// for edges node => ?, create targets => ?
				for (E edge : out) {
					E repl = replicateEdge(edge, tgt, edge.end());
					ok &= addEdge(repl);
				}
			}
			if (remove) ok &= removeNode(node);
			return ok;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reduce(N node) {
		lock();
		try {
			UniqueList<E> srcs = node.edges(Sense.IN, false);	// A =>
			UniqueList<E> dsts = node.edges(Sense.OUT, false);	// => C

			boolean ok = true;
			for (E src : srcs) {
				for (E dst : dsts) {
					ok &= reduce(src, dst);
				}
			}
			return ok;

		} finally {
			unlock();
		}
	}

	@Override
	public boolean reduce(E src, E dst) {
		lock();
		try {
			Assert.isTrue(GraphEx.of("Invalid Edge"), src.valid() && dst.valid());

			boolean ok = true;
			if (src.end().equals(dst.beg())) {
				removeEdge(src, false);
				src.setEnd(dst.end());
				src.putAllIfAbsent(dst.properties());
				ok &= addEdge(src);
				ok &= removeEdge(dst, true);

			} else {
				ok &= removeEdge(src, false);
				src.setEnd(dst.beg());
				ok &= addEdge(src);
			}
			return ok;

		} finally {
			unlock();
		}
	}

	/**
	 * Returns the {@code DotStyle} store for the {@code Node} or {@code Edge} containing
	 * this properties store. Creates and adds an {@code ON#GRAPHS} default category
	 * {@code DotStyle} store, if a store does not exist.
	 *
	 * @return the dot style store
	 */
	public DotStyle getDotStyle() {
		return Dot.getStyles(this, ON.GRAPHS);
	}

	/**
	 * Defines a custom style for this graph. The default implementation provides some
	 * sane style values, but will not overwrite any existing styles.
	 */
	public DotStyle defineStyle() {
		DotStyle ds = getDotStyle();
		ds.putIfAbsent(DotAttr.LABEL, ON.GRAPHS, name());
		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.GRAPHS, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.GRAPHS, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.GRAPHS, 18);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.CLUSTERS, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.CLUSTERS, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.CLUSTERS, 14);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.NODES, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.NODES, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.NODES, 12);

		ds.putIfAbsent(DotAttr.FONTCOLOR, ON.EDGES, DotStyle.BLACK);
		ds.putIfAbsent(DotAttr.FONTNAME, ON.EDGES, DotStyle.FONTS);
		ds.putIfAbsent(DotAttr.FONTSIZE, ON.EDGES, 10);
		return ds;
	}

	@Override
	public void clear() {
		getEdges().forEach(e -> e.remove(true));
		nodes.forEach(n -> n.clear());
		super.clear();
	}

	/** Required for testing. */
	public void reset() {
		Graph.CTR.set(0);
		Node.CTR.set(0);
		Edge.CTR.set(0);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Graph<?, ?> other = (Graph<?, ?>) obj;
		return Objects.equals(nodes, other.nodes);
	}

	@Override
	public String toString() {
		return uniqueName();
	}
}
