package net.certiv.common.graph.demo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Printer;
import net.certiv.common.graph.Walker;
import net.certiv.common.stores.Counter;

public class DemoGraph extends Graph<DemoNode, DemoEdge> {

	public static final Walker<DemoNode, DemoEdge> WALKER = new Walker<>();
	public static final Printer<DemoNode, DemoEdge> PRINTER = new Printer<>();

	private final Counter IdFactory = new Counter();

	/** Unique node name index: key=name; value=node. */
	public final Map<String, DemoNode> index = new LinkedHashMap<>();

	public final String name;
	public final Long id;

	public DemoGraph(String name) {
		this.name = name;
		id = nextId();
	}

	public Long nextId() {
		return IdFactory.getAndIncrement();
	}

	@Override
	public Set<DemoNode> getRoots() {
		return super.getRoots();
	}

	@Override
	public String name() {
		return String.format("%s(%d)", name, id);
	}

	public DemoNode createNode(String name) {
		DemoNode node = index.get(name);
		if (node != null) return node;

		node = new DemoNode(this, name, nextId());
		index.put(name, node);
		return node;
	}

	public DemoEdge createEdge(String parent, String child) {
		return createEdge(createNode(parent), createNode(child));
	}

	public DemoEdge createEdge(DemoNode parent, DemoNode child) {
		DemoEdge edge = new DemoEdge(parent, child);
		addEdge(edge);
		return edge;
	}

	public String dump() {
		return PRINTER.dump(this);
	}

	public String dot() {
		return PRINTER.render(this);
	}

	@Override
	public String toString() {
		return name();
	}
}
