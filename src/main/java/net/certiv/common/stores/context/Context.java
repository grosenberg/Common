/*******************************************************************************
 * Copyright (c) 2017 - 2023 Certiv Analytics. All rights reserved.
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
import net.certiv.common.stores.LimitList;
import net.certiv.common.util.Maths;

/** Context implements a scoped key/value store. */
public class Context implements IContext {

	/** Scoping stack of instance key/value stores. Default depth is 1. */
	private final LimitList<KVStore> scopes = new LimitList<>(1);

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
	 * Creates a new {@code Context} containing the given stores as the ordered scopes for
	 * storing typed key/value pairs. The context maximum depth is set to the given stores
	 * size.
	 *
	 * @param stores ordered scope list
	 * @return a new Context initialized with the given stores
	 */
	public static Context of(List<KVStore> stores) {
		Context ctx = new Context(stores.size(), false);
		ctx.insertAll(stores);
		return ctx;
	}

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param init  {@code true} to include an empty top-level scope
	 * @return a new, conditionally initialized Context
	 */
	public static Context of(int depth, boolean init) {
		return new Context(depth, init);
	}

	/**
	 * Creates a new {@code Context} with read-through support to a list of up to maximum
	 * of {@code depth} key/value stores.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param init  initialize to contain an empty first scope
	 */
	private Context(int depth, boolean init) {
		scopes.adjustLimit(depth);
		if (init) scopes.add(new KVStore());
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
		return scopes.limit();
	}

	/**
	 * Adjusts the maximum scope depth of this Context.
	 * <p>
	 * If the adjustment reduces the depth to less than the current actual depth, the
	 * excess scopes are either trimmed (and lost) or flattened and merged into a single
	 * scope at max depth, depending on the state of the {@code trim} parameter.
	 *
	 * @param depth the defined maximum number of scoped key/value stores
	 * @param trim  {@code true} to trim excess scopes; otherwise flatten
	 */
	public void adjustMaxDepth(int depth, boolean trim) {
		LinkedList<KVStore> res = scopes.adjustLimit(depth);
		if (!res.isEmpty() && !trim) {
			Context last = Context.of(res);
			last.insert(scopes.removeLast());
			scopes.addLast(flatten(last));
		}
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
		depth = Maths.constrain(depth, 1, depth());

		Context copy = Context.of(depth, false);
		for (int idx = 0; idx < depth; idx++) {
			copy.scopes.addLast(scopes.get(idx).dup());
		}
		return copy;
	}

	@Override
	public UUID mergeFirst(IKVStore store) {
		return merge(0, store);
	}

	@Override
	public UUID merge(int idx, IKVStore store) {
		Assert.isTrue(store != null);
		idx = Maths.constrain(idx, 0, depth());
		if (idx == depth()) return mergeLast(store);

		UUID mark = scopes.get(idx).get(MARK);
		int limit = scopes.limit();
		if (store instanceof KVStore) {
			if (depth() == limit) scopes.adjustLimit(limit + 1);
			insert(idx, (KVStore) store.dup());

		} else if (store instanceof Context) {
			Context ctx = (Context) store;
			int max = depth() + ctx.depth();
			if (max > limit) scopes.adjustLimit(max);

			for (int jdx = store.depth() - 1; jdx >= 0; jdx--) {
				insert(idx, ctx.scopes.get(jdx).dup());
			}

		} else {
			throw new NotImplementedException();
		}
		return mark;
	}

	@Override
	public UUID mergeLast(IKVStore store) {
		Assert.isTrue(store != null);
		UUID mark = scopes.getLast().get(MARK);
		int limit = scopes.limit();
		if (store instanceof KVStore) {
			if (depth() == limit) scopes.adjustLimit(limit + 1);
			scopes.addLast((KVStore) store.dup());

		} else if (store instanceof Context) {
			Context ctx = (Context) store;
			int max = depth() + ctx.depth();
			if (max > limit) scopes.adjustLimit(max);

			for (int idx = 0; idx < ctx.depth(); idx++) {
				scopes.addLast(ctx.scopes.get(idx).dup());
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

	private void insert(KVStore store) {
		insert(0, store);
	}

	private void insert(int idx, KVStore store) {
		if (store != null) {
			scopes.add(idx, store);
		}
	}

	private void insertAll(Collection<KVStore> stores) {
		insertAll(0, stores);
	}

	private void insertAll(int idx, Collection<KVStore> stores) {
		if (stores != null) {
			idx = Maths.constrain(idx, 0, depth());
			scopes.addAll(idx, stores);
		}
	}

	@SuppressWarnings("unchecked")
	private <V> KVStore flatten(Context ctx) {
		KVStore store = new KVStore();
		ctx.keyStream().forEach(k -> store.putValue((Key<V>) k, (Value<V>) ctx.getValue(k)));
		return store;
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
