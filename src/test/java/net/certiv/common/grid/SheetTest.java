package net.certiv.common.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.certiv.common.diff.Differ;
import net.certiv.common.stores.Result;
import net.certiv.common.util.test.CommonTestBase;

class SheetTest extends CommonTestBase {

	static final boolean FORCE = true;

	@Test
	void testInsert() {
		SheetBuilder sb = SheetBuilder.on(Sheet.of("SheetInsert")) //
				.define("One[1:1], Two[1:1]") //
				.gridWidth(80);

		sb.insertRow(0, "A", "B");
		sb.insertRow(0, "C", "D");
		sb.insertRow(2, "E", "F");

		Result<Boolean> ok = sb.validate();
		assertTrue(!ok.err());

		Sheet sheet = sb.get(true);
		String name = sb.name();
		assertEquals(sheet.name(), name);

		String render = sb.render();

		String resname = name + ".txt";
		writeResource(getClass(), resname, render, FORCE);
		String txt = loadResource(getClass(), resname);
		Differ.diff(name, txt, render).sdiff(true, 120).out();

		assertEquals(txt, render);
	}

	@Test
	void testBuilder() {

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		SheetBuilder sb = SheetBuilder.on(Sheet.of("SheetBuilder")).gridWidth(50);

		// construct columns
		sb.col().name("Left").left().fixed().width(20);
		sb.col().name("Center").center();
		sb.col().name("Right").right();

		List<String> data = List.of(a, b, c);
		sb.appendRow(data);

		Sheet sheet = sb.get(true);
		String render = sheet.render();

		String name = sb.name();
		String resname = name + ".txt";
		writeResource(getClass(), resname, render, FORCE);
		String txt = loadResource(getClass(), resname);
		Differ.diff(name, txt, render).sdiff(true, 120).out();

		assertEquals(txt, render);
	}

	@Test
	void testAlign() {
		SheetBuilder sb = SheetBuilder.on(Sheet.of("SheetAlign")) //
				.define("L:Left:F:16, C:Center, R:Right") //
				.gridWidth(28);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		sb.appendRow(a, b, c);

		Sheet sheet = sb.get(true);
		String render = sheet.render();

		String name = sb.name();
		String resname = name + ".txt";
		writeResource(getClass(), resname, render, FORCE);
		String txt = loadResource(getClass(), resname);
		Differ.diff(name, txt, render).sdiff(true, 120).out();

		assertEquals(txt, render);
	}

	@Test
	void testFlow() {
		SheetBuilder sb = SheetBuilder.on(Sheet.of("SheetFlow")) //
				.define("L:Left:F:20, C:Center:F:30, R:Right:A") //
				.gridWidth(80);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "All good men have somewhere to be!";
		String b = "No matter where you go, there you are.";
		String c = "Insanity is repeating the same failure and expecting a different outcome";

		sb.appendRow(a, b, c);

		Sheet sheet = sb.get(true);
		String render = sheet.render();

		String name = sb.name();
		String resname = name + ".txt";
		writeResource(getClass(), resname, render, FORCE);
		String txt = loadResource(getClass(), resname);
		Differ.diff(name, txt, render).sdiff(true, 120).out();

		assertEquals(txt, render);
	}

	@Test
	void testIndent() {
		Sheet sheet = Sheet.of("SheetIndent") //
				.define("L:Left:F:20[4], C:Center, R:Right:20[4]") //
				.gridWidth(50);

		// ---------1234567890123456789012345678901234567890123456789012345678901234567890
		String a = "A word";
		String b = "B period";
		String c = "C sentence";

		sheet.appendRow(a, b, c);

		String render = sheet.render(true);

		String name = sheet.name();
		String resname = name + ".txt";
		writeResource(getClass(), resname, render, FORCE);
		String txt = loadResource(getClass(), resname);
		Differ.diff(name, txt, render).sdiff(true, 120).out();

		assertEquals(txt, render);
	}
}
