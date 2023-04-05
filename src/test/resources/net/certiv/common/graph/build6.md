digraph "Builder(0)" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "C(2)"
      "Delta(3)"
      "Z(5)"
      "Eta(4)"

      "A(0)" -> "C(2)"
      "C(2)" -> "Delta(3)"
      "Delta(3)" -> "Z(5)"
      "C(2)" -> "Eta(4)"
      "Eta(4)" -> "Z(5)"

  }

  subgraph "cluster_B(1)" {

      "B(1)"
      "C(2)"

      "B(1)" -> "C(2)"

  }
}
