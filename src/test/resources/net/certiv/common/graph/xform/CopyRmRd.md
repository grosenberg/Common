digraph "Copy RemoveReduce" {

    "A(0)"
    "B(1)"
    "C(2)"
    "D(3)"
    "E(4)"
    "U(11)"
    "X(12)"
    "Y(13)"
    "G(6)"
    "Z(14)"

    "A(0)" -> "B(1)"
    "B(1)" -> "C(2)"
    "C(2)" -> "D(3)"
    "D(3)" -> "E(4)"
    "C(2)" -> "B(1)"
    "C(2)" -> "C(2)"
    "C(2)" -> "E(4)"
    "C(2)" -> "U(11)"
    "U(11)" -> "X(12)"
    "X(12)" -> "Y(13)"
    "Y(13)" -> "G(6)"
    "U(11)" -> "Z(14)"
    "Z(14)" -> "G(6)"

}