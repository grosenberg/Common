package net.certiv.common.graph;

/**
 * Name object wrapper class.
 * <p>
 * Useful where there are multiple node types when the name object does not intrinsically
 * or readily encode the desired node type.
 *
 * @param <I> wrapped identifier object type
 * @param <T> node categorizing type
 * @see Graph#createNode(Object)
 * @see Graph#copyNode(Node)
 */
public interface INameId<I, T> {

	/** @return the wrapped identifier object */
	I id();

	/** @return a node categorizing type */
	T type();
}
