package net.certiv.common.event;

import java.util.EventObject;

import net.certiv.common.check.Assert;

/**
 * Base class for typed events.
 * <p>
 * Events are categorically type by an {@link IEvtType}. Events are nominally either an
 * action event, identifying some action command and an optional associated value, or a
 * value change event, nominally reflecting a change in some named property.
 * <p>
 * Additional types may be defined by extending {@link IEvtType}, preferably using an
 * application-specific {@code enum}.
 */
public abstract class TypedEvent extends EventObject {

	/** Event type. */
	protected transient final IEvtType type;
	/** Event name */
	protected transient final String name;

	protected <S> TypedEvent(S source, IEvtType type) {
		this(source, type, type.typeName());
	}

	protected <S> TypedEvent(S source, IEvtType type, String name) {
		super(source);
		Assert.notNull(source, type, name);
		this.type = type;
		this.name = name;
	}

	/**
	 * Returns the source object associated with this event.
	 *
	 * @return the source object
	 */
	@SuppressWarnings("unchecked")
	public <S> S source() {
		return (S) source;
	}

	/**
	 * Returns the simple class name of the source object associated with this event.
	 *
	 * @return the simple name of the underlying source object class
	 */
	public String sourceSimpleName() {
		return source.getClass().getSimpleName();
	}

	/**
	 * Returns the defined type of this event.
	 *
	 * @return the source object
	 */
	@SuppressWarnings("unchecked")
	public <T extends IEvtType> T type() {
		return (T) type;
	}

	/**
	 * Returns the event name.
	 *
	 * @return the event name
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns whether this event is an action type event.
	 *
	 * @return {@code true} if this is an action type event
	 */
	public boolean isActionEvent() {
		return type.isActionType();
	}

	/**
	 * Returns whether this event is an change type event.
	 *
	 * @return {@code true} if this is a change type event
	 */
	public boolean isChangeEvent() {
		return type.isChangeType();
	}

	/**
	 * Returns whether this event may be dispatched.
	 *
	 * @return {@code true} if this event can be fired
	 */
	public boolean issuable() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s => [%s] %s", sourceSimpleName(), type(), name());
	}

	// --------------------------------

	/** Defines an event type. */
	public static interface IEvtType {

		/** @return the type name */
		String typeName();

		/** @return {@code true} if this type defines an action event */
		boolean isActionType();

		/** @return {@code true} if this type defines a property change event */
		boolean isChangeType();

		/** @return {@code true} if this type is an Enum */
		default boolean isEnum() {
			return this instanceof Enum;
		}
	}

	/** General Event type. */
	public enum EvtType implements IEvtType {

		ACTION("Action", true, false),
		CHANGE("Change", false, true);

		private final String name;
		private final boolean action;
		private final boolean change;

		EvtType(String name, boolean action, boolean change) {
			this.name = name;
			this.action = action;
			this.change = change;
		}

		@Override
		public String typeName() {
			return name;
		}

		@Override
		public boolean isActionType() {
			return action;
		}

		@Override
		public boolean isChangeType() {
			return change;
		}
	}
}
