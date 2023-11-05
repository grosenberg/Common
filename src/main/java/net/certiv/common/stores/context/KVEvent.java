package net.certiv.common.stores.context;

import net.certiv.common.event.TypedChangeEvent;

public class KVEvent extends TypedChangeEvent {

	public static <S extends IKVScope, K, V> KVEvent of(S store, K key, V value) {
		return new KVEvent(store, key.toString(), value, null);
	}

	public static <S extends IKVScope, K, V> KVEvent of(S store, K key, V value, V prior) {
		return new KVEvent(store, key.toString(), value, prior);
	}

	public static <S extends IKVScope, T extends IEvtType, V> KVEvent of(S store, T type, V value, V prior) {
		return new KVEvent(store, type, value, prior);
	}

	public static <S extends IKVScope, T extends IEvtType, K, V> KVEvent of(S store, T type, K key, V value,
			V prior) {
		return new KVEvent(store, type, key.toString(), value, prior);
	}

	// --------------------------------

	private <S extends IKVScope, V> KVEvent(S store, String name, V value) {
		super(store, EvtType.CHANGE, name, value);
	}

	private <S extends IKVScope, V> KVEvent(S store, String name, V value, V prior) {
		super(store, EvtType.CHANGE, name, value, prior);
	}

	private <S extends IKVScope, T extends IEvtType, V> KVEvent(S store, T type, String name, V value,
			V prior) {
		super(store, type, name, value, prior);
	}

	private <S extends IKVScope, T extends IEvtType, V> KVEvent(S store, T type, V value, V prior) {
		super(store, type, value, prior);
	}
}
