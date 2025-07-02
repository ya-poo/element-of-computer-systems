package me.yapoo.computer.circuit

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class BitTest : FunSpec({
    test("not演算") {
        Bit.HIGH.not() shouldBe Bit.LOW
        Bit.LOW.not() shouldBe Bit.HIGH
    }

    test("and演算") {
        (Bit.HIGH and Bit.HIGH) shouldBe Bit.HIGH
        (Bit.HIGH and Bit.LOW) shouldBe Bit.LOW
        (Bit.LOW and Bit.HIGH) shouldBe Bit.LOW
        (Bit.LOW and Bit.LOW) shouldBe Bit.LOW
    }

    test("or演算") {
        (Bit.HIGH or Bit.HIGH) shouldBe Bit.HIGH
        (Bit.HIGH or Bit.LOW) shouldBe Bit.HIGH
        (Bit.LOW or Bit.HIGH) shouldBe Bit.HIGH
        (Bit.LOW or Bit.LOW) shouldBe Bit.LOW
    }

    test("xor演算") {
        (Bit.HIGH xor Bit.HIGH) shouldBe Bit.LOW
        (Bit.HIGH xor Bit.LOW) shouldBe Bit.HIGH
        (Bit.LOW xor Bit.HIGH) shouldBe Bit.HIGH
        (Bit.LOW xor Bit.LOW) shouldBe Bit.LOW
    }

    test("mux関数") {
        mux(
            Bit.LOW,
            Bit.LOW,
            Bit.LOW,
        ) shouldBe Bit.LOW
        mux(
            Bit.LOW,
            Bit.HIGH,
            Bit.LOW,
        ) shouldBe Bit.LOW
        mux(
            Bit.HIGH,
            Bit.LOW,
            Bit.LOW,
        ) shouldBe Bit.HIGH
        mux(
            Bit.HIGH,
            Bit.HIGH,
            Bit.LOW,
        ) shouldBe Bit.HIGH
        mux(
            Bit.LOW,
            Bit.LOW,
            Bit.HIGH,
        ) shouldBe Bit.LOW
        mux(
            Bit.LOW,
            Bit.HIGH,
            Bit.HIGH,
        ) shouldBe Bit.HIGH
        mux(
            Bit.HIGH,
            Bit.LOW,
            Bit.HIGH,
        ) shouldBe Bit.LOW
        mux(
            Bit.LOW,
            Bit.HIGH,
            Bit.HIGH,
        ) shouldBe Bit.HIGH
    }

    test("dMux関数") {
        val result1 =
            dMux(
                Bit.HIGH,
                Bit.LOW,
            )
        result1.first shouldBe Bit.HIGH
        result1.second shouldBe Bit.LOW

        val result2 =
            dMux(
                Bit.HIGH,
                Bit.HIGH,
            )
        result2.first shouldBe Bit.LOW
        result2.second shouldBe Bit.HIGH

        val result3 =
            dMux(
                Bit.LOW,
                Bit.LOW,
            )
        result3.first shouldBe Bit.LOW
        result3.second shouldBe Bit.LOW

        val result4 =
            dMux(
                Bit.LOW,
                Bit.HIGH,
            )
        result4.first shouldBe Bit.LOW
        result4.second shouldBe Bit.LOW
    }

    test("muxNWay16関数") {
        val bitArb = Arb.enum<Bit>()
        val bitListArb = Arb.list(bitArb, 16..16)

        // 2-way test
        checkAll(bitListArb, bitListArb) { a, b ->
            muxNWay16(listOf(a, b), listOf(Bit.LOW)) shouldBe a
            muxNWay16(listOf(a, b), listOf(Bit.HIGH)) shouldBe b
        }

        // 4-way test
        checkAll(bitListArb, bitListArb, bitListArb, bitListArb) { a, b, c, d ->
            muxNWay16(listOf(a, b, c, d), listOf(Bit.LOW, Bit.LOW)) shouldBe a
            muxNWay16(listOf(a, b, c, d), listOf(Bit.HIGH, Bit.LOW)) shouldBe b
            muxNWay16(listOf(a, b, c, d), listOf(Bit.LOW, Bit.HIGH)) shouldBe c
            muxNWay16(listOf(a, b, c, d), listOf(Bit.HIGH, Bit.HIGH)) shouldBe d
        }

        // 8-way test
        checkAll(bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb) { a, b, c, d, e, f, g, h ->
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe a
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe b
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe c
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe d
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe e
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe f
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe g
            muxNWay16(listOf(a, b, c, d, e, f, g, h), listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe h
        }

        //  16-way test (4ビット選択)
        checkAll(Arb.list(bitListArb, 16..16)) { inputs ->
            muxNWay16(inputs, listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe inputs[0]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe inputs[1]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe inputs[2]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe inputs[3]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe inputs[4]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe inputs[5]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe inputs[6]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe inputs[7]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe inputs[8]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe inputs[9]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe inputs[10]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe inputs[11]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe inputs[12]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe inputs[13]
            muxNWay16(inputs, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe inputs[14]
            muxNWay16(inputs, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe inputs[15]
        }
    }

    test("dMuxNWay関数") {
        // 2-way test
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.HIGH)
        dMuxNWay(Bit.LOW, listOf(Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.LOW, listOf(Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW)

        // 4-way test
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)

        // 8-way test
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)

        // 16-way test (4ビット選択)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)
        dMuxNWay(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)

        // LOW input test
        checkAll(Arb.list(Arb.enum<Bit>(), 4..4)) { sel ->
            dMuxNWay(Bit.LOW, sel).all { it == Bit.LOW } shouldBe true
        }
    }
})
