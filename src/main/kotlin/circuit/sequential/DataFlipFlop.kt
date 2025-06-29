package me.yapoo.computer.circuit.sequential

import me.yapoo.computer.circuit.Bit

class DataFlipFlop {
    // out(t) = in(t-1)

    private var prev: Bit = Bit.LOW

    fun current(): Bit = prev

    fun tick(input: Bit): Bit {
        val out = prev
        prev = input

        return out
    }
}
