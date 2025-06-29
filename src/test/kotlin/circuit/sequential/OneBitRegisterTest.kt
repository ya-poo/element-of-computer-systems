package me.yapoo.computer.circuit.sequential

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import me.yapoo.computer.circuit.Bit

class OneBitRegisterTest : FunSpec({
    val bitArb = Arb.enum<Bit>()
    test("初期状態では出力はLOW") {
        checkAll(bitArb, bitArb) { input, load ->
            val register = OneBitRegister()
            val output = register.tick(input, load)
            output shouldBe Bit.LOW
        }
    }

    test("load=HIGHの場合、入力値が次のクロックで出力される") {
        checkAll(bitArb, bitArb, bitArb) { first, second, third ->
            val register = OneBitRegister()
            val output1 = register.tick(first, Bit.HIGH)
            output1 shouldBe Bit.LOW

            val output2 = register.tick(second, Bit.HIGH)
            output2 shouldBe first

            val output3 = register.tick(third, Bit.HIGH)
            output3 shouldBe second
        }
    }

    test("load=LOWの場合、現在の値が保持される") {
        checkAll(bitArb, bitArb, bitArb) { first, second, third ->
            val register = OneBitRegister()

            // 値を設定
            val output1 = register.tick(first, Bit.HIGH)
            output1 shouldBe Bit.LOW

            val output2 = register.tick(second, Bit.LOW)
            output2 shouldBe first

            val output3 = register.tick(third, Bit.LOW)
            output3 shouldBe first
        }
    }

    test("複数クロックサイクルでの状態遷移") {
        val register = OneBitRegister()
        val inputs = listOf(Bit.HIGH, Bit.LOW, Bit.HIGH, Bit.LOW)
        val loads = listOf(Bit.HIGH, Bit.HIGH, Bit.LOW, Bit.HIGH)
        val expectedOutputs = listOf(Bit.LOW, Bit.HIGH, Bit.LOW, Bit.LOW)

        inputs.zip(loads).zip(expectedOutputs).forEach { (inputLoad, expected) ->
            val (input, load) = inputLoad
            val output = register.tick(input, load)
            output shouldBe expected
        }
    }
})
