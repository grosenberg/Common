digraph "Dot Styles" {
  graph [label="Dot Styles" fontcolor=teal fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=32]
  node [fontcolor=black fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=12]
  edge [fontcolor=black fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=10]

    A [label="Node A" color=blue fillcolor=lightblue style=filled]
    B [label="Node B" color=red fillcolor=orange shape=rectangle style="filled, rounded"]
    C

    A -> B [label="Edge 0" color=blue penwidth=2]
    B -> C [label="Edge 1" arrowhead=vee arrowtail=inv arrowsize=0.7 color=maroon fontsize=11 fontcolor=navy style=dashed]

}
