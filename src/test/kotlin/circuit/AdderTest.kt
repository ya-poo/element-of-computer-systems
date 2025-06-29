package me.yapoo.computer.circuit

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

class AdderTest : FunSpec({
    val length = 16
    val bitArb = Arb.int(0, 1).map { if (it == 0) Bit.LOW else Bit.HIGH }
    val bitListArb = Arb.list(bitArb, length..length)

    test("halfAdder") {
        halfAdder(Bit.LOW, Bit.LOW) shouldBe (Bit.LOW to Bit.LOW)
        halfAdder(Bit.LOW, Bit.HIGH) shouldBe (Bit.HIGH to Bit.LOW)
        halfAdder(
            Bit.HIGH,
            Bit.LOW,
        ) shouldBe (Bit.HIGH to Bit.LOW)
        halfAdder(
            Bit.HIGH,
            Bit.HIGH,
        ) shouldBe (Bit.LOW to Bit.HIGH)
    }

    test("fullAdder") {
        fullAdder(
            Bit.LOW,
            Bit.LOW,
            Bit.LOW,
        ) shouldBe (Bit.LOW to Bit.LOW)
        fullAdder(
            Bit.LOW,
            Bit.LOW,
            Bit.HIGH,
        ) shouldBe (Bit.HIGH to Bit.LOW)
        fullAdder(
            Bit.LOW,
            Bit.HIGH,
            Bit.LOW,
        ) shouldBe (Bit.HIGH to Bit.LOW)
        fullAdder(
            Bit.LOW,
            Bit.HIGH,
            Bit.HIGH,
        ) shouldBe (Bit.LOW to Bit.HIGH)
        fullAdder(
            Bit.HIGH,
            Bit.LOW,
            Bit.LOW,
        ) shouldBe (Bit.HIGH to Bit.LOW)
        fullAdder(
            Bit.HIGH,
            Bit.LOW,
            Bit.HIGH,
        ) shouldBe (Bit.LOW to Bit.HIGH)
        fullAdder(
            Bit.HIGH,
            Bit.HIGH,
            Bit.LOW,
        ) shouldBe (Bit.LOW to Bit.HIGH)
        fullAdder(
            Bit.HIGH,
            Bit.HIGH,
            Bit.HIGH,
        ) shouldBe (Bit.HIGH to Bit.HIGH)
    }

    context("plus") {
        test("基本動作") {
            listOf(Bit.LOW) + listOf(Bit.LOW) shouldBe listOf(Bit.LOW)
            listOf(Bit.LOW) + listOf(Bit.HIGH) shouldBe listOf(Bit.HIGH)
            listOf(Bit.HIGH) + listOf(Bit.LOW) shouldBe listOf(Bit.HIGH)
            listOf(Bit.HIGH) + listOf(Bit.HIGH) shouldBe listOf(Bit.LOW)
            listOf(Bit.HIGH, Bit.LOW) + listOf(Bit.HIGH, Bit.LOW) shouldBe
                listOf(
                    Bit.LOW, Bit.HIGH,
                )
            listOf(Bit.HIGH, Bit.HIGH) + listOf(Bit.HIGH, Bit.LOW) shouldBe
                listOf(
                    Bit.LOW, Bit.LOW,
                )
            listOf(Bit.HIGH, Bit.HIGH, Bit.LOW) +
                listOf(
                    Bit.HIGH, Bit.LOW, Bit.HIGH,
                ) shouldBe
                listOf(
                    Bit.LOW,
                    Bit.LOW,
                    Bit.LOW,
                )
        }

        test("交換法則") {
            checkAll(bitListArb, bitListArb) { a: List<Bit>, b: List<Bit> ->
                a + b shouldBe b + a
            }
        }

        test("ゼロとの加算は恒等元") {
            checkAll(bitListArb) { bits: List<Bit> ->
                bits + zero(length) shouldBe bits
                zero(length) + bits shouldBe bits
            }
        }
    }

    context("minus") {
        test("元の数とマイナスの合計はゼロ") {
            checkAll(bitListArb) { x ->
                (x.minus() + x) shouldBe zero(length)
            }
        }
    }

    context("alu") {
        test("0 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, zr, ng) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                    )
                out shouldBe zero(length)
                zr shouldBe Bit.HIGH
                ng shouldBe Bit.LOW
            }
        }

        test("1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, zr, ng) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe one(length)
                zr shouldBe Bit.LOW
                ng shouldBe Bit.LOW
            }
        }

        test("-1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, zr, ng) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                    )

                out shouldBe List(length) { Bit.HIGH }
                zr shouldBe Bit.LOW
                ng shouldBe Bit.HIGH
            }
        }

        test("x function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                    )

                out shouldBe x
            }
        }

        test("y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                    )

                out shouldBe y
            }
        }

        test("not x function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                    )

                out shouldBe x.not()
            }
        }

        test("not y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                    )

                out shouldBe y.not()
            }
        }

        test("minus x function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe x.minus()
            }
        }

        test("minus y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe y.minus()
            }
        }

        test("x + 1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe x.inc()
            }
        }

        test("y + 1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe y.inc()
            }
        }

        test("x - 1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                    )

                out shouldBe x - one(length)
            }
        }

        test("y - 1 function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                    )

                out shouldBe y - one(length)
            }
        }

        test("x + y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                    )

                out shouldBe (x + y)
            }
        }

        test("x - y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe (x - y)
            }
        }

        test("y - x function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.HIGH,
                        Bit.HIGH,
                    )

                out shouldBe (y - x)
            }
        }

        test("x & y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                        Bit.LOW,
                    )

                out shouldBe (x and y)
            }
        }

        test("x | y function") {
            checkAll(bitListArb, bitListArb) { x: List<Bit>, y: List<Bit> ->
                val (out, _, _) =
                    alu(
                        x,
                        y,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                        Bit.LOW,
                        Bit.HIGH,
                    )

                out shouldBe (x or y)
            }
        }
    }
})
