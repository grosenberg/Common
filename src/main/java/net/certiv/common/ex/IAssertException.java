package net.certiv.common.ex;

public interface IAssertException {

	String ASSERT_FAILED = "Assertion failed"; //$NON-NLS-1$
	String NULL_ARG = "Null argument(s)"; //$NON-NLS-1$
	String EMPTY_ARG = "Empty argument(s)"; //$NON-NLS-1$

	enum Test implements IType {
		OTHER,
		IS_TRUE,
		IS_NULL,
		IS_EMPTY,
		NOT_TRUE,
		NOT_NULL,
		NOT_EMPTY;
	}

	IType type();

	IAssertException setType(IType type);

	IAssertException msg(String msg);

	IAssertException msg(String fmt, Object... args);

	Object[] elements();

	/**
	 * Note: will wrap the given single object element, including primitive arrays, in an
	 * Object array.
	 */
	IAssertException setElement(Object element);

	IAssertException setElements(Object[] objects);

}
