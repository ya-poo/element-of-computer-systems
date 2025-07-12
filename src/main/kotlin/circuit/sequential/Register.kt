package circuit.sequential

import circuit.Bit

class Register {
    private val length = 16
    private val bits = List(length) { OneBitRegister() }

    fun current(): List<Bit> = bits.map { it.current() }

    fun tick(
        input: List<Bit>,
        load: Bit,
    ): List<Bit> {
        require(input.size == length) {
            "Invalid input length: ${input.size}"
        }
        return input.zip(bits).map { (i, b) ->
            b.tick(i, load)
        }
    }
}
