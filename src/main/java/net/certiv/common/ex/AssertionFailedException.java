package net.certiv.common.ex;

import net.certiv.common.util.Assert;

/**
 * {@code AssertionFailedException} is a runtime exception thrown by some of the
 * methods in {@code Assert}.
 * <p>
 * Not intended to be instantiated or sub-classed by clients.
 *
 * @see Assert
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class AssertionFailedException extends RuntimeException {

	public AssertionFailedException(String msg) {
		super(msg);
	}

	public AssertionFailedException(String fmg, Object... args) {
		super(String.format(fmg, args));
	}
}
