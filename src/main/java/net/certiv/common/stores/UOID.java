package net.certiv.common.stores;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for assigning a unique ID to objects and returning objects for a
 * given ID.
 */
public class UOID {

	public static final UOID BANK = new UOID();

	private long nextId = 1;

	// key=object entry; value=object id
	private final Map<Entry, Long> ids = new HashMap<>();
	// key=object id; value=object
	private final Map<Long, Object> objects = new HashMap<>();

	private UOID() {}

	/**
	 * Returns a unique ID for the specified object. Guaranteed to return the same
	 * unique value for the same object, upto {@code long} number of instances
	 * banked.
	 *
	 * @param obj an object
	 * @return 0 for {@code null} objects; otherwise a unique ID != 0
	 */
	public long getId(Object obj) {
		if (obj == null) return 0;

		Entry entry = new Entry(obj);
		if (!ids.containsKey(entry)) {
			ids.put(entry, nextId);
			objects.put(nextId++, obj);
		}
		return ids.get(entry);
	}

	/**
	 * Returns the object assigned the given ID, or {@code null}.
	 *
	 * @param id the object Id
	 * @return the corresponding object or {@code null}
	 */
	public Object getObject(long id) {
		return objects.get(id);
	}

	public void dispose() {
		ids.clear();
		objects.clear();
	}

	/**
	 * Wrapper around an Object used as the key for the ids map. The wrapper is
	 * needed to ensure that the equals method only returns true if the two objects
	 * are the same instance and to ensure that the hash code is always the same for
	 * the same instance.
	 */
	private class Entry {

		private Object obj;

		/** Instantiates an Entry wrapper around the specified object */
		public Entry(Object obj) {
			this.obj = obj;
		}

		/**
		 * Returns true if and only if the objects contained in this wrapper and the
		 * other wrapper are the exact same object (same instance, not just equivalent)
		 */
		@Override
		public boolean equals(Object other) {
			return obj == ((Entry) other).obj;
		}

		/**
		 * Returns the contained object's identityHashCode. Note that identityHashCode
		 * values are not guaranteed to be unique from object to object, but the hash
		 * code is guaranteed to not change over time for a given instance of an Object.
		 */
		@Override
		public int hashCode() {
			return System.identityHashCode(obj);
		}
	}
}
