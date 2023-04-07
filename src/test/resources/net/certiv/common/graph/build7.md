digraph "Builder Multi" {

    Root
    A
    C
    Delta
    Z
    Eta
    B

    Root -> A
    A -> C
    C -> Delta
    Delta -> Z
    C -> Eta
    Eta -> Z
    C -> B
    C -> B
    B -> C
    C -> Z
    C -> C
    Root -> B

}
