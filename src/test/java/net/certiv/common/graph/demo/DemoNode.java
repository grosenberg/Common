package net.certiv.common.graph.demo;

import java.util.Objects;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Node;
import net.certiv.common.graph.Walker.NodeVisitor;
import net.certiv.common.stores.HashList;

public class DemoNode extends Node<DemoNode, DemoEdge> {

	public final DemoGraph graph;
	public final String name;
	public final Long id;

	public DemoNode(DemoGraph graph, String name, Long id) {
		this.graph = graph;
		this.name = name;
		this.id = id;
	}

	public Long id() {
		return id;
	}

	@Override
	public String name() {
		return String.format("%s.%s", name, id);
	}

	@Override
	public boolean enter(Sense dir, HashList<DemoNode, DemoNode> visited, NodeVisitor<DemoNode> listener,
			DemoNode parent) {
		return listener.enter(dir, visited, parent, this);
	}

	@Override
	public boolean exit(Sense dir, HashList<DemoNode, DemoNode> visited, NodeVisitor<DemoNode> listener,
			DemoNode parent) {
		return listener.exit(dir, visited, parent, this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof DemoNode)) return false;
		DemoNode other = (DemoNode) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
