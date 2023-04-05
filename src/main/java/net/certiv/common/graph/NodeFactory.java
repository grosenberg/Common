package net.certiv.common.graph;

import java.util.Map;

public abstract class NodeFactory<G extends Graph<N, E>, N extends Node<N, E>, E extends Edge<N, E>> {

	protected final G graph;

	public NodeFactory(G graph) {
		this.graph = graph;
	}

	public abstract N createNode();

	public abstract N createNode(String name);

	public abstract N createNode(Map<Object, Object> props);
}
