package net.certiv.common.id;

public interface IUId /* <U extends Comparable<? super U>> */ {

	/** The containing namespace. */
	String namespace();

	/**
	 * Determines if the namespace is the {@code default} namespace.
	 *
	 * @return {@code true} if the namespace is the {@code default} namespace
	 */
	boolean isDefaultNamespace();

	boolean sameNamespace(UId<?> id);

	/** Simple name - excludes the namespace; potentially not unique. */
	String name();

	/** Structured unique name; includes the namespace and name. */
	String uname();

	// U get();
	// int compareTo(UId<U> id);
}
