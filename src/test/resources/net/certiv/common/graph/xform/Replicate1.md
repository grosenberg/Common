digraph Replicate {

    "A(0)"
    "B(1)"
    "C(2)"
    "X(3)"
    "Y(4)"
    "Z(5)"

    "A(0)" -> "B(1)"
    "B(1)" -> "C(2)"
    "A(0)" -> "X(3)"
    "X(3)" -> "C(2)"
    "A(0)" -> "Y(4)"
    "Y(4)" -> "C(2)"
    "A(0)" -> "Z(5)"
    "Z(5)" -> "C(2)"

}