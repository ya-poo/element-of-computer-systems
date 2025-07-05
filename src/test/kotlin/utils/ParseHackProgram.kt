package utils

import me.yapoo.computer.circuit.Bit

fun parseHackProgram(lines: String): List<List<Bit>> {
    return lines.split("\n")
        .filter { it.isNotBlank() }
        .map { line ->
            // コメント以降を除去
            // また hack プログラムは下位ビットが右に位置しており、扱いづらいので逆順にする
            line.replace(" ", "").split("//")[0].map { char ->
                when (char) {
                    '0' -> Bit.LOW
                    '1' -> Bit.HIGH
                    else -> throw IllegalArgumentException("Invalid character in instruction: $char")
                }
            }.reversed()
        }
}
