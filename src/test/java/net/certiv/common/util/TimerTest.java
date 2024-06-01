package net.certiv.common.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import net.certiv.common.util.Maths.RangeStyle;

class TimerTest {

	static String N1 = "Timer1";
	static String N2 = "Timer2";
	static String N3 = "Timer3";
	static String N4 = "Timer4";
	static String N5 = "Timer5";

	@Test
	void testStartStopLastElapsed() throws Exception {
		Timer<String> timer = new Timer<>();
		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(100);
		timer.stop(N1);

		double delta = timer.lastElapsed(N1);
		assertTrue(Maths.inRange(delta, 100, 120, RangeStyle.CLOSED));
	}

	@Test
	void testStopLastElapsed() throws Exception {
		Timer<String> timer = new Timer<>();
		// timer.start(N1);
		// TimeUnit.MILLISECONDS.sleep(100);
		timer.stop(N1);

		double delta = timer.lastElapsed(N1);
		// System.out.println(timer.totalElapsed(N1, "%sms"));
		assertTrue(Maths.inRange(delta, 0, 2, RangeStyle.CLOSED));
	}

	@Test
	void testTimerNames() {}

	@Test
	void testTotalElapsedT() throws Exception {
		Timer<String> timer = new Timer<>();

		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(50);
		timer.stop(N1);

		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(50);
		timer.stop(N1);

		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(50);
		timer.stop(N1);

		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(60);
		timer.stop(N1);

		double delta = timer.lastElapsed(N1);
		// System.out.println(timer.lastElapsed(N1));
		assertTrue(Maths.inRange(delta, 60, 64, RangeStyle.CLOSED));

		delta = timer.totalElapsed(N1);
		// System.out.println(timer.totalElapsed(N1));
		// System.out.println(timer.totalElapsed(N1, "%sms"));
		assertTrue(Maths.inRange(delta, 210, 270, RangeStyle.CLOSED));
	}

	@Test
	void testAllElapsed() throws Exception {
		Timer<String> timer = new Timer<>("All Elapsed");
		timer.start(N1);
		TimeUnit.MILLISECONDS.sleep(50);

		timer.start(N2);
		TimeUnit.MILLISECONDS.sleep(50);

		timer.start(N3);
		TimeUnit.MILLISECONDS.sleep(50);

		timer.start(N4);
		TimeUnit.MILLISECONDS.sleep(50);

		timer.stop(N1);
		timer.stop(N2);
		timer.stop(N3);
		timer.stop(N4);

		LinkedHashMap<String, Double> delta = timer.allElapsed();
		assertNotNull(delta);

		// System.out.println(timer.toString());

		// delta.forEach((n, v) -> System.out.println(String.format("%s : %sms", n, v)));
		// List<String> names = timer.timerNames();
		// double total = delta.values().stream().mapToDouble(v -> v).sum();
		// System.out.println(String.format("%s %sms", names, total));
	}
}
