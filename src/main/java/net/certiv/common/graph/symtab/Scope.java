package net.certiv.common.graph.symtab;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.certiv.common.util.Strings;

public class Scope {

	protected final Map<String, Symbol<?>> symbolMap = new LinkedHashMap<>();

	public final int genId;
	public ScopeType type;
	public Scope enclosing;

	public Scope(ScopeType type, int genId, Scope enclosing) {
		this.type = type;
		this.genId = genId;
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
	 * Return the symbol identified by the given name or {@code null} if not found in the
	 * current or any ancestral scope. The search is progressive, returning the first
	 * match found.
	 */
	public Symbol<?> resolve(String name) {
		Symbol<?> symbol = symbolMap.get(name); // current scope
		if (symbol != null) return symbol;
		if (enclosing != null) return enclosing.resolve(name);
		return null;
	}

	/**
	 * Return the symbol identified by the given name qualified by the given {@code args}
	 * or {@code null} if not found in the current or any ancestral scope. The search is
	 * progressive, returning the first match found.
	 */
	public Symbol<?> resolve(String name, List<String> args) {
		String qualifier = Strings.asString(args, true, Strings.DOT);
		return resolve(name + qualifier);
	}

	/** Where to look next for symbols */
	public Scope enclosingScope() {
		return enclosing;
	}

	@Override
	public String toString() {
		return symbolMap.keySet().toString();
	}
}
