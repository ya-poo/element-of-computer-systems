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

    test("mux4Way16関数") {
        val bitArb = Arb.enum<Bit>()
        val bitListArb = Arb.list(bitArb, 16..16)

        checkAll(bitListArb, bitListArb, bitListArb, bitListArb) { a, b, c, d ->
            mux4Way16(a, b, c, d, listOf(Bit.LOW, Bit.LOW)) shouldBe a
            mux4Way16(a, b, c, d, listOf(Bit.HIGH, Bit.LOW)) shouldBe b
            mux4Way16(a, b, c, d, listOf(Bit.LOW, Bit.HIGH)) shouldBe c
            mux4Way16(a, b, c, d, listOf(Bit.HIGH, Bit.HIGH)) shouldBe d
        }
    }

    test("mux8Way16関数") {
        val bitArb = Arb.enum<Bit>()
        val bitListArb = Arb.list(bitArb, 16..16)

        checkAll(bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb, bitListArb) { a, b, c, d, e, f, g, h ->
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe a
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe b
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe c
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe d
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe e
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe f
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe g
            mux8Way16(a, b, c, d, e, f, g, h, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe h
        }
    }

    test("dMux4Way関数") {
        dMux4Way(Bit.HIGH, listOf(Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux4Way(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)
        dMux4Way(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)
        dMux4Way(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)

        dMux4Way(Bit.LOW, listOf(Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux4Way(Bit.LOW, listOf(Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux4Way(Bit.LOW, listOf(Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux4Way(Bit.LOW, listOf(Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
    }

    test("dMux8Way関数") {
        dMux8Way(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH, Bit.LOW)
        dMux8Way(Bit.HIGH, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.HIGH)

        dMux8Way(Bit.LOW, listOf(Bit.LOW, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.HIGH, Bit.LOW, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.LOW, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.HIGH, Bit.HIGH, Bit.LOW)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.LOW, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.HIGH, Bit.LOW, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.LOW, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
        dMux8Way(Bit.LOW, listOf(Bit.HIGH, Bit.HIGH, Bit.HIGH)) shouldBe listOf(Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW, Bit.LOW)
    }
})
