package net.certiv.common.graph;

/**
 * Typing interface for unique node naming. Extend to create a class for wrapping a node
 * name object to associate a node specializing type.
 * <p>
 * Nominally, the node name object provides a unique identifier for nodes, alone or in
 * conjunction with the node {@code nid}.
 * <p>
 * For graphs with a single node type, a {@code String} is a sufficient node name object.
 * <p>
 * For graphs supporting multiple node types, certain graph operations, such as
 * {@link Graph#createNode(Object)} and {@link Graph#copyNode(Node)}, reqiure being able
 * to distinquish node type from a node name object instance. So, where the name object
 * does not intrinsically or readily encode node types, the name object is preferably
 * wrapped by a class implementing this (or similar) typing interface.
 *
 * @param <I> node name object type
 * @param <T> node categorizing type
 * @see Graph#setGraphId(Object)
 * @see Graph#createNode(Object)
 * @see Graph#copyNode(Node)
 */
public interface IName<I, T> {

	/** @return the wrapped identifier object */
	I obj();

	/**
	 * Returns a full, unique name for the node, composed of the {@link #prefix()} and
	 * {@link #name()}. Nominally, some unique, displayable representation of the wrapped
	 * identifier object.
	 *
	 * @return a full node name
	 */
	default String nodename() {
		return obj().toString();
	}

	/**
	 * Returns a node name prefix. Nominally, some intermediary element(s) of the full
	 * name.
	 *
	 * @return a name prefix
	 */
	String prefix();

	/**
	 * Returns a simple name for the node. Nominally, some last element of the full name.
	 *
	 * @return a simple node name
	 */
	String name();

	/**
	 * Returns a node typing object. Typically, an {@code enum}.
	 *
	 * @return a node categorizing type
	 */
	T type();

	/**
	 * Returns a string representation of the node object type.
	 *
	 * @return a representation of the node type
	 */
	default String typename() {
		return type().toString();
	}
}
