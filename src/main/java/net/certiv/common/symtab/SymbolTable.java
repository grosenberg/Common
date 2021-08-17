package net.certiv.common.symtab;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.certiv.common.log.Log;

public class SymbolTable {

	private final Stack<Scope> scopeStack = new Stack<>();
	private final List<Scope> allScopes = new ArrayList<>();

	protected int genId = 0;

	public SymbolTable() {
		Scope globals = new Scope(ScopeType.GLOBAL, nextGenId(), null);
		scopeStack.push(globals);
		allScopes.add(globals);
	}

	public Scope pushScope() {
		Scope enclosingScope = scopeStack.peek();
		Scope scope = new Scope(ScopeType.LOCAL, nextGenId(), enclosingScope);
		scopeStack.push(scope);
		allScopes.add(scope);
		return scope;
	}

	public void popScope() {
		scopeStack.pop();
	}

	public int getScopeDepth() {
		return scopeStack.size();
	}

	public Scope currentScope() {
		if (scopeStack.size() > 0) return scopeStack.peek();

		Log.info(this, "Unbalanced scope stack.");
		return allScopes.get(0);
	}

	public Scope getScope(int genId) {
		for (Scope scope : scopeStack) {
			if (scope.genId == genId) return scope;
		}
		return null;
	}

	public int getCurrentGen() {
		return genId;
	}

	private int nextGenId() {
		genId++;
		return genId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Scope scope : scopeStack.subList(0, scopeStack.size())) {
			sb.append(scope.toString());
		}
		return sb.toString();
	}
}
