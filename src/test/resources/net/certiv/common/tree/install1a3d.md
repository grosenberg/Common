digraph Forest {
  graph [label="Forest" fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="14"]

  subgraph "cluster_0: A" {
    graph [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="12"]
    node [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="10"]
    edge [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="10"]

      "0: A"
      "1: B"
      "2: D"
      "3: E"
      "4: F"
      "5: G"
      "6: C"
      "7: D"
      "8: F"
      "9: G"
      "10: F"
      "11: G"
      "12: X"
      "13: Z"
      "14: Y"
      "15: Z"
      "16: Z"


      "0: A" -> "1: B"
      "1: B" -> "2: D"
      "2: D" -> "3: E"
      "3: E" -> "4: F"
      "4: F" -> "5: G"
      "1: B" -> "6: C"
      "6: C" -> "7: D"
      "7: D" -> "8: F"
      "8: F" -> "9: G"
      "2: D" -> "10: F"
      "10: F" -> "11: G"
      "1: B" -> "12: X"
      "12: X" -> "13: Z"
      "1: B" -> "14: Y"
      "14: Y" -> "15: Z"
      "1: B" -> "16: Z"

  }

  subgraph "cluster_17: N" {
    graph [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="12"]
    node [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="10"]
    edge [fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="10"]

      "17: N"
      "18: O"
      "19: P"
      "20: X"
      "21: Y"
      "22: Z"
      "23: Y"
      "24: P"


      "17: N" -> "18: O"
      "18: O" -> "19: P"
      "19: P" -> "20: X"
      "20: X" -> "21: Y"
      "21: Y" -> "22: Z"
      "19: P" -> "23: Y"
      "17: N" -> "24: P"

  }
}
