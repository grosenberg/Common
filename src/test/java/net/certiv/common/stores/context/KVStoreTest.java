package net.certiv.common.stores.context;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.common.stores.context.type.Hex;

class KVStoreTest {

	private KVStore store;
	private Key<String> A = Key.of("A");
	private Key<String> B = Key.of("B");
	// private Key<String> C = Key.of("C");
	private Key<Integer> One = Key.of("One");
	// private Key<Integer> Two = Key.of("Two");
	private Key<Double> Dbl = Key.of("Num");
	private Key<Hex> HexNum = Key.of("Hex");
	private Key<Long> INTERVAL = Key.of("Delta");
	private Key<Duration> DURATION = Key.of("Duration");

	@BeforeEach
	void setup() {
		store = new KVStore();
	}

	@Test
	void testOps() {
		assertDoesNotThrow(() -> { store.put(A, "LetterA"); });
		assertDoesNotThrow(() -> { store.put(One, 1); });
		assertDoesNotThrow(() -> { store.put(Dbl, 5.5); });

		assertDoesNotThrow(() -> { store.put(HexNum, Hex.parseString("FE3A")); });
		// assertDoesNotThrow(() -> { store.put(HexNum,
		// Hex.parseString(Strings.ELLIPSIS_MARK)); });

		assertDoesNotThrow(() -> { store.put(INTERVAL, 123L, ChronoUnit.SECONDS.name()); });
		assertDoesNotThrow(() -> { store.put(DURATION, Duration.of(123, ChronoUnit.SECONDS)); });

		assertEquals(store.get(A), "LetterA");
		assertNull(store.get(B));

		assertEquals(store.get(B, "DefaultB"), "DefaultB");

		store.put(B, "LetterB");
		assertEquals(store.get(B), "LetterB");

		store.putIfAbsent(B, "RevisedB");
		assertEquals(store.get(B), "LetterB");

		store.put(B, "RevisedB");
		assertEquals(store.get(B), "RevisedB");
	}
}
