package net.certiv.common.check.ex;

import java.util.Arrays;

import net.certiv.common.check.Assert;
import net.certiv.common.util.MsgBuilder;

/**
 * {@code AssertionException} is a runtime exception thrown by some of the methods in
 * {@code Assert}.
 * <p>
 * Not intended to be instantiated or sub-classed by clients.
 *
 * @see Assert
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class AssertException extends RuntimeException implements IAssertException {

	private Test type;
	private String msg;
	private Object[] elements;
	private boolean hasElems;

	public AssertException() {
		this(Test.OTHER);
	}

	public AssertException(Test type) {
		super(ASSERT_FAILED);
		this.type = type;
	}

	public AssertException(Test type, String msg) {
		super(msg);
		this.type = type;
	}

	public AssertException(Test type, String fmt, Object... args) {
		super(String.format(fmt, args));
		this.type = type;
	}

	public AssertException(Test type, Throwable cause) {
		super(cause);
		this.type = type;
	}

	public AssertException(Test type, Throwable cause, String msg) {
		super(msg, cause);
		this.type = type;
	}

	public AssertException(Test type, Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
		this.type = type;
	}

	@Override
	public Object[] elements() {
		return elements;
	}

	@Override
	public AssertException setElement(Object element) {
		elements = new Object[] { element };
		hasElems = true;
		return this;
	}

	@Override
	public AssertException setElements(Object[] elements) {
		this.elements = elements;
		hasElems = true;
		return this;
	}

	@Override
	public Test type() {
		return type;
	}

	@Override
	public AssertException setType(Test type) {
		this.type = type;
		return this;
	}

	@Override
	public AssertException msg(String msg) {
		this.msg = msg;
		return this;
	}

	@Override
	public String getMessage() {
		if (msg != null) return msg;
		return super.getMessage();
	}

	@Override
	public AssertException msg(String fmt, Object... args) {
		String msg = (args != null && args.length > 0) ? String.format(fmt, args) : fmt;
		return msg(msg);
	}

	@Override
	public String toString() {
		return MsgBuilder.of("AssertException") //
				.append(" %s", getMessage()) //
				.append(hasElems, " :: %s", Arrays.toString(elements)) //
				.toString();
	}
}