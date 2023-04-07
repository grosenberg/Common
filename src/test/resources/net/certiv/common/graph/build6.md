digraph Builder {

  subgraph cluster_A {

      A
      C
      Delta
      Z
      Eta

      A -> C
      C -> Delta
      Delta -> Z
      C -> Eta
      Eta -> Z

  }

  subgraph cluster_B {

      B
      C

      B -> C

  }
}
