package me.yapoo.computer.circuit

enum class Bit {
    LOW,
    HIGH,
}

infix fun Bit.nand(other: Bit): Bit {
    return if (this == Bit.HIGH && other == Bit.HIGH) {
        Bit.LOW
    } else {
        Bit.HIGH
    }
}

fun Bit.not(): Bit {
    return this nand this
}

infix fun Bit.and(other: Bit): Bit {
    return (this nand other).not()
}

infix fun Bit.or(other: Bit): Bit {
    return (this.not() and other.not()).not()
}

infix fun Bit.xor(other: Bit): Bit {
    return (this and other.not()) or (this.not() and other)
}

fun mux(
    a: Bit,
    b: Bit,
    sel: Bit,
): Bit {
    // if sel=0 then out=a else out=b
    return (a and sel.not()) or (b and sel)
}

fun dMux(
    input: Bit,
    sel: Bit,
): Pair<Bit, Bit> {
    // if sel=0 then {in, 0} else {o, in}
    return Pair(input and sel.not(), input and sel)
}

fun List<Bit>.not(): List<Bit> {
    return this.map { it.not() }
}

fun List<Bit>.and(): Bit {
    return this.fold(Bit.HIGH) { acc, x ->
        acc and x
    }
}

fun List<Bit>.or(): Bit {
    return this.fold(Bit.LOW) { acc, x ->
        acc or x
    }
}

infix fun List<Bit>.and(other: List<Bit>): List<Bit> {
    if (this.size != other.size) {
        throw IllegalArgumentException("invalid length: ${this.size} and ${other.size}")
    }

    return this.zip(other).map { (a, b) -> a and b }
}

infix fun List<Bit>.or(other: List<Bit>): List<Bit> {
    if (this.size != other.size) {
        throw IllegalArgumentException("invalid length: ${this.size} and ${other.size}")
    }

    return this.zip(other).map { (a, b) -> a or b }
}

fun mux(
    a: List<Bit>,
    b: List<Bit>,
    sel: Bit,
): List<Bit> {
    if (a.size != b.size) {
        throw IllegalArgumentException("invalid length: ${a.size} and ${b.size}")
    }
    return a.zip(b).map { (f, s) ->
        (f and sel.not()) or (s and sel)
    }
}
