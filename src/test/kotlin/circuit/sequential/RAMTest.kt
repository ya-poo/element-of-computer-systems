package me.yapoo.computer.circuit.sequential

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import me.yapoo.computer.circuit.Bit

class RAMTest : FunSpec({
    val bitArb = Arb.enum<Bit>()
    val bit16Arb = Arb.list(bitArb, 16..16)
    val sizeAndAddressArb = Arb.element(listOf(8, 64, 512, 4096, 16384)).flatMap { size ->
        Arb.int(0 until size).map { address -> size to address }
    }

    fun createAddressBits(value: Int, size: Int): List<Bit> {
        val length = size.countTrailingZeroBits()
        return (0 until length).map { i ->
            if ((value shr i) and 1 == 1) Bit.HIGH else Bit.LOW
        }
    }

    test("初期状態では全アドレスでLOW出力") {
        checkAll(sizeAndAddressArb, bit16Arb) { (size, address), input ->
            val ram = RAM(size)
            val addressBits = createAddressBits(address, size)
            val output = ram.tick(input, addressBits, Bit.LOW)
            output shouldBe List(16) { Bit.LOW }
        }
    }

    test("書き込みと読み出し") {
        checkAll(sizeAndAddressArb, bit16Arb) { (size, address), input ->
            val ram = RAM(size)
            val addressBits = createAddressBits(address, size)

            // 書き込み
            val writeOutput = ram.tick(input, addressBits, Bit.HIGH)
            writeOutput shouldBe input

            // 読み出し
            val readOutput = ram.tick(List(16) { Bit.LOW }, addressBits, Bit.LOW)
            readOutput shouldBe input
        }
    }

    test("load=LOWの場合、書き込みが行われない") {
        checkAll(sizeAndAddressArb, bit16Arb, bit16Arb) { (size, address), originalData, newData ->
            val ram = RAM(size)
            val addressBits = createAddressBits(address, size)

            ram.tick(originalData, addressBits, Bit.HIGH)
            ram.tick(newData, addressBits, Bit.LOW)

            val output = ram.tick(List(16) { Bit.LOW }, addressBits, Bit.LOW)
            output shouldBe originalData
        }
    }

    test("同じアドレスへの書き込みによって上書きされる") {
        checkAll(sizeAndAddressArb, bit16Arb, bit16Arb) { (size, address), originalData, newData ->
            val ram = RAM(size)
            val addressBits = createAddressBits(address, size)

            ram.tick(originalData, addressBits, Bit.HIGH)
            ram.tick(newData, addressBits, Bit.HIGH)

            val output = ram.tick(List(16) { Bit.LOW }, addressBits, Bit.LOW)
            output shouldBe newData
        }
    }

    test("異なるアドレスへの書き込みが他のアドレスに影響しない") {
        val arb = Arb.element(listOf(8, 64, 512, 4096, 16384)).flatMap { size ->
            Arb.list(Arb.int(0 until size), 2..2).filter {
                it[0] != it[1]
            }.map { addresses ->
                size to addresses
            }
        }
        checkAll(arb, bit16Arb, bit16Arb) { (size, addresses), data1, data2 ->
            val ram = RAM(size)
            val address1 = addresses[0]
            val address2 = addresses[1]

            val addressBits1 = createAddressBits(address1, size)
            ram.tick(data1, addressBits1, Bit.HIGH)

            val addressBits2 = createAddressBits(address2, size)
            ram.tick(data2, addressBits2, Bit.HIGH)

            val output1 = ram.tick(List(16) { Bit.LOW }, addressBits1, Bit.LOW)
            output1 shouldBe data1

            val output2 = ram.tick(List(16) { Bit.LOW }, addressBits2, Bit.LOW)
            output2 shouldBe data2
        }
    }
})
