package circuit.sequential

import circuit.Bit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll

class OneBitRegisterTest : FunSpec({
    val bitArb = Arb.enum<Bit>()
    test("初期状態では出力はLOW") {
        val register = OneBitRegister()
        register.current() shouldBe Bit.LOW
    }

    test("load=HIGHの場合、入力値が出力される") {
        checkAll(bitArb) { input ->
            val register = OneBitRegister()
            val output = register.tick(input, Bit.HIGH)
            output shouldBe input
        }
    }

    test("load=LOWの場合、現在の値が保持される") {
        checkAll(bitArb, bitArb) { first, second ->
            val register = OneBitRegister()

            register.tick(first, Bit.HIGH)

            val output = register.tick(second, Bit.LOW)
            output shouldBe first
        }
    }
})
