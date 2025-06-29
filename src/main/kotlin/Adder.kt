package me.yapoo.computer

fun halfAdder(
    a: Bit,
    b: Bit,
): Pair<Bit, Bit> {
    val sum = a xor b
    val carry = a and b
    return sum to carry
}

fun fullAdder(
    a: Bit,
    b: Bit,
    c: Bit,
): Pair<Bit, Bit> {
    val (s1, c1) = halfAdder(a, b)
    val (s2, c2) = halfAdder(s1, c)

    val sum = s2
    val carry = c1 or c2
    return sum to carry
}

infix operator fun List<Bit>.plus(other: List<Bit>): List<Bit> {
    if (this.size != other.size) {
        throw IllegalArgumentException("invalid length: ${this.size} and ${other.size}")
    }

    return this.zip(other)
        .fold(emptyList<Bit>() to Bit.LOW) { (result, carry), (a, b) ->
            val (sum, newCarry) = fullAdder(a, b, carry)
            (result + sum) to newCarry
        }.first
}

infix operator fun List<Bit>.minus(other: List<Bit>): List<Bit> {
    if (this.size != other.size) {
        throw IllegalArgumentException("invalid length: ${this.size} and ${other.size}")
    }
    return this + other.minus()
}

fun zero(length: Int): List<Bit> {
    if (length < 1) {
        throw IllegalArgumentException("length must be greater than 0: actual = $length")
    }
    return List(length) { Bit.LOW }
}

fun one(length: Int): List<Bit> {
    if (length < 1) {
        throw IllegalArgumentException("length must be greater than 0: actual = $length")
    }
    return buildList {
        add(Bit.HIGH)
        repeat(length - 1) {
            add(Bit.LOW)
        }
    }
}

fun List<Bit>.minus(): List<Bit> {
    return this.not().inc()
}

fun List<Bit>.inc(): List<Bit> {
    return this + one(this.size)
}

fun alu(
    x: List<Bit>,
    y: List<Bit>,
    zx: Bit,
    nx: Bit,
    zy: Bit,
    ny: Bit,
    f: Bit,
    no: Bit,
): Triple<List<Bit>, Bit, Bit> {
    if (x.size != y.size) {
        throw IllegalArgumentException("invalid length: ${x.size} and ${y.size}")
    }

    // if zx then x = 0
    // if nx then x = !x
    // if zy then y = 0
    // if ny then y = !y
    // if f then out = x + y
    //      else out = x & y
    // if no then out = !out
    // it out = 0 then zr = 1 else zr = 0
    // if out < 0 then ng = 1 else ng = 0
    // return {out, zr, ng}
    val xx = applyNot(applyZero(x, zx), nx)
    val yy = applyNot(applyZero(y, zy), ny)
    val out = applyNot(mux(xx and yy, xx + yy, f), no)

    val zr = out.or().not()
    val ng = out.last()
    return Triple(out, zr, ng)
}

private fun applyZero(
    input: List<Bit>,
    z: Bit,
): List<Bit> {
    return mux(input, zero(input.size), z)
}

private fun applyNot(
    input: List<Bit>,
    n: Bit,
): List<Bit> {
    return mux(input, input.not(), n)
}
