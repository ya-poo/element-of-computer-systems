package vm

import utilities.createRandomSymbol

fun Command.toHack(filename: String): String {
    val prefix = "// start: ${this::class.java.simpleName}"
    val calculation: String = when (this) {
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
                M=M-1
                A=M
                D=M
                A=A-1
                $calc
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
            val label = createRandomSymbol()
            """
                @SP
                M=M-1
                A=M
                D=M
                @SP
                M=M-1
                A=M
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

        is Command.Label -> """
            ($filename$$symbol)
        """.trimIndent()

        is Command.Goto -> """
            @$filename$$symbol
            0;JMP
        """.trimIndent()

        is Command.IfGoto -> """
            @SP
            M=M-1
            A=M
            D=M
            @$filename$$symbol
            D;JNE
        """.trimIndent()

        is Command.Function -> {
            listOf(
                "($functionName)",
                """
                    @0
                    D=A
                    @SP
                    A=M
                    M=D
                    @SP
                    M=M+1
                """.trimIndent().repeat(nLocals),
            ).joinToString("\n")
        }

        is Command.Call -> {
            val returnAddressLabel = "RETURN_${functionName}_${createRandomSymbol()}"
            """
                @$returnAddressLabel
                D=A
                
                // push return-address
                @SP
                A=M
                M=D
                @SP
                M=M+1
                
                // push LCL
                @LCL
                D=M
                @SP
                A=M
                M=D
                @SP
                M=M+1

                // push ARG
                @ARG
                D=M
                @SP
                A=M
                M=D
                @SP
                M=M+1

                // push THIS
                @THIS
                D=M
                @SP
                A=M
                M=D
                @SP
                M=M+1

                // push THAT
                @THAT
                D=M
                @SP
                A=M
                M=D
                @SP
                M=M+1

                // ARG = SP - nArgs - 5
                @SP
                D=M
                @${nArgs + 5}
                D=D-A
                @ARG
                M=D

                // LCL = SP
                @SP
                D=M
                @LCL
                M=D

                // goto f
                @$functionName
                0;JMP
                
                ($returnAddressLabel)
            """.trimIndent()
        }

        is Command.Return -> {
            """
                // FRAME = M[R13] = LCL
                @LCL
                D=M
                @R13
                M=D

                // RET = M[R14] = *(FRAME-5)
                @5
                A=D-A
                D=M
                @R14          // R14 ← RET
                M=D

                // *ARG = pop()
                @SP
                M=M-1
                A=M
                D=M
                @ARG
                A=M
                M=D

                @ARG          // SP = ARG + 1
                D=M+1
                @SP
                M=D

                @R13          // THAT = *(FRAME-1)
                M=M-1
                A=M
                D=M
                @THAT
                M=D

                @R13          // THIS = *(FRAME-2)
                M=M-1
                A=M
                D=M
                @THIS
                M=D

                @R13          // ARG = *(FRAME-3)
                M=M-1
                A=M
                D=M
                @ARG
                M=D

                @R13          // LCL = *(FRAME-4)
                M=M-1
                A=M
                D=M
                @LCL
                M=D

                @R14          // goto RET
                A=M
                0;JMP
            """.trimIndent()
        }
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
                M=M-1
                A=M
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
                M=M-1
                A=M
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
                M=M-1
                A=M
                D=M
                
                @${if (index in 0..7) index + 5 else throw Exception("Invalid temp index")}
                M=D
            """.trimIndent()
        }

        Segment.Static -> {
            """
                @SP
                M=M-1
                A=M
                D=M
                
                @$filename.$index
                M=D
            """.trimIndent()
        }
    }
}
