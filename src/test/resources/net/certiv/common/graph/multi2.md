digraph "Multigraph Test" {

  subgraph "cluster_A(0)" {

      "A(0)"

      "A(0)" -> "A(0)" [label="0"]

  }

  subgraph "cluster_B(1)" {

      "B(1)"
      "C(2)"
      "D(3)"

      "B(1)" -> "C(2)" [label="1"]
      "C(2)" -> "D(3)" [label="2"]

  }

  subgraph "cluster_E(4)" {

      "E(4)"
      "F(5)"
      "G(6)"

      "E(4)" -> "F(5)" [label="3"]
      "F(5)" -> "G(6)" [label="4"]
      "G(6)" -> "E(4)" [label="5"]

  }
}
