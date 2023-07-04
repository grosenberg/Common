digraph "Consolidate Cyclic" {

    "A(0)"
    "B(1)"
    "D(3)"

    "A(0)" -> "B(1)" [label="0"]
    "B(1)" -> "B(1)" [label="1"]
    "B(1)" -> "B(1)" [label="3"]
    "B(1)" -> "D(3)" [label="2"]

}
