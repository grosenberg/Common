package net.certiv.common.graph;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.id.IUId;
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
public class Walker<I extends IUId, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	private static class StopEx extends RuntimeException {}

	private static final StopEx EX_STOP = new StopEx();

	private final LinkedHashList<N, N> visited = new LinkedHashList<>();
	private boolean debug;

	/** Construct a walker instance. */
	public Walker() {
		this(false);
	}

	/**
	 * Construct a walker instance.
	 *
	 * @param debug {@code true} to log walker steps
	 */
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
		try {
			walk(Sense.IN, visited, visitor, null, start);
		} catch (StopEx flag) {}
	}

	/**
	 * Initiate a graph walk descending from the given node. Applies the given visitor to
	 * each visited node, including the start node.
	 *
	 * @param visitor the node visitor action
	 * @param start   the node to start descending from
	 */
	public void descend(NodeVisitor<N> visitor, N start) {
		try {
			walk(Sense.OUT, visited, visitor, null, start);
		} catch (StopEx flag) {}
	}

	/**
	 * Set the walker to to log walker steps.
	 *
	 * @return the walker
	 */
	public Walker<I, N, E> debug() {
		this.debug = true;
		return this;
	}

	/**
	 * Set the walker to to log walker steps.
	 *
	 * @param enable {@code true} to log walker steps
	 * @return the walker
	 */
	public Walker<I, N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	/**
	 * Clears the internal record of visited node associations.
	 */
	public void reset() {
		visited.clear();
	}

	/**
	 * Returns a copy of the visited node associations.
	 *
	 * @return list of prev -> node(s) visited
	 */
	public LinkedHashList<N, N> visited() {
		return new LinkedHashList<>(visited);
	}

	private void walk(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N prev, N node) {
		if (!visitor.done) {
			boolean ok = enter(dir, visited, visitor, prev, node);
			if (debug) Log.debug("[enter=%s] %s --> %s", ok ? "Ok" : "Xx", prev, node);

			if (ok) {
				for (N child : node.adjacent(dir, true)) {
					if (!visitor.done) {
						walk(dir, visited, visitor, node, child);
					}
				}
			}
		}
		boolean ok = exit(dir, visited, visitor, prev, node);
		if (debug) Log.debug("[exit =%s] %s <-- %s", ok ? "Ok" : "Fail", prev, node);
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
	 * @param prev    the previously visited, nominally parent, node
	 * @param node    the current node
	 * @return {@code true} to walk the children of the current node
	 */
	protected boolean enter(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N prev, N node) {
		if (prev == null && node == null) return false;
		if (visited.containsEntry(prev, node)) return false;

		boolean ok = node.enter(dir, visited, visitor, prev);
		visited.put(prev, node);
		return ok;
	}

	protected boolean exit(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N prev, N node) {
		return node.exit(dir, visited, visitor, prev);
	}

	public static abstract class NodeVisitor<T> {

		/** Flag to block visitor from entering previously unvisited nodes. */
		private boolean done = false;

		/**
		 * Evaluate given parameters on {@code entry} -- before walking the children of
		 * the given node. Return {@code true} if the walker should walk the children of
		 * the given node.
		 * <p>
		 * Descent walks are identified by a {@code Sense.OUT} direction. Ascent walks are
		 * identified by {@code Sense.IN} direction.
		 * <p>
		 * When beginning a walk, regardless of direction, the {@code prev} node will be
		 * {@code null} and the {@code node} value will be the starting node.
		 *
		 * @param dir     the walk traversal direction
		 * @param visited collection of previously visited parent/node combinations
		 * @param prev    the previously visited, nominally parent, node
		 * @param node    the current node
		 * @return {@code true} to walk the children, if any, of the current node, or
		 *         {@code false} to skip waling any childref of the current node
		 */
		public boolean enter(Sense dir, LinkedHashList<T, T> visited, T prev, T node) {
			return true;
		}

		/**
		 * Evaluate given parameters on {@code exit} -- after walking the children of the
		 * given node. Return {@code true} on success (not currently used).
		 * <p>
		 * Descent walks are identified by a {@code Sense.OUT} direction. Ascent walks are
		 * identified by {@code Sense.IN} direction.
		 * <p>
		 * When beginning a walk, regardless of direction, the {@code prev} node will be
		 * {@code null} and the {@code node} value will be the starting node.
		 *
		 * @param dir     the walk traversal direction
		 * @param visited collection of previously visited parent/node combinations
		 * @param prev    the previously visited, nominally parent, node
		 * @param node    the current node
		 * @return {@code true} on success
		 */
		public boolean exit(Sense dir, LinkedHashList<T, T> visited, T prev, T node) {
			return true;
		}

		/**
		 * Mark the walk as complete. The walker will not enter any previously unvisited
		 * nodes. The walker will continue to exit any previously entered/not yet exited
		 * nodes.
		 */
		public void done() {
			done = true;
		}

		/**
		 * Force an immediate walk termination. The walker will neither enter any
		 * previously unvisited nodes nor will it exit any previously entered/not yet
		 * exited nodes.
		 *
		 * @implNote throws an internal runtime exception that is silently caught at the
		 *           top level of the walker
		 */
		public void stop() {
			throw EX_STOP;
		}
	}
}
