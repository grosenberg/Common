digraph "Consolidate Complex Cyclic" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "B(1)"
      "D(3)"
      "G(6)"
      "H(7)"
      "I(8)"

      "A(0)" -> "B(1)" [label="0"]
      "B(1)" -> "B(1)" [label="1"]
      "B(1)" -> "B(1)" [label="4"]
      "B(1)" -> "B(1)" [label="8"]
      "B(1)" -> "B(1)" [label="10"]
      "B(1)" -> "B(1)" [label="9"]
      "B(1)" -> "D(3)" [label="2"]
      "D(3)" -> "B(1)" [label="3"]
      "B(1)" -> "G(6)" [label="5"]
      "G(6)" -> "H(7)" [label="6"]
      "H(7)" -> "I(8)" [label="7"]

  }

  subgraph "cluster_U(9)" {

      "U(9)"
      "X(10)"
      "Y(11)"
      "Z(12)"

      "U(9)" -> "X(10)" [label="11"]
      "X(10)" -> "Y(11)" [label="12"]
      "U(9)" -> "Z(12)" [label="13"]

  }
}
