package net.certiv.common.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Walker.NodeListener;
import net.certiv.common.stores.HashList;
import net.certiv.common.util.Strings;

public class Printer<N extends Node<N, E>, E extends Edge<N, E>> {

	/** The literal indent char(s) used for pretty-printing */
	private static final String DENT = "  ";
	// private static final String BLN = "(?m)^\\s+$";
	// private static final String RN2 = "\\r?\\n\\r?\\n";

	private static final Map<Integer, String> LEADS = new HashMap<>();

	public Printer() {}

	/** Pretty print out a whole graph. */
	public String print(final Graph<N, E> graph) {
		StringBuilder sb = new StringBuilder();
		for (N root : graph.roots) {
			sb.append(String.format("\n// ---- %s ----\n", root));
			sb.append(process(root));
		}
		return sb.toString();
	}

	public String process(final N node) {
		StringBuilder sb = new StringBuilder();
		Walker<N, E> WALKER = new Walker<>();
		WALKER.debug(true);
		WALKER.descend(new Dumper(sb), node);
		return sb.toString();
	}

	private class Dumper extends NodeListener<N> {

		private StringBuilder sb;
		private Map<N, Integer> levels = new HashMap<>();
		private N last;

		public Dumper(StringBuilder sb) {
			this.sb = sb;
		}

		@Override
		public boolean enter(Sense dir, HashList<N, N> visited, N src, N dst) {
			if (src == null) {
				// starting a new flow
				String str = String.format("%s ", dst);
				levels.put(dst, str.length());
				sb.append(str);

			} else if (src == last) {
				// continuing a subflow
				Set<E> edges = src.to(dst);
				String str = String.format("-%s-> %s ", edges, dst);
				int at = levels.get(src);
				levels.putIfAbsent(dst, at + str.length());
				sb.append(str);

			} else {
				// branch the subflow
				int at = levels.get(src);
				at = Math.max(0, at - src.toString().length() / 2);
				String lead = lead(at);

				Set<E> edges = src.to(dst);
				String str = lead + String.format("|-- -%s-> %s ", edges, dst);
				levels.putIfAbsent(dst, at + str.length());
				sb.append(str);
			}
			last = dst;
			return true;
		}
	}

	private static String lead(int at) {
		String lead = LEADS.get(at);
		if (lead == null) {
			StringBuilder sb = new StringBuilder();
			if (at > 0) {
				sb.append(Strings.EOL);
				for (int cnt = 0; cnt < at; cnt++) {
					sb.append(DENT);
				}
			}
			lead = sb.toString();
			LEADS.put(at, lead);
		}
		return lead;
	}
}
