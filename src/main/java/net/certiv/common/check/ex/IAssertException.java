package net.certiv.common.check.ex;

public interface IAssertException {

	String ASSERT_FAILED = "Assertion failed"; //$NON-NLS-1$
	String NULL_ARG = "Null argument(s)"; //$NON-NLS-1$
	String EMPTY_ARG = "Empty argument(s)"; //$NON-NLS-1$

	enum Test {
		OTHER,
		IS_TRUE,
		IS_NULL,
		IS_EMPTY,
		NOT_TRUE,
		NOT_NULL,
		NOT_EMPTY;
	}

	Test type();

	IAssertException setType(Test type);

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
