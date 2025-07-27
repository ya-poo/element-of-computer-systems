package utilities

import circuit.Bit

fun bitToInt(bits: List<Bit>): Int {
    val unsignedValue = bits.foldIndexed(0) { index, acc, bit ->
        acc + if (bit == Bit.HIGH) (1 shl index) else 0
    }

    // 2の補数として解釈する（最上位ビットが符号ビット）
    val bitCount = bits.size
    val signBit = 1 shl (bitCount - 1)

    return if (unsignedValue >= signBit) {
        unsignedValue - (1 shl bitCount)
    } else {
        unsignedValue
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
