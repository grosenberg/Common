package net.certiv.common.ex;

/**
 * JsonException is an unchecked {@code RuntimeException} representing a failure in any
 * JSON related processing operations.
 */
public class JsonException extends RuntimeException {

	public JsonException() {}

	public JsonException(String msg) {
		super(msg);
	}

	public JsonException(Throwable t) {
		super(t);
	}
}
