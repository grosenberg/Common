package net.certiv.common.util;

import java.util.Objects;

/**
 * A status object represents the outcome of an operation. All
 * {@code CoreException}s carry a status object to indicate what went wrong.
 * Status objects are also returned by methods needing to provide details of
 * failures (e.g., validation methods).
 * <p>
 * A status carries the following information:
 * <ul>
 * <li>source identifier (required)
 * <li>severity (required)
 * <li>status code (required)
 * <li>message (required) - localized to current locale
 * <li>exception (optional) - for problems stemming from a failure at a lower
 * level
 * </ul>
 * <p>
 * Some status objects, known as multi-statuses, have other status objects as
 * children.
 * <p>
 * The class {@code Status} is the standard public implementation of status
 * objects; the subclass {@code MultiStatus} is the implements multi-status
 * objects.
 */
public class Status {

	private static final Status[] EMPTY_STATUS = {};

	public enum Severity {
		OK, DEBUG, INFO, WARNING, ERROR, CANCEL;
	}

	/** Standard OK status. */
	public static final Status OK = new Status(Severity.OK);
	/** Standard CANCEL status. */
	public static final Status CANCEL = new Status(Severity.CANCEL);
	/** Standard ERROR status. */
	public static final Status ERROR = new Status(Severity.ERROR);

	/** The severity. */
	private Severity severity = Severity.OK;

	/** Unique source identifier. */
	private Object source;

	/** A optional source specific status code. */
	private int code;

	/** Message, localized to the current locale. */
	private String message;

	/** Wrapped exception, or {@code null} if none. */
	private Throwable exception = null;

	public Status(Severity severity) {
		this(severity, Strings.EMPTY, 0, severity.toString(), null);
	}

	/**
	 * Simplified constructor of a new status object; assumes that code is
	 * {@code OK} and exception is {@code null}. The created status has no children.
	 *
	 * @param severity the severity; one of {@code OK}, {@code ERROR}, {@code INFO},
	 *            {@code WARNING}, or {@code CANCEL}
	 * @param source the unique identifier of the relevant plug-in
	 * @param message a human-readable message, localized to the current locale
	 * @since org.eclipse.equinox.common 3.3
	 */
	public Status(Severity severity, Object source, String message) {
		this(severity, source, 0, message, null);
	}

	/**
	 * Simplified constructor of a new status object; assumes that code is
	 * {@code OK}. The created status has no children.
	 *
	 * @param severity the severity; one of {@code OK}, {@code ERROR}, {@code INFO},
	 *            {@code WARNING}, or {@code CANCEL}
	 * @param source the unique identifier of the relevant plug-in
	 * @param message a human-readable message, localized to the current locale
	 * @param exception a low-level exception, or {@code null} if not applicable
	 * @since org.eclipse.equinox.common 3.3
	 */
	public Status(Severity severity, Object source, String message, Throwable exception) {
		this(severity, source, 0, message, exception);
	}

	/**
	 * Creates a new status object. The created status has no children.
	 *
	 * @param severity the severity
	 * @param source the unique identifier of the relevant plug-in
	 * @param code the plug-in-specific status code, or {@code OK}
	 * @param message a human-readable message, localized to the current locale
	 * @param exception a low-level exception, or {@code null} if not applicable
	 */
	public Status(Severity severity, Object source, int code, String message, Throwable exception) {
		this.severity = severity;
		this.source = source;
		this.code = code;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Returns whether this status indicates everything is okay (neither info,
	 * warning, nor error).
	 *
	 * @return {@code true} if this status has severity {@code OK}, and
	 *             {@code false} otherwise
	 */
	public boolean isOK() {
		return severity == Severity.OK;
	}

	/**
	 * Returns the severity. The severities are as follows (in descending order):
	 * <ul>
	 * <li>{@code OK} - everything is just fine
	 * <li>{@code CANCEL} - cancelation occurred
	 * <li>{@code ERROR} - a serious error (most severe)
	 * <li>{@code WARNING} - a warning (less severe)
	 * <li>{@code INFO} - an informational ("fyi") message (least severe)
	 * </ul>
	 * <p>
	 * The severity of a multi-status is defined to be the maximum severity of any
	 * of its children, or {@code OK} if it has no children.
	 *
	 * @return the severity: one of {@code OK}, {@code ERROR}, {@code INFO},
	 *             {@code WARNING}, or {@code CANCEL}
	 * @see #matches(int)
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Sets the severity.
	 *
	 * @param severity the severity
	 */
	protected void setSeverity(Severity severity) {
		this.severity = severity;
	}

	/**
	 * Returns the unique identifier of the source associated with this status (this
	 * is the source that defines the meaning of the status code).
	 *
	 * @return the unique identifier of the relevant source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Sets the plug-in id.
	 *
	 * @param pluginId the unique identifier of the relevant plug-in
	 */
	protected void setSource(String source) {
		Assert.isLegal(source != null && source.length() > 0);
		this.source = source;
	}

	/**
	 * Returns the message describing the outcome. The message is localized to the
	 * current locale.
	 *
	 * @return a localized message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message. If null is passed, message is set to an empty string.
	 *
	 * @param message a human-readable message, localized to the current locale
	 */
	protected void setMessage(String message) {
		if (message == null)
			this.message = ""; //$NON-NLS-1$
		else
			this.message = message;
	}

	/**
	 * Returns the relevant low-level exception, or {@code null} if none. For
	 * example, when an operation fails because of a network communications failure,
	 * this might return the {@code java.io.IOException} describing the exact nature
	 * of that failure.
	 *
	 * @return the relevant low-level exception, or {@code null} if none
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Sets the exception.
	 *
	 * @param exception a low-level exception, or {@code null} if not applicable
	 */
	protected void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Returns the source-specific status code describing the outcome.
	 *
	 * @return source-specific status code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the status code.
	 *
	 * @param code the plug-in-specific status code, or {@code OK}
	 */
	protected void setCode(int code) {
		this.code = code;
	}

	/**
	 * Returns whether this status is a multi-status. A multi-status describes the
	 * outcome of an operation involving multiple operands.
	 * <p>
	 * The severity of a multi-status is derived from the severities of its
	 * children; a multi-status with no children is {@code OK} by definition. A
	 * multi-status carries a source identifier, a status code, a message, and an
	 * optional exception. Clients may treat multi-status objects in a multi-status
	 * unaware way.
	 *
	 * @return {@code true} for a multi-status, {@code false} otherwise
	 * @see #getChildren()
	 */
	public boolean isMultiStatus() {
		return false;
	}

	/**
	 * Returns a list of status object immediately contained in this multi-status,
	 * or an empty list if this is not a multi-status.
	 *
	 * @return an array of status objects
	 * @see #isMultiStatus()
	 */
	public Status[] getChildren() {
		return EMPTY_STATUS;
	}

	@Override
	public int hashCode() {
		return Objects.hash(severity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Status)) return false;
		Status other = (Status) obj;
		return severity == other.severity;
	}

	@Override
	public String toString() {
		return String.format("%s: %s(%s) '%s' [%s]", //
				severity, source.getClass().getSimpleName(), code, message, exception);
	}
}
