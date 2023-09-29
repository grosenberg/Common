digraph "Multigraph Test" {

    "A(0)"
    "B(1)"
    "C(2)"

    "A(0)" -> "B(1)" [label="0"]
    "A(0)" -> "B(1)" [label="2"]
    "B(1)" -> "A(0)" [label="5"]
    "B(1)" -> "A(0)" [label="7"]
    "B(1)" -> "C(2)" [label="1"]
    "B(1)" -> "C(2)" [label="3"]
    "C(2)" -> "A(0)" [label="8"]
    "C(2)" -> "B(1)" [label="4"]
    "C(2)" -> "B(1)" [label="6"]

}
