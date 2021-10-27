package net.certiv.common.symtab;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.certiv.common.util.Strings;

public class Scope {

	enum ScopeType {
		GLOBAL, LOCAL;
	}

	protected final Map<String, Symbol<?>> symbolMap = new LinkedHashMap<>();

	public final long gen;
	public ScopeType type;
	public Scope enclosing;

	public Scope(ScopeType type, long gen, Scope enclosing) {
		this.type = type;
		this.gen = gen;
		this.enclosing = enclosing;
	}

	/** Define a symbol in the current scope */
	public <V> void addSymbol(String name) {
		Symbol<V> symbol = new Symbol<>(this, name);
		symbolMap.put(name, symbol);
	}

	/** Define a symbol in the current scope */
	public <V> void addSymbol(String name, V value) {
		Symbol<V> symbol = new Symbol<>(this, name, value);
		symbolMap.put(name, symbol);
	}

	/**
	 * Return the symbol identified by the given name, or {@code null} if not found
	 * in this scope.
	 */
	@SuppressWarnings("unchecked")
	public <V> Symbol<V> get(String name) {
		return (Symbol<V>) symbolMap.get(name);
	}

	/**
	 * Return the symbol identified by the given name, or {@code null} if not found
	 * in the current or any ancestral scope. The search is progressive, returning
	 * the first match found.
	 */
	@SuppressWarnings("unchecked")
	public <V> Symbol<V> resolve(String name) {
		Symbol<V> symbol = (Symbol<V>) symbolMap.get(name); // current scope
		if (symbol != null) return symbol;
		if (enclosing != null) return enclosing.resolve(name);
		return null;
	}

	/**
	 * Return the symbol identified by the given name qualified by the given
	 * {@code args}, or {@code null} if not found in the current or any ancestral
	 * scope. The search is progressive, returning the first match found.
	 */
	public <V> Symbol<V> resolve(String name, List<String> args) {
		String qualifier = Strings.asString(args, true, Strings.DOT);
		return resolve(name + qualifier);
	}

	/** Where to look next for symbols */
	public Scope enclosingScope() {
		return enclosing;
	}

	public void clear() {
		symbolMap.clear();
	}

	@Override
	public String toString() {
		return String.format("%d => %s", gen, symbolMap);
	}
}
