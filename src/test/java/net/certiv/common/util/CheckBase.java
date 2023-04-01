package net.certiv.common.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

public class CheckBase {

	protected static final String strx = null;
	protected static final String str0 = "";
	protected static final String str1 = " ";
	protected static final String str2 = "Test";

	protected static final int[] intsx = null;
	protected static final int[] ints0 = {};
	protected static final int[] ints1 = { 1 };
	protected static final int[] ints2 = { 1, 2 };

	protected static final Object[] objsx = null;
	protected static final Object[] objs0 = {};
	protected static final Object[] objs1 = { "1" };
	protected static final Object[] objs2 = { "1", "2" };
	protected static final Object[] objs3 = { "1", null, "3" };
	protected static final Object[] objsn = { null };

	protected static final List<String> listx = null;
	protected static final List<String> list0 = new ArrayList<>();
	protected static final List<String> list1 = new ArrayList<>();
	protected static final List<String> list2 = new ArrayList<>();
	protected static final List<String> list3 = new ArrayList<>();
	protected static final List<String> list4 = new ArrayList<>();
	protected static final List<String> listn = new ArrayList<>();

	@BeforeAll
	static void setup() {
		list1.add(str0);

		list2.add(str0);
		list2.add(str1);

		list3.add(str1);
		list3.add(str2);

		list4.add(str1);
		list4.add(null);
		list4.add(str2);

		listn.add(strx);
	}
}
