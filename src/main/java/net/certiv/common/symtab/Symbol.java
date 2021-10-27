package net.certiv.common.symtab;

import net.certiv.common.util.Assert;

public class Symbol<V> {

	/** The owning scope. */
	public final Scope scope;
	/** Symbol name. */
	public final String name;
	/** Symbol has a value. */
	private boolean valued;
	/** Symbol value. */
	public final V value;

	/** Defines a name-only/valueless symbol. */
	public Symbol(Scope scope, String name) {
		this(scope, name, null);
		valued = false;
	}

	/** Defines a named symbol with a value. */
	public Symbol(Scope scope, String name, V value) {
		Assert.notNull(scope);
		Assert.notEmpty(name);
		this.scope = scope;
		this.name = name;
		this.value = value;
		valued = true;
	}

	public Scope getScope() {
		return scope;
	}

	public String getName() {
		return name;
	}

	public boolean hasValue() {
		return valued;
	}

	public V getValue() {
		return value;
	}

	public long gen() {
		return scope.gen;
	}

	@Override
	public String toString() {
		if (!valued) return String.format("%s (%d)", name, scope.gen);
		return String.format("%s=%s (%d)", name, value, scope.gen);
	}
}
