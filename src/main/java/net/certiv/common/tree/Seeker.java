package net.certiv.common.tree;

import java.util.function.Predicate;

import net.certiv.common.stores.Holder;
import net.certiv.common.stores.UniqueList;

public class Seeker<T> {

	private final Predicate<TreeNode<T>> TRUE = n -> true;
	private final Predicate<TreeNode<T>> FALSE = n -> false;

	private final Forest<T> forest;

	private Predicate<TreeNode<T>> include = TRUE;
	private Predicate<TreeNode<T>> exclude = FALSE;
	private Predicate<TreeNode<T>> whilst = TRUE;

	/**
	 * Instantiate a new seeker on the given forest.
	 *
	 * @param forest search target
	 * @return new seeker instance
	 */
	public static <T> Seeker<T> in(Forest<T> forest) {
		return new Seeker<>(forest);
	}

	/**
	 * Private constructor
	 *
	 * @param forest search target
	 */
	private Seeker(Forest<T> forest) {
		this.forest = forest;
	}

	/**
	 * Node search {@code include} {@code TRUE} criteria.
	 *
	 * @return this seeker instance
	 */
	public Seeker<T> include() {
		this.include = TRUE;
		return this;
	}

	/**
	 * Node search using the given {@code include} criteria.
	 *
	 * @param include node inclusion criteria
	 * @return this seeker instance
	 */
	public Seeker<T> include(Predicate<TreeNode<T>> include) {
		this.include = include != null ? include : TRUE;
		return this;
	}

	/**
	 * Node search {@code exclude} {@code FALSE} criteria.
	 *
	 * @return this seeker instance
	 */
	public Seeker<T> exclude() {
		this.exclude = FALSE;
		return this;
	}

	/**
	 * Node search using the given {@code exclude} criteria.
	 *
	 * @param exclude node exclusion criteria
	 * @return this seeker instance
	 */
	public Seeker<T> exclude(Predicate<TreeNode<T>> exclude) {
		this.exclude = exclude != null ? exclude : FALSE;
		return this;
	}

	/**
	 * Node search {@code continue while} {@code TRUE} criteria.
	 *
	 * @return this seeker instance
	 */
	public Seeker<T> whilst() {
		this.whilst = TRUE;
		return this;
	}

	public Seeker<T> whilst(Predicate<TreeNode<T>> whilst) {
		this.whilst = whilst != null ? whilst : TRUE;
		return this;
	}

	/**
	 * Find all nodes in the forest that satisfy the {@link #include} and {@link #exclude}
	 * criteria during the {@link #whilst} criteria.
	 *
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<TreeNode<T>> all() {
		UniqueList<TreeNode<T>> all = new UniqueList<>();
		for (TreeNode<T> root : forest.roots()) {
			all.addAll(all(root));
		}
		return all;
	}

	/**
	 * Find all nodes in the forest, beginning from the given start node, that satisfy the
	 * {@link #include} and {@link #exclude} criteria during the {@link #whilst} criteria.
	 *
	 * @param start forest walk start node
	 * @return the selected nodes in encounter order
	 */
	public UniqueList<TreeNode<T>> all(TreeNode<T> start) {
		UniqueList<TreeNode<T>> all = new UniqueList<>();
		if (start != null) {
			forest.dfsWalk(node -> {
				if (!whilst.test(node)) return false;
				if (include.test(node) && !exclude.test(node)) {
					all.add(node);
				}
				return true;
			}, start);
		}
		return all;
	}

	/**
	 * Find the first node in the forest that satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return the first selected node, or {@code null} if none found
	 */
	public TreeNode<T> first() {
		for (TreeNode<T> root : forest.roots()) {
			TreeNode<T> found = first(root);
			if (found != null) return found;
		}
		return null;
	}

	/**
	 * Find the first node in the forest, beginning from the given start node, that
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @param start forest walk start node
	 * @return the first selected node, or {@code null} if none found
	 */
	public TreeNode<T> first(TreeNode<T> start) {
		Holder<TreeNode<T>> found = new Holder<>();
		if (start != null) {
			forest.dfsWalk(node -> {
				if (!whilst.test(node)) return false;
				if (include.test(node) && !exclude.test(node)) {
					found.set(node);
					return false;
				}
				return true;
			}, start);
		}
		return found.get();
	}

	/**
	 * Find the last node in the forest that satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 *
	 * @return the first selected node, or {@code null} if none found
	 */
	public TreeNode<T> last() {
		return all().peekLast();
	}

	/**
	 * Find the last node in the forest, beginning from the given start node, that
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 *
	 * @param start forest walk start node
	 * @return the first selected node, or {@code null} if none found
	 */
	public TreeNode<T> last(TreeNode<T> start) {
		return all(start).peekLast();
	}

	/**
	 * Returns whether any node in the forest satisfies the {@link #include} and
	 * {@link #exclude} criteria during the {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return {@code true} if any criterial qualified node exists in the forest
	 */
	public boolean any() {
		return first() != null;
	}

	/**
	 * Returns whether any node in the forest, beginning from the given start node,
	 * satisfies the {@link #include} and {@link #exclude} criteria during the
	 * {@link #whilst} criteria.
	 * <p>
	 * This is a short-circuiting terminal operation.
	 *
	 * @return {@code true} if any criterial qualified node exists in the forest
	 */
	public boolean any(TreeNode<T> start) {
		return first(start) != null;
	}

	/** Defaults the seeker criteria and {@code debug} state. */
	public void reset() {
		include = TRUE;
		exclude = FALSE;
		whilst = TRUE;
	}
}
