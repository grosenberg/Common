package net.certiv.common.event;

import net.certiv.common.check.Assert;
import net.certiv.common.util.MsgBuilder;
import net.certiv.common.util.Strings;

/**
 * Base class for typed action events.
 * <p>
 * Action events identify some defined action command and an optional action qualifying
 * value.
 */
public abstract class TypedActionEvent extends TypedEvent {

	/** Event action value. */
	protected transient final Object action;
	/** Event action value. */
	protected transient final Object value;

	public <S, A extends IEvtCmd> TypedActionEvent(S source, A action) {
		this(source, EvtType.ACTION, action, null);
	}

	public <S, A extends IEvtCmd, V> TypedActionEvent(S source, A action, V value) {
		this(source, EvtType.ACTION, action, value);
	}

	public <S, T extends IEvtType, A extends IEvtCmd> TypedActionEvent(S source, T type, A action) {
		this(source, type, action, null);
	}

	public <S, T extends IEvtType, A extends IEvtCmd, V> TypedActionEvent(S source, T type, A action,
			V value) {
		super(source, type, action.name());
		Assert.isTrue(type.isActionType());
		this.action = action;
		this.value = value;
	}

	/**
	 * Returns the action that this event defines.
	 *
	 * @return the event action
	 */
	@SuppressWarnings("unchecked")
	public <A extends IEvtCmd> A action() {
		return (A) action;
	}

	/**
	 * Returns the value associated with this action event.
	 *
	 * @return the action value
	 */
	@SuppressWarnings("unchecked")
	public <V> V value() {
		return (V) value;
	}

	@Override
	public String toString() {
		MsgBuilder mb = new MsgBuilder();
		mb.append(name());
		mb.append("[%s]", source != null ? source.getClass().getSimpleName() : Strings.UNKNOWN);
		mb.append(" %s(%s)", action, value);
		return mb.toString();
	}

	/** Defines an event action command. */
	public static interface IEvtCmd {

		/** @return the action name */
		String name();

		/** @return {@code true} if this command is an Enum */
		default boolean isEnum() {
			return this instanceof Enum;
		}
	}
}
