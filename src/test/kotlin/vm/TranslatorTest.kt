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
            repeat(instructionsBit.size) {
                computer.tick(Bit.LOW)
            }

            bitToInt(computer.readMemory(intToBit(256, 15))) shouldBe 15
        }

        test("StackTest") {
            val assembly = translateLines(
                "StackTest",
                """
                    push constant 17
                    push constant 17
                    eq
                    push constant 892
                    push constant 891
                    lt
                    push constant 32767
                    push constant 32766
                    gt
                    push constant 56
                    push constant 31
                    push constant 53
                    add
                    push constant 112
                    sub
                    neg
                    and
                    push constant 82
                    or
                """.trimIndent().split("\n").asSequence(),
            ).flatMap {
                it.split("\n")
            }.toList()

            (bootStrap + assembly).forEach {
                println(it)
            }

            val instructions = assembleLines(bootStrap + assembly)
            val instructionsBit = instructions.map {
                println(it)
                parseBit(it.reversed())
            }.toList()

            val computer = Computer(instructions = instructionsBit)
            repeat(instructionsBit.size) {
                computer.tick(Bit.LOW)
            }
            bitToInt(computer.readMemory(intToBit(0, 15))) shouldBe 260
            bitToInt(computer.readMemory(intToBit(256, 15))) shouldBe -1
            bitToInt(computer.readMemory(intToBit(257, 15))) shouldBe 0
            bitToInt(computer.readMemory(intToBit(258, 15))) shouldBe -1
            bitToInt(computer.readMemory(intToBit(259, 15))) shouldBe 90
        }
    },
)
