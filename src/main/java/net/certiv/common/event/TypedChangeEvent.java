package net.certiv.common.event;

import net.certiv.common.check.Assert;

/**
 * Base class for typed change events.
 * <p>
 * Change events nominally represent a change in a named value property.
 */
public abstract class TypedChangeEvent extends TypedEvent {

	/** Event current value. */
	protected transient final Object value;
	/** Event prior value. */
	protected transient final Object prior;

	protected <S, T extends IEvtType, V> TypedChangeEvent(S source, T type, V value) {
		this(source, type, type.typeName(), value, null);
	}

	protected <S, T extends IEvtType, V> TypedChangeEvent(S source, T type, String name, V value) {
		this(source, type, name, value, null);
	}

	protected <S, T extends IEvtType, V> TypedChangeEvent(S source, T type, V value, V prior) {
		this(source, type, type.typeName(), value, prior);
	}

	protected <S, T extends IEvtType, V> TypedChangeEvent(S source, T type, String name, V value, V prior) {
		super(source, type, name);
		Assert.isTrue(type.isChangeType());
		this.value = value;
		this.prior = prior;
	}

	/**
	 * Returns the action value or current change value associated with this event.
	 *
	 * @return the current value
	 */
	@SuppressWarnings("unchecked")
	public <V> V value() {
		return (V) value;
	}

	/**
	 * Returns the prior change value associated with this event.
	 *
	 * @return the prior value
	 */
	@SuppressWarnings("unchecked")
	public <V> V prior() {
		return (V) value;
	}

	@Override
	public boolean issuable() {
		return valueChanged();
	}

	/**
	 * Returns whether this event presents an actual {@code prior ==> value} change.
	 *
	 * @return {@code true} if this event presents an actual value change
	 */
	public boolean valueChanged() {
		return value != prior && (value != null || prior != null);
	}

	@Override
	public String toString() {
		return String.format("%s (%s => %s)", super.toString(), prior(), value());
	}
}
