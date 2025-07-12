package hack

import circuit.Bit
import circuit.zero
import utilities.bitToInt
import utilities.intToBit

class Computer(
    instructions: List<List<Bit>> = emptyList(),
    initialMemory: Map<List<Bit>, List<Bit>> = emptyMap(),
    private val debug: Boolean = false,
) {
    private val dataMemory = Memory()
    private val instructionMemory = ROM32K(instructions)
    private val cpu = CPU()

    private var nextInstructionAddress = zero(15)
    private var nextCPUInM = zero(16)

    init {
        initialMemory.forEach { (address, value) ->
            dataMemory.tick(value, Bit.HIGH, address)
        }
    }

    fun tick(reset: Bit) {
        val instruction = instructionMemory.current(nextInstructionAddress)
        if (debug) {
            println("next instruction = ${bitToInt(nextInstructionAddress)}")
        }
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

        if (debug) {
            println("A = ${bitToInt(cpu.registerA.current())}")
            println("D = ${bitToInt(cpu.registerD.current())}")
            println("M[A] = ${bitToInt(dataMemory.read(cpu.registerA.current().subList(0, 15)))}")
        }
    }

    fun readMemory(address: List<Bit>): List<Bit> {
        return dataMemory.read(address)
    }

    fun getScreen(): String {
        val screenPixels = (16384 until 24576).flatMap { address ->
            val addressBits = intToBit(address, 15)
            val memoryValue = readMemory(addressBits)
            memoryValue.map { bit -> if (bit == Bit.HIGH) "■" else "□" }
        }

        return screenPixels.chunked(512).joinToString("\n") { row ->
            row.joinToString("")
        }
    }
}
