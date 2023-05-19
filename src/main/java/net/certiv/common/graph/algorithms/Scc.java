/*******************************************************************************
 * Copyright (c) 2006, 2020 THALES GLOBAL SERVICES.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package net.certiv.common.graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.certiv.common.graph.Edge;
import net.certiv.common.graph.Edge.Sense;
import net.certiv.common.graph.Graph;
import net.certiv.common.graph.Node;

/**
 * An implementation of Tarjan's algorithm to find the strongly connected components of a
 * graph. This is similar (but not 100% the same) to finding cycles of a graph.
 * {@link http://en.wikipedia.org/wiki/Strongly_connected_components}
 * {@link http://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm}
 */
public class Scc<N extends Node<N, E>, E extends Edge<N, E>> {

	/** Properties assigned to each node. */
	private final class Properties {
		public Properties(int index, int lowlink) {
			this.index = index;
			this.lowlink = lowlink;
		}

		int index; // index where the corresponding element was discovered
		int lowlink; // minimal index for some other node reachable from this one
	}

	/** key=visited node; value=properties */
	private Map<N, Properties> propertyMap;
	private int index;
	private Stack<N> stack;
	private List<List<N>> result;

	/**
	 * Find the strongly connected components of a graph.
	 *
	 * @param contents An iterator over the graph's nodes.
	 * @param advisor  Provides informations about the graphs edges.
	 * @param strict   whether elementary (size 1) sccs should be added to the result
	 * @return A list of strongly connected components of the graph. Never null.
	 */
	public List<List<N>> find(Graph<N, E> graph, boolean strict) {
		result = new ArrayList<>();
		index = 0;
		propertyMap = new HashMap<>();
		stack = new Stack<>();
		result = new ArrayList<>();

		// hit all nodes
		Iterator<N> nodes = graph.getNodes().iterator();
		while (nodes.hasNext()) {
			N current = nodes.next();
			if (propertyMap.get(current) == null) {
				strongconnect(current, strict);
			}
		}

		return result;
	}

	private void strongconnect(N current, boolean strict) {
		Properties properties = new Properties(index, index);
		propertyMap.put(current, properties);
		index++;
		stack.push(current);
		Iterator<N> successors = current.adjacent(Sense.OUT).iterator();
		while (successors.hasNext()) {
			N successor = successors.next();
			if (propertyMap.get(successor) == null) {
				// have not seen successor before
				strongconnect(successor, strict);
				properties.lowlink = Math.min(properties.lowlink, propertyMap.get(successor).lowlink);
			} else if (stack.contains(successor)) {
				// seen successor in the currently handled scc
				properties.lowlink = Math.min(properties.lowlink, propertyMap.get(successor).lowlink);
			}
		}

		if (properties.lowlink == properties.index) {
			List<N> scc = new ArrayList<>();
			N t;
			do {
				t = stack.pop();
				scc.add(t);
			} while (t != current);
			if (scc.size() == 1) {
				if (strict) {
					result.add(scc);
				}
			} else {
				result.add(scc);
			}
		}
	}
}
