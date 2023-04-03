package net.certiv.common.graph;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.log.Log;
import net.certiv.common.stores.LinkedHashList;

public class Walker<N extends Node<N, E>, E extends Edge<N, E>> {

	private boolean debug;

	public Walker() {
		this.debug = false;
	}

	public Walker(boolean debug) {
		this.debug = debug;
	}

	public void ascend(NodeVisitor<N> visitor, N node) {
		LinkedHashList<N, N> visited = new LinkedHashList<>();
		walk(Sense.IN, visited, visitor, null, node);
	}

	public void descend(NodeVisitor<N> visitor, N node) {
		LinkedHashList<N, N> visited = new LinkedHashList<>();
		walk(Sense.OUT, visited, visitor, null, node);
	}

	public Walker<N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	private void walk(Sense dir, LinkedHashList<N, N> visited, NodeVisitor<N> visitor, N parent, N node) {
		boolean ok = enter(dir, visited, visitor, parent, node);
		if (debug) {
			Integer cnt = node != null ? node.size(dir, e -> !e.cyclic()) : null;
			Log.debug(this, "[enter=%s] %s -> %s (%s)", ok, parent, node, cnt);
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
