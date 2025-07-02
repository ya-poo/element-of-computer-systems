package hack

import io.kotest.core.spec.style.FunSpec
import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.hack.Computer
import utils.parseHackProgram

class ComputerTest : FunSpec({
    test("Add.hack") {
        val instructions = parseHackProgram(
            """
            0000000000000010 // @2
            1110110000010000 // D=A
            0000000000000011 // @3
            1110000010010000 // D=D+A
            0000000000000000 // noop
            1110001100001000 // M=D
            """.trimIndent(),
        )
        val computer = Computer(instructions)
        repeat(instructions.size) {
            computer.tick(Bit.LOW)
        }
        // TODO: データメモリの 0 番目に 5 が入っている
    }
})
