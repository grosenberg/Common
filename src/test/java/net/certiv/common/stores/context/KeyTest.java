package net.certiv.common.stores.context;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class KeyTest {

	@Test
	void testKey() {
		Key<Path> key1 = Key.of("test");
		Key<Path> key2 = Key.of("test");

		assertEquals(key1, key2);
	}

	@Test
	void testValid() {
		Key<Path> key1 = Key.of("test");

		assertTrue(key1.valid(Path.of("")));
	}

	@Test
	void testCast() {
		Key<Path> key1 = Key.of("test");

		assertDoesNotThrow(() -> { key1.cast(Path.of("")); });
	}
}
