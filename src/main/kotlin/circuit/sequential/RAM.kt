package me.yapoo.computer.circuit.sequential

import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.dMuxNWay
import me.yapoo.computer.circuit.muxNWay16
import utilities.bitToInt

class RAM(
    private val n: Int,
) {
    init {
        require(n in listOf(8, 64, 512, 4096, 16384, 32768)) {
            "invalid n value: $n"
        }
    }

    private val k: Int = when (n) {
        8 -> 3
        64 -> 6
        512 -> 9
        4096 -> 12
        16384 -> 14
        else -> 15
    }

    private val registers = List(n) { Register() }
    private val wordLength = 16

    // input: in[16], address[k], load
    // output: out[16]
    // out(t) = RAM[address(t)](t)
    // if load(t-1) then RAM[address(t-1)](t) = in(t-1)

    fun tick(
        input: List<Bit>,
        address: List<Bit>,
        load: Bit,
    ): List<Bit> {
        require(input.size == wordLength) {
            "Invalid input length: ${input.size}"
        }
        require(address.size == k) {
            "Invalid address length: ${address.size}"
        }

        val loadSignals = dMuxNWay(load, address)

        val outputs = registers.mapIndexed { index, register ->
            register.tick(input, loadSignals[index])
        }

        return muxNWay16(outputs, address)
    }

    fun read(address: List<Bit>): List<Bit> {
        // 本来は以下のような実装にするべきだが、パフォーマンスの問題があるので避けた
        //   val outputs = registers.map { register ->
        //       register.current()
        //   }
        //   return muxNWay16(outputs, address)
        require(address.size == k) {
            "Invalid address length: ${address.size}"
        }
        val addressInt = bitToInt(address)
        return registers[addressInt].current()
    }
}
