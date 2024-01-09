package net.certiv.common.graph.paths;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.UniqueList;

/**
 * Sub-graph of a graph represented by a set of graph paths.
 */
public class SubGraph<I extends Id, N extends Node<I, N, E>, E extends Edge<I, N, E>>
		implements Iterable<GraphPath<I, N, E>> {

	/** {@code key=head node; value=graph path} */
	private final LinkedHashMap<N, GraphPath<I, N, E>> paths = new LinkedHashMap<>();
	private final Graph<I, N, E> graph;

	public SubGraph(Graph<I, N, E> graph) {
		this.graph = graph;
	}

	/**
	 * Returns the graph this subgraph is defined on.
	 *
	 * @return the owning graph
	 */
	public Graph<I, N, E> graph() {
		return graph;
	}

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
	public GraphPath<I, N, E> getPath(N head) {
		return paths.get(head);
	}

	/**
	 * Add the given graph path to this subgraph.
	 *
	 * @param path a path to add
	 * @return any previous path associated with the head node of the path being added
	 * @throws IllegalArgumentException if the given path is empty
	 */
	public GraphPath<I, N, E> addPath(GraphPath<I, N, E> path) {
		if (path.head() == null) throw new IllegalArgumentException("The path is empty");
		return paths.put(path.head(), path);
	}

	/**
	 * Add all of the graph paths in the given subgraph to this subgraph.
	 *
	 * @param sg subgraph containing the paths to add
	 * @throws IllegalArgumentException if any path in the given subgraph is empty
	 */
	public void addPaths(SubGraph<I, N, E> sg) {
		for (GraphPath<I, N, E> path : sg) {
			addPath(path);
		}
	}

	/**
	 * Create a new graph path defined by the given head node and path weight property
	 * key. Discards any prior existing path associated with the given head node.
	 *
	 * @param head path head node
	 * @param key  path weight property key
	 * @return a new path associated with the given head node
	 */
	public GraphPath<I, N, E> startPath(N head, String key) {
		GraphPath<I, N, E> path = new GraphPath<>(graph, key);
		paths.put(head, path);
		return path;
	}

	public N head(GraphPath<I, N, E> path) {
		return heads().stream().filter(h -> getPath(h).equals(path)).findFirst().orElse(null);
	}

	public UniqueList<N> heads() {
		return new UniqueList<>(paths.keySet()).unmodifiable();
	}

	public UniqueList<N> terminals() {
		return paths.values().stream() //
				.map(p -> p.terminals()) //
				.flatMap(t -> t.stream()) //
				.collect(Collectors.toCollection(UniqueList::new)) //
				.unmodifiable();
	}

	public List<GraphPath<I, N, E>> paths() {
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
	public GraphPath<I, N, E> containing(N node) {
		return paths.values().stream().filter(p -> p.contains(node)).findFirst().orElse(null);
	}

	public GraphPath<I, N, E> remove(N head) {
		return paths.remove(head);
	}

	public GraphPath<I, N, E> remove(GraphPath<I, N, E> path) {
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

	public SubGraph<I, N, E> dup() {
		SubGraph<I, N, E> dup = new SubGraph<>(graph);
		dup.addPaths(this);
		return dup;
	}

	public Stream<GraphPath<I, N, E>> stream() {
		return paths.values().stream();
	}

	@Override
	public Iterator<GraphPath<I, N, E>> iterator() {
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
