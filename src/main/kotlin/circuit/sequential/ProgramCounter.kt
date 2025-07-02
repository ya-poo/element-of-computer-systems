package me.yapoo.computer.circuit.sequential

import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.inc
import me.yapoo.computer.circuit.mux
import me.yapoo.computer.circuit.zero

class ProgramCounter {
    private val register = Register()
    private val length = 16

    fun current(): List<Bit> {
        return register.current()
    }

    fun tick(
        input: List<Bit>,
        inc: Bit,
        load: Bit,
        reset: Bit,
    ): List<Bit> {
        require(input.size == length) {
            "Invalid input length: ${input.size}"
        }

        // if reset(t-1) then out(t) = 0
        //    else if load(t-1) then out(t) = in(t-1)
        //    else if inc(t-1) then out(t) = out(t-1) + 1
        //    else out(t) = out(t-1)

        val currentOutput = register.current()
        val afterInc = mux(currentOutput, currentOutput.inc(), inc)
        val afterLoad = mux(afterInc, input, load)
        val nextValue = mux(afterLoad, zero(length), reset)

        return register.tick(nextValue, Bit.HIGH)
    }
}
