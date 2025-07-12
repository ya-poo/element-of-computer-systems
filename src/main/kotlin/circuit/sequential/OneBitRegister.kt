package circuit.sequential

import circuit.Bit
import circuit.mux

class OneBitRegister {
    // if load(t-1) then out(t) = in(t-1)
    // else out(t) = out(t-1)

    private val dff = DataFlipFlop()

    fun current(): Bit = dff.current()

    fun tick(
        input: Bit,
        load: Bit,
    ): Bit {
        val muxOut = mux(dff.current(), input, load)
        return dff.tick(muxOut)
    }
}
