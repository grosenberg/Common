/*******************************************************************************
 * Copyright (c) 2017, 2020 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores.context;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.ex.NotImplementedException;

/** Context implements a scoped key/value store. */
public class Context implements IContext {

	/** Scoping stack of instance key/value stores. */
	private final LinkedList<KVStore> scopes = new LinkedList<>();
	/** Maximum depth of the scope stack. Default is 1. */
	private int maxDepth = 1;

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @return a new initialized Context
	 */
	public static Context of() {
		return new Context(1, true);
	}

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @return a new initialized Context
	 */
	public static Context of(int depth) {
		return new Context(depth, true);
	}

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param init  {@code true} to initialize to contain an empty first scope
	 * @return a new, conditionally initialized Context
	 */
	private static Context of(int depth, boolean init) {
		return new Context(depth, init);
	}

	/**
	 * Creates a new {@code Context} containing the given stores as the ordered scopes for
	 * storing typed key/value pairs. The context maximum depth is set to the given stores
	 * size.
	 *
	 * @param stores ordered scope list
	 * @return a new Context initialized with the given stores
	 */
	public static Context of(List<KVStore> stores) {
		Context ctx = new Context(stores.size(), false);
		ctx.addAll(stores);
		return ctx;
	}

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param init  initialize to contain an empty first scope
	 */
	private Context(int depth, boolean init) {
		this.maxDepth = depth;
		if (init) scopes.add(new KVStore());
	}

	private int add(KVStore store) {
		return add(0, store);
	}

	private int add(int idx, KVStore store) {
		Assert.notNull(store);
		idx = fixIndex(idx);
		scopes.add(idx, store);
		fixDepth();
		return idx;
	}

	private int addAll(Collection<KVStore> stores) {
		return addAll(0, stores);
	}

	private int addAll(int idx, Collection<KVStore> stores) {
		Assert.notNull(stores);
		idx = fixIndex(idx);
		scopes.addAll(idx, stores);
		fixDepth();
		return idx;
	}

	private int fixIndex(int idx) {
		Assert.isTrue(idx >= 0 && idx < maxDepth);
		return Math.min(idx, depth());
	}

	private void fixDepth() {
		while (scopes.size() > maxDepth) {
			scopes.removeLast();
		}
	}

	@SuppressWarnings("unchecked")
	private <V> KVStore flatten(Context ctx) {
		KVStore store = new KVStore();
		ctx.keyStream().forEach(k -> store.putValue((Key<V>) k, (Value<V>) ctx.getValue(k)));
		return store;
	}

	@Override
	public <V> V get(Key<V> key) {
		for (KVStore scope : scopes) {
			if (scope.contains(key)) return scope.get(key);
		}
		return null;
	}

	@Override
	public <V> V get(Key<V> key, V def) {
		for (KVStore scope : scopes) {
			if (scope.contains(key)) return scope.get(key);
		}
		return def;
	}

	@Override
	public Map<Key<?>, Value<?>> getAll() {
		return flatten(this).getAll();
	}

	@Override
	public <V> Value<V> getValue(Key<V> key) {
		for (KVStore scope : scopes) {
			if (scope.contains(key)) return scope.getValue(key);
		}
		return null;
	}

	@Override
	public <V> Value<V> getValue(Key<V> key, Value<V> def) {
		for (KVStore scope : scopes) {
			if (scope.contains(key)) return scope.getValue(key);
		}
		return def;
	}

	@Override
	public boolean contains(Key<?> key) {
		for (KVStore scope : scopes) {
			if (scope.contains(key)) return true;
		}
		return false;
	}

	@Override
	public <V> V put(Key<V> key, V value) {
		return scopes.getFirst().put(key, value);
	}

	@Override
	public <V> V put(Key<V> key, V value, String unit) {
		Assert.notNull(key, value);
		Assert.notEmpty(unit);
		return (V) putValue(key, Value.of(value, unit));
	}

	@Override
	public <V> void putAll(IKVStore store) {
		scopes.getFirst().putAll(store);
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value) {
		if (!contains(key)) put(key, value);
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value, String unit) {
		putValueIfAbsent(key, Value.of(value, unit));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> void putAllIfAbsent(IKVStore store) {
		store.getAll().forEach((k, v) -> scopes.getFirst().putValueIfAbsent((Key<V>) k, (Value<V>) v));
	}

	@Override
	public <V> V putValue(Key<V> key, Value<V> value) {
		return scopes.getFirst().putValue(key, value);
	}

	@Override
	public <V> void putValueIfAbsent(Key<V> key, Value<V> value) {
		if (!contains(key)) putValue(key, value);
	}

	@Override
	public <V> V putIfNotNull(Key<V> key, V value) {
		if (value != null) return put(key, value);
		return get(key);
	}

	@Override
	public <V> V remove(Key<V> key) {
		if (!scopes.getFirst().contains(key)) return null;
		return scopes.getFirst().remove(key);
	}

	@Override
	public Set<Key<?>> keys() {
		return scopes.stream() //
				.flatMap(s -> s.keyStream()) //
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public Stream<Key<?>> keyStream() {
		return keys().stream();
	}

	@Override
	public void forEach(BiConsumer<? super Key<?>, ? super Value<?>> action) {
		flatten(this).forEach(action);
	}

	/** Returns the current maximum scope depth of this Context. */
	public int maxDepth() {
		return maxDepth;
	}

	/**
	 * Adjusts the maximum scope depth of this Context. If the adjustment reduces the
	 * depth to less than the current actual depth, the excess scopes are either trimmed
	 * (and lost) or flattened and merged into a single scope at max depth, depending on
	 * the state of the {@code trim} parameter.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param trim  {@code true} to trim excess scopes; otherwise flatten
	 */
	public void adjustMaxDepth(int depth, boolean trim) {
		Assert.isTrue(depth > 0);
		if (!trim && depth < scopes.size()) {
			KVStore last = flatten(Context.of(scopes.subList(depth - 1, scopes.size())));
			scopes.set(depth - 1, last);
		}
		while (scopes.size() > depth) {
			scopes.removeLast();
		}
		this.maxDepth = depth;
	}

	@Override
	public int depth() {
		return scopes.size();
	}

	@Override
	public int size() {
		return keys().size();
	}

	@Override
	public boolean isEmpty() {
		return keys().isEmpty();
	}

	@Override
	public void clear() {
		scopes.forEach(s -> s.clear());
	}

	/** Returns a copy of just the top-most scope of this {@code Context}. */
	@Override
	public KVStore delta() {
		return scopes.getFirst().dup();
	}

	@Override
	public Context dup() {
		return dup(depth());
	}

	@Override
	public Context dup(int depth) {
		Assert.isTrue(depth >= 1 && depth <= maxDepth);
		depth = Math.min(depth, depth());
		Context copy = Context.of(depth, false);
		for (int idx = 0; idx < depth; idx++) {
			copy.scopes.addLast(scopes.get(idx).dup());
		}
		return copy;
	}

	@Override
	public UUID merge(IKVStore store) {
		Assert.isTrue(store != null);
		UUID mark = get(MARK);
		if (store instanceof KVStore) {
			add((KVStore) store.dup());
		} else if (store instanceof Context) {
			for (int idx = store.size() - 1; idx >= 0; idx--) {
				add(((Context) store).scopes.get(idx).dup());
			}
		} else {
			throw new NotImplementedException();
		}
		return mark;
	}

	@Override
	public UUID merge(int idx, IKVStore store) {
		Assert.isTrue(store != null);
		Assert.isTrue(idx < depth());
		UUID mark = scopes.get(idx).get(MARK);
		if (store instanceof KVStore) {
			add(idx, (KVStore) store.dup());
		} else if (store instanceof Context) {
			for (int jdx = store.size() - 1; jdx >= 0; jdx--) {
				add(idx, ((Context) store).scopes.get(jdx).dup());
			}
		} else {
			throw new NotImplementedException();
		}
		return mark;
	}

	@Override
	public boolean restore(UUID mark) {
		Assert.notNull(mark);
		int idx = scopeOf(MARK, mark);
		if (idx == -1) return false;

		scopes.subList(0, idx).clear();
		return true;
	}

	protected int scopeOf(Key<?> key, Object value) {
		for (int idx = 0; idx < depth(); idx++) {
			KVStore scope = scopes.get(idx);
			if (scope.contains(key)) {
				if (scope.get(key).equals(value)) return idx;
			}
		}
		return -1;
	}

	protected int scopeOf(Key<?> key) {
		for (int idx = 0; idx < depth(); idx++) {
			if (scopes.get(idx).contains(key)) return idx;
		}
		return -1;
	}

	@Override
	public int hashCode() {
		return Objects.hash(scopes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Context)) return false;
		Context other = (Context) obj;
		return Objects.equals(scopes, other.scopes);
	}

	@Override
	public String toString() {
		return scopes.toString();
	}
}
