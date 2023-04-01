package net.certiv.common.graph.demo;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.LinkedHashList;

public class DemoNode extends Node<DemoNode, DemoEdge> {

	public final DemoGraph graph;

	public DemoNode(DemoGraph graph, String name) {
		super();
		this.graph = graph;
		if (!name.isBlank()) put(NODE_NAME, name);
	}

	public Long id() {
		return _nid;
	}

	@Override
	public String name() {
		return String.format("%s(%d)", get(NODE_NAME), id());
	}

	@Override
	public boolean enter(Sense dir, LinkedHashList<DemoNode, DemoNode> visited, NodeVisitor<DemoNode> listener,
			DemoNode parent) {
		return listener.enter(dir, visited, parent, this);
	}

	@Override
	public boolean exit(Sense dir, LinkedHashList<DemoNode, DemoNode> visited, NodeVisitor<DemoNode> listener,
			DemoNode parent) {
		return listener.exit(dir, visited, parent, this);
	}
}
