package me.yapoo.computer.circuit

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
})
