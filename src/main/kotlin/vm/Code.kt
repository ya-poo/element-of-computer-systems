package vm

import java.util.UUID

fun Command.toHack(filename: String): String {
    val prefix = "// start: ${this::class.java.simpleName}"
    val calculation = when (this) {
        is Command.Add,
        is Command.Sub,
        is Command.And,
        is Command.Or,
        -> {
            val calc = when (this) {
                is Command.Add -> "M=D+M"
                is Command.Sub -> "M=M-D"
                is Command.And -> "M=D&M"
                else -> "M=D|M"
            }
            """
                @SP
                AM=M-1
                D=M
                @SP
                AM=M-1
                $calc
                @SP
                M=M+1
            """.trimIndent()
        }

        is Command.Neg -> """
            @SP
            A=M-1
            M=-M
        """.trimIndent()

        is Command.Eq,
        is Command.Gt,
        is Command.Lt,
        -> {
            val label = UUID.randomUUID().toString()
            """
                @SP
                AM=M-1
                D=M
                @SP
                AM=M-1
                D=M-D
                @${label}_TRUE
                D;${
                when (this) {
                    is Command.Eq -> {
                        "JEQ"
                    }

                    is Command.Gt -> {
                        "JGT"
                    }

                    else -> {
                        "JLT"
                    }
                }
            }
                @SP
                A=M
                M=0
                @${label}_END
                0;JMP
                (${label}_TRUE)
                @SP
                A=M
                M=-1
                (${label}_END)
                @SP
                M=M+1
            """.trimIndent()
        }

        is Command.Not -> """
            @SP
            A=M-1
            M=!M
        """.trimIndent()

        is Command.Push -> this.toHack(filename)

        is Command.Pop -> this.toHack(filename)

        else -> TODO()
    }
    val suffix = "// end: ${this::class.java.simpleName}"

    return listOf(
        prefix,
        calculation,
        suffix,
    ).joinToString("\n")
}

fun Command.Push.toHack(filename: String): String {
    val handleSp = """
        @SP
        A=M
        M=D
        @SP
        M=M+1
    """.trimIndent()
    return when (segment) {
        Segment.Argument,
        Segment.Local,
        Segment.This,
        Segment.That,
        -> {
            """
                @$index
                D=A
                @${
                when (segment) {
                    Segment.Argument -> "ARG"
                    Segment.Local -> "LCL"
                    Segment.This -> "THIS"
                    else -> "THAT"
                }
            }
                A=M
                A=D+A
                D=M
            """.trimIndent()
        }

        Segment.Constant -> {
            """
                @$index
                D=A
            """.trimIndent()
        }

        Segment.Pointer -> {
            """
                @${
                if (index == 0) {
                    "THIS"
                } else if (index == 1) {
                    "THAT"
                } else {
                    throw Exception("Invalid pointer index")
                }
            }
                D=M
            """.trimIndent()
        }

        Segment.Temp -> {
            """
                @${if (index in 0..7) index + 5 else throw Exception("Invalid temp index")}
                D=M
            """.trimIndent()
        }

        Segment.Static -> {
            """
                @$filename.$index
                D=M
            """.trimIndent()
        }
    } + "\n$handleSp"
}

fun Command.Pop.toHack(filename: String): String {
    return when (segment) {
        Segment.Argument,
        Segment.Local,
        Segment.This,
        Segment.That,
        -> {
            """
                @$index
                D=A
                @${
                when (segment) {
                    Segment.Argument -> "ARG"
                    Segment.Local -> "LCL"
                    Segment.This -> "THIS"
                    else -> "THAT"
                }
            }
                D=D+M
                @R13
                M=D
                
                @SP
                AM=M-1
                D=M
                
                @R13
                A=M
                M=D
            """.trimIndent()
        }

        Segment.Constant -> {
            throw Exception("invalid instruction: pop constant is prohibited")
        }

        Segment.Pointer -> {
            """
                @SP
                AM=M-1
                D=M
                
                @${
                when (index) {
                    0 -> {
                        "THIS"
                    }

                    1 -> {
                        "THAT"
                    }

                    else -> {
                        throw Exception("Invalid pointer index")
                    }
                }
            }
                M=D
            """.trimIndent()
        }

        Segment.Temp -> {
            """
                @SP
                AM=M-1
                D=M
                
                @${if (index in 0..7) index + 5 else throw Exception("Invalid temp index")}
                M=D
            """.trimIndent()
        }

        Segment.Static -> {
            """
                @SP
                AM=M-1
                D=M
                
                @$filename.$index
                M=D
            """.trimIndent()
        }
    }
}
