package net.certiv.common.event;

/** Defines an event type. */
public interface IEvtType {

	/** @return the type name */
	String name();

	/** @return {@code true} if this type defines an action event */
	boolean isActionType();

	/** @return {@code true} if this type defines a property change event */
	boolean isChangeType();

	/** @return {@code true} if this type is an Enum */
	default boolean isEnum() {
		return this instanceof Enum;
	}
}
