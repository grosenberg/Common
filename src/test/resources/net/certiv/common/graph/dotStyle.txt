digraph "Dot Styles(0)" {
  graph [label="Dot Styles(0)" fontcolor=teal fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=32]
  node [fontcolor=black fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=12]
  edge [fontcolor=black fontname="roboto, fira sans, lucida sans, segoe ui" fontsize=10]

    "A(0)" [label="Node A(0)" color=blue fillcolor=lightblue style=filled]
    "B(1)" [label="Node B(1)" color=red fillcolor=orange shape=rectangle style="filled, rounded"]
    "C(2)"

    "A(0)" -> "B(1)" [label="Edge 0" color=blue penwidth=2]
    "B(1)" -> "C(2)" [label="Edge 1" arrowhead=vee arrowtail=inv arrowsize=0.7 color=maroon fontsize=11 fontcolor=navy style=dashed]

}
