digraph Forest {
  graph [label="Forest" fontcolor="black" fontname="roboto, fira sans, lucida sans, segoe ui" fontsize="14"]
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


    "0: A" -> "1: B"
    "1: B" -> "2: D"
    "2: D" -> "3: E"
    "3: E" -> "4: F"
    "4: F" -> "5: G"
    "1: B" -> "6: C"
    "6: C" -> "7: D"
    "7: D" -> "8: F"
    "8: F" -> "9: G"

}
