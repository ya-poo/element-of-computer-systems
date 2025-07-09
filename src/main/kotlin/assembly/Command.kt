package me.yapoo.computer.assembly

sealed class Command {
    data class A(
        val value: Int?,
        val symbol: String?,
    ) : Command()

    data class C(
        val dest: Dest?,
        val comp: Comp,
        val jump: Jump?,
    ) : Command()

    data class L(
        val symbol: String,
    ) : Command()
}

enum class Dest {
    M,
    D,
    MD,
    A,
    AM,
    AD,
    AMD,
}

@Suppress("EnumEntryName")
enum class Comp {
    `0`,
    `1`,
    `-1`,
    D,
    A,
    M,
    `!D`,
    `!A`,
    `!M`,
    `-D`,
    `-A`,
    `-M`,
    `D+1`,
    `A+1`,
    `M+1`,
    `D-1`,
    `A-1`,
    `M-1`,
    `D+A`,
    `D+M`,
    `D-A`,
    `D-M`,
    `A-D`,
    `M-D`,
    `D&A`,
    `D&M`,
    `D|A`,
    `D|M`,
}

enum class Jump {
    JGT,
    JEQ,
    JGE,
    JLT,
    JNE,
    JLE,
    JMP,
}
