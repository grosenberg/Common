package net.certiv.common.symtab;

public class Symbol<V> {

	public final Scope scope; // the owning scope
	public final String name;
	public final V value;

	public Symbol(Scope scope, String name) {
		this(scope, name, null);
	}

	public Symbol(Scope scope, String name, V value) {
		this.scope = scope;
		this.name = name;
		this.value = value;
	}

	public Scope getScope() {
		return scope;
	}

	public String getName() {
		return name;
	}

	public V getValue() {
		return value;
	}

	public int genId() {
		return scope.genId;
	}

	@Override
	public String toString() {
		if (value == null) return String.format("<%s>", name);
		return String.format("<%s:%s>", name, value);
	}
}
