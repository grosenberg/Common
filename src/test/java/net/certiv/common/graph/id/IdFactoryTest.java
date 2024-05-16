package net.certiv.common.graph.id;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.util.Strings;

class IdFactoryTest {

	static final String GRAPH_TEST = "GraphTest";

	static final String ALPHA = "alpha";
	static final String BETA = "beta";
	static final String GAMMA = "gamma";

	static final List<String> L_A = List.of(ALPHA);
	static final List<String> L_AB = List.of(ALPHA, BETA);
	static final List<String> L_ABG = List.of(ALPHA, BETA, GAMMA);

	static final String NAME1 = Strings.join(Strings.DOT, L_A);
	static final String NAME2 = Strings.join(Strings.DOT, L_AB);
	static final String NAME3 = Strings.join(Strings.DOT, L_ABG);

	IdFactory factory;	// factory

	Id id1;
	Id id2;
	Id id3;

	@BeforeEach
	void setup() {
		factory = new IdFactory(GRAPH_TEST);

		id1 = factory.make(NAME1);
		id2 = factory.make(NAME2);
		id3 = factory.make(NAME3);
	}

	@Test
	void testNamespace() {
		assertEquals(GRAPH_TEST, factory.ns);
	}

	@Test
	void testDefined() {
		Set<Id> exp = Set.of(id1, id2, id3);
		assertEquals(exp, factory.defined());
	}

	@Test
	void testMakeName() {
		assertEquals(NAME1, id1.name());
		assertEquals(NAME2, id2.name());
		assertEquals(NAME3, id3.name());
	}

	@Test
	void testElements() {
		assertEquals(1, id1.size());
		assertEquals(2, id2.size());
		assertEquals(3, id3.size());

		assertEquals(L_A, id1.elements());
		assertEquals(L_AB, id2.elements());
		assertEquals(L_ABG, id3.elements());
	}

	@Test
	void testResolve() {
		Id id = factory.resolve(id2, GAMMA);
		assertEquals(NAME3, id.name());
	}

	@Test
	void testResolveOverlap() {
		Id id = factory.resolve(id2, NAME3);
		assertEquals(NAME3, id.name());
	}
}
