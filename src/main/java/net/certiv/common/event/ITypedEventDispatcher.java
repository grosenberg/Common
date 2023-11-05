package net.certiv.common.event;

import java.util.Collection;
import java.util.Set;

import net.certiv.common.event.TypedEvent.IEvtType;

public interface ITypedEventDispatcher {

	/**
	 * Fires the given event to the listeners registered to track updates of the given
	 * event instance. The event is fired only if the event is {@link TypedEvent#issuable}
	 * and only to listeners defined against the type key corresponding to the event.
	 *
	 * @param event the {@code Event} to be fired
	 */
	<TE extends TypedEvent> void fire(TE event);

	/**
	 * Add the given listener.
	 * <p>
	 * Registers the listener for all of the event types specified by the listener. No
	 * exception is thrown and no action is taken if {@code listener} is {@code null} or
	 * has been previously added.
	 *
	 * @param listener an event listener
	 */
	void addListener(TypedEventListener listener);

	/**
	 * Add the given listeners.
	 * <p>
	 * Registers each listener for all of the event types specified by the listener. No
	 * exception is thrown and no action is taken if a listener has been previously added.
	 *
	 * @param listeners event listeners
	 */
	void addListeners(Collection<TypedEventListener> listeners);

	/**
	 * Remove the given listener from the listener list.
	 * <p>
	 * This deregisters the listener for all of the event types specified by the listener.
	 * The same listener object may be removed more than once. No exception is thrown and
	 * no action is taken if {@code listener} is {@code null} or has been previously
	 * removed, or was never added.
	 *
	 * @param listener an event listener
	 */
	void removeListener(TypedEventListener listener);

	/**
	 * Check if there are any listeners registered for the given event type.
	 *
	 * @param type an event type.
	 * @return {@code true} if one or more listeners are registered for the given event
	 *         type
	 */
	boolean hasListeners(IEvtType type);

	/** Returns a set of all registered listener types. */
	Set<IEvtType> getListenerTypes();

	// /** Returns a set of all registered listener type keys. */
	// Set<TypeKey> getListenerKeys();

	/** Returns a set of all registered listeners. */
	Set<TypedEventListener> getListeners();

	/** Returns then number of all registered listeners for all event types. */
	int getListenerCount();

	/** Removes all of the registered listeners from this dispatcher. */
	void clearListeners();
}
