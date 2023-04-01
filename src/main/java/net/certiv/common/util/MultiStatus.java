package net.certiv.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A concrete multi-status implementation, suitable either for instantiating or
 * subclassing.
 */
public class MultiStatus extends Status {

	/** List of child statuses. */
	private final List<Status> children = new ArrayList<>();

	/**
	 * Creates and returns a new multi-status object with the given children.
	 *
	 * @param source      the unique identifier of the relevant plug-in
	 * @param code        the plug-in-specific status code
	 * @param newChildren the list of children status objects
	 * @param message     a human-readable message, localized to the current locale
	 * @param exception   a low-level exception, or {@code null} if not applicable
	 */
	public MultiStatus(String source, int code, Status[] newChildren, String message, Throwable exception) {
		this(source, code, message, exception);
		Assert.notNull((Object) newChildren);
		addAllInternal(newChildren);
	}

	/**
	 * Creates and returns a new multi-status object with no children.
	 *
	 * @param source    the unique identifier of the relevant plug-in
	 * @param code      the plug-in-specific status code
	 * @param message   a human-readable message, localized to the current locale
	 * @param exception a low-level exception, or {@code null} if not applicable
	 */
	public MultiStatus(String source, int code, String message, Throwable exception) {
		super(Severity.OK, source, code, message, exception);
	}

	/**
	 * Adds the given status to this multi-status.
	 *
	 * @param status the new child status
	 */
	public void add(Status status) {
		Assert.notNull(status);
		children.add(status);
		if (status.getSeverity().ordinal() > getSeverity().ordinal()) {
			setSeverity(status.getSeverity());
		}
	}

	/**
	 * Adds all of the children of the given status to this multi-status. Does nothing if
	 * the given status has no children (which includes the case where it is not a
	 * multi-status).
	 *
	 * @param status the status whose children are to be added to this one
	 */
	public void addAll(Status status) {
		Assert.notNull(status);
		addAllInternal(status.getChildren());
	}

	private void addAllInternal(Status[] children) {
		for (Status child : children) {
			add(child);
		}
	}

	@Override
	public Status[] getChildren() {
		return children.toArray(new Status[0]);
	}

	@Override
	public boolean isMultiStatus() {
		return true;
	}

	/**
	 * Merges the given status into this multi-status. Equivalent to {@code add(status)}
	 * if the given status is not a multi-status. Equivalent to {@code addAll(status)} if
	 * the given status is a multi-status.
	 *
	 * @param status the status to merge into this one
	 * @see #add(Status)
	 * @see #addAll(Status)
	 */
	public void merge(Status status) {
		Assert.notNull(status);
		if (!status.isMultiStatus()) {
			add(status);
		} else {
			addAll(status);
		}
	}

	/**
	 * Returns a string representation of the status, suitable for debugging purposes
	 * only.
	 */
	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(" ", super.toString() + " children=[", "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (Status child : children) {
			joiner.add(child.toString());
		}
		return joiner.toString();
	}
}
