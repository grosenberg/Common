package net.certiv.common.tool;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import net.certiv.common.tool.Options.Flag;
import net.certiv.common.util.FsUtil;

class OptionsTest {

	// Flag.bool(opts, "C", "check", "parse doc for suitability to format");
	// Flag.bool(opts, "L", "learn", "add doc to corpus training repo");
	//
	// Flag.bool(opts, "b", "backup", "create doc backup before formatting");
	// Flag.bool(opts, "s", "save", "save formatted doc");
	//
	// Flag.bool(opts, "f", "format", "format"); //
	// Flag.bool(opts, "fbreak", "breakLongLines", "break long lines");
	// Flag.bool(opts, "falign", "alignFields", "align fields");
	// Flag.bool(opts, "fcmtalign", "alignComments", "align comments");
	// Flag.bool(opts, "fcmt", "formatComments", "format comments");
	// Flag.bool(opts, "fcmthdr", "formatHdrComment", "format header comment");
	// Flag.bool(opts, "fcmtrmvblanks", "rmvBlanksComment", "remove blank lines in
	// comments");
	//
	// Flag.str(opts, "c", "config", "config settings file pathname");
	// Flag.str(opts, "d", "corpusRoot", "corpus root directory");
	// Flag.str(opts, "g", "lang", "grammar language ('antlr', 'java', 'stg',
	// 'xv')");
	// Flag.bool(opts, "r", "rebuild", "force rebuild of the corpus model");
	// Flag.num(opts, "t", "tabWidth", "tab width");
	// Flag.str(opts, "v", "verbose", "verbosity ('quiet', 'info', 'warn',
	// 'error')");
	//
	// Flag.bool(opts, "h", "help", "help");

	@Test
	void testHelpString() {

		try {
			Options opts = new Options();
			Flag.str(opts, "d", "dir", "Root directory");
			Flag.num(opts, "t", "tabWidth", "Tab width");
			Flag.bool(opts, "h", "help", "Help");
			opts.parse(new String[] { "-h" });
			String help = opts.help();

			String res = FsUtil.loadResourceString(getClass(), "help.txt");
			assertEquals(res, help);

		} catch (Exception e) {
			fail("Failed", e);
		}
	}

	@Test
	void testHas() {
		Options opts = new Options();
		Flag _h = Flag.bool(opts, "h", "help", "help");
		opts.parse(new String[] { "-h" });

		assertTrue(opts.has(_h));
	}
}