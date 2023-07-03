digraph "Transfuture Consolidate" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "B(1)"
      "D(3)"
      "G(6)"

      "A(0)" -> "B(1)"
      "B(1)" -> "B(1)"
      "B(1)" -> "B(1)"
      "B(1)" -> "D(3)"
      "D(3)" -> "B(1)"
      "B(1)" -> "G(6)"

  }

  subgraph "cluster_C(2)" {

      "C(2)"
      "B(1)"

      "C(2)" -> "C(2)"
      "C(2)" -> "B(1)"

  }
}
