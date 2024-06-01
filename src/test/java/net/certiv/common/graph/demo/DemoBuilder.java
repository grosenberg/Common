package net.certiv.common.graph.demo;

import net.certiv.common.check.Assert;
import net.certiv.common.graph.Builder;
import net.certiv.common.id.Id;
import net.certiv.common.id.IdFactory;

public class DemoBuilder extends Builder<Id, Id, DemoGraph, DemoNode, DemoEdge> {

	IdFactory factory = new IdFactory(IdFactory.DEFAULT);

	public DemoBuilder(DemoGraph graph) {
		super(graph);
	}

	@Override
	public DemoNode createNode(Id id) {
		return graph.createNode(id);
	}

	@Override
	protected Id makeId(Object nameObj) {
		Assert.isTrue(nameObj instanceof String);
		return (Id) factory.make((String) nameObj);
	}

	@Override
	protected String nameOf(Id id) {
		return id.name();
	}
}
