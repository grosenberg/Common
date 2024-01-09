package net.certiv.common.graph.demo;

import java.util.Map;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.EdgeSet;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.graph.id.Id;
import net.certiv.common.stores.LinkedHashList;

public class DemoNode extends Node<Id, DemoNode, DemoEdge> {

	public final DemoGraph graph;

	public DemoNode(DemoGraph graph, Id id, EdgeSet<Id, DemoNode, DemoEdge> in,
			EdgeSet<Id, DemoNode, DemoEdge> out) {
		super(id, in, out);
		this.graph = graph;
	}

	public DemoNode(DemoGraph graph, Id id, EdgeSet<Id, DemoNode, DemoEdge> in,
			EdgeSet<Id, DemoNode, DemoEdge> out, Map<Object, Object> props) {
		super(id, in, out, props);
		this.graph = graph;
	}

	@Override
	public boolean enter(Sense dir, LinkedHashList<DemoNode, DemoNode> visited,
			NodeVisitor<DemoNode> listener, DemoNode parent) {
		return listener.enter(dir, visited, parent, this);
	}

	@Override
	public boolean exit(Sense dir, LinkedHashList<DemoNode, DemoNode> visited, NodeVisitor<DemoNode> listener,
			DemoNode parent) {
		return listener.exit(dir, visited, parent, this);
	}

	@Override
	public String label() {
		return String.format("%s(%d)", super.label(), _nid);
	}
}
