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
    // if sel=0 then {in, 0} else {0, in}
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

fun mux4Way16(
    a: List<Bit>,
    b: List<Bit>,
    c: List<Bit>,
    d: List<Bit>,
    sel: List<Bit>,
): List<Bit> {
    // out = a if sel = 00
    //       b if sel = 01
    //       c if sel = 10
    //       d if sel = 11
    require(a.size == 16) { "invalid length of a: ${a.size}" }
    require(b.size == 16) { "invalid length of a: ${b.size}" }
    require(c.size == 16) { "invalid length of a: ${c.size}" }
    require(d.size == 16) { "invalid length of a: ${d.size}" }
    require(sel.size == 2) { "invalid length of sel: ${sel.size}" }

    val ab = mux(a, b, sel[0])
    val cd = mux(c, d, sel[0])
    return mux(ab, cd, sel[1])
}

fun mux8Way16(
    a: List<Bit>,
    b: List<Bit>,
    c: List<Bit>,
    d: List<Bit>,
    e: List<Bit>,
    f: List<Bit>,
    g: List<Bit>,
    h: List<Bit>,
    sel: List<Bit>,
): List<Bit> {
    // out = a if sel = 000
    //       b if sel = 001
    //       c if sel = 010
    //       d if sel = 011
    //       e if sel = 100
    //       f if sel = 101
    //       g if sel = 110
    //       h if sel = 111
    require(a.size == 16) { "invalid length of a: ${a.size}" }
    require(b.size == 16) { "invalid length of b: ${b.size}" }
    require(c.size == 16) { "invalid length of c: ${c.size}" }
    require(d.size == 16) { "invalid length of d: ${d.size}" }
    require(e.size == 16) { "invalid length of e: ${e.size}" }
    require(f.size == 16) { "invalid length of f: ${f.size}" }
    require(g.size == 16) { "invalid length of g: ${g.size}" }
    require(h.size == 16) { "invalid length of h: ${h.size}" }
    require(sel.size == 3) { "invalid length of sel: ${sel.size}" }

    val abcd = mux4Way16(a, b, c, d, listOf(sel[0], sel[1]))
    val efgh = mux4Way16(e, f, g, h, listOf(sel[0], sel[1]))
    return mux(abcd, efgh, sel[2])
}

fun muxNWay16(
    inputs: List<List<Bit>>,
    sel: List<Bit>,
): List<Bit> {
    require(sel.isNotEmpty()) { "sel cannot be empty" }
    require((1 shl sel.size) == inputs.size) { "inputs size must be 2^sel.size: inputs=${inputs.size}, sel.size=${sel.size}" }
    require(inputs.all { it.size == 16 }) { "all inputs must be 16-bit" }

    return when (sel.size) {
        1 -> mux(inputs[0], inputs[1], sel[0])
        2 -> mux4Way16(inputs[0], inputs[1], inputs[2], inputs[3], sel)
        3 -> mux8Way16(inputs[0], inputs[1], inputs[2], inputs[3], inputs[4], inputs[5], inputs[6], inputs[7], sel)
        else -> {
            val mid = inputs.size / 2
            val lower = inputs.subList(0, mid)
            val upper = inputs.subList(mid, inputs.size)
            val lowerResult = muxNWay16(lower, sel.dropLast(1))
            val upperResult = muxNWay16(upper, sel.dropLast(1))
            mux(lowerResult, upperResult, sel.last())
        }
    }
}

fun dMux4Way(
    input: Bit,
    sel: List<Bit>,
): List<Bit> {
    // [a, b, c, d] = [in, 0, 0, 0] if sel = 00
    //                [0, in, 0, 0] if sel = 01
    //                [0, 0, in, 0] if sel = 10
    //                [0, 0, 0, in] if sel = 11
    require(sel.size == 2) { "invalid length of sel: ${sel.size}" }

    val (ab, cd) = dMux(input, sel[1])
    return buildList {
        addAll(dMux(ab, sel[0]).toList())
        addAll(dMux(cd, sel[0]).toList())
    }
}

fun dMux8Way(
    input: Bit,
    sel: List<Bit>,
): List<Bit> {
    // [a, b, c, d, e, f, g, h] = [in, 0,  0,  0,  0,  0,  0,  0] if sel = 000
    //                            [0, in,  0,  0,  0,  0,  0,  0] if sel = 001
    //                            [0,  0, in,  0,  0,  0,  0,  0] if sel = 010
    //                            [0,  0,  0, in,  0,  0,  0,  0] if sel = 011
    //                            [0,  0,  0,  0, in,  0,  0,  0] if sel = 100
    //                            [0,  0,  0,  0,  0, in,  0,  0] if sel = 101
    //                            [0,  0,  0,  0,  0,  0, in,  0] if sel = 110
    //                            [0,  0,  0,  0,  0,  0,  0, in] if sel = 111
    require(sel.size == 3) { "invalid length of sel: ${sel.size}" }

    val (abcd, efgh) = dMux(input, sel[2])
    return buildList {
        addAll(dMux4Way(abcd, listOf(sel[0], sel[1])))
        addAll(dMux4Way(efgh, listOf(sel[0], sel[1])))
    }
}

fun dMuxNWay(
    input: Bit,
    sel: List<Bit>,
): List<Bit> {
    require(sel.isNotEmpty()) { "sel cannot be empty" }

    return when (sel.size) {
        1 -> dMux(input, sel[0]).toList()
        2 -> dMux4Way(input, sel)
        3 -> dMux8Way(input, sel)
        else -> {
            val highBit = sel.last()
            val lowBits = sel.dropLast(1)
            val (lower, upper) = dMux(input, highBit)
            val lowerResult = dMuxNWay(lower, lowBits)
            val upperResult = dMuxNWay(upper, lowBits)
            buildList {
                addAll(lowerResult)
                addAll(upperResult)
            }
        }
    }
}
