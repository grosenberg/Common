package net.certiv.common.graph.ex;

import java.util.Arrays;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.IAssertException;
import net.certiv.common.ex.IGraphExType;
import net.certiv.common.util.MsgBuilder;

/**
 * {@code GraphException} is a runtime exception thrown by some of the methods in
 * {@code Assert}.
 *
 * @see Assert
 */
public class GraphException extends RuntimeException implements IAssertException {

	private IGraphExType graphExType;
	private String msg;
	private Object[] elements;
	private boolean hasElems;

	public GraphException() {
		this(Test.OTHER);
	}

	public GraphException(IGraphExType graphExType) {
		super(ASSERT_FAILED);
		this.graphExType = graphExType;
	}

	public GraphException(IGraphExType graphExType, String msg) {
		super(msg);
		this.graphExType = graphExType;
	}

	public GraphException(IGraphExType graphExType, String fmt, Object... args) {
		super(fmt);
		this.graphExType = graphExType;
		msg = (args != null && args.length > 0) ? String.format(fmt, args) : fmt;
	}

	public GraphException(IGraphExType graphExType, Throwable cause) {
		super(cause);
		this.graphExType = graphExType;
	}

	public GraphException(IGraphExType graphExType, Throwable cause, String msg) {
		super(msg, cause);
		this.graphExType = graphExType;
	}

	public GraphException(IGraphExType graphExType, Throwable cause, String fmt, Object... args) {
		super(fmt, cause);
		this.graphExType = graphExType;
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
	public IGraphExType graphExType() {
		return graphExType;
	}

	@Override
	public GraphException setType(IGraphExType graphExType) {
		this.graphExType = graphExType;
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
