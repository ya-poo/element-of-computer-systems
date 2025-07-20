package vm

import assembly.assembleLines
import circuit.Bit
import hack.Computer
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.logging.debug
import utils.parseBit

class TranslatorTest : FunSpec(
    {
        test("SimpleAdd") {
            val instructions = translateLines(
                "SimpleAdd",
                """
                    push constant 7
                    push constant 8
                    add
                """.trimIndent().split("\n").asSequence(),
            ).flatMap {
                it.split("\n")
            }.let {
                assembleLines(it)
            }.map {
                parseBit(it.reversed())
            }.toList()

            val computer = Computer(instructions = instructions, debug = true)
            repeat(instructions.size) {
                computer.tick(Bit.LOW)
            }
            println(computer.getMemoryValues())
        }
    },
)
