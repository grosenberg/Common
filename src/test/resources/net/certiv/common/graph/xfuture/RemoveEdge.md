digraph "Transfuture Remove Edge" {

  subgraph "cluster_A(0)" {

      "A(0)"
      "B(1)"
      "C(2)"
      "D(3)"
      "E(4)"

      "A(0)" -> "B(1)"
      "B(1)" -> "C(2)"
      "C(2)" -> "B(1)"
      "C(2)" -> "C(2)"
      "C(2)" -> "D(3)"
      "C(2)" -> "E(4)"
      "D(3)" -> "E(4)"

  }

  subgraph "cluster_F(5)" {

      "F(5)"
      "G(6)"

      "F(5)" -> "G(6)"

  }
}
