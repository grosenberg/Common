package net.certiv.common.graph;

import java.util.function.Predicate;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.Holder;
import net.certiv.common.stores.LinkedHashList;
import net.certiv.common.stores.UniqueList;

public class Finder<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> {

	private final Predicate<N> TRUE = n -> true;
	private final Predicate<N> FALSE = n -> false;

	private final Graph<I, N, E> graph;

	private Predicate<? super N> include = TRUE;
	private Predicate<? super N> exclude = FALSE;
	private Predicate<? super N> whilst = TRUE;
	private boolean debug = false;

	public static <I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>> Finder<I, N, E> in(
			Graph<I, N, E> graph) {
		return new Finder<>(graph);
	}

	private Finder(Graph<I, N, E> graph) {
		this.graph = graph;
	}

	public Finder<I, N, E> include() {
		this.include = TRUE;
		return this;
	}

	public Finder<I, N, E> include(Predicate<? super N> include) {
		this.include = include != null ? include : TRUE;
		return this;
	}

	public Finder<I, N, E> exclude() {
		this.exclude = FALSE;
		return this;
	}

	public Finder<I, N, E> exclude(Predicate<? super N> exclude) {
		this.exclude = exclude != null ? exclude : FALSE;
		return this;
	}

	public Finder<I, N, E> whilst() {
		this.whilst = TRUE;
		return this;
	}

	public Finder<I, N, E> whilst(Predicate<? super N> whilst) {
		this.whilst = whilst != null ? whilst : TRUE;
		return this;
	}

	/**
	 * Find all nodes in the graph that satisfy the {@link #include} and {@link #exclude}
	 * criteria during the {@link #whilst} criteria.
	 *
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<N> all() {
		UniqueList<N> all = new UniqueList<>();
		for (N root : graph.getRoots()) {
			all.addAll(all(root));
		}
		return all;
	}

	/**
	 * Find all nodes in the graph, beginning from the given start node, that satisfy the
	 * {@link #include} and {@link #exclude} criteria during the {@link #whilst} criteria.
	 *
	 * @param start graph walk start node
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<N> all(N start) {
		UniqueList<N> all = new UniqueList<>();
		if (start != null) {
			Walker<I, N, E> walker = graph.walker().debug(debug);
			walker.descend(new NodeVisitor<N>() {

				@Override
				public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
					if (!whilst.test(node)) return false;
					if (include.test(node) && !exclude.test(node)) {
						all.add(node);
					}
					return true;
				}

			}, start);
		}

		return all;
	}

	/**
	 * Find the first node in the graph that satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return the first selected node, or {@code null} if none found
	 */
	public N first() {
		for (N root : graph.getRoots()) {
			N found = first(root);
			if (found != null) return found;
		}
		return null;
	}

	/**
	 * Find the first node in the graph, beginning from the given start node, that
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @param start graph walk start node
	 * @return the first selected node, or {@code null} if none found
	 */
	public N first(N start) {
		Holder<N> found = new Holder<>();
		if (start != null) {
			Walker<I, N, E> walker = graph.walker().debug(debug);
			walker.descend(new NodeVisitor<N>() {

				@Override
				public boolean enter(Sense dir, LinkedHashList<N, N> visited, N parent, N node) {
					if (!whilst.test(node)) return false;
					if (include.test(node) && !exclude.test(node)) {
						found.set(node);
						stop();
					}
					return true;
				}

			}, start);
		}

		return found.get();
	}

	/**
	 * Find the last node in the graph that satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 *
	 * @return the first selected node, or {@code null} if none found
	 */
	public N last() {
		return all().peekLast();
	}

	/**
	 * Find the last node in the graph, beginning from the given start node, that
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 *
	 * @param start graph walk start node
	 * @return the first selected node, or {@code null} if none found
	 */
	public N last(N start) {
		return all(start).peekLast();
	}

	/**
	 * Returns whether any node in the graph satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return {@code true} if any criterial qualified node exists in the graph
	 */
	public boolean any() {
		return first() != null;
	}

	/**
	 * Returns whether any node in the graph, beginning from the given start node,
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return {@code true} if any criterial qualified node exists in the graph
	 */
	public boolean any(N start) {
		return first(start) != null;
	}

	/**
	 * Set this finder to log visitor steps.
	 *
	 * @return the finder
	 */
	public Finder<I, N, E> debug() {
		this.debug = true;
		return this;
	}

	/**
	 * Set this finder to log visitor steps.
	 *
	 * @param enable {@code true} to log visitor steps
	 * @return the finder
	 */
	public Finder<I, N, E> debug(boolean enable) {
		this.debug = enable;
		return this;
	}

	/** Defaults the finder criteria and {@code debug} state. */
	public void reset() {
		include = TRUE;
		exclude = FALSE;
		whilst = TRUE;
		debug = false;
	}
}
