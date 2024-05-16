package net.certiv.common.tree;

import java.util.List;

public class TestUtil {

	static final List<String> V1a = List.of("A", "B", "D", "E", "F", "G");
	static final List<String> V1b = List.of("A", "B", "C", "D", "F", "G");
	static final List<String> V1c = List.of("A", "B", "D", "F", "G");
	static final List<String> V1d = List.of("A", "B", "C");
	static final List<String> V1e = List.of("A", "B");

	static final List<String> V2a = List.of("A", "B", "X", "Z");
	static final List<String> V2b = List.of("A", "B", "Y", "Z");
	static final List<String> V2c = List.of("A", "B", "X");
	static final List<String> V2d = List.of("A", "B", "Z");

	static final List<String> V3a = List.of("N", "O", "P", "X", "Y", "Z");
	static final List<String> V3b = List.of("N", "O", "P", "Y");
	static final List<String> V3c = List.of("N", "O", "P");
	static final List<String> V3d = List.of("N", "P");

	static Forest<String> buildForest1ab() {
		TreeNode.reset();

		Forest<String> f = new Forest<>();
		f.install(V1a);
		f.install(V1b);
		return f;
	}

	static Forest<String> buildForest1a3d() {
		TreeNode.reset();

		Forest<String> f = new Forest<>();
		f.install(V1a);
		f.install(V1b);
		f.install(V1c);
		f.install(V1d);
		f.install(V1e);

		f.install(V2a);
		f.install(V2b);
		f.install(V2c);
		f.install(V2d);

		f.install(V3a);
		f.install(V3b);
		f.install(V3c);
		f.install(V3d);

		return f;
	}
}
