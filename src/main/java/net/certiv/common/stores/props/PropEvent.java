package net.certiv.common.stores.props;

import net.certiv.common.event.IEvtType;
import net.certiv.common.event.TypedChangeEvent;

public class PropEvent extends TypedChangeEvent {

	public static <S extends Props, K, V> PropEvent of(S source, K key, V value) {
		return new PropEvent(source, key.toString(), value, null);
	}

	public static <S extends Props, K, V> PropEvent of(S source, K key, V value, V prior) {
		return new PropEvent(source, key.toString(), value, prior);
	}

	public static <S extends Props, T extends IEvtType, V> PropEvent of(S source, T type, V value, V prior) {
		return new PropEvent(source, type, value, prior);
	}

	public static <S extends Props, T extends IEvtType, K, V> PropEvent of(S source, T type, K key, V value,
			V prior) {
		return new PropEvent(source, type, key.toString(), value, prior);
	}

	// --------------------------------

	private <S extends Props, V> PropEvent(S source, String name, V value) {
		super(source, EvtType.CHANGE, name, value);
	}

	private <S extends Props, V> PropEvent(S source, String name, V value, V prior) {
		super(source, EvtType.CHANGE, name, value, prior);
	}

	private <S extends Props, T extends IEvtType, V> PropEvent(S source, T type, String name, V value,
			V prior) {
		super(source, type, name, value, prior);
	}

	private <S extends Props, T extends IEvtType, V> PropEvent(S source, T type, V value, V prior) {
		super(source, type, value, prior);
	}
}
