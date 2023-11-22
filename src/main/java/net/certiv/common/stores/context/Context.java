/*******************************************************************************
 * Copyright (c) 2017 - 2023 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.common.stores.context;

import java.util.Collection;
import java.util.Collections;
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
import net.certiv.common.event.ITypedEventDispatcher;
import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEvent.IEvtType;
import net.certiv.common.event.TypedEventDispatcher;
import net.certiv.common.event.TypedEventListener;
import net.certiv.common.ex.NotImplementedException;
import net.certiv.common.stores.LimitList;
import net.certiv.common.util.Maths;

/**
 * Context implements a multi-level scoped key:value store. Each stored {@link Key} and
 * {@link Value} pair is consistently typed, while the store itself is untyped.
 * <p>
 * Each scope level is implemented using a {@link KVScope} instance. Value write-style
 * operations are directed to the first, top-most level. Read-style operations execute
 * against each level, top down, until a value is found.
 * <p>
 * When reactivity is enabled, write events are only issued from the dispatcher present in
 * this context; all scope level dispatchers are disabled.
 */
public class Context implements IContext {

	/** Implementing class name for Convertable. */
	protected final String className = getClass().getName();

	/** Scoping stack of instance key:value stores. Default depth is 1. */
	private final LimitList<KVScope> scopes = new LimitList<>(1);
	/** Event dispatcher. */
	private transient ITypedEventDispatcher dispatcher;
	/** Event dispatch enabled state. */
	private transient boolean reactive;

	/**
	 * Creates a context having, initially, one level and a depth limit initially set to
	 * the given depth.
	 *
	 * @param depth the defined maximum number of level scopes
	 * @return a context
	 */
	public static Context of(int depth) {
		return new Context(depth, true);
	}

	/**
	 * Creates a context having a depth limit initially set to the given depth and
	 * conditionally initialized to include a first scope level.
	 *
	 * @param depth the defined maximum number of level scopes
	 * @param init  {@code true} to include an empty top-level scope
	 * @return a context
	 */
	public static Context of(int depth, boolean init) {
		return new Context(depth, init);
	}

	/**
	 * Creates a context containing the given stores as the scope levels of this context.
	 * The depth limit is initially set to the given stores size.
	 *
	 * @param scopes ordered scope list
	 * @return a new Context initialized with the given stores
	 */
	public static Context of(List<KVScope> scopes) {
		Context context = new Context(scopes.size(), false);
		context.insert(scopes);
		return context;
	}

	// --------------------------------

	/**
	 * Creates a Context with read-through support to a list of up to maximum of
	 * {@code depth} key:value stores.
	 *
	 * @param depth the defined maximum number of scoped key:value stores
	 * @param init  initialize to contain an empty first scope
	 */
	private Context(int depth, boolean init) {
		scopes.adjustLimit(depth);
		if (init) scopes.add(new KVScope());
	}

	/**
	 * Marks this Context as reactive by enabling the dispatcher. Adds a new
	 * {@link TypedEventDispatcher} if no other dispatcher has been set on this Context.
	 *
	 * @return this
	 */
	public Context reactive() {
		return reactive(true);
	}

	/**
	 * Marks this Context as reactive by enabling the dispatcher depending on the given
	 * enable state. Adds a new {@link TypedEventDispatcher} if no other dispatcher has
	 * been set on this Context.
	 *
	 * @param enable reactivity state
	 * @return this
	 */
	public Context reactive(boolean enable) {
		if (reactive && dispatcher == null) dispatcher = new TypedEventDispatcher();
		this.reactive = enable;
		return this;
	}

	/**
	 * Marks this Context as reactive by adding and enabling the given dispatcher.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	public Context reactive(ITypedEventDispatcher dispatcher) {
		return reactive(dispatcher, true);
	}

	/**
	 * Marks this Context as reactive by adding and enabling the given dispatcher
	 * depending on the given enable state.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param enable     reactivity state
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	public Context reactive(ITypedEventDispatcher dispatcher, boolean enable) {
		this.dispatcher = dispatcher;
		this.reactive = dispatcher != null ? enable : false;
		return this;
	}

	@Override
	public <V> V get(Key<V> key) {
		for (KVScope scope : scopes) {
			if (scope.contains(key)) return scope.get(key);
		}
		return null;
	}

	@Override
	public <V> V get(Key<V> key, V def) {
		for (KVScope scope : scopes) {
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
		for (KVScope scope : scopes) {
			if (scope.contains(key)) return scope.getValue(key);
		}
		return null;
	}

	@Override
	public <V> Value<V> getValue(Key<V> key, Value<V> def) {
		for (KVScope scope : scopes) {
			if (scope.contains(key)) return scope.getValue(key);
		}
		return def;
	}

	@Override
	public boolean contains(Key<?> key) {
		for (KVScope scope : scopes) {
			if (scope.contains(key)) return true;
		}
		return false;
	}

	@Override
	public <V> V put(Key<V> key, V value) {
		return putValue(key, Value.of(value));
	}

	@Override
	public <V> V put(Key<V> key, V value, String unit) {
		return (V) putValue(key, Value.of(value, unit));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> void putAll(IKVScope scope) {
		scope.getAll().forEach((k, v) -> putValue((Key<V>) k, (Value<V>) v));
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value) {
		if (!contains(key)) putValue(key, Value.of(value));
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value, String unit) {
		if (!contains(key)) putValue(key, Value.of(value, unit));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> void putAllIfAbsent(IKVScope scope) {
		scope.getAll().forEach((k, v) -> putValueIfAbsent((Key<V>) k, (Value<V>) v));
	}

	@Override
	public <V> V putValue(Key<V> key, Value<V> value) {
		V prior = firstScope().putValue(key, value);
		fire(KVEvent.of(this, key, value, prior));
		return prior;
	}

	@Override
	public <V> void putValueIfAbsent(Key<V> key, Value<V> value) {
		if (!contains(key)) putValue(key, value);
	}

	@Override
	public <V> V putIfNotNull(Key<V> key, V value) {
		if (value != null) return putValue(key, Value.of(value));
		return get(key);
	}

	@Override
	public <V> V remove(Key<V> key) {
		return remove(key, RmvScope.TOP_LEVEL);
	}

	@Override
	public <V> V remove(Key<V> key, RmvScope at) {
		V prior = null;
		switch (at) {
			default:
			case TOP_LEVEL:
				prior = firstScope().remove(key);
				break;

			case FIRST_VISBLE:
				int dot = scopeOf(key);
				if (dot > -1) prior = scopes.get(dot).remove(key);
				break;

			case ALL:
				prior = get(key);
				if (prior != null) {
					for (KVScope scope : scopes) {
						scope.remove(key);
					}
				}
				break;
		}
		fire(KVEvent.of(this, key, null, prior));
		return prior;
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

	@Override
	public int depth() {
		return scopes.size();
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
	 * @param depth the defined maximum number of scoped key:value stores
	 * @param trim  {@code true} to trim excess scopes; otherwise flatten
	 */
	public void adjustMaxDepth(int depth, boolean trim) {
		LinkedList<KVScope> excess = scopes.adjustLimit(depth);
		if (!excess.isEmpty() && !trim) {
			Context last = Context.of(excess);
			last.insert(scopes.removeLast());
			scopes.addLast(flatten(last));
		}
	}

	@Override
	public int size() {
		return keys().size();
	}

	@Override
	public boolean isEmpty() {
		return keys().isEmpty();
	}

	public boolean isFirstEmpty() {
		return keys().isEmpty();
	}

	@Override
	public void clear() {
		keys().forEach(k -> remove(k, RmvScope.ALL));
	}

	/** Returns a copy of just the top-most scope of this {@code Context}. */
	@Override
	public KVScope delta() {
		return firstScope().dup();
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
	public UUID freshen() {
		if (firstScope().isEmpty()) return EOS;
		return merge(0, new KVScope());
	}

	@Override
	public UUID mergeFirst(IKVScope scope) {
		return merge(0, scope);
	}

	@Override
	public UUID merge(int idx, IKVScope scope) {
		Assert.isTrue(scope != null);
		idx = Maths.constrain(idx, 0, depth());
		if (idx == depth()) return mergeLast(scope);

		UUID mark = scopes.get(idx).mark;
		int limit = scopes.limit();
		if (scope instanceof KVScope) {
			if (depth() == limit) scopes.adjustLimit(limit + 1);
			insert(idx, (KVScope) scope.dup());

		} else if (scope instanceof Context) {
			Context ctx = (Context) scope;
			int max = depth() + ctx.depth();
			if (max > limit) scopes.adjustLimit(max);

			for (int jdx = scope.depth() - 1; jdx >= 0; jdx--) {
				insert(idx, ctx.scopes.get(jdx).dup());
			}

		} else {
			throw new NotImplementedException();
		}
		return mark;
	}

	@Override
	public UUID mergeLast(IKVScope scope) {
		Assert.isTrue(scope != null);
		UUID mark = !scopes.isEmpty() ? scopes.getLast().mark : EOS;
		int limit = scopes.limit();
		if (scope instanceof KVScope) {
			if (depth() == limit) scopes.adjustLimit(limit + 1);
			scopes.addLast((KVScope) scope.dup());

		} else if (scope instanceof Context) {
			Context ctx = (Context) scope;
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
		int idx = scopeOf(mark);
		if (idx == -1) return false;

		scopes.subList(0, idx).clear();
		return true;
	}

	// /** Internal: index of the first scope containing key:value, or -1 if not found. */
	// private int scopeOf(Key<?> key, Value<?> value) {
	// for (int idx = 0; idx < depth(); idx++) {
	// KVScope scope = scopes.get(idx);
	// if (scope.contains(key)) {
	// if (scope.getValue(key).equals(value)) return idx;
	// }
	// }
	// return -1;
	// }

	/** Internal: index of the first scope containing key, or -1 if not found. */
	private int scopeOf(Key<?> key) {
		for (int idx = 0; idx < depth(); idx++) {
			if (scopes.get(idx).contains(key)) return idx;
		}
		return -1;
	}

	/** Internal: index of the first scope containing mark, or -1 if not found. */
	private int scopeOf(UUID mark) {
		for (int idx = 0; idx < depth(); idx++) {
			if (scopes.get(idx).mark.equals(mark)) return idx;
		}
		return -1;
	}

	/** Internal: get first scope; ensures the scope exists. */
	private KVScope firstScope() {
		if (scopes.isEmpty()) insert(new KVScope());
		return scopes.peekFirst();
	}

	/** Internal: get last scope; ensures the scope exists. */
	@SuppressWarnings("unused")
	private KVScope lastScope() {
		if (scopes.isEmpty()) insert(new KVScope());
		return scopes.peekLast();
	}

	/** Internal: insert at 0; sets scope reactivity to false. */
	private void insert(KVScope scope) {
		insert(0, scope);
	}

	/** Internal: insert all starting at 0; sets all scopes reactivity to false. */
	private void insert(Collection<KVScope> scopes) {
		insert(0, scopes);
	}

	/** Internal: insert at idx; sets scope reactivity to false. */
	private void insert(int idx, KVScope scope) {
		if (scope != null) {
			scope.reactive(false);
			int at = Maths.constrain(idx, 0, depth());
			scopes.add(at, scope);
		}
	}

	/** Internal: insert all starting at idx; sets all scopes reactivity to false. */
	private void insert(int idx, Collection<KVScope> scopes) {
		if (scopes != null) {
			int at = Maths.constrain(idx, 0, depth());
			List<KVScope> ordered = new LinkedList<>(scopes);
			Collections.reverse(ordered);
			ordered.forEach(s -> insert(at, s));
		}
	}

	/** Internal: flatten context to a single scope. */
	@SuppressWarnings("unchecked")
	private <V> KVScope flatten(Context context) {
		KVScope scope = new KVScope();
		context.keyStream().forEach(k -> scope.putValue((Key<V>) k, (Value<V>) context.getValue(k)));
		return scope;
	}

	// ---- Event Handler Delegates ----

	@Override
	public void fire(TypedEvent event) {
		if (reactive && dispatcher != null) dispatcher.fire(event);
	}

	@Override
	public void addListener(TypedEventListener listener) {
		if (dispatcher != null) dispatcher.addListener(listener);
	}

	@Override
	public void addListeners(Collection<TypedEventListener> listeners) {
		if (dispatcher != null) dispatcher.addListeners(listeners);
	}

	@Override
	public boolean hasListeners(IEvtType type) {
		return dispatcher != null ? dispatcher.hasListeners(type) : false;
	}

	@Override
	public Set<TypedEventListener> getListeners() {
		return dispatcher != null ? dispatcher.getListeners() : Set.of();
	}

	@Override
	public Set<IEvtType> getListenerTypes() {
		return dispatcher != null ? dispatcher.getListenerTypes() : Set.of();
	}

	@Override
	public int getListenerCount() {
		return dispatcher != null ? dispatcher.getListenerCount() : 0;
	}

	@Override
	public void removeListener(TypedEventListener listener) {
		if (dispatcher != null) dispatcher.removeListener(listener);
	}

	@Override
	public void clearListeners() {
		if (dispatcher != null) dispatcher.clearListeners();
	}

	// --------------------------------

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
