package hack

import circuit.Bit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import utilities.bitToInt
import utilities.intToBit
import utils.parseHackProgram

class ComputerInstructionTest : FunSpec({

    context("A命令（@value）") {
        test("@0") {
            val instructions = parseHackProgram(
                """
                0000000000000000 // @0
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 0
        }

        test("@1") {
            val instructions = parseHackProgram(
                """
                0000000000000001 // @1
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(1, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        test("@100") {
            val instructions = parseHackProgram(
                """
                0000000001100100 // @100
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(100, 15))
            bitToInt(memoryValue) shouldBe 100
        }
    }

    context("C命令 - 全計算パターン") {
        // comp=0, dest=M, jump=null
        test("M=0") {
            val instructions = parseHackProgram(
                """
                1110101010001000 // M=0 (comp=101010)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 0
        }

        // comp=1, dest=M, jump=null
        test("M=1") {
            val instructions = parseHackProgram(
                """
                1110111111001000 // M=1 (comp=111111)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // comp=-1, dest=M, jump=null
        test("M=-1") {
            val instructions = parseHackProgram(
                """
                1110111010001000 // M=-1 (comp=111010)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe -1
        }

        // comp=D, dest=M, jump=null
        test("M=D") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110000010000 // D=A
                0000000000000000 // @0
                1110001100001000 // M=D (comp=001100)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 5
        }

        // comp=A, dest=M, jump=null
        test("M=A") {
            val instructions = parseHackProgram(
                """
                0000000000000111 // @7
                1110110000001000 // M=A (comp=110000)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(7, 15))
            bitToInt(memoryValue) shouldBe 7
        }

        // comp=!D, dest=M, jump=null
        test("M=!D") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110000010000 // D=A
                0000000000000000 // @0
                1110001101001000 // M=!D (comp=001101)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe -6 // ~5 = -6
        }

        // comp=!A, dest=M, jump=null
        test("M=!A") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110001001000 // M=!A (comp=110001)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe -6 // ~5 = -6
        }

        // comp=-D, dest=M, jump=null
        test("M=-D") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110000010000 // D=A
                0000000000000000 // @0
                1110001111001000 // M=-D (comp=001111)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe -5
        }

        // comp=-A, dest=M, jump=null
        test("M=-A") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110011001000 // M=-A (comp=110011)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe -5
        }

        // comp=D+1, dest=M, jump=null
        test("M=D+1") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110000010000 // D=A
                0000000000000000 // @0
                1110011111001000 // M=D+1 (comp=011111)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 6
        }

        // comp=A+1, dest=M, jump=null
        test("M=A+1") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110111001000 // M=A+1 (comp=110111)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 6
        }

        // comp=D-1, dest=M, jump=null
        test("M=D-1") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110000010000 // D=A
                0000000000000000 // @0
                1110001110001000 // M=D-1 (comp=001110)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 4
        }

        // comp=A-1, dest=M, jump=null
        test("M=A-1") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5
                1110110010001000 // M=A-1 (comp=110010)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 4
        }

        // comp=D+A, dest=M, jump=null
        test("M=D+A") {
            val instructions = parseHackProgram(
                """
                0000000000000011 // @3
                1110110000010000 // D=A
                0000000000000101 // @5
                1110000010001000 // M=D+A (comp=000010)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 8
        }

        // comp=D-A, dest=M, jump=null
        test("M=D-A") {
            val instructions = parseHackProgram(
                """
                0000000000000111 // @7
                1110110000010000 // D=A
                0000000000000011 // @3
                1110010011001000 // M=D-A (comp=010011)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(3, 15))
            bitToInt(memoryValue) shouldBe 4
        }

        // comp=A-D, dest=M, jump=null
        test("M=A-D") {
            val instructions = parseHackProgram(
                """
                0000000000000011 // @3
                1110110000010000 // D=A
                0000000000000111 // @7
                1110000111001000 // M=A-D (comp=000111)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(7, 15))
            bitToInt(memoryValue) shouldBe 4
        }

        // comp=D&A, dest=M, jump=null
        test("M=D&A") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5 (101)
                1110110000010000 // D=A
                0000000000000011 // @3 (011)
                1110000000001000 // M=D&A (comp=000000)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(3, 15))
            bitToInt(memoryValue) shouldBe 1 // 101 & 011 = 001
        }

        // comp=D|A, dest=M, jump=null
        test("M=D|A") {
            val instructions = parseHackProgram(
                """
                0000000000000101 // @5 (101)
                1110110000010000 // D=A
                0000000000000011 // @3 (011)
                1110010101001000 // M=D|A (comp=010101)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(3, 15))
            bitToInt(memoryValue) shouldBe 7 // 101 | 011 = 111
        }

        // Mレジスタを使用した計算パターン
        // comp=M, dest=M, jump=null
        test("M=M") {
            val instructions = parseHackProgram(
                """
                1111110000001000 // M=M (comp=110000, a=1)
                """.trimIndent(),
            )
            val computer = Computer(
                instructions,
                initialMemory = mapOf(intToBit(0, 15) to intToBit(42, 16)),
            )
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 42
        }

        // comp=M+1, dest=M, jump=null
        test("M=M+1") {
            val instructions = parseHackProgram(
                """
                1111110111001000 // M=M+1 (comp=110111, a=1)
                """.trimIndent(),
            )
            val computer = Computer(
                instructions,
                initialMemory = mapOf(intToBit(0, 15) to intToBit(5, 16)),
            )
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 6
        }

        // comp=D+M, dest=M, jump=null
        test("M=D+M") {
            val instructions = parseHackProgram(
                """
                0000000000000011 // @3
                1110110000010000 // D=A
                0000000000000000 // @0
                1111000010001000 // M=D+M (comp=000010, a=1)
                """.trimIndent(),
            )
            val computer = Computer(
                instructions,
                initialMemory = mapOf(intToBit(0, 15) to intToBit(5, 16)),
                debug = true,
            )
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 8
        }
    }

    context("C命令 - 全dest組み合わせ") {
        // dest=null (000)
        test("計算のみ - dest=null") {
            val instructions = parseHackProgram(
                """
                1110101010000000 // comp=0, dest=null
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            // メモリに何も書き込まれないことを確認
            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 0 // 初期値のまま
        }

        // dest=M (001)
        test("dest=M") {
            val instructions = parseHackProgram(
                """
                1110111111001000 // M=1
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // dest=D (010)
        test("dest=D") {
            val instructions = parseHackProgram(
                """
                1110111111010000 // D=1
                0000000000000000 // @0
                1110001100001000 // M=D
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(3) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // dest=MD (011)
        test("dest=MD") {
            val instructions = parseHackProgram(
                """
                1110111111011000 // MD=1
                0000000000000001 // @1
                1110001100001000 // M=D
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(3) { computer.tick(Bit.LOW) }

            val memoryValue0 = computer.readMemory(intToBit(0, 15))
            val memoryValue1 = computer.readMemory(intToBit(1, 15))
            bitToInt(memoryValue0) shouldBe 1
            bitToInt(memoryValue1) shouldBe 1
        }

        // dest=A (100)
        test("dest=A") {
            val instructions = parseHackProgram(
                """
                1110111111100000 // A=1
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(1, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // dest=AM (101)
        test("dest=AM").config(enabled = false) {
            val instructions = parseHackProgram(
                """
                1110111111101000 // AM=1
                """.trimIndent(),
            )
            val computer = Computer(instructions, debug = true)
            computer.tick(Bit.LOW)

            // AレジスタとM[更新前のA=0]の両方に1が書き込まれる
            val memoryValue0 = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue0) shouldBe 1

            // Aレジスタが1に設定されたことを間接的に確認
            // 次の命令でM=Aを実行してAレジスタの値を確認
            val testInstructions = parseHackProgram(
                """
                1110111111101000 // AM=1
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val testComputer = Computer(testInstructions, debug = true)
            testComputer.tick(Bit.LOW)
            testComputer.tick(Bit.LOW)

            val aRegisterValue = testComputer.readMemory(intToBit(1, 15))
            bitToInt(aRegisterValue) shouldBe 1
        }

        // dest=AD (110)
        test("dest=AD") {
            // AD=1でAレジスタとDレジスタの両方に1を書き込む
            val instructions1 = parseHackProgram(
                """
                1110111111110000 // AD=1
                0000000000000000 // @0
                1110001100001000 // M=D
                """.trimIndent(),
            )
            val computer1 = Computer(instructions1)
            repeat(3) { computer1.tick(Bit.LOW) }

            // Dレジスタの値確認（Memory[0]に書き込まれる）
            val memoryValue0 = computer1.readMemory(intToBit(0, 15))
            bitToInt(memoryValue0) shouldBe 1

            // Aレジスタが1に設定されたことを別のテストで確認
            val instructions2 = parseHackProgram(
                """
                1110111111110000 // AD=1
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val computer2 = Computer(instructions2)
            repeat(2) { computer2.tick(Bit.LOW) }

            val aRegisterValue = computer2.readMemory(intToBit(1, 15))
            bitToInt(aRegisterValue) shouldBe 1
        }

        // dest=AMD (111)
        test("dest=AMD").config(enabled = false) {
            val instructions = parseHackProgram(
                """
                1110111111111000 // AMD=1
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            computer.tick(Bit.LOW)

            // M[更新前のA=0]、Aレジスタ、Dレジスタ全てに1が書き込まれる
            val memoryValue0 = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue0) shouldBe 1

            // Aレジスタが1に設定されたことを間接的に確認
            val testInstructions = parseHackProgram(
                """
                1110111111111000 // AMD=1
                1110110000001000 // M=A
                """.trimIndent(),
            )
            val testComputer = Computer(testInstructions)
            testComputer.tick(Bit.LOW)
            testComputer.tick(Bit.LOW)

            val aRegisterValue = testComputer.readMemory(intToBit(1, 15))
            bitToInt(aRegisterValue) shouldBe 1

            // Dレジスタが1に設定されたことを確認
            val testInstructions2 = parseHackProgram(
                """
                1110111111111000 // AMD=1
                0000000000000010 // @2
                1110001100001000 // M=D
                """.trimIndent(),
            )
            val testComputer2 = Computer(testInstructions2)
            repeat(3) { testComputer2.tick(Bit.LOW) }

            val dRegisterValue = testComputer2.readMemory(intToBit(2, 15))
            bitToInt(dRegisterValue) shouldBe 1
        }
    }

    context("C命令 - 全jump組み合わせ") {
        // jump=null (000)
        test("jump=null") {
            val instructions = parseHackProgram(
                """
                1110111111001000 // M=1
                1110101010001000 // M=0
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(2) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(0, 15))
            bitToInt(memoryValue) shouldBe 0 // 2番目の命令が実行された
        }

        // jump=JGT (001) - 正の値でジャンプ
        test("jump=JGT") {
            val instructions = parseHackProgram(
                """
                0000000000000001 // @1
                1110110000010000 // D=A
                0000000000000101 // @5
                1110001100000001 // D;JGT
                1110101010001000 // M=0 (実行されない)
                1110111111001000 // M=1 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(6) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // jump=JEQ (010) - ゼロでジャンプ
        test("jump=JEQ") {
            val instructions = parseHackProgram(
                """
                0000000000000000 // @0
                1110110000010000 // D=A
                0000000000000101 // @5
                1110001100000010 // D;JEQ
                1110111111001000 // M=1 (実行されない)
                1110101010001000 // M=0 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(6) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 0
        }

        // jump=JGE (011) - ゼロ以上でジャンプ
        test("jump=JGE") {
            val instructions = parseHackProgram(
                """
                0000000000000001 // @1
                1110110000010000 // D=A
                0000000000000101 // @5
                1110001100000011 // D;JGE
                1110101010001000 // M=0 (実行されない)
                1110111111001000 // M=1 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(6) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // jump=JLT (100) - 負の値でジャンプ
        test("jump=JLT") {
            val instructions = parseHackProgram(
                """
                0000000000000001 // @1
                1110110000010000 // D=A
                1110001111010000 // D=-D
                0000000000000101 // @5
                1110001100000100 // D;JLT
                1110101010001000 // M=0 (実行されない)
                1110111111001000 // M=1 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(7) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // jump=JNE (101) - ゼロ以外でジャンプ
        test("jump=JNE") {
            val instructions = parseHackProgram(
                """
                0000000000000001 // @1
                1110110000010000 // D=A
                0000000000000101 // @5
                1110001100000101 // D;JNE
                1110101010001000 // M=0 (実行されない)
                1110111111001000 // M=1 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(6) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 1
        }

        // jump=JLE (110) - ゼロ以下でジャンプ
        test("jump=JLE") {
            val instructions = parseHackProgram(
                """
                0000000000000000 // @0
                1110110000010000 // D=A
                0000000000000101 // @5
                1110001100000110 // D;JLE
                1110111111001000 // M=1 (実行されない)
                1110101010001000 // M=0 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(6) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(5, 15))
            bitToInt(memoryValue) shouldBe 0
        }

        // jump=JMP (111) - 無条件ジャンプ
        test("jump=JMP") {
            val instructions = parseHackProgram(
                """
                0000000000000011 // @3
                1110101010000111 // 0;JMP
                1110101010001000 // M=0 (実行されない)
                1110111111001000 // M=1 (ジャンプ先)
                """.trimIndent(),
            )
            val computer = Computer(instructions)
            repeat(4) { computer.tick(Bit.LOW) }

            val memoryValue = computer.readMemory(intToBit(3, 15))
            bitToInt(memoryValue) shouldBe 1
        }
    }
})
