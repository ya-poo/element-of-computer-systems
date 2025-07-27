package vm

import assembly.assembleLines
import circuit.Bit
import hack.Computer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import utilities.bitToInt
import utilities.intToBit
import utils.parseBit

class TranslatorTest : FunSpec(
    {
        val bootStrap = """
            @256
            D=A
            @SP
            M=D
        """.trimIndent().split("\n").asSequence()
        test("SimpleAdd") {
            val assembly = translateLines(
                "SimpleAdd",
                """
                    push constant 7
                    push constant 8
                    add
                """.trimIndent().split("\n").asSequence(),
            ).flatMap {
                it.split("\n")
            }.toList()

            val instructions = assembleLines(bootStrap + assembly)
            val instructionsBit = instructions.map {
                parseBit(it.reversed())
            }.toList()

            val computer = Computer(instructions = instructionsBit)
            repeat(25) {
                computer.tick(Bit.LOW)
            }

            bitToInt(computer.readMemory(intToBit(256, 15))) shouldBe 15
        }
    },
)
