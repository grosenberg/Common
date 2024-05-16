package net.certiv.common.tree;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import net.certiv.common.ex.IllegalArgsEx;

public class TreeUtil {

	public static void chkSequenced(Collection<?> values) {
		if (values instanceof List || values instanceof SortedSet) return;
		throw IllegalArgsEx.of("Values collection must be ordered.");
	}
}
