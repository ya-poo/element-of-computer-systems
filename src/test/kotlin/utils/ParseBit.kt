package utils

import circuit.Bit

fun parseBit(instruction: String): List<Bit> {
    return instruction.map {
        if (it == '0') {
            Bit.LOW
        } else {
            Bit.HIGH
        }
    }
}
