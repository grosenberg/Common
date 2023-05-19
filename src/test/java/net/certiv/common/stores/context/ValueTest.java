package net.certiv.common.stores.context;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ValueTest {

	private final Path path = Path.of("/some/path");

	@Test
	void testValid() {
		Key<Path> key = Key.of("test");
		Value<Path> val = Value.of(path);

		assertTrue(key.valid(path));
		assertTrue(key.valid(val));

		assertDoesNotThrow(() -> { key.cast(val); });
	}
}
