package me.yapoo.computer.hack

import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.zero

class Computer(
    instructions: List<List<Bit>> = emptyList(),
) {
    private val dataMemory = Memory()
    private val instructionMemory = ROM32K(instructions)
    private val cpu = CPU()

    private var nextInstructionAddress = zero(15)
    private var nextCPUInM = zero(16)

    fun tick(reset: Bit) {
        val instruction = instructionMemory.current(nextInstructionAddress)
        val cpuOut = cpu.tick(
            nextCPUInM,
            instruction,
            reset,
        )
        val memoryOut = dataMemory.tick(
            input = cpuOut.outM,
            load = cpuOut.writeM,
            address = cpuOut.addressM,
        )
        nextCPUInM = memoryOut
        nextInstructionAddress = cpuOut.pc
    }

    fun readMemory(address: List<Bit>): List<Bit> {
        return dataMemory.read(address)
    }
}
