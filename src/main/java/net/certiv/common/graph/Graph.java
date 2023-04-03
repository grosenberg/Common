package net.certiv.common.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.certiv.common.check.Assert;
import net.certiv.common.dot.Dictionary.ON;
import net.certiv.common.dot.DotAttr;
import net.certiv.common.dot.DotStyle;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.stores.Counter;
import net.certiv.common.stores.UniqueDeque;

public abstract class Graph<N extends Node<N, E>, E extends Edge<N, E>> extends Props {

	private static final String ERR_LOOKUP = "Node lookup-by-name requires unique node names: %s %s";

	public static final String GRAPH_NAME = "GraphName";

	static final Counter CTR = new Counter();

	/** Unique numerical graph identifier */
	public final long _gid;

	/** All graph nodes. */
	private final LinkedHashSet<N> nodes = new LinkedHashSet<>();

	public Graph() {
		super();
		_gid = CTR.getAndIncrement();
	}

	public Graph(String name) {
		this();
		put(GRAPH_NAME, name);
	}

	public Graph(Map<Object, Object> props) {
		this();
		putAll(props);
	}

	/**
	 * Descriptive name of this {@code Graph}. Defaults to the simple class name of the
	 * implementing class.
	 */
	public String name() {
		String name = (String) get(GRAPH_NAME, getClass().getSimpleName());
		return String.format("%s(%s)", name, _gid);
	}

	// ======================================================
	// Convenience methods for string-named nodes
	// ------------------------------------------------------

	/**
	 * Creates a new node instance with the given name.
	 * <p>
	 * Use {@code #findOrCreateNode(String)} to protect against creating multiple nodes
	 * with the same name. The {@code #getNode} method relies on unique node names.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param name the node name
	 * @return a new node otherwise unassociated with the graph
	 */
	protected abstract N createNode(String name);

	/**
	 * Finds the named terminal node in the graph or, if not pre-existing, creates a new
	 * named node.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param name the node name
	 * @return the named node
	 */
	public N findOrCreateNode(String name) {
		Assert.notEmpty(name);
		N node = getNode(name);
		if (node != null) return node;
		return createNode(name);
	}

	/**
	 * Creates a new edge instance with terminal nodes having the given names. The named
	 * terminal nodes will be created if they do not pre-exist in the graph.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param beg the begin terminal node name
	 * @param end the end terminal node name
	 * @return a new edge otherwise unassociated with the graph
	 */
	public E createEdge(String beg, String end) {
		Assert.notEmpty(beg, end);
		return createEdge(findOrCreateNode(beg), findOrCreateNode(end));
	}

	/**
	 * Creates a new edge instance with terminal nodes having the given names. The named
	 * terminal nodes will be created if they do not pre-exist in the graph. Adds the
	 * edge, including the terminal nodes, to the graph.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param beg the begin terminal node name
	 * @param end the end terminal node name
	 * @return a new edge newly associated with the graph
	 */
	public E createAndAddEdge(String beg, String end) {
		Assert.notEmpty(beg, end);
		E edge = createEdge(beg, end);
		addEdge(edge); // add edge, including nodes, to graph
		return edge;
	}

	/**
	 * Returns the existing nodes having the given name. Requires given name be unique.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param name a node node
	 * @return the existing node uniquely having the given name, or {@code null}
	 * @throws UnsupportedOperationException if the given name is not unique
	 */
	public N getNode(String name) {
		Assert.notEmpty(name);
		UniqueDeque<N> res = nodes.stream() //
				.filter(n -> n.get(Node.NODE_NAME).equals(name)) //
				.collect(Collectors.toCollection(UniqueDeque::new));

		Assert.isTrue(GraphEx.of(ERR_LOOKUP, name, res), res.size() <= 1);
		return res.peek();
	}

	/**
	 * Verifies that the given name references a unique (or non-existant) node.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param name a node name
	 * @return {@code true} if the given name references a unique (or non-existant) node
	 */
	public boolean verifyUnique(String name) {
		Assert.notEmpty(name);
		Set<N> res = nodes.stream().filter(n -> n.get(Node.NODE_NAME).equals(name))
				.collect(Collectors.toSet());
		return res.size() < 2;
	}

	/**
	 * Returns the set of edges existing between the given named nodes.
	 * <p>
	 * Convenience operation for and only intended to work with string-named nodes.
	 *
	 * @param beg a source node name
	 * @param end a destination node name
	 * @return the edges existing between the given nodes
	 */
	public UniqueDeque<E> getEdges(String beg, String end) {
		Assert.notEmpty(beg, end);
		N src = getNode(beg);
		N dst = getNode(end);
		if (src == null || dst == null) return UniqueDeque.empty();
		return getEdges(src, dst);
	}

	// ======================================================

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

	/** Returns an immutable list of the current graph root nodes. */
	public UniqueDeque<N> getRoots() {
		return nodes.stream().filter(n -> n.isRoot()) //
				.collect(Collectors.toCollection(UniqueDeque::new)).unmodifiable();
	}

	/** Returns {@code true} if the graph contains the given node. */
	public boolean hasNode(N node) {
		return nodes.contains(node);
	}

	/** Returns a copy of the current graph node set. */
	public UniqueDeque<N> getNodes() {
		return new UniqueDeque<>(nodes).unmodifiable();
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
	public UniqueDeque<E> getEdges() {
		UniqueDeque<E> edges = new UniqueDeque<>();
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
	public UniqueDeque<E> getEdges(N src, N dst) {
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

	/**
	 * Removes the given node from the graph. All connecting edges are removed (and
	 * cleared).
	 *
	 * @param node the node to remove
	 * @return {@code true} if the node was present in the graph and is now removed
	 */
	public boolean removeNode(N node) {
		Assert.notNull(node);
		boolean ok = _remove(node);
		node.edges().forEach(e -> removeEdge(e, true));
		node.clear();
		return ok;
	}

	/**
	 * Removes the given edge from the graph. Removes either terminal node if the node has
	 * no remaining edge connections.
	 *
	 * @param edge  a graph edge
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@code true} if the edge was removed
	 */
	public boolean removeEdge(E edge, boolean clear) {
		Assert.notNull(edge);

		N beg = edge.beg();
		N end = edge.end();

		boolean ok = edge.remove(clear);
		if (beg.adjacent().isEmpty()) _remove(beg);
		if (end.adjacent().isEmpty()) _remove(end);
		return ok;
	}

	/**
	 * Removes the given edges from the graph. Removes either terminal node of an edge if
	 * that node has no remaining edge connections.
	 *
	 * @param edges a list of graph edges
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@code true} if all of the edges were removed
	 */
	public boolean removeEdges(Collection<? extends E> edges, boolean clear) {
		Assert.notNull(edges);
		return edges.stream().allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Removes the given edge if the edge satisfies the given filter predicate or if the
	 * filter is {@code null}.
	 *
	 * @param edge   a graph edge
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if the edge was removed
	 */
	public boolean removeEdgeIf(E edge, boolean clear, Predicate<? super E> filter) {
		Assert.notNull(edge);
		if (filter != null && !filter.test(edge)) return false;
		return removeEdge(edge, clear);
	}

	/**
	 * Removes all edges directly connecting the given nodes.
	 *
	 * @param src   a source node
	 * @param dst   a destination node
	 * @param clear {@code true} to clear the edge terminal nodes and properties
	 * @return {@code true} if an edge was removed
	 */
	public boolean removeEdges(N src, N dst, boolean clear) {
		Assert.notNull(src, dst);
		UniqueDeque<E> edges = src.to(dst);
		if (edges.isEmpty()) return false;
		return edges.stream().allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Removes all edges directly connecting the given nodes and that satisfy the given
	 * filter predicate or if the filter is {@code null}.
	 *
	 * @param src    a source node
	 * @param dst    a destination node
	 * @param clear  {@code true} to clear the edge terminal nodes and properties
	 * @param filter a predicate returning {@code true} to select for removal
	 * @return {@code true} if any element was removed
	 */
	public boolean removeEdgesIf(N src, N dst, boolean clear, Predicate<? super E> filter) {
		Assert.notNull(src, dst);
		UniqueDeque<E> edges = src.to(dst);
		if (edges.isEmpty()) return false;
		if (filter == null) return edges.stream().allMatch(e -> removeEdge(e, clear));
		return edges.stream().filter(filter).allMatch(e -> removeEdge(e, clear));
	}

	/**
	 * Transfers the subgraph represented by the given edge to depend from the given node.
	 * <p>
	 * Does nothing if the given edge begin node is the same as the given node.
	 * <p>
	 * If the transfer would create a new root node cycle -- the given edge end node is
	 * the same as the given node -- the edge is removed from the graph.
	 * <p>
	 * If the given edge begin node becomes unconnected in the graph, except by
	 * self-cyclic edges, that terminal node is removed from the graph.
	 *
	 * @param edge an edge defining a subgraph
	 * @param beg  the target beg node for the subgraph
	 * @return {@code true} if the edge was transferred
	 */
	public boolean transfer(E edge, N beg) {
		Assert.notNull(edge, beg);
		if (edge.beg().equals(beg)) return false;
		return move(edge, beg, edge.end(), false);
	}

	/**
	 * Transfers the subgraphs represented by the given edges to depend from the given
	 * node.
	 * <p>
	 * Does nothing for a subgraph if its given edge begin node is the same as the given
	 * node.
	 * <p>
	 * If any transfer would create a new root node cycle -- that given edge end node is
	 * the same as the given node -- that edge is removed from the graph.
	 * <p>
	 * If any given edge begin node becomes unconnected in the graph, except by
	 * self-cyclic edges, that terminal node is removed from the graph.
	 *
	 * @param edges the edges defining subgraphs
	 * @param beg   the target beg node for the subgraphs
	 */
	public void transfer(Collection<? extends E> edges, N beg) {
		Assert.notNull(edges, beg);
		edges.forEach(e -> transfer(e, beg));
	}

	/**
	 * Moves the given edge to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if the result
	 * of the move would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   an existing graph edge
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was moved
	 */
	public boolean move(E edge, N beg, N end, boolean cyclic) {
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
	}

	/**
	 * Moves the given edges to connect between the given nodes.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if the result
	 * of the move would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges  the existing graph edges
	 * @param beg    the new begin node
	 * @param end    the new end node
	 * @param cyclic {@code true} to permit creation of single edge cycles
	 */
	public void move(Collection<? extends E> edges, N beg, N end, boolean cyclic) {
		Assert.notNull(edges);
		edges.forEach(e -> move(e, beg, end, cyclic));
	}

	/**
	 * Reterminate the given edge with the given end node.
	 * <p>
	 * Removes the edge from the graph if retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   a graph edge
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated
	 */
	public boolean reterminate(E edge, N end) {
		return reterminate(edge, end, false);
	}

	/**
	 * Reterminate the given edge with the given end node.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes the edge from the graph if
	 * retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of the given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edge   a graph edge
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 * @return {@code true} if the edge was reterminated
	 */
	public boolean reterminate(E edge, N end, boolean cycles) {
		Assert.notNull(edge, end);
		if (edge.end().equals(end)) return false;
		return move(edge, edge.beg(), end, cycles);
	}

	/**
	 * Reterminate the given edges with the given end node.
	 * <p>
	 * Removes any edge from the graph if retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges existing graph edges
	 * @param end   a new edge end node
	 */
	public void reterminate(Collection<? extends E> edges, N end) {
		reterminate(edges, end, false);
	}

	/**
	 * Reterminate the given edges with the given end node.
	 * <p>
	 * If {@code cyclic} is {@code false}, removes any edge from the graph if
	 * retermination would create a single edge cycle.
	 * <p>
	 * If either of the initial terminal nodes of any given edge become unconnected in the
	 * graph, except by self-cyclic edges, that initial terminal node is removed from the
	 * graph.
	 *
	 * @param edges  existing graph edges
	 * @param end    a new edge end node
	 * @param cycles {@code true} to permit creation of single edge cycles
	 */
	public void reterminate(Collection<? extends E> edges, N end, boolean cycles) {
		Assert.notNull(edges, end);
		edges.forEach(e -> reterminate(e, end, cycles));
	}

	/**
	 * Consolidate the edges connecting to the source nodes to the target node. Excludes
	 * the target node from the source nodes. Removes the finally unconnected source nodes
	 * from the graph.
	 *
	 * <pre>
	 * A => B => C
	 * D => E => F
	 * G => H => I
	 * consolidateEdges([B,E,H], B);	// implicitly removes [E,H]
	 * consolidateEdges([E,H], B);		// equivalent
	 * [A,D,G] => B => [C,F,I]
	 * </pre>
	 *
	 * @param edges  the source nodes
	 * @param target a target node
	 * @return the newly created edges
	 */
	public void consolidateEdges(Collection<? extends N> edges, N target) {
		Assert.notNull(edges, target);
		Set<N> nodes = new LinkedHashSet<>(edges);
		nodes.remove(target);
		for (N node : nodes) {
			// convert [D,G] => [E,H] to [D,G] => B
			UniqueDeque<E> in = node.edges(Sense.IN);
			reterminate(in, target);

			// convert [E,H] => [F,I] to B => [F,I]
			UniqueDeque<E> out = node.edges(Sense.OUT);
			transfer(out, target);
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
		E repl = createEdge(edge.beg(), edge.end());
		repl.putAllIfAbsent(edge.properties());
		return repl;
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
		E repl = replicateEdge(edge);
		repl.setBeg(beg);
		repl.setEnd(end);
		return repl;
	}

	/**
	 * Replicates the existing edge connections with given source node to the given target
	 * nodes. Creates new edge connections to each target node equivalent to the source
	 * node edge connections. Conditionally removes the source node.
	 * <p>
	 * All replica edges are added to the graph.
	 *
	 * <pre>
	 * A => B => C
	 * replicateEdges(B, [B,X,Y,Z], false);
	 * A => [B,X,Y,Z] => C
	 * </pre>
	 *
	 * <pre>
	 * A => B => C
	 * replicateEdges(B, [B,X,Y,Z], true); // removes B
	 * A => [X,Y,Z] => C
	 * </pre>
	 *
	 * @param node    a source node
	 * @param targets the target nodes
	 * @param reduce  {@code true} to remove the source node
	 */
	public void replicateEdges(N node, Collection<? extends N> targets, boolean reduce) {
		Set<? extends N> tgts = new LinkedHashSet<>(targets);
		tgts.remove(node);

		UniqueDeque<E> in = node.edges(Sense.IN);
		UniqueDeque<E> out = node.edges(Sense.OUT);

		for (N tgt : tgts) {

			// for edges ? => node, create ? => targets
			for (E edge : in) {
				E repl = replicateEdge(edge, edge.beg(), tgt);
				addEdge(repl);
			}

			// for edges node => ?, create targets => ?
			for (E edge : out) {
				E repl = replicateEdge(edge, tgt, edge.end());
				addEdge(repl);
			}
		}
		if (reduce) reduce(node);
	}

	/**
	 * Reduce the graph by removing the given node while retaining the connectivity
	 * between the inbound and outbound nodes.
	 *
	 * <pre>
	 * reduce(B)
	 * A => B => C
	 * becomes
	 * A => C
	 * </pre>
	 *
	 * @param node the term to remove from the graph
	 */
	public void reduce(N node) {
		UniqueDeque<E> srcs = node.edges(Sense.IN, false);	// A =>
		UniqueDeque<E> dsts = node.edges(Sense.OUT, false);	// => C

		for (E src : srcs) {
			for (E dst : dsts) {
				reduce(src, dst);
			}
		}
	}

	/**
	 * Reduce the graph by reterminating the given source edge to the distal node of the
	 * given destination edge. The retermination retains, and appropriately adjusts the
	 * connectivity between the resultant distal inbound and outbound nodes. The given
	 * destination edge is removed, potentially resulting in the shared node being
	 * removed.
	 *
	 * <pre>
	 * reduce(AB,BC)
	 * A -> B -> C
	 * becomes
	 * A -> C
	 * </pre>
	 *
	 * <pre>
	 * reduce(AB,CD)
	 * A -> B
	 * C -> D
	 * becomes
	 * A -> C -> D
	 * </pre>
	 *
	 * @param src the source edge
	 * @param src the destination edge
	 */
	public void reduce(E src, E dst) {
		Assert.isTrue(GraphEx.of("Invalid Edge"), src.valid() && dst.valid());

		if (src.end().equals(dst.beg())) {
			removeEdge(src, false);
			src.setEnd(dst.end());
			src.putAllIfAbsent(dst.properties());
			addEdge(src);
			removeEdge(dst, true);

		} else {
			removeEdge(src, false);
			src.setEnd(dst.beg());
			addEdge(src);
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
		return getDotStyle(ON.GRAPHS);
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
		return name();
	}
}
