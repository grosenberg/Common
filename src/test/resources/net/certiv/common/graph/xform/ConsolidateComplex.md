digraph "Consolidate Complex" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "B(1)"
      "C(2)"
      "D(3)"
      "I(8)"
      "F(5)"
      "G(6)"

      "A(0)" -> "B(1)" [label="0"]
      "B(1)" -> "C(2)" [label="1"]
      "B(1)" -> "I(8)" [label="7"]
      "C(2)" -> "B(1)" [label="8"]
      "C(2)" -> "B(1)" [label="10"]
      "C(2)" -> "C(2)" [label="9"]
      "C(2)" -> "D(3)" [label="2"]
      "C(2)" -> "F(5)" [label="4"]
      "D(3)" -> "B(1)" [label="3"]
      "F(5)" -> "G(6)" [label="5"]
      "G(6)" -> "B(1)" [label="6"]

  }

  subgraph "cluster_U(9)" {

      "U(9)"
      "X(10)"
      "Y(11)"
      "Z(12)"

      "U(9)" -> "X(10)" [label="11"]
      "U(9)" -> "Z(12)" [label="13"]
      "X(10)" -> "Y(11)" [label="12"]

  }
}
