package hack

import circuit.Bit
import circuit.muxNWay16
import circuit.sequential.Register
import circuit.zero

// 命令メモリ
class ROM32K(
    instructions: List<List<Bit>> = emptyList(),
) {
    private val size = 32768
    private val registers = List(size) { Register() }

    init {
        require(instructions.size < size) {
            "Too large instructions. size: ${instructions.size}"
        }
        instructions.forEachIndexed { index, instruction ->
            if (index < size) {
                registers[index].tick(instruction, Bit.HIGH)
            }
        }

        for (i in instructions.size until size) {
            registers[i].tick(zero(16), Bit.HIGH)
        }
    }

    fun current(address: List<Bit>): List<Bit> {
        val currentValues = registers.map {
            it.current()
        }

        return muxNWay16(currentValues, address)
    }
}
