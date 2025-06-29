package me.yapoo.computer.circuit.sequential

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import me.yapoo.computer.circuit.Bit
import me.yapoo.computer.circuit.inc
import me.yapoo.computer.circuit.one
import me.yapoo.computer.circuit.zero

class ProgramCounterTest : FunSpec({
    val bitArb = Arb.enum<Bit>()
    val bitListArb = Arb.list(bitArb, 16..16)

    test("初期状態では出力は0") {
        val pc = ProgramCounter()
        pc.current() shouldBe zero(16)
    }

    test("reset=HIGHの場合、常に0が出力される") {
        checkAll(bitListArb, bitArb, bitArb) { input, inc, load ->
            val pc = ProgramCounter()
            pc.tick(input, Bit.LOW, Bit.HIGH, Bit.LOW)

            val output = pc.tick(input, inc, load, Bit.HIGH)
            output shouldBe zero(16)
        }
    }

    test("load=HIGHの場合、入力値が次のクロックで出力される") {
        checkAll(bitListArb) { input ->
            val pc = ProgramCounter()
            
            pc.tick(input, Bit.LOW, Bit.HIGH, Bit.LOW)
            
            val output = pc.tick(zero(16), Bit.LOW, Bit.LOW, Bit.LOW)
            output shouldBe input
        }
    }

    test("inc=HIGHの場合、現在の値+1が出力される") {
        checkAll(bitListArb) { input ->
            val pc = ProgramCounter()

            pc.tick(input, Bit.LOW, Bit.HIGH, Bit.LOW)

            val output = pc.tick(zero(16), Bit.HIGH, Bit.LOW, Bit.LOW)
            output shouldBe input.inc()
        }
    }

    test("全フラグがLOWの場合、現在の値が保持される") {
        checkAll(bitListArb) { input ->
            val pc = ProgramCounter()
            
            // 値を設定
            pc.tick(input, Bit.LOW, Bit.HIGH, Bit.LOW)
            val setValue = pc.tick(zero(16), Bit.LOW, Bit.LOW, Bit.LOW)
            setValue shouldBe input
            
            // 全フラグLOWで値が保持される
            val output = pc.tick(zero(16), Bit.LOW, Bit.LOW, Bit.LOW)
            output shouldBe input
        }
    }

    test("優先順位: reset > load > inc > no-op") {
        checkAll(bitListArb, bitListArb, bitListArb) { first, second, third ->
            val pc = ProgramCounter()

            pc.tick(first, Bit.LOW, Bit.HIGH, Bit.LOW)

            // reset=HIGH, load=HIGH, inc=HIGHでもresetが優先される
            val output1 = pc.tick(second, Bit.HIGH, Bit.HIGH, Bit.HIGH)
            output1 shouldBe zero(16)

            val output2 = pc.tick(third, Bit.HIGH, Bit.HIGH, Bit.LOW)
            output2 shouldBe third
        }
    }
})