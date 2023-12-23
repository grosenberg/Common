package net.certiv.common.event;

import java.util.LinkedList;
import java.util.Objects;

import net.certiv.common.check.Assert;
import net.certiv.common.check.Check;
import net.certiv.common.stores.Table;
import net.certiv.common.util.CompareUtil;

public class TypeKey {

	/** Eccentric contant representing a name wildcard. */
	public static final String ANY = "Any%&$32879";

	/**
	 * Get all previously defined type keys.
	 *
	 * @return existing type keys
	 */
	public static LinkedList<TypeKey> defined() {
		return CACHE.values();
	}

	/**
	 * Determine the previously defined type key for the given type and ANY name.
	 *
	 * @param type event type
	 * @return {@code true} if a corresponding type key has been created
	 */
	public static boolean defined(IEvtType type) {
		return defined(type, ANY);
	}

	/**
	 * Determine the previously defined type key for the given type and name.
	 *
	 * @param type event type
	 * @param name event name
	 * @return {@code true} if a corresponding type key has been created
	 */
	public static boolean defined(IEvtType type, String name) {
		if (Check.isNull(type, name)) return false;
		return CACHE.get(type, name) != null;
	}

	/**
	 * Get a type key for the given type and ANY name.
	 *
	 * @param type event type
	 * @return a type key
	 */
	public static TypeKey get(IEvtType type) {
		return get(type, ANY);
	}

	/**
	 * Get a type key for the given type and name.
	 *
	 * @param type event type
	 * @param name event name
	 * @return a type key
	 */
	public static TypeKey get(IEvtType type, String name) {
		Assert.notNull(type, name);
		TypeKey key = CACHE.get(type, name);
		if (key == null) {
			key = new TypeKey(type, name);
			CACHE.put(type, name, key);
		}
		return key;
	}

	/**
	 * Get the type key corresponding to the given event.
	 *
	 * @param event a typed event
	 * @return a type key defined by the event
	 */
	public static TypeKey get(TypedEvent event) {
		return get(event.type(), event.name());
	}

	// --------------------------------

	private static final Table<IEvtType, String, TypeKey> CACHE = new Table<>();

	final IEvtType type;
	final String name;

	private TypeKey(IEvtType type, String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Compares the key name element of this key to those of the given name.
	 * <p>
	 * Returns an ordering indicia of:
	 * <ul>
	 * <li>-2: before (no common root)
	 * <li>-1: before (has common root)
	 * <li>+0: same (has common root)
	 * <li>+1: after (has common root)
	 * <li>+2: after (no common root)
	 * </ul>
	 *
	 * @param name a target name
	 * @return an extended compare-style relative ordering indicator
	 * @see CompareUtil#within(String, String)
	 */
	public int compare(String name) {
		return CompareUtil.within(this.name, name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TypeKey other = (TypeKey) obj;
		return Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		return String.format("'%s' %s", name, type);
	}
}
