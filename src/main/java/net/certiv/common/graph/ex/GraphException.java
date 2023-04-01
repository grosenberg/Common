package net.certiv.common.graph.ex;

import java.util.Arrays;

import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.ex.IAssertException;

/**
 * {@code GraphException} is a runtime exception thrown by some of the methods in
 * {@code Assert}.
 *
 * @see Assert
 */
public class GraphException extends RuntimeException implements IAssertException {

	private Test type;
	private String msg;
	private Object[] elements;
	private boolean hasElems;

	public GraphException() {
		this(Test.OTHER);
	}

	public GraphException(Test type) {
		super(ASSERT_FAILED);
		this.type = type;
	}

	public GraphException(Test type, String msg) {
		super(msg);
		this.type = type;
	}

	public GraphException(Test type, String fmt, Object... args) {
		super(String.format(fmt, args));
		this.type = type;
	}

	public GraphException(Test type, Throwable cause) {
		super(cause);
		this.type = type;
	}

	public GraphException(Test type, Throwable cause, String msg) {
		super(msg, cause);
		this.type = type;
	}

	public GraphException(Test type, Throwable cause, String fmt, Object... args) {
		super(String.format(fmt, args), cause);
		this.type = type;
	}

	@Override
	public Object[] elements() {
		return elements;
	}

	@Override
	public GraphException setElement(Object element) {
		elements = new Object[] { element };
		hasElems = true;
		return this;
	}

	@Override
	public GraphException setElements(Object[] elements) {
		this.elements = elements;
		hasElems = true;
		return this;
	}

	@Override
	public Test type() {
		return type;
	}

	@Override
	public GraphException setType(Test type) {
		this.type = type;
		return this;
	}

	@Override
	public GraphException msg(String msg) {
		this.msg = msg;
		return this;
	}

	@Override
	public String getMessage() {
		if (msg != null) return msg;
		return super.getMessage();
	}

	@Override
	public GraphException msg(String fmt, Object... args) {
		String msg = (args != null && args.length > 0) ? String.format(fmt, args) : fmt;
		return msg(msg);
	}

	@Override
	public String toString() {
		return MsgBuilder.of("GraphException") //
				.append(" %s", getMessage()) //
				.append(hasElems, " :: %s", Arrays.toString(elements)) //
				.toString();
	}
}