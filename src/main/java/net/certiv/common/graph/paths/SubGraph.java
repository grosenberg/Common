package net.certiv.common.graph.paths;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Node;

/**
 * Sub-graph of a graph represented by a set of graph paths.
 */
public class SubGraph<N extends Node<N, E>, E extends Edge<N, E>> implements Iterable<GraphPath<N, E>> {

	/** {@code key=head node; value=graph path} */
	private final LinkedHashMap<N, GraphPath<N, E>> paths = new LinkedHashMap<>();

	/**
	 * Return whether a path is associated with the given head node.
	 *
	 * @param head a head node
	 * @return {@code true} if a path is associated with the given head node
	 */
	public boolean hasPath(N head) {
		return paths.containsKey(head);
	}

	/**
	 * Returns the path associated with the given head node.
	 *
	 * @param head a head node
	 * @return the path associated with the given head node, or {@code null} if none
	 *         exists
	 */
	public GraphPath<N, E> getPath(N head) {
		return paths.get(head);
	}

	/**
	 * Add the given graph path to this subgraph.
	 *
	 * @param path a path to add
	 * @return any previous path associated with the head node of the path being added
	 * @throws IllegalArgumentException if the given path is empty
	 */
	public GraphPath<N, E> addPath(GraphPath<N, E> path) {
		if (path.head() == null) throw new IllegalArgumentException("The path is empty");
		return paths.put(path.head(), path);
	}

	/**
	 * Add all of the graph paths in the given subgraph to this subgraph.
	 *
	 * @param sg subgraph containing the paths to add
	 * @throws IllegalArgumentException if any path in the given subgraph is empty
	 */
	public void addPaths(SubGraph<N, E> sg) {
		for (GraphPath<N, E> path : sg) {
			addPath(path);
		}
	}

	/**
	 * Create a new graph path defined by the given head node and path weight property
	 * key.
	 *
	 * @param head path head node
	 * @param key  path weight property key
	 * @return any previous path associated with the given head node
	 */
	public GraphPath<N, E> startPath(N head, String key) {
		return paths.put(head, new GraphPath<>(key));
	}

	public Set<N> heads() {
		return paths.keySet();
	}

	public List<GraphPath<N, E>> paths() {
		return List.copyOf(paths.values());
	}

	/**
	 * Return whether any of the graph paths in this path set contains the given node.
	 *
	 * @param node the node to find
	 * @return {@code true} if any path contains the given node
	 */
	public boolean contains(N node) {
		return hasPath(node) || paths.values().stream().anyMatch(p -> p.contains(node));
	}

	/**
	 * Returns the first path in this path set that contains the given node.
	 *
	 * @param node a node within a path to find
	 * @return the first path that contains the given node, or {@code null} if not found
	 */
	public GraphPath<N, E> containing(N node) {
		return paths.values().stream().filter(p -> p.contains(node)).findFirst().orElse(null);
	}

	public GraphPath<N, E> remove(N head) {
		return paths.remove(head);
	}

	public GraphPath<N, E> remove(GraphPath<N, E> path) {
		return paths.remove(path.head());
	}

	public boolean isEmpty() {
		return paths.isEmpty();
	}

	public int size() {
		return paths.size();
	}

	public void clear() {
		paths.clear();
	}

	@Override
	public Iterator<GraphPath<N, E>> iterator() {
		return paths.values().iterator();
	}

	@Override
	public boolean equals(Object o) {
		return paths.equals(o);
	}

	@Override
	public int hashCode() {
		return paths.hashCode();
	}

	@Override
	public String toString() {
		return paths.toString();
	}
}
