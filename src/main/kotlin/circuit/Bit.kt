package circuit

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

fun muxNWay16(
    inputs: List<List<Bit>>,
    sel: List<Bit>,
): List<Bit> {
    // out = inputs[sel]

    require(sel.isNotEmpty()) { "sel cannot be empty" }
    require((1 shl sel.size) == inputs.size) { "inputs size must be 2^sel.size: inputs=${inputs.size}, sel.size=${sel.size}" }
    require(inputs.all { it.size == 16 }) { "all inputs must be 16-bit" }

    if (sel.size == 1) {
        return mux(inputs[0], inputs[1], sel[0])
    }

    val mid = inputs.size / 2
    val lower = inputs.subList(0, mid)
    val upper = inputs.subList(mid, inputs.size)
    val lowerResult = muxNWay16(lower, sel.dropLast(1))
    val upperResult = muxNWay16(upper, sel.dropLast(1))
    return mux(lowerResult, upperResult, sel.last())
}

fun dMuxNWay(
    input: Bit,
    sel: List<Bit>,
): List<Bit> {
    // 例えば sel の長さが 2 の場合、以下の振る舞いをする
    // [a, b, c, d] = [in, 0, 0, 0] if sel = 00
    //                [0, in, 0, 0] if sel = 01
    //                [0, 0, in, 0] if sel = 10
    //                [0, 0, 0, in] if sel = 11
    require(sel.isNotEmpty()) { "sel cannot be empty" }

    if (sel.size == 1) {
        return dMux(input, sel[0]).toList()
    }

    val highBit = sel.last()
    val lowBits = sel.dropLast(1)
    val (lower, upper) = dMux(input, highBit)
    val lowerResult = dMuxNWay(lower, lowBits)
    val upperResult = dMuxNWay(upper, lowBits)
    return buildList {
        addAll(lowerResult)
        addAll(upperResult)
    }
}
