package hack

import circuit.Bit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import utilities.bitToInt
import utilities.intToBit
import utils.parseHackProgram

class ComputerTest : FunSpec({
    test("Add.hack") {
        val instructions = parseHackProgram(
            """
            0000000000000010 // @2
            1110110000010000 // D=A
            0000000000000011 // @3
            1110000010010000 // D=D+A
            0000000000000000 // @0
            1110001100001000 // M=D
            """.trimIndent(),
        )
        val computer = Computer(instructions)
        repeat(instructions.size) {
            computer.tick(Bit.LOW)
        }

        val memoryValue = computer.readMemory(intToBit(0, 15))
        val intValue = bitToInt(memoryValue)
        intValue shouldBe 5
    }

    listOf(
        3 to 5,
        5 to 3,
        5 to 5,
    ).forEach { (first, second) ->
        test("Max.hack ($first, $second)") {
            val instructions = parseHackProgram(
                """
            0000000000000000 // @0
            1111110000010000 // D=M
            0000000000000001 // @1
            1111010011010000 // D=D-M
            0000000000001010 // @10
            1110001100000001 // D;JGT
            0000000000000001 // @1
            1111110000010000 // D=M
            0000000000001100 // @12
            1110101010000111 // 0;JMP
            0000000000000000 // @0
            1111110000010000 // D=M
            0000000000000010 // @2
            1110001100001000 // M=D
            0000000000001110 // @14
            1110101010000111 // 0;JMP
                """.trimIndent(),
            )

            val computer = Computer(
                instructions,
                initialMemory = mapOf(
                    intToBit(0, 15) to intToBit(first, 16),
                    intToBit(1, 15) to intToBit(second, 16),
                ),
            )

            repeat(instructions.size) {
                computer.tick(Bit.LOW)
            }

            val memoryValue = computer.readMemory(intToBit(2, 15))
            val intValue = bitToInt(memoryValue)
            intValue shouldBe 5
        }
    }
    test("Rect.hack") {
        val instructions = parseHackProgram(
            """
            0000000000000000 // @0
            1111110000010000 // D=M
            0000000000010111 // @23
            1110001100000110 // D;JLE
            0000000000010000 // @16
            1110001100001000 // M=D
            0100000000000000 // @16384
            1110110000010000 // D=A
            0000000000010001 // @17
            1110001100001000 // M=D
            0000000000010001 // @17
            1111110000100000 // A=M
            1110111010001000 // M=-1
            0000000000010001 // @17
            1111110000010000 // D=M
            0000000000100000 // @32
            1110000010010000 // D=D+A
            0000000000010001 // @17
            1110001100001000 // M=D
            0000000000010000 // @16
            1111110010011000 // MD=M-1
            0000000000001010 // @10
            1110001100000001 // D;JGT
            0000000000010111 // @23
            1110101010000111 // 0;JMP
            """.trimIndent(),
        )

        val computer = Computer(
            instructions,
            initialMemory = mapOf(
                intToBit(0, 15) to intToBit(4, 16),
            ),
        )

        repeat(60) { t ->
            computer.tick(Bit.LOW)
        }

        val screenOutput = computer.getScreen()
        screenOutput.split("\n").forEachIndexed { i, line ->
            if (i <= 3) {
                line.take(16).all { it == '■' } shouldBe true
                line.substring(16).all { it == '□' } shouldBe true
            } else {
                line.all { it == '□' } shouldBe true
            }
        }
    }
})
