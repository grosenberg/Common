package net.certiv.common.stores.context;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import net.certiv.common.check.Assert;
import net.certiv.common.event.ITypedEventDispatcher;
import net.certiv.common.event.TypedEvent;
import net.certiv.common.event.TypedEvent.IEvtType;
import net.certiv.common.event.TypedEventDispatcher;
import net.certiv.common.event.TypedEventListener;

/**
 * A consistently key:value store. That is, each stored {@link Key} and {@link Value} pair
 * is consistently typed, while the store itself is untyped.
 * <p>
 * Used as the single scope in a single-level {@link KVStore} and for each scope in a
 * multi-level {@link Context}.
 * <p>
 * Use of an {@link ITypedEventDispatcher} instance is supported. Both {@link KVStore} and
 * {@link Context} will install an {@link TypedEventDispatcher} instance.
 */
public class KVScope implements IKVScope {

	protected final Map<Key<?>, Value<?>> scope = new LinkedHashMap<>();
	/** Permanent, unique scope identification marker. */
	protected final UUID mark;
	/** Event dispatcher. */
	protected transient ITypedEventDispatcher dispatcher;
	/** Event dispatch reactivity enabled state. */
	protected transient boolean reactive;

	/**
	 * Makes a random scope UUID marker. By definition, will not be equal to the
	 * {@link IKVScopeMin#NO_SCOPE} marker internally used as an end-of-scopes
	 * delineation.
	 *
	 * @return a unique scope UUID
	 */
	static UUID mkMark() {
		UUID uuid = UUID.randomUUID();
		if (uuid.equals(NO_SCOPE)) {
			uuid = UUID.randomUUID();
		}
		return uuid;
	}

	public KVScope() {
		mark = mkMark();
	}

	/**
	 * Marks this KVScope as reactive by enabling the dispatcher depending on the given
	 * enable state. Adds a new {@link TypedEventDispatcher} if no other dispatcher has
	 * been set on this KVScope.
	 *
	 * @param enable reactivity state
	 * @return this
	 */
	public KVScope reactive(boolean enable) {
		this.reactive = enable;
		if (reactive && dispatcher == null) dispatcher = new TypedEventDispatcher();
		return this;
	}

	/**
	 * Marks this KVScope as reactive by adding and enabling the given dispatcher.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	public KVScope reactive(ITypedEventDispatcher dispatcher) {
		return reactive(dispatcher, true);
	}

	/**
	 * Marks this KVScope as reactive by adding and enabling the given dispatcher
	 * depending on the given enable state.
	 * <p>
	 * Removes any existing dispatcher, and disables reactivity, if the given dispatcher
	 * is {@code null}.
	 *
	 * @param enable     reactivity state
	 * @param dispatcher a dispatcher, or {@code null} to remove
	 * @return this
	 */
	public KVScope reactive(ITypedEventDispatcher dispatcher, boolean enable) {
		this.dispatcher = dispatcher;
		this.reactive = dispatcher != null ? enable : false;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V get(Key<V> key) {
		Assert.notNull(key);
		if (!scope.containsKey(key)) return null;
		return (V) scope.get(key).value();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V get(Key<V> key, V def) {
		Assert.notNull(key);
		if (!scope.containsKey(key)) return def;
		return (V) scope.get(key).value();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Value<V> getValue(Key<V> key) {
		return (Value<V>) scope.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Value<V> getValue(Key<V> key, Value<V> def) {
		if (!scope.containsKey(key)) return def;
		return (Value<V>) scope.get(key);
	}

	@Override
	public Map<Key<?>, Value<?>> getAll() {
		return Collections.unmodifiableMap(scope);
	}

	@Override
	public <V> V put(Key<V> key, V value) {
		Assert.notNull(key, value);
		return (V) putValue(key, Value.of(value));
	}

	@Override
	public <V> V put(Key<V> key, V value, String unit) {
		Assert.notNull(key, value);
		return (V) putValue(key, Value.of(value, unit));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> V putValue(Key<V> key, Value<V> value) {
		Assert.notNull(key, value);
		V prior = (V) scope.put(key, value);
		fire(KVEvent.of(this, key, value, prior));
		return prior;
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value) {
		putIfAbsent(key, value, null);
	}

	@Override
	public <V> void putIfAbsent(Key<V> key, V value, String unit) {
		Assert.notNull(key);
		if (!contains(key)) put(key, value, unit);
	}

	@Override
	public <V> void putValueIfAbsent(Key<V> key, Value<V> value) {
		Assert.notNull(key, value);
		if (!contains(key)) putValue(key, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> void putAll(IKVScope scope) {
		scope.forEach((k, v) -> putValue((Key<V>) k, (Value<V>) v));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> void putAllIfAbsent(IKVScope scope) {
		scope.getAll().forEach((k, v) -> putValueIfAbsent((Key<V>) k, (Value<V>) v));
	}

	@Override
	public <V> V putIfNotNull(Key<V> key, V value) {
		if (value != null) return put(key, value);
		return get(key);
	}

	@Override
	public Set<Key<?>> keys() {
		return new LinkedHashSet<>(scope.keySet());
	}

	@Override
	public Stream<Key<?>> keyStream() {
		return keys().stream();
	}

	@Override
	public void forEach(BiConsumer<? super Key<?>, ? super Value<?>> action) {
		scope.forEach(action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V remove(Key<V> key) {
		V prior = (V) scope.remove(key);
		fire(KVEvent.of(this, key, null, prior));
		return prior;
	}

	@Override
	public boolean contains(Key<?> key) {
		return scope.containsKey(key);
	}

	@Override
	public void clear() {
		keys().forEach(k -> remove(k));
	}

	@Override
	public int size() {
		return keys().size();
	}

	@Override
	public int depth() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return keys().isEmpty();
	}

	@Override
	public KVScope dup() {
		KVScope dup = new KVScope();
		keys().forEach(k -> dup.scope.put(k, getValue(k)));
		dup.reactive(dispatcher, reactive);
		return dup;
	}

	@Override
	public KVScope delta() {
		return dup();
	}

	@Override
	public UUID mergeFirst(IKVScope scope) {
		scope.keys().forEach(k -> this.scope.put(k, getValue(k)));
		return mark;
	}

	@Override
	public UUID mergeLast(IKVScope scope) {
		scope.keys().forEach(k -> this.scope.putIfAbsent(k, getValue(k)));
		return mark;
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
		return Objects.hash(scope);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof KVScope)) return false;
		KVScope other = (KVScope) obj;
		return Objects.equals(scope, other.scope);
	}

	@Override
	public String toString() {
		return String.format("%s %s", mark, scope);
	}
}
