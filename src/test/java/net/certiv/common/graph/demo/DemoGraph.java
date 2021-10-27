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

	/** Nodes index: key=ident; value=node. */
	public final Map<Long, DemoNode> index = new LinkedHashMap<>();

	public final String name;
	public final Long id;

	public DemoGraph(String name) {
		this.name = name;
		id = nextId();
	}

	public Long nextId() {
		return IdFactory.getAndIncrement();
	}

	public Set<DemoNode> getRoots() {
		return roots;
	}

	@Override
	public String name() {
		return String.format("%s.%s", name, id);
	}

	public DemoNode createNode(String name, Long id) {
		DemoNode node = index.get(id);
		if (node != null) return node;

		node = new DemoNode(this, name, id);
		index.put(id, node);
		addNode(node);
		return node;
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
		return PRINTER.toDot(this);
	}

	@Override
	public String toString() {
		return name();
	}
}
