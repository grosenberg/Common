package net.certiv.common.graph.id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.certiv.common.check.Assert;

public class IdFactory {

	public static final String ANON = "anon";
	public static final String DEFAULT = "default";
	public static final String NIL = "nil";
	public static final String UNKNOWN = "unknown";

	/** Cache: value=id */
	protected final Set<Id> Cache = new HashSet<>();

	/** The namespace specific to this factory instance. */
	public final String ns;

	private static Id ANON_ID;
	private static Id UNKNOWN_ID;

	/**
	 * Creates the factory with the given default namespace; {@code ANON} if {@code null}.
	 *
	 * @param ns the namespace for factored idents
	 */
	public IdFactory(String ns) {
		this.ns = ns != null ? ns : DEFAULT;
	}

	// ---- Public API ----

	/**
	 * Returns the current id set for the default namespace.
	 *
	 * @return default namespace id set
	 */
	public Set<Id> defined() {
		return defined(ns);
	}

	/**
	 * Returns the id defined in the given namespace.
	 *
	 * @param ns namespace
	 * @return namespace ids
	 */
	public Set<Id> defined(String ns) {
		return Cache.stream() //
				.filter(k -> k.ns.equals(ns)) //
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Return an existing id corresponding to the given naming elements in the given
	 * namespace.
	 *
	 * @param ns   namespace
	 * @param name id name
	 * @return existing id or {@code null}
	 */
	public Id find(String ns, String name) {
		return Cache.stream() //
				.filter(t -> t.ns.equals(ns) && t.name().equals(name)) //
				.findFirst() //
				.orElse(null);
	}

	/**
	 * Return an existing ident corresponding to the given naming elements in the default
	 * namespace.
	 *
	 * @param elems identifier elements
	 * @return existing ident or {@code null}
	 */
	public Id find(List<String> elems) {
		return find(ns, elems);
	}

	/**
	 * Return an existing ident corresponding to the given naming elements in the given
	 * namespace.
	 *
	 * @param ns    namespace
	 * @param elems identifier elements
	 * @return existing ident or {@code null}
	 */
	public Id find(String ns, List<String> elems) {
		return Cache.stream() //
				.filter(t -> t.ns.equals(ns) && t.elems.equals(elems)) //
				.findFirst() //
				.orElse(null);
	}

	/**
	 * Makes a ident with the given name in the default namespace.
	 *
	 * @param name an identifier
	 * @return existing or new ident
	 */
	public Id make(String name) {
		return make(ns, List.of(name));
	}

	/**
	 * Makes an ident with the given name in the given namespace.
	 *
	 * @param ns   namespace
	 * @param name an identifier
	 * @return existing or new ident
	 */
	public Id make(String ns, String name) {
		return make(ns, List.of(name));
	}

	/**
	 * Makes an ident with the given name elements in the default namespace. Does not
	 * check for name part overlaps.
	 *
	 * @param elems identifier elements
	 * @return existing or new ident
	 */
	public Id make(String[] elems) {
		return make(ns, Arrays.asList(elems));
	}

	/**
	 * Makes an ident with the given name elements in the given namespace. Does not check
	 * for name part overlaps.
	 *
	 * @param ns    namespace
	 * @param elems identifier elements
	 * @return existing or new ident
	 */
	public Id make(String ns, String[] elems) {
		return make(ns, Arrays.asList(elems));
	}

	/**
	 * Makes an ident with the given name elements in the default namespace. Does not
	 * check for name part overlaps.
	 *
	 * @param elems identifier element
	 * @return existing or new ident
	 */
	public Id make(List<String> elems) {
		return make(ns, elems);
	}

	/**
	 * Makes a ident with the given name elements in the given namespace. Does not check
	 * for name part overlaps.
	 *
	 * @param ns    namespace
	 * @param elems identifier elements
	 * @return existing or new ident
	 */
	public Id make(String ns, List<String> elems) {
		List<String> elements = parse(elems);
		Id id = find(ns, elements);
		if (id != null) return id;
		return _make(new Id(ns, elements));
	}

	// --------------------------------

	public static Id anon() {
		if (ANON_ID == null) ANON_ID = new Id(DEFAULT, List.of(ANON));
		return ANON_ID;
	}

	public static Id unknown() {
		if (UNKNOWN_ID == null) UNKNOWN_ID = new Id(DEFAULT, List.of(UNKNOWN));
		return UNKNOWN_ID;
	}

	// --------------------------------

	/**
	 * Resolves a new name based on the name of the given id and added elements. Name
	 * resolution accounts for any name element overlap.
	 *
	 * @param id       base name
	 * @param elements name elements to add
	 * @return resolved name
	 */
	public List<String> resolveName(Id id, String... elements) {
		return resolveName(id, Arrays.asList(elements));
	}

	/**
	 * Resolves a new name based on the name of the given id and added elements. Name
	 * resolution accounts for any name element overlap.
	 *
	 * @param id       base name
	 * @param elements name elements to add
	 * @return resolved name
	 */
	public List<String> resolveName(Id id, List<String> elements) {
		Assert.notNull(id);
		Assert.notEmpty(elements);

		List<String> parts = id.elements();
		List<String> names = parse(elements);

		int end = begOverlap(parts, names);
		if (end != -1) {
			parts = parts.subList(0, end);
		}

		parts.addAll(names);
		return parts;
	}

	/**
	 * Returns an identifier corresponding to the non-overlapping elements of this
	 * identifier with the given elements appended. Creates a new identifier as needed.
	 */
	public Id resolve(Id id, String... elements) {
		return resolve(id, Arrays.asList(elements));
	}

	/**
	 * Returns an identifier corresponding to the non-overlapping elements of this
	 * identifier with the given elements appended. Creates a new identifier as needed.
	 */
	public Id resolve(Id id, List<String> elements) {
		List<String> resolved = resolveName(id, elements);
		return make(id.ns, resolved);
	}

	/**
	 * Find the beginning of any name element overlap between the given name lists. Does
	 * not check for consecutive element matches. Override as needed.
	 *
	 * @param base  base name elements
	 * @param added added name elements
	 * @return overlap index, or {@code -1} if no overlap
	 */
	protected int begOverlap(List<String> base, List<String> added) {
		String n0 = added.get(0);
		for (int idx = 0, plen = base.size(); idx < plen; idx++) {
			String px = base.get(idx);
			if (px.equals(n0)) return idx;
		}
		return -1;
	}

	/**
	 * Find the {@code nth} existing parent of the given ident, or {@code null} if
	 * nonexistent, subject to the given limit, where limit is defined as:
	 *
	 * <pre>
	 * -1 : first existing superior ident; range unlimited
	 *  0 : immediate parent ident
	 *  n : first existing superior ident within the range [0,n]
	 * </pre>
	 *
	 * @param id    reference ident
	 * @param limit operation qualifier
	 * @return a parent ident or {@code null} if nonexistent
	 */
	public Id findParent(Id id, int limit) {
		Assert.notNull(id);
		Assert.isTrue(id.elems.size() > 0);

		LinkedList<String> elems = id.elements();
		elems.removeLast();

		if (limit < 0) {
			while (elems.size() > 0) {
				Id result = find(id.ns, elems);
				if (result != null) return result;
				elems.removeLast();
			}
			return null;
		}

		for (int end = limit; elems.size() > 0 && end >= 0; end--) {
			Id result = find(id.ns, elems);
			if (result != null) return result;
			elems.removeLast();
		}
		return null;
	}

	// ---- Internal API --------------

	/** Record a newly made id in the cache. */
	@SuppressWarnings("unchecked")
	protected <T extends Id> T _make(Id id) {
		Cache.add(id);
		return (T) id;
	}

	// ---- Utilities -----------------

	/** Namespace separator: double colon. */
	public static final Pattern NS_SEP = Pattern.compile("\\:\\:");
	/** Name element separator: Single DOT pattern */
	public static final Pattern DOTTED = Pattern.compile("\\.");

	/**
	 * Determine if the given potentially compound name text contains a namespace prefix
	 * delimited by a double colon pattern.
	 *
	 * @param txt name text
	 * @return {@code true} if a namespace prefix is present
	 */
	public static boolean nsPresent(String txt) {
		return nsPresent(NS_SEP, txt);
	}

	/**
	 * Determine if the given potentially compound name text contains a namespace prefix
	 * delimited by the given namespace separator pattern.
	 *
	 * @param sep namespace separator pattern
	 * @param txt name text
	 * @return {@code true} if a namespace prefix is present
	 */
	public static boolean nsPresent(Pattern sep, String txt) {
		return nsParse(sep, txt, null) != null;
	}

	/**
	 * Return the namespace derived from the given potentially compound name text using a
	 * double colon namespace separator.
	 *
	 * @param txt name text
	 * @param def namespace default value
	 * @return namespace
	 */
	public static String nsParse(String txt, String def) {
		return nsParse(NS_SEP, txt, def);
	}

	/**
	 * Return the namespace derived from the given potentially compound name text using a
	 * the given namespace separator.
	 *
	 * @param sep namespace separator pattern
	 * @param txt name text
	 * @param def namespace default value
	 * @return namespace
	 */
	public static String nsParse(Pattern sep, String txt, String def) {
		String[] nss = sep.split(txt, 2);
		return nss.length == 2 ? nss[0] : def;
	}

	/**
	 * Parse the given potentially compound name into a list of name elements using a
	 * single DOT regex pattern.
	 *
	 * @param name a potentially compound name
	 * @return a list of name elements
	 */
	public static List<String> parse(String name) {
		if (name == null || name.isBlank()) return Collections.emptyList();
		return parse(Arrays.asList(name));
	}

	/**
	 * Parse the given potentially compound names into a list of name elements using a
	 * single DOT regex pattern.
	 *
	 * @param names potentially compound names
	 * @return name elements
	 */
	public static List<String> parse(String... names) {
		if (names == null || names.length == 0) return Collections.emptyList();
		return parse(Arrays.asList(names));
	}

	/**
	 * Parse the given elements into a list of potentially compound name elements using a
	 * single DOT regex pattern.
	 *
	 * @param names list of potentially compound names
	 * @return name elements
	 */
	public static List<String> parse(List<String> names) {
		List<String> results = new ArrayList<>();
		if (names != null) names.forEach(e -> results.addAll(parse(DOTTED, e)));
		return results;
	}

	/**
	 * Parse the given source text into a list of name elements using the given pattern to
	 * split the source.
	 *
	 * @param sep name element separator regex pattern
	 * @param txt name text
	 * @return name elements
	 */
	public static List<String> parse(Pattern sep, String txt) {
		Assert.notNull(sep, txt);
		return Arrays.asList(sep.split(txt, 0));
	}

	/**
	 * Compare lists of id elements for order. Returns a negative integer, zero, or a
	 * positive integer if the reference is to be ordered less than, equal to, or greater
	 * than that of the given argument.
	 *
	 * @param <E> the underlying id element type
	 * @param ref the reference element list
	 * @param arg the argument element list
	 * @return a relative ordering indicator
	 */
	public static <E extends Comparable<E>> int compare(List<E> ref, List<E> arg) {
		for (Iterator<E> a = ref.iterator(), b = arg.iterator(); a.hasNext() || b.hasNext();) {
			E e1, e2;
			if (a.hasNext()) {
				e1 = a.next();
			} else {
				return -1;
			}

			if (b.hasNext()) {
				e2 = b.next();
			} else {
				return 1;
			}

			int c = e1.compareTo(e2);
			if (c != 0) return c;
		}
		return 0;
	}

	// --------------------------------

	@Override
	public String toString() {
		return ns;
	}
}
