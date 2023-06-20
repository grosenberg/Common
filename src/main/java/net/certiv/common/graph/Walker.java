package net.certiv.common.graph;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.log.Log;
import net.certiv.common.stores.LinkedHashList;

/**
 * Walker supporting both ascending and descending path traversals.
 * <p>
 * Allows walking of most any graph connectivity, including multi-graphs with single node
 * cycles. Internally records each adjacent node path interval and precluding repeat
 * traversal of visited intervals. This also allows repeat use of the walker, typically
 * starting in different direction or from a different start node, to walk the graph
 * without repeat traversal of any previously visited path internval.
 * <p>
 * Reseting the walker clears the record or internal visited path intervals.
 */
public class Walker<N extends Node<N, E>, E extends Edge<N, E>> {

	private final LinkedHashList<N, N> visited = new LinkedHashList<>();
	private boolean debug;

	public Walker() {
		this(false);
	}

	public Walker(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Initiate a graph walk ascending from the given node. Applies the given visitor to
	 * each visited node, including the start node.
	 *
	 * @param visitor the node visitor action
	 * @param start   the node to start ascending from
	 */
	public void ascend(NodeVisitor<N> visitor, N start) {
		walk(Sense.IN, visited, visitor, null, start);
	}

	/**
	 * Initiate a graph walk descending from the given node. Applies the given visitor to
	 * each visited node, including the start node.
	 *
	 * @param visitor the node visitor action
	 * @param start   the node to start descending from
	 */
	public void descend(NodeVisitor<N> visitor, N from) {
		walk(Sense.OUT, visited, visitor, null, from);
	}

	public Walker<N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	/**
	 * Clears the internal record of visited paths (represented as a collection of node
	 * pairings).
	 */
	public void reset() {
		visited.clear();
	}

	private void walk(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N parent, N node) {
		boolean ok = enter(dir, visited, visitor, parent, node);
		if (debug) {
			Integer cnt = node != null ? node.size(dir, e -> !e.cyclic()) : null;
			Log.debug("[enter=%s] %s -> %s (%s)", ok, parent, node, cnt);
		}

		if (ok) {
			// walk including cycles, relying on visit check to terminate branch
			for (N child : node.adjacent(dir, true)) {
				walk(dir, visited, visitor, node, child);
			}
		}
		exit(dir, visited, visitor, parent, node);
	}

	/**
	 * Invoke the delegate visitor with the given parameters. Return {@code true} if the
	 * walker should walk the children of the given node.
	 * <p>
	 * Descent walks are identified by a {@code Sense.OUT} direction. Ascent walks are
	 * identified by {@code Sense.OUT} direction.
	 *
	 * @param dir     the walk traversal direction
	 * @param visited collection of previously visited parent/node combinations
	 * @param visitor the delegate visitor
	 * @param parent  the parent node
	 * @param node    the current node
	 * @return {@code true} to walk the children of the current node
	 */
	protected boolean enter(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N parent,
			N node) {
		if (parent == null && node == null) return false;
		if (visited.containsEntry(parent, node)) return false;

		boolean ok = node.enter(dir, visited, visitor, parent);
		visited.put(parent, node);
		return ok;
	}

	protected boolean exit(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N parent,
			N node) {
		return node.exit(dir, visited, visitor, parent);
	}

	public static abstract class NodeVisitor<T> {

		/**
		 * Evaluate given parameters on {@code entry} -- before walking the children of
		 * the given node. Return {@code true} if the walker should walk the children of
		 * the given node.
		 * <p>
		 * Descent walks are identified by a {@code Sense.OUT} direction. Ascent walks are
		 * identified by {@code Sense.OUT} direction.
		 *
		 * @param dir     the walk traversal direction
		 * @param visited collection of previously visited parent/node combinations
		 * @param parent  the parent node
		 * @param node    the current node
		 * @return {@code true} to walk the children of the current node
		 */
		public boolean enter(Sense dir, LinkedHashList<T, T> visited, T parent, T node) {
			return true;
		}

		/**
		 * Evaluate given parameters on {@code exit} -- after walking the children of the
		 * given node. Return {@code true} on success (not currently used).
		 * <p>
		 * Descent walks are identified by a {@code Sense.OUT} direction. Ascent walks are
		 * identified by {@code Sense.OUT} direction.
		 *
		 * @param dir     the walk traversal direction
		 * @param visited collection of previously visited parent/node combinations
		 * @param parent  the parent node
		 * @param node    the current node
		 * @return {@code true} on success
		 */
		public boolean exit(Sense dir, LinkedHashList<T, T> visited, T parent, T node) {
			return true;
		}
	}
}
