# Certiv Common Graph

A generalized Graph data structure allowing loops and multiple edges between vertices. 
Also known as a **multidigraph** or **quiver**.

Implentation supports a graph having multiple roots (or no root).

## Transforms

Immediate graph transforms are implemented using **Transform**.

Deferred transforms are collected for subsequent application using **Transfuture**. 

|Transform             |Description                                                                                                                                                                                                                           |
|:---------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|remove(N)             |Removes the node from the graph, including any edges terminating on the node.                                                                                                                                                         |
|removeEdge(E)         |Removes edge from the graph, including any nodes left unconnected in the graph.                                                                                                                                                       |
|transfer(E,N)         |Transfers the edge by changing the edge begin node to the specified node                                                                                                                                                              |
|copy(SG,N)            |Copies the given subgraph into the graph. Both nodes and edges of the subgraph are replicated. The destination node defines the inbound and outbound edges to the copied subgraph.                                                    |
|move(E,N,N)           |Moves the edge by changing the edge begin and end nodes to the specified nodes                                                                                                                                                        |
|reterminate(E,N)      |Reterminates the edge by changing the edge end node to the specified node                                                                                                                                                             |
|consolidate(List<N>,N)|Consolidate all edges connecting to the source nodes to the target node. Excludes the target node from the source nodes. Removes the finally unconnected source nodes from the graph.Existing cycles are moved. May create new cycles.|
|replicate(N,List<N>)  |Replicates all edge connecting to given source node to the target nodes. Creates new edge connections to each target node equivalent to the source node edge connections. All replica edges are added to the graph.                   |
|reduce(N)             |Reduce the graph by removing the given node while retaining the connectivity between the inbound and outbound nodes.                                                                                                                  |



## Node Architecture

digraph NodeDesign {
  rankdir=LR;
  node[shape=rectangle];
  
  node1[label="Node1"]
  node2[label="Node2"]

  node4[label="Node4"]
  node5[label="Node5"]
  
	edge1[label="Edge1" style="rounded"]
	edge2[label="Edge2" style="rounded"]
	edge3[label="Edge3" style="rounded"]
	edge4[label="Edge4" style="rounded"]
	edge5[label="Edge5" style="rounded"]
	edge6[label="Edge6" style="rounded"]

  subgraph cluster_node {
  	label="Node";
		labelloc="t"

  	body[label="Node\nMethods" shape=octagon style=rounded]
  	outRoute[label="EdgeSet\nOutbound" shape=house orientation=90]
  	inRoute[label="EdgeSet\nInbound" shape=house orientation=-90]
  	
		inRoute -> body -> outRoute;
	}
  
  node1 -> edge1 -> inRoute ;
  node2 -> edge2 -> inRoute ;
  node2 -> edge3 -> inRoute ;
  outRoute -> edge4 -> node4
  outRoute -> edge5 -> node5  
  outRoute -> edge6 -> node5  
}
