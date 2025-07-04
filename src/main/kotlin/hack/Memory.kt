package me.yapoo.computer.hack

import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.sequential.RAM

class Memory {
    private val wordLength = 16
    private val memory = RAM(32768)

    private fun isAddressValid(address: List<Bit>): Boolean {
        // 24576 (0x6000) = 110000000000000 in 15-bit binary
        // Check if address <= 24576
        // Invalid if address > 24576, which means bit 14 and 13 are both HIGH and any other bit is HIGH
        val isExactly24576 = address[14] == Bit.HIGH && address[13] == Bit.HIGH &&
            (0..12).all { address[it] == Bit.LOW }
        val isLessThan24576 = !(address[14] == Bit.HIGH && address[13] == Bit.HIGH)
        return isLessThan24576 || isExactly24576
    }

    // input: in[16], address[15], load
    // output: out[16]
    // out(t) = Memory[address(t)](t)
    // if load(t-1) then Memory[address(t-1)](t) = in(t-1)

    fun tick(
        input: List<Bit>,
        load: Bit,
        address: List<Bit>,
    ): List<Bit> {
        require(input.size == wordLength) {
            "Invalid input length: ${input.size}"
        }
        require(address.size == wordLength - 1) {
            "Invalid address length: ${address.size}"
        }
        require(isAddressValid(address)) {
            "Invalid address: address must be <= 24576"
        }

        return memory.tick(input, address, load)
    }

    fun read(address: List<Bit>): List<Bit> {
        require(address.size == wordLength - 1) {
            "Invalid address length: ${address.size}"
        }
        require(isAddressValid(address)) {
            "Invalid address: address must be <= 24576"
        }

        return memory.read(address)
    }
}
