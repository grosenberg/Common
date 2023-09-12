package net.certiv.common.graph;

import java.util.function.Predicate;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.Holder;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;

public class Finder<N extends Node<N, E>, E extends Edge<N, E>> {

	private final Graph<N, E> graph;

	public Finder(Graph<N, E> graph) {
		this.graph = graph;
	}

	/**
	 * Find all nodes in the graph that satisfy the given include criteria.
	 *
	 * @param include an inclusion predicate
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<N> all(Predicate<? super N> include) {
		return all(include, null);
	}

	/**
	 * Find all nodes in the graph that satisfy the given include and exclude criteria. A
	 * {@code null} exclusion criteria excludes none.
	 *
	 * @param include an inclusion predicate
	 * @param exclude an exclusion predicate
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<N> all(Predicate<? super N> include, Predicate<? super N> exclude) {
		UniqueList<N> all = new UniqueList<>();
		for (N root : graph.getRoots()) {
			all.addAll(all(root, include, exclude));
		}
		return all;
	}

	/**
	 * Find all nodes in the graph, under the given start node, that satisfy the given
	 * include and exclude criteria. A {@code null} exclusion criteria excludes none.
	 *
	 * @param start   a start node
	 * @param include an inclusion predicate
	 * @param exclude an exclusion predicate
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<N> all(N start, Predicate<? super N> include, Predicate<? super N> exclude) {
		UniqueList<N> all = new UniqueList<>();
		if (start != null) {
			Walker<N, E> walker = graph.walker();
			walker.descend(new NodeVisitor<N>() {

				@Override
				public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
					if (include.test(node)) {
						if (exclude == null || !exclude.test(node)) {
							all.add(node);
						}
					}
					return true;
				}

			}, start);
		}

		return all;
	}

	/**
	 * Finds the first node in the graph that satisfies the given include criteria.
	 *
	 * @param include an inclusion predicate
	 * @return the first selected node, or {@code null} if none found
	 */
	public N first(Predicate<? super N> include) {
		return first(include, null);
	}

	/**
	 * Finds the first node in the graph that satisfies the given include and exclude
	 * criteria. A {@code null} exclusion criteria excludes none.
	 *
	 * @param include an inclusion predicate
	 * @param exclude an exclusion predicate
	 * @return the first selected node, or {@code null} if none found
	 */
	public N first(Predicate<? super N> include, Predicate<? super N> exclude) {
		for (N root : graph.getRoots()) {
			N found = first(root, include, exclude);
			if (found != null) return found;
		}
		return null;
	}

	/**
	 * Finds the first node in the graph, under the given start node, that satisfies the
	 * given include and exclude criteria. A {@code null} exclusion criteria excludes
	 * none.
	 *
	 * @param start   a start node
	 * @param include an inclusion predicate
	 * @param exclude an exclusion predicate
	 * @return the first selected node, or {@code null} if none found
	 */
	public N first(N start, Predicate<? super N> include, Predicate<? super N> exclude) {
		Holder<N> found = new Holder<>();
		if (start != null) {
			Walker<N, E> walker = graph.walker();
			walker.descend(new NodeVisitor<N>() {

				boolean ok = true;

				@Override
				public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
					if (ok && include.test(node)) {
						if (exclude == null || !exclude.test(node)) {
							found.value = node;
							ok = false;
						}
					}
					return ok;
				}

			}, start);
		}

		return found.value;
	}
}
