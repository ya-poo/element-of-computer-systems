package circuit.sequential

import circuit.Bit

class DataFlipFlop {
    // out(t) = in(t-1)

    private var prev: Bit = Bit.LOW

    fun current(): Bit = prev

    fun tick(input: Bit): Bit {
        prev = input
        return input
    }
}
