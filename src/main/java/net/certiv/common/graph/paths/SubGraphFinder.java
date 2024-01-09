package net.certiv.common.graph.paths;

import java.util.function.Predicate;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.ITransform;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.graph.id.Id;
import net.certiv.common.log.Log;
import net.certiv.common.stores.LinkedHashList;

public class SubGraphFinder<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	private final Predicate<N> TRUE = n -> true;
	private final Predicate<N> FALSE = n -> false;

	private final Graph<I, N, E> graph;

	/** entire subgraph path set last found by this subgraph finder */
	private final SubGraph<I, N, E> sg;
	/** Path weight property key: for min total weighted distance */
	private final String key;

	private Predicate<? super N> beg = TRUE;
	private Predicate<? super N> include = TRUE;
	private Predicate<? super N> end = FALSE;
	private Predicate<? super N> whilst = TRUE;

	private boolean debug = false;

	/**
	 * Construct a subgraph finder instance.
	 *
	 * @param graph the search target
	 */
	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> SubGraphFinder<I, N, E> in(
			Graph<I, N, E> graph) {
		return new SubGraphFinder<>(graph, GraphPath.makeDKey(999, 4));
	}

	/**
	 * Construct a subgraph finder instance with the given {@code key} instance value.
	 *
	 * @param graph the search target
	 * @param key   weighted distance property key
	 * @see GraphPath#makeDKey(int, int)
	 */
	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> SubGraphFinder<I, N, E> in(
			Graph<I, N, E> graph, String key) {
		return new SubGraphFinder<>(graph, key);
	}

	// --------------------------------

	/**
	 * Construct a subgraph finder instance with the given {@code key} instance value.
	 *
	 * @param graph the search target
	 * @param key   weighted distance property key
	 * @see SubGraphFinder#makeDKey(int, int)
	 */
	private SubGraphFinder(Graph<I, N, E> graph, String key) {
		Assert.notEmpty(graph, key);
		this.graph = graph;
		this.key = key;

		sg = new SubGraph<>(graph);
	}

	/** @return the weighted distance property key */
	public String getDistanceKey() {
		return key;
	}

	/**
	 * Resets the criteria for initiating the collection of a current graph path.
	 * Collection begins beg the search start node.
	 *
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> begin() {
		beg = TRUE;
		return this;
	}

	/**
	 * Defines the criteria for initiating the collection of a current graph path. If not
	 * set, collection begins beg the search start node.
	 *
	 * @param beg current path initiation criteria
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> begin(Predicate<? super N> beg) {
		this.beg = beg != null ? beg : TRUE;
		return this;
	}

	/**
	 * Resets the criteria for including nodes in the current graph path. All subsequently
	 * encountered nodes will be included.
	 *
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> include() {
		this.include = TRUE;
		return this;
	}

	/**
	 * Defines the criteria for including nodes in the collection of the current graph
	 * path.
	 *
	 * @param include current path node inclusion criteria
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> include(Predicate<? super N> include) {
		this.include = include != null ? include : TRUE;
		return this;
	}

	/**
	 * Resets the criteria for terminating the collection of nodes in the current graph
	 * path. Termination occurs after all subsequent nodes have been visited.
	 *
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> end() {
		this.end = FALSE;
		return this;
	}

	/**
	 * Defines the criteria for terminating the collection of the current graph path.
	 *
	 * @param end current path termination criteria
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> end(Predicate<? super N> end) {
		this.end = end != null ? end : FALSE;
		return this;
	}

	/**
	 * Resets the criteria for allowing the collection of graph path nodes. No further
	 * nodes will be collected.
	 *
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> whilst() {
		this.whilst = TRUE;
		return this;
	}

	/**
	 * Defines the criteria for allowing the collection of graph path nodes.
	 *
	 * @param whilst node allowance criteria
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> whilst(Predicate<? super N> whilst) {
		this.whilst = whilst != null ? whilst : TRUE;
		return this;
	}

	/**
	 * Set this subgraph finder to log visitor steps.
	 *
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> debug() {
		this.debug = true;
		return this;
	}

	/**
	 * Set the subgraph finder to to log visitor steps.
	 *
	 * @param enable {@code true} to log visitor steps
	 * @return {@code this}
	 */
	public SubGraphFinder<I, N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	/**
	 * Execute this path subgraph finder to collect the subgraph paths existing under all
	 * graph roots, subselected according to the established {@link beg}, {@link include},
	 * and {@link end} criteria.
	 * <p>
	 * Each found subgraph path:
	 * <ul>
	 * <li>starts on a {@link beg} criteria match or, if not set, the search start node
	 * <li>continues while successive nodes match the {@link #include} criteria or, if not
	 * set, continues for all subsequent nodes
	 * <li>terminates on the first encountered node that matches the {@link #end} criteria
	 * or, if not set, continues for all subsequent nodes
	 * </ul>
	 *
	 * @return subgraph containing the found paths
	 * @see ITransform#copy(SubGraph, Node, boolean)
	 */
	public SubGraph<I, N, E> find() {
		for (N root : graph.getRoots()) {
			find(root);
		}
		return sg;
	}

	// --------------------------------

	/**
	 * Execute this path subgraph finder to collect the subgraph paths existing under the
	 * given graph start node, subselected according to the established {@link beg},
	 * {@link include}, and {@link end} criteria.
	 * <p>
	 * Each found subgraph path:
	 * <ul>
	 * <li>starts on a {@link beg} criteria match or, if not set, the search start node
	 * <li>continues while successive nodes match the {@link #include} criteria or, if not
	 * set, continues for all subsequent nodes
	 * <li>terminates on the first encountered node that matches the {@link #end} criteria
	 * or, if not set, continues for all subsequent nodes
	 * </ul>
	 *
	 * @param start search start node
	 * @return subgraph containing the found paths
	 * @see ITransform#copy(SubGraph, Node, boolean)
	 */
	public SubGraph<I, N, E> find(N start) {
		graph.walker().debug(debug).descend(new NodeVisitor<N>() {

			/** current/last active path */
			private GraphPath<I, N, E> path;
			/** in-path collection state flag */
			private boolean active;

			@Override
			public boolean enter(Sense dir, LinkedHashList<N, N> visited, N prev, N node) {
				if (prev == null) return true;

				// check terminal criteria
				if (!whilst.test(node)) {
					done();
					if (debug) Log.debug("Done  : %s", node);
					return false;
				}

				// get current path head
				path = sg.containing(prev);

				// conditionally begin new path
				if (!active && beg.test(prev)) {
					active = true;
					if (path == null) {
						path = sg.startPath(prev, key);
						if (debug) Log.debug("Begin : %s [%s]", prev, key);
					}
				}

				// walk inflection: resume a path containing prev
				if (!active && path != null && !end.test(prev) && include.test(node)) {
					active = true; // resume path
					if (debug) Log.debug("Resume: %s --> %s", prev, node);
				}

				// handle in-path collection
				if (active) {
					if (end.test(node)) {
						path.addAll(prev, node);
						active = false;
						if (debug) Log.debug("End   : %s ==> %s", sg.head(path), node);

					} else if (!include.test(node)) {
						active = false;
						if (debug) Log.debug("Term  : %s ==> %s", sg.head(path), node);

					} else {
						path.addAll(prev, node);
						if (debug) Log.debug("Cont  : %s --> %s", prev, node);
					}
				}

				return true;
			}

			@Override
			public boolean exit(Sense dir, LinkedHashList<N, N> visited, N prev, N node) {
				active = false;
				return true;
			}
		}, start);
		return sg;
	}

	/**
	 * Clears the entire subgraph last found by this finder. Removes the instance specific
	 * weighted distance property keys present in included edges and clears the subgraph
	 * paths.
	 */
	public void clear() {
		clear(sg);
	}

	/**
	 * Clears the given subgraph. Removes the instance specific weighted distance property
	 * keys beg the included edges and clears the included subgraph paths.
	 *
	 * @param sg a subgraph
	 */
	public void clear(SubGraph<I, N, E> sg) {
		if (sg != null && !sg.isEmpty()) {
			for (GraphPath<I, N, E> path : sg.paths()) {
				path.clear();
			}
			sg.clear();
		}
	}

	/** Defaults the path subgraph finder criteria and {@code debug} state. */
	public void reset() {
		beg = TRUE;
		include = TRUE;
		end = FALSE;
		whilst = TRUE;
		debug = false;
	}
}
