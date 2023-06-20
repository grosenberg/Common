package net.certiv.common.graph.ex;

import java.util.Arrays;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IAssertException;
import net.certiv.common.ex.IType;
import net.certiv.common.util.MsgBuilder;

/**
 * {@code GraphException} is a runtime exception thrown by some of the methods in
 * {@code Assert}.
 *
 * @see Assert
 */
public class GraphException extends RuntimeException implements IAssertException {

	private IType type;
	private String msg;
	private Object[] elements;
	private boolean hasElems;

	public GraphException() {
		this(Test.OTHER);
	}

	public GraphException(IType type) {
		super(ASSERT_FAILED);
		this.type = type;
	}

	public GraphException(IType type, String msg) {
		super(msg);
		this.type = type;
	}

	public GraphException(IType type, String fmt, Object... args) {
		super(fmt);
		this.type = type;
		msg = (args != null && args.length > 0) ? String.format(fmt, args) : fmt;
	}

	public GraphException(IType type, Throwable cause) {
		super(cause);
		this.type = type;
	}

	public GraphException(IType type, Throwable cause, String msg) {
		super(msg, cause);
		this.type = type;
	}

	public GraphException(IType type, Throwable cause, String fmt, Object... args) {
		super(fmt, cause);
		this.type = type;
		msg = (args != null && args.length > 0) ? String.format(fmt, args) : fmt;
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
	public IType type() {
		return type;
	}

	@Override
	public GraphException setType(IType type) {
		this.type = type;
		return this;
	}

	@Override
	public GraphException on(Object... args) {
		if (args != null && args.length > 0) {
			String fmt = super.getMessage();
			msg = String.format(fmt, args);
		}
		return this;
	}

	@Override
	public String getMessage() {
		if (msg != null) return msg;
		return super.getMessage();
	}

	@Override
	public String toString() {
		return MsgBuilder.of("GraphException") //
				.append(" %s", getMessage()) //
				.append(hasElems, " :: %s", Arrays.toString(elements)) //
				.toString();
	}
}
