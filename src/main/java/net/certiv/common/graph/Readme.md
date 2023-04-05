# Certiv Common Graph

A generalized Graph 

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

## Operations 


|OP                 |Description |    |    |    |    |    |
|:------------------|:-----------|:---|:---|:---|:---|:---|
|Graph#remove(N)    |removes node|    |    |    |    |    |
|Graph#removeEdge(E)|removes edge|    |    |    |    |    |
|                   |            |    |    |    |    |    |
|                   |            |    |    |    |    |    |


### Remove Edge

digraph "Remove(0)" {
  	label="Remove Edge (before)";
		labelloc="b"
  	rankdir=LR;
  
    "A(0)"
    "B(1)"
    "C(2)"

    "A(0)" -> "B(1)"
    "B(1)" -> "B(1)" [color=red]
    "B(1)" -> "C(2)"

}

``` java
		Graph#removeEdge("B", "B")
```

digraph "Remove(1)" {
  	label="Remove Edge (after)";
		labelloc="b"
  	rankdir=LR;
  
    "A(0)"
    "B(1)"
    "C(2)"

    "A(0)" -> "B(1)"
    "B(1)" -> "C(2)"

}

``` java
		Graph#remove("B")
```

digraph "Remove(2)" {
  	label="Remove Edge (after)";
		labelloc="b"
  	rankdir=LR;
  
    "A(0)"
    "C(2)"
}
