package net.certiv.common.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.annotations.VisibleForTesting;
import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.algorithms.GraphPath;
import net.certiv.common.graph.ops.ITransformOp;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.stores.props.Props;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> extends Props
		implements IBuild<N, E> {

	protected static final String GRAPH_NAME = "GraphName";

	static final Counter CTR = new Counter();

	/** Unique numerical graph identifier */
	public final long _gid;

	/** All graph nodes. */
	private final LinkedHashSet<N> nodes = new LinkedHashSet<>();

	/** Graph modification control lock. */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Construct a graph.
	 */
	public Graph() {
		super();
		_gid = CTR.getAndIncrement();
	}

	/**
	 * Construct a graph with the given graph identifier.
	 *
	 * @param id a graph identifier object.
	 */
	public Graph(Object id) {
		this();
		setNameObj(id);
	}

	/** Lock access to this graph. */
	public void lock() {
		lock.lock();
	}

	/** Unlock access to this graph. */
	public void unlock() {
		lock.unlock();
	}

	/** Return the object used to provide the name of this graph instance. */
	public Object nameObj() {
		return get(GRAPH_NAME);
	}

	/** Set the object used to provide the name of this graph instance. */
	public Object setNameObj(Object id) {
		Object old = nameObj();
		put(GRAPH_NAME, id);
		return old;
	}

	/** Return a display name for this graph instance. */
	public String name() {
		Object name = nameObj();
		if (name == null || name.toString().isBlank()) return String.valueOf(_gid);
		return name.toString();
	}

	/** Return a unique display name for this graph instance. */
	public String uname() {
		String name = name();
		String gid = String.valueOf(_gid);
		if (name.equals(gid)) return gid;
		return String.format("%s(%s)", name, gid);
	}

	/**
	 * Create a new specialized walker instance for use in walking this graph.
	 *
	 * @return a walker
	 */
	public abstract Walker<N, E> walker();

	/**
	 * Creates a new node instance uniquely identifiable by the given {@code id}.
	 * <p>
	 * For graphs with some hierarchy of node types, the {@code id} preferably encodes the
	 * desired node type.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 * <p>
	 * {@code Builder} notes:
	 * <ul>
	 * <li>{@code Builder} methods accepting string literal arguments, such as
	 * {@code Builder#getNode(String)}, require each {@code id} have a unique string
	 * representation.
	 * <li>{@code Builder#findOrCreateNode(Node)} and
	 * {@code Builder#findOrCreateNode(String)}use is preferred to protect against
	 * creating multiple nodes with the same name.
	 * </ul>
	 *
	 * @param id an object instance providing node name
	 * @return a new node otherwise unassociated with the graph
	 */
	protected abstract N createNode(Object id);

	/**
	 * Creates a new edge instance with the given terminal nodes.
	 * <p>
	 * For graphs with some hierarchy of edge types, an actual edge type is preferably
	 * determinable from the node types. The alternative would be to add an {@code id}
	 * parameter to explicity define an edge type.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 *
	 * @param beg the begin terminal node
	 * @param end the end terminal node
	 * @return a new edge otherwise unassociated with the graph
	 */
	protected abstract E createEdge(N beg, N end);

	/**
	 * Creates a new edge instance with the given terminal nodes. Adds the edge, including
	 * the terminal nodes, to the graph.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 *
	 * @param beg the begin terminal node
	 * @param end the end terminal node
	 * @return a new edge newly associated with the graph
	 */
	public E createAndAddEdge(N beg, N end) {
		Assert.notNull(beg, end);
		E edge = createEdge(beg, end);
		addEdge(edge);
		return edge;
	}

	/**
	 * Copy the given node to create a new node instance.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 *
	 * @param node a reference node
	 * @return a new node otherwise unassociated with the graph
	 */
	public N copyNode(N node) {
		N repl = createNode(node.nameObj());
		repl.putAll(node.properties());
		return repl;
	}

	/**
	 * Copy the given edge to create a new edge instance with the given terminal nodes.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 *
	 * @param edge the reference edge
	 * @param beg  the begin terminal node
	 * @param end  the end terminal node
	 * @return a new edge otherwise unassociated with the graph
	 */
	public E copyEdge(E edge, N beg, N end) {
		E repl = createEdge(beg, end);
		repl.putAll(edge.properties());
		return repl;
	}

	/**
	 * Copy the given edge to create a new edge instance with the given terminal nodes.
	 * Adds the edge, including the terminal nodes, to the graph.
	 * <p>
	 * Nominally, use the {@link Builder} for graph construction and {@link ITransform}
	 * for manipulation.
	 *
	 * @param edge the reference edge
	 * @param beg  the begin terminal node
	 * @param end  the end terminal node
	 * @return a new edge newly associated with the graph
	 */
	public E copyAndAddEdge(E edge, N beg, N end) {
		Assert.notNull(edge, beg, end);
		E copy = copyEdge(edge, beg, end);
		addEdge(copy);
		return copy;
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

	/** Returns {@code true} if the graph contains the given edge. */
	public boolean contains(E edge) {
		if (!edge.valid()) return false;
		return getEdges(edge.beg(), edge.end()).stream().anyMatch(e -> e.equals(edge));
	}

	/** Returns {@code true} if the graph contains the given path. */
	public boolean contains(GraphPath<N, E> path) {
		if (path == null) return false;
		return getEdges(true).containsAll(path.edges());
	}

	/** Returns {@code true} if the graph contains the given paths. */
	public boolean containsAll(Collection<GraphPath<N, E>> paths) {
		if (paths == null) return false;
		return paths.stream().allMatch(p -> contains(p));
	}

	/** Returns the size of the graph. Equivalent to the node count. */
	public int size() {
		return nodes.size();
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

	@Override
	public boolean hasEdge(N src, N dst) {
		Assert.notNull(src, dst);
		return src.isAdjacent(dst);
	}

	@Override
	public UniqueList<E> getEdges() {
		return getEdges(false);
	}

	@Override
	public UniqueList<E> getEdges(boolean cyclic) {
		UniqueList<E> edges = new UniqueList<>();
		for (N node : nodes) {
			edges.addAll(node.edges(Sense.BOTH, cyclic));
		}
		return edges;
	}

	@Override
	public UniqueList<E> getEdges(N src, N dst) {
		return getEdges(Sense.BOTH, src, dst);
	}

	@Override
	public UniqueList<E> getEdges(Sense dir, N src, N dst) {
		Assert.notNull(dir, src, dst);
		return src.edges(dir, e -> e.between(src, dst));
	}

	// --------------------------------------

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
		Assert.notNull(ERR_EDGE, edge, edge.beg(), edge.end());
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
	boolean _add(N node) {
		return nodes.add(node);
	}

	/**
	 * Internal use only. Removes the node from the graph node list.
	 *
	 * @param node the node to remove
	 * @return {@code true} if removed
	 * @see Graph#addEdge(Edge)
	 */
	boolean _remove(N node) {
		return nodes.remove(node);
	}

	/**
	 * Duplicates the given edge. Override, if needed, to adjust edge decorations. The
	 * returned duplicate edge is unassociated with the graph.
	 *
	 * @param edge an exemplar edge
	 * @return a duplicate edge
	 */
	public E duplicateEdge(E edge) {
		lock();
		try {
			E dup = createEdge(edge.beg(), edge.end());
			dup.putAllIfAbsent(edge.properties());
			return dup;

		} finally {
			unlock();
		}
	}

	/**
	 * Duplicates the given edge and changes the edge terminals to the given nodes.
	 * Override, if needed, to adjust edge decorations that are dependent on the changed
	 * terminals. The returned duplicate edge is unassociated with the graph.
	 *
	 * @param edge an exemplar edge
	 * @param beg  duplicate begin node
	 * @param end  duplicate end node
	 * @return a duplicate edge
	 */
	public E duplicateEdge(E edge, N beg, N end) {
		lock();
		try {
			E dup = duplicateEdge(edge);
			dup.setBeg(beg);
			dup.setEnd(end);
			return dup;

		} finally {
			unlock();
		}
	}

	/**
	 * Defines whether this graph permits a graph manipulation/transform operation of the
	 * given type.
	 * <p>
	 * Default is to allow all manipulation/transform operation types.
	 *
	 * @param type a manipulation/transform operation type
	 * @return {@code true} if the operation is permitted
	 * @see ITransformOp#canApply(Graph)
	 */
	public boolean permits(XfPermits type) {
		return true;
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
		getEdges(true).forEach(e -> e.remove(true));
		nodes.forEach(n -> n.clear());
		super.clear();
	}

	/** Required for testing. */
	@VisibleForTesting
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
		return uname();
	}
}
