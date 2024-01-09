package net.certiv.common;

import net.certiv.common.stores.context.IContext;
import net.certiv.common.stores.context.IKVScope;
import net.certiv.common.stores.context.KVStore;
import net.certiv.common.stores.context.Key;

public class KVSupport {

	public final Key<String> NAME = Key.of("context.text.name");
	public final Key<Integer> NUM = Key.of("context.text.data");

	public final Key<String> KeyA = Key.of("KeyA");
	public final Key<String> KeyB = Key.of("KeyB");

	public IContext context;
	public IKVScope a;
	public IKVScope b;
	public IKVScope c;
	public IKVScope d;
	public IKVScope e;
	public IKVScope f;

	public void setUp() {
		a = createStore("A", 0);
		b = createStore("B", 1);
		c = createStore("C", 2);
		d = createStore("D", 3);
		e = createStore("E", 4);
		f = createStore("F", 5);
	}

	public IKVScope createStore(String name, int num) {
		IKVScope store = new KVStore();

		// will shadow
		store.put(NAME, name);
		store.put(NUM, num);

		// will be visible
		String id = String.format("%s.layer[%s]", NAME.name, num);
		Key<String> IDENT = Key.of(id);
		store.put(IDENT, id);
		return store;
	}

	public void tearDown() {
		context = null;
		a = null;
		b = null;
		c = null;
		d = null;
		e = null;
		f = null;
	}

}
