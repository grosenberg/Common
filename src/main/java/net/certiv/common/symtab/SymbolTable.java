package net.certiv.common.symtab;

import java.util.LinkedList;

import net.certiv.common.stores.Counter;
import net.certiv.common.symtab.Scope.ScopeType;

public class SymbolTable {

	private final LinkedList<Scope> scopes = new LinkedList<>();
	private final Counter genCounter = new Counter();

	public SymbolTable() {
		Scope scope = new Scope(ScopeType.GLOBAL, getGen(), null);
		scopes.push(scope);
	}

	/** Adds a named symbol (valueless) to the current scope. */
	public <V> void add(String name) {
		scopes.peek().addSymbol(name);
	}

	/** Adds a named symbol (valueless) to the global scope. */
	public <V> void addGlobal(String name) {
		scopes.peekLast().addSymbol(name);
	}

	/** Adds a named symbol with a value to the current scope. */
	public <V> void add(String name, V value) {
		scopes.peek().addSymbol(name, value);
	}

	/** Adds a named symbol with a value to the global scope. */
	public <V> void addGlobal(String name, V value) {
		scopes.peekLast().addSymbol(name, value);
	}

	/**
	 * Return the symbol identified by the given name, or {@code null} if not found
	 * in the current or any ancestral scope. The search starts in the current
	 * scope, returning the first match found.
	 */
	public <V> Symbol<V> resolve(String name) {
		return scopes.peek().resolve(name);
	}

	/**
	 * Return the symbols identified by the given name in the current and all
	 * ancestral scopes. The search starts in the current scope, returning all
	 * matches found in order of occurrence.
	 */
	public <V> LinkedList<Symbol<V>> resolveAll(String name) {
		LinkedList<Symbol<V>> symbols = new LinkedList<>();
		for (Scope scope : scopes) {
			Symbol<V> symbol = scope.get(name);
			if (symbol != null) symbols.add(symbol);
		}
		return symbols;
	}

	/** Pushes a new scope onto the symbol table scope stack. */
	public Scope pushScope() {
		Scope scope = new Scope(ScopeType.LOCAL, nextGen(), scopes.peek());
		scopes.push(scope);
		return scope;
	}

	/** Pops the current scope; will not remove the global scope. */
	public void popScope() {
		if (scopes.size() > 1) scopes.pop();
	}

	public void clear() {
		while (scopes.size() > 1) {
			Scope scope = scopes.pop();
			scope.clear();
		}
		scopes.peek().clear();
	}

	public int scopeDepth() {
		return scopes.size();
	}

	public Scope currentScope() {
		return scopes.peek();
	}

	public Scope getScope(long gen) {
		for (Scope scope : scopes) {
			if (scope.gen == gen) return scope;
		}
		return null;
	}

	public long getGen() {
		return currentScope().gen;
	}

	private long nextGen() {
		return genCounter.incrementAndGet();
	}

	@Override
	public String toString() {
		return scopes.toString();
	}
}
