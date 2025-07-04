package utils

import me.yapoo.computer.circuit.Bit

fun bitToInt(bits: List<Bit>): Int {
    return bits.foldIndexed(0) { index, acc, bit ->
        acc + if (bit == Bit.HIGH) (1 shl index) else 0
    }
}

fun intToBit(
    value: Int,
    size: Int,
): List<Bit> {
    return (0 until size).map { index ->
        if ((value shr index) and 1 == 1) Bit.HIGH else Bit.LOW
    }
}
