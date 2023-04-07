digraph "Two-root Test" {

  subgraph cluster_A {

      A
      B
      C

      A -> B
      B -> C

  }

  subgraph cluster_D {

      D
      E

      D -> E

  }
}
