package net.certiv.common.check;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.certiv.common.ex.AssertException;
import net.certiv.common.ex.IAssertException;
import net.certiv.common.graph.ex.GraphEx;
import net.certiv.common.graph.ex.GraphException;

class AssertTest extends CheckBase {

	private static final Class<AssertException> ERR_CLS0 = AssertException.class;
	private static final Class<GraphException> ERR_CLS1 = GraphException.class;

	@Test
	void testGraphEx() {
		GraphException ex = GraphEx.of(IAssertException.Test.IS_TRUE, "Truthiness failed: %s",
				"message params");

		Throwable t = assertThrows(ERR_CLS1, () -> { Assert.isTrue(ex, str2.isEmpty()); });
		assertEquals(ex, t);
	}

	@Test
	void testIsTrue() {
		assertDoesNotThrow(() -> { Assert.isTrue(str1.isBlank()); });
		assertThrows(ERR_CLS0, () -> { Assert.isTrue(str1.isEmpty()); });
	}

	@Test
	void testNotNull() {
		assertDoesNotThrow(() -> { Assert.notNull(str0, ints0, objs0, list0); });
		assertDoesNotThrow(() -> { Assert.notNull(str1, ints1, objs1, list1); });
		assertDoesNotThrow(() -> { Assert.notNull(str2, ints2, objs2, list2); });

		assertThrows(ERR_CLS0, () -> { Assert.notNull(strx); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(intsx); });

		assertThrows(ERR_CLS0, () -> { Assert.notNull(objsx); });

		assertDoesNotThrow(() -> { Assert.notNull(objs1); });
		assertDoesNotThrow(() -> { Assert.notNull(objs1, objs2); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(objs3); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(objs1, objs3); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(objsn); });

		assertThrows(ERR_CLS0, () -> { Assert.notNull(listx); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(list1, list2, list4); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(listn); });

		assertDoesNotThrow(() -> { Assert.notNull(objs0, objs2, list1, list2, list3); });
		assertThrows(ERR_CLS0, () -> { Assert.notNull(str0, ints0, listx); });
	}

	@Test
	void testNotEmpty() {
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(strx); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(str0); });
		assertDoesNotThrow(() -> { Assert.notEmpty(str1); });
		assertDoesNotThrow(() -> { Assert.notEmpty(str2); });

		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(intsx); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(ints0); });
		assertDoesNotThrow(() -> { Assert.notEmpty(ints1); });
		assertDoesNotThrow(() -> { Assert.notEmpty(ints2); });

		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(objsx); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(objs0); });
		assertDoesNotThrow(() -> { Assert.notEmpty(objs1); });
		assertDoesNotThrow(() -> { Assert.notEmpty(objs2); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(objs3); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(objsn); });

		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(listx); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(list0); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(list1); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(list2); });
		assertDoesNotThrow(() -> { Assert.notEmpty(list3); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(list4); });
		assertThrows(ERR_CLS0, () -> { Assert.notEmpty(listn); });
	}
}
