package me.yapoo.computer.assembly

fun Command.A.toHack(symbolTable: Map<String, Int>): String {
    val encodedValue = if (value != null) {
        value.toString(2)
            .padStart(15, '0')
    } else if (symbol != null) {
        symbolTable[symbol]
            ?.toString(2)
            ?.padStart(15, '0')
            .also {
                requireNotNull(it) {
                    "未定義のシンボルです: $it"
                }
            }
    } else {
        throw Exception("不正な形式の A 命令です")
    }

    return "0$encodedValue"
}

fun Command.C.toHack(): String {
    val destBits = when (dest) {
        null -> "000"
        Dest.M -> "001"
        Dest.D -> "010"
        Dest.MD -> "011"
        Dest.A -> "100"
        Dest.AM -> "101"
        Dest.AD -> "110"
        Dest.AMD -> "111"
    }

    val compBits = when (comp) {
        Comp.`0` -> "0101010"
        Comp.`1` -> "0111111"
        Comp.`-1` -> "0111010"
        Comp.D -> "0001100"
        Comp.A -> "0110000"
        Comp.M -> "1110000"
        Comp.`!D` -> "0001101"
        Comp.`!A` -> "0110001"
        Comp.`!M` -> "1110001"
        Comp.`-D` -> "0001111"
        Comp.`-A` -> "0110011"
        Comp.`-M` -> "1110011"
        Comp.`D+1` -> "0011111"
        Comp.`A+1` -> "0110111"
        Comp.`M+1` -> "1110111"
        Comp.`D-1` -> "0001110"
        Comp.`A-1` -> "0110010"
        Comp.`M-1` -> "1110010"
        Comp.`D+A` -> "0000010"
        Comp.`D+M` -> "1000010"
        Comp.`D-A` -> "0010011"
        Comp.`D-M` -> "1010011"
        Comp.`A-D` -> "0000111"
        Comp.`M-D` -> "1000111"
        Comp.`D&A` -> "0000000"
        Comp.`D&M` -> "1000000"
        Comp.`D|A` -> "0010101"
        Comp.`D|M` -> "1010101"
    }

    val jumpBits = when (jump) {
        null -> "000"
        Jump.JGT -> "001"
        Jump.JEQ -> "010"
        Jump.JGE -> "011"
        Jump.JLT -> "100"
        Jump.JNE -> "101"
        Jump.JLE -> "110"
        Jump.JMP -> "111"
    }

    return "111$compBits$destBits$jumpBits"
}
