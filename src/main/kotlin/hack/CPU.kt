package me.yapoo.computer.hack

import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.alu
import me.yapoo.computer.circuit.and
import me.yapoo.computer.circuit.mux
import me.yapoo.computer.circuit.not
import me.yapoo.computer.circuit.or
import me.yapoo.computer.circuit.sequential.ProgramCounter
import me.yapoo.computer.circuit.sequential.Register

class CPU {
    val registerD = Register()
    val registerA = Register()
    val pc = ProgramCounter()

    // input:
    //   inM[16],
    //   instruction[16],
    //   reset
    // output:
    //   outM[16],
    //   writeM,
    //   addressM[15],
    //   pc[15]
    fun tick(
        inM: List<Bit>,
        instruction: List<Bit>,
        reset: Bit,
    ): CPUOutput {
        require(inM.size == 16) {
            "Invalid input size: ${inM.size}"
        }
        require(instruction.size == 16) {
            "Invalid instruction size: ${instruction.size}"
        }

        val doInstructionC = instruction[15] // これが HIGH なら C 命令、LOW なら A 命令

        // instructionC
        val (outM, zr, ng) = alu(
            registerD.current(),
            // A または M
            mux(registerA.current(), inM, instruction[12]),
            instruction[11],
            instruction[10],
            instruction[9],
            instruction[8],
            instruction[7],
            instruction[6],
        )
        val writeToA = instruction[5]
        val writeToD = instruction[4]
        val writeToM = instruction[3]

        val jump = doInstructionC and (
            (instruction[2] and ng) or
                (instruction[1] and zr) or
                (instruction[0] and ng.not() and zr.not())
        )

        val outPc = pc.tick(registerA.current(), Bit.HIGH, jump, reset)

        val inputA = mux(
            instruction,
            outM,
            doInstructionC,
        )
        val outA = registerA.tick(inputA, writeToA or doInstructionC.not())
        registerD.tick(outM, writeToD and doInstructionC)

        return CPUOutput(
            outM = outM,
            writeM = writeToM,
            addressM = outA.subList(0, 15),
            pc = outPc.subList(0, 15),
        )
    }

    data class CPUOutput(
        val outM: List<Bit>,
        val writeM: Bit,
        val addressM: List<Bit>,
        val pc: List<Bit>,
    )
}
