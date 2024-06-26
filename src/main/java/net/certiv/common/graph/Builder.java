package net.certiv.common.graph;

import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.id.IUId;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Strings;

/**
 * Standard builder for using string patterns to drive node and edge creation. Also
 * functions as a node factory, supporting heterogeneous {@code EdgeSet} implementations.
 * <p>
 * The key entry point for graph creation is {@code Graph#addEdge(Edge)}. This builder or
 * any other analogous mechanism is meant to drive that entry point.
 * <p>
 * This builder is suitable primarily where node uniqueness is based on or derived from a
 * string specification. Refer to the unit tests for examples.
 *
 * @param <T> graph Id type
 * @param <I> node Id type
 * @param <G> graph type
 * @param <N> node type
 * @param <E> edge type
 */
public abstract class Builder<T extends IUId, I extends IUId, G extends Graph<I, N, E>, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements IBuild<I, N, E> {

	public static final String ERR_NODE_LOOKUP = "Node lookup-by-name requires unique node names: %s %s";

	private static final Pattern NAME = Pattern.compile("\\w+");
	private static final Pattern MARK = Pattern.compile("\\h*=>|->\\h*");
	private static final Pattern SEPR = Pattern.compile("\\h*,\\h*");
	private static final Pattern NODES = Pattern.compile("\\w+|\\[.+?\\]");

	/** All nodes known to have been built. */
	private final UniqueList<N> built = new UniqueList<>();
	/** Temporary edge list. */
	private final UniqueList<E> edges = new UniqueList<>();

	/** The graph being constructed. */
	protected final G graph;

	public Builder(G graph) {
		Assert.notNull(graph);
		this.graph = graph;
	}

	/**
	 * Creates a new node instance uniquely identifiable by the given {@code id}. Builder
	 * operation requires the {@code id} have a correspodingly unique string
	 * representation.
	 * <p>
	 * Use {@code #findOrCreateNode(String)} to protect against creating multiple nodes
	 * with the same name. The {@code #getNode(String)} method relies on unique node
	 * names.
	 *
	 * @param id an object representing the node name
	 * @return a new node otherwise unassociated with the graph
	 */
	protected abstract N createNode(I id);

	/**
	 * Convert the given unique node naming object to a correspondingly unique {@code id}.
	 *
	 * @param nameObj object providing a unique node name
	 * @return typed identifier
	 */
	protected abstract I makeId(Object nameObj);

	/** Convert the given unique {@code id} to a correspondingly unique node name. */
	protected abstract String nameOf(I id);

	/**
	 * Returns the node, existing in the graph or built by this builder and either not yet
	 * added to the graph, having the given unique id.
	 *
	 * @param id    unique node id
	 * @param built {@code true} to include any known node
	 * @return node having the given id, or {@code null}
	 * @throws UnsupportedOperationException if the given id is not unique
	 */
	public N getNode(I id, boolean built) {
		N node = null;
		if (built) {
			LinkedList<N> nodes = this.built.stream() //
					.filter(n -> n.get(Node.NODE_ID).equals(id)) //
					.collect(Collectors.toCollection(LinkedList::new));
			Assert.isTrue(GraphEx.of(ERR_NODE_LOOKUP, id, nodes), nodes.size() <= 1);
			node = nodes.peek();
		}

		if (node == null) {
			LinkedList<N> nodes = graph.getNodes().stream() //
					.filter(n -> n.get(Node.NODE_ID).equals(id)) //
					.collect(Collectors.toCollection(LinkedList::new));
			Assert.isTrue(GraphEx.of(ERR_NODE_LOOKUP, id, nodes), nodes.size() <= 1);
			node = nodes.peek();
		}

		return node;
	}

	/**
	 * Finds the single identified terminal node in the graph or, if not pre-existing,
	 * creates a new node with the given id.
	 *
	 * @param id node id
	 * @return named node
	 */
	public N findOrCreateNode(I id) {
		N node = getNode(id, true);
		if (node != null) return node;

		node = createNode(id);
		built.add(node);
		return node;
	}

	/**
	 * @param beg
	 * @param end
	 * @return
	 * @see Graph#addEdge(Node, Node)
	 */
	public E createAndAddEdge(N beg, N end) {
		return graph.addEdge(beg, end);
	}

	// --------------------------------

	/**
	 * Returns the node existing in the graph having the given name. Requires given name
	 * be unique.
	 *
	 * @param name a unique node name
	 * @return the node existing in the graph uniquely having the given name, or
	 *         {@code null}
	 * @throws UnsupportedOperationException if the given name is not unique
	 */
	public N getNode(String name) {
		Assert.isTrue(NAME.matcher(name).matches());
		return getNode(name, false);
	}

	/**
	 * Returns the node, existing in the graph or built by this builder and either not yet
	 * added to the graph, having the given name. Requires given name be unique.
	 *
	 * @param name  a unique node name
	 * @param built {@code true} to include any known node
	 * @return a node with the given name, or {@code null}
	 * @throws UnsupportedOperationException if the given name is not unique
	 */
	public N getNode(String name, boolean built) {
		Assert.isTrue(NAME.matcher(name).matches());
		I id = makeId(name);
		return getNode(id, built);
	}

	/**
	 * Finds the single named terminal node in the graph or, if not pre-existing, creates
	 * a new named node.
	 *
	 * @param name the node name
	 * @return the named node
	 */
	public N findOrCreateNode(String name) {
		Assert.isTrue(NAME.matcher(name).matches());
		N node = getNode(name, true);
		if (node != null) return node;

		node = createNode(makeId(name));
		built.add(node);
		return node;
	}

	/**
	 * Finds the terminal nodes specified in the given node spec. Returns the matching
	 * node(s) that are pre-built or pre-existing in the graph.
	 * <p>
	 * Node Specification form:
	 *
	 * <pre>
	 * A
	 * [A,B]
	 * </pre>
	 *
	 * @param nodeSpec the node name specification
	 * @return the node(s) named in the node spec
	 */
	public UniqueList<N> findNodes(String nodeSpec) {
		Assert.isTrue(NODES.matcher(nodeSpec).matches());
		return parseNodeSpec(nodeSpec, false).unmodifiable();
	}

	/**
	 * Finds the terminal nodes specified in the given node spec. Creates new named
	 * node(s) if not pre-built or pre-existing in the graph.
	 * <p>
	 * Node Specification form:
	 *
	 * <pre>
	 * A
	 * [A,B]
	 * </pre>
	 *
	 * @param nodeSpec the node name specification
	 * @return the node(s) named in the node spec
	 */
	public UniqueList<N> findOrCreateNodes(String nodeSpec) {
		Assert.notEmpty(nodeSpec);
		return parseNodeSpec(nodeSpec, true).unmodifiable();
	}

	/**
	 * Creates a new edge instance with terminal nodes having the given begin and end
	 * names. The named terminal nodes will be created if they do not pre-exist in the
	 * graph.
	 *
	 * @param beg the begin terminal node name
	 * @param end the end terminal node name
	 * @return a new edge otherwise unassociated with the graph
	 */
	public E createEdge(String beg, String end) {
		Assert.notEmpty(beg, end);
		return graph.createEdge(findOrCreateNode(beg), findOrCreateNode(end));
	}

	/**
	 * Creates a new edge instance with terminal nodes having the given names. The named
	 * terminal nodes will be created if they do not pre-exist in the graph. Adds the
	 * edge, including the terminal nodes, to the graph.
	 *
	 * @param beg the begin terminal node name
	 * @param end the end terminal node name
	 * @return a new edge newly associated with the graph
	 */
	public E createAndAddEdge(String beg, String end) {
		Assert.notEmpty(beg, end);
		E edge = createEdge(beg, end);
		graph.addEdge(edge); // add edge, including nodes, to graph
		return edge;
	}

	// TODO: consider adding
	// /**
	// * @param node
	// * @return
	// * @see Graph#copyNode(Node)
	// */
	// public N copyNode(N node) {
	// return graph.copyNode(node);
	// }
	//
	// /**
	// * @param edge
	// * @param beg
	// * @param end
	// * @return
	// * @see Graph#copyEdge(Edge, Node, Node)
	// */
	// public E copyEdge(E edge, N beg, N end) {
	// return graph.copyEdge(edge, beg, end);
	// }
	//
	// /**
	// * @param edge
	// * @param beg
	// * @param end
	// * @return
	// * @see Graph#copyAndAddEdge(Edge, Node, Node)
	// */
	// public E copyAndAddEdge(E edge, N beg, N end) {
	// return graph.copyAndAddEdge(edge, beg, end);
	// }
	//
	// /**
	// * @param edge
	// * @return
	// * @see Graph#addEdge(Edge)
	// */
	// public boolean addEdge(E edge) {
	// return graph.addEdge(edge);
	// }

	/**
	 * Verifies that the given name references a unique (or non-existant) node.
	 *
	 * @param name a node name
	 * @return {@code true} if the given name references a unique (or non-existant) node
	 */
	public boolean verifyUnique(String name) {
		Assert.notEmpty(name);
		I id = makeId(name);
		return graph.getNodes().stream() //
				.filter(n -> n.get(Node.NODE_ID).equals(id)) //
				.count() < 2;
	}

	/**
	 * Creates new edges corresponding to the given edge spec and adds them to the graph.
	 * Creates new named node(s) if not pre-built or pre-existing in the graph.
	 * <p>
	 * Edge Specification form:
	 *
	 * <pre>
	 * builder.createAndAddEdges("A -> B -> C");
	 * builder.createAndAddEdges("[AB] => C => [Delta,Eta]");
	 * </pre>
	 *
	 * Symbols {@code ->} and {@code =>} are fully interchangeable.
	 *
	 * @param edgeSpec specification of edges to be built and added
	 * @return the {@code Builder}
	 */
	public Builder<T, I, G, N, E> createAndAddEdges(String edgeSpec) {
		Assert.isTrue(MARK.matcher(edgeSpec).find());
		return createEdges(edgeSpec).addEdges();
	}

	/**
	 * Creates new edges corresponding to the given edge spec. Creates new named node(s)
	 * if not pre-built or pre-existing in the graph.
	 * <p>
	 * Edge Specification form:
	 *
	 * <pre>
	 * builder.createAndAddEdges("A -> B -> C");
	 * builder.createAndAddEdges("[AB] => C => [Delta,Eta]");
	 * </pre>
	 *
	 * Symbols {@code ->} and {@code =>} are fully interchangeable.
	 *
	 * @param edgeSpec specification of edges to be built
	 * @return the {@code Builder} holding the edges pending addition to the graph
	 */
	public Builder<T, I, G, N, E> createEdges(String edgeSpec) {
		Assert.isTrue(MARK.matcher(edgeSpec).find());
		parseEdgeSpec(edgeSpec);
		return this;
	}

	/**
	 * Returns the set of edges existing between the given named nodes.
	 *
	 * @param beg a source node name
	 * @param end a destination node name
	 * @return the edges existing between the given nodes
	 */
	public UniqueList<E> getEdges(String beg, String end) {
		Assert.notEmpty(beg, end);
		N src = getNode(beg);
		N dst = getNode(end);
		if (src == null || dst == null) return UniqueList.empty();
		return getEdges(src, dst);
	}

	/**
	 * Returns the unique set of all edges known to this builder.
	 *
	 * @return the unique set of builder edges
	 */
	@Override
	public UniqueList<E> getEdges() {
		return edges;
	}

	@Override
	public UniqueList<E> getEdges(boolean cyclic) {
		return graph.getEdges(cyclic);
	}

	@Override
	public boolean hasEdge(N src, N dst) {
		return !src.to(dst).isEmpty();
	}

	@Override
	public UniqueList<E> getEdges(N src, N dst) {
		return graph.getEdges(Sense.BOTH, src, dst);
	}

	@Override
	public UniqueList<E> getEdges(Sense dir, N src, N dst) {
		return graph.getEdges(dir, src, dst);
	}

	public void addEdges(UniqueList<E> edges) {
		this.edges.addAll(edges);
	}

	public Builder<T, I, G, N, E> addEdges() {
		edges.forEach(e -> graph.addEdge(e));
		return this;
	}

	private void parseEdgeSpec(String edgeSpec) {
		edgeSpec = edgeSpec.trim();

		if (MARK.matcher(edgeSpec).find()) {
			String[] nodeSpec = MARK.split(edgeSpec);
			if (nodeSpec.length > 1) {
				for (int idx = 0; idx < nodeSpec.length - 1; idx++) {
					UniqueList<N> src = parseNodeSpec(nodeSpec[idx], true);
					UniqueList<N> dst = parseNodeSpec(nodeSpec[idx + 1], true);

					for (N beg : src) {
						for (N end : dst) {
							edges.add(graph.createEdge(beg, end));
						}
					}
				}
			}
		}
	}

	private UniqueList<N> parseNodeSpec(String spec, boolean create) {
		UniqueList<N> nodes = new UniqueList<>();

		spec = Strings.deQuote(spec.trim());
		String[] names = SEPR.split(spec);
		for (int idx = 0; idx < names.length; idx++) {
			String name = names[idx].trim();
			N node = getNode(name, true);
			if (node == null && create) {
				node = createNode(makeId(name));
			}
			if (node != null) {
				nodes.add(node);
				built.add(node);
			}
		}
		return nodes;
	}

	public void clear() {
		built.clear();
		edges.clear();
	}
}
