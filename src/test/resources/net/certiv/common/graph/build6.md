digraph Builder {

  subgraph "cluster_A(0)" {

      "A(0)"
      "C(2)"
      "Delta(3)"
      "Eta(4)"
      "Z(5)"

      "A(0)" -> "C(2)"
      "C(2)" -> "Delta(3)"
      "C(2)" -> "Eta(4)"
      "Delta(3)" -> "Z(5)"
      "Eta(4)" -> "Z(5)"

  }

  subgraph "cluster_B(1)" {

      "B(1)"
      "C(2)"

      "B(1)" -> "C(2)"

  }
}
