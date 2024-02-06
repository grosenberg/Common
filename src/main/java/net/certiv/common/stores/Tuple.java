package net.certiv.common.stores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tuple<V> {

	protected final List<V> data;

	@SuppressWarnings("unchecked")
	public static <V> Tuple<V> of(V... data) {
		return new Tuple<>(data);
	}

	@SuppressWarnings("unchecked")
	protected Tuple(V... data) {
		this.data = Arrays.asList(data);
	}

	public List<V> get() {
		return new ArrayList<>(data);
	}

	public V get(int idx) {
		return data.get(idx);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Tuple)) return false;
		Tuple<?> other = (Tuple<?>) obj;
		return Objects.equals(data, other.data);
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
