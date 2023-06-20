digraph "Two-root Test" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "B(1)"
      "C(2)"

      "A(0)" -> "B(1)"
      "B(1)" -> "C(2)"

  }

  subgraph "cluster_D(3)" {

      "D(3)"
      "E(4)"

      "D(3)" -> "E(4)"

  }
}
