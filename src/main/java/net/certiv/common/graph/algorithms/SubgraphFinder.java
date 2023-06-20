package net.certiv.common.graph.algorithms;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.ITransform;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;
import net.certiv.common.util.Maths;

public class SubgraphFinder<N extends Node<N, E>, E extends Edge<N, E>> {

	private static final String DKEY_PREFIX = "DKEY-";
	private static final GraphException ERR_DKEY = GraphEx.of("No weighted distance key/value for edge: %s");

	private final Graph<N, E> graph;
	/** property key for min total weighted distance */
	private String key;
	/** entire subgraph last found by this finder */
	private LinkedHashMap<N, GraphPath<N, E>> subgraph;

	/**
	 * Construct a finder instance.
	 *
	 * @param graph the search target
	 */
	public SubgraphFinder(Graph<N, E> graph) {
		this.graph = graph;
		this.key = makeDKey(999, 4);
	}

	/**
	 * Construct a finder instance with the given {@code key} instance value.
	 *
	 * @param graph the search target
	 * @param key   weighted distance property key
	 * @see SubgraphFinder#makeDKey(int, int)
	 */
	public SubgraphFinder(Graph<N, E> graph, String key) {
		Assert.notEmpty(key);
		this.graph = graph;
		this.key = key;
	}

	/**
	 * Make a pseudo random property head for use in storing the min total weighted
	 * distance on each edge found using this finder instance.
	 *
	 * @param bound the positive upper bound
	 * @param width the resultant string width
	 * @return a next positive pseudorandom number
	 * @throws IllegalArgumentException if parameters are not positive
	 * @see Maths#nextRandomFilled(int, int)
	 */
	public static String makeDKey(int bound, int width) {
		return DKEY_PREFIX + Maths.nextRandomFilled(bound, width);
	}

	/** @return the weighted distance property key */
	public String getDistanceKey() {
		return key;
	}

	/**
	 * Clears the entire subgraph last found by this finder. Removes the instance specific
	 * weighted distance property keys from all included edges and clears the subgraph
	 * paths.
	 */
	public void clear() {
		clear(subgraph);
	}

	/**
	 * Clears the given subgraph. Removes the instance specific weighted distance property
	 * keys from the included edges and clears the included subgraph paths.
	 *
	 * @param subgraph a subgraph path map
	 */
	public void clear(LinkedHashMap<N, GraphPath<N, E>> subgraph) {
		if (subgraph != null && !subgraph.isEmpty()) {
			for (GraphPath<N, E> path : subgraph.values()) {
				path.clear();
			}
			subgraph.clear();
		}
		subgraph = null;
	}

	/**
	 * Finds the subgraph paths following from the given node in this graph.
	 * <p>
	 * Returns a map of: {@code head=unique head node; value=GraphPath}.
	 *
	 * @param from an initial node
	 * @return subgraph path map
	 * @see ITransform#copy(LinkedHashMap, Node, boolean)
	 */
	public LinkedHashMap<N, GraphPath<N, E>> subset(N from) {
		return subset(from, n -> n.equals(from), n -> n != null, n -> n == null);
	}

	/**
	 * Returns the subset path traversed from the given begin node and ending at the given
	 * end node.
	 * <p>
	 * Returns a map of: {@code head=unique head node; value=GraphPath}.
	 *
	 * @param beg a beg node
	 * @param end an ending node
	 * @return subgraph path map
	 * @see ITransform#copy(LinkedHashMap, Node, boolean)
	 */
	public LinkedHashMap<N, GraphPath<N, E>> subset(N beg, N end) {
		return subset(beg, n -> n.equals(beg), n -> n != null, n -> n.equals(end));
	}

	/**
	 * Returns the subgraph paths chosen following from the given node, further
	 * subselected according to the given include. Each path:
	 * <ul>
	 * <li>starts on a unique begin include match
	 * </ul>
	 * <p>
	 * Returns a map of: {@code head=unique head node; value=GraphPath}.
	 *
	 * @param from search begin node
	 * @param beg  subgraph collection begin selection include
	 * @return subgraph path map
	 * @see ITransform#copy(LinkedHashMap, Node, boolean)
	 */
	public LinkedHashMap<N, GraphPath<N, E>> subset(N from, Predicate<? super N> beg) {
		return subset(from, beg, n -> n != null, n -> n == null);
	}

	/**
	 * Returns the subgraph paths chosen following from the given node, further
	 * subselected according to the given filters. Each path:
	 * <ul>
	 * <li>starts on a unique begin include match
	 * <li>ends on an end include match
	 * </ul>
	 * <p>
	 * Returns a map of: {@code head=unique head node; value=GraphPath}.
	 *
	 * @param from search begin node
	 * @param beg  subgraph collection begin selection include
	 * @param end  subgraph collection end selection include
	 * @return subgraph path map
	 * @see ITransform#copy(LinkedHashMap, Node, boolean)
	 */
	public LinkedHashMap<N, GraphPath<N, E>> subset(N from, Predicate<? super N> beg,
			Predicate<? super N> end) {
		return subset(from, beg, n -> n != null, end);
	}

	/**
	 * Returns the subgraph paths chosen following from the given node, further
	 * subselected according to the given filters. Each path:
	 * <ul>
	 * <li>starts on a unique begin include match
	 * <li>continues while successive nodes match the include include
	 * <li>ends on the first of an end include match or an include include non-match
	 * </ul>
	 * <p>
	 * Returns a map of: {@code head=unique head node; value=GraphPath}.
	 *
	 * @param from    search begin node
	 * @param beg     subgraph collection begin selection include
	 * @param include subgraph collection duration inclusion include
	 * @param end     subgraph collection end selection include
	 * @return subgraph path map
	 * @see ITransform#copy(LinkedHashMap, Node, boolean)
	 */
	public LinkedHashMap<N, GraphPath<N, E>> subset(N from, Predicate<? super N> beg,
			Predicate<? super N> include, Predicate<? super N> end) {
		Tracer tracer = new Tracer(beg, include, end);
		graph.walker().descend(tracer, from);
		subgraph = tracer.found();
		return subgraph;
	}

	// --------------------------------------

	private class Tracer extends NodeVisitor<N> {

		/** current subgraph: {@code key=head node; value=graphpath} */
		private final LinkedHashMap<N, GraphPath<N, E>> paths = new LinkedHashMap<>();

		private final Predicate<? super N> beg;
		private final Predicate<? super N> include;
		private final Predicate<? super N> end;

		/** transision state flag */
		private boolean exiting;
		/** collection state flag */
		private boolean active;

		/** current/last active path head */
		private N head;
		/** current/last active path */
		private GraphPath<N, E> path;
		/** current/last node entered */
		private N last;

		public Tracer(Predicate<? super N> beg, Predicate<? super N> include, Predicate<? super N> end) {
			this.beg = beg;
			this.include = include;
			this.end = end;
		}

		public LinkedHashMap<N, GraphPath<N, E>> found() {
			return paths;
		}

		@Override
		public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
			// Log.debug("In.: %s->%s", parent, node);
			last = node;

			// handle path start
			if (parent != null && beg.test(parent)) {
				active = true;
				head = parent; // beg new path
				if (!paths.containsKey(head)) {
					paths.put(head, new GraphPath<>(key));
					// Log.debug("New: %s->%s[%s]", parent, node, key);
					// } else {
					// Log.debug("Beg: %s->%s", parent, node);
				}
				path = paths.get(head);
			}

			// handle walk inflection: resume an existing path
			if (!active && exiting && !end.test(parent) && inPath(parent)) {
				// Log.debug("Res: %s->%s", parent, node);
				active = true; // resume path
				path = containingPath(parent);
				head = path.peekFirst().beg();
			}

			// handle active path
			if (active) {
				if (end.test(node)) {
					addEdges(parent, node);
					path.addTerminal(node);
					// Log.debug("Tm+: %s", node);

					active = false;
					// Log.debug("StE: %s->%s", head, node);

				} else if (!include.test(node)) {
					path.addTerminal(parent);
					// Log.debug("Tm+: %s", parent);

					active = false;
					// Log.debug("StI: %s->%s", head, node);

				} else {
					addEdges(parent, node);
				}
			}

			exiting = false;
			return true;
		}

		@Override
		public boolean exit(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
			exiting = true;
			if (atActiveDistal(node)) {
				path.addTerminal(node);
				// Log.debug("Tm+: %s", node);

				active = false;
				// Log.debug("StD: %s->%s", head, node);
			}
			return true;
		}

		private boolean inPath(N node) {
			if (paths.containsKey(node)) return true;
			return paths.values().stream().anyMatch(p -> p.contains(node));
		}

		private GraphPath<N, E> containingPath(N node) {
			return paths.values().stream().filter(p -> p.contains(node)).findFirst().orElse(null);
		}

		private void addEdges(N parent, N node) {
			for (E edge : parent.to(node)) {
				path.addLast(edge);
				/* double w = */ updateMinWeight(edge);
				// Log.debug("Add: %s[%s]", edge, w);
			}
			if (path.containsTerminal(parent)) {
				path.removeTerminal(parent);
				// Log.debug("Tm-: %s", parent);
			}
		}

		/** @return {@code true} if the walk is at an active distal node */
		private boolean atActiveDistal(N node) {
			return active && last.equals(node) && noNextInPath(node);
		}

		/** @return {@code true} if the active path does not now contain any next node */
		private boolean noNextInPath(N node) {
			return active && node.adjacent(Sense.OUT, nxt -> path.contains(nxt)).isEmpty();
		}

		private double updateMinWeight(E edge) {
			double w = edge.weight();
			w = supra(edge.beg()) + w;
			edge.put(key, w);
			return w;
		}

		/** @return the min edge weight of any in-path parent edge, or 0 */
		private double supra(N beg) {
			UniqueList<E> edges = beg.edges(Sense.IN, e -> !e.cyclic() && path.contains(e));
			if (edges.isEmpty()) return 0.0;

			double min = Double.POSITIVE_INFINITY;
			for (E edge : edges) {
				Double val = edge.get(key);
				Assert.notNull(ERR_DKEY.on(edge), val);
				min = Math.min(min, val);
			}
			return min;
		}
	}
}
