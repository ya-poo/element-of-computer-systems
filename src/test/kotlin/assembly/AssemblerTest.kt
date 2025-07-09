package me.yapoo.computer.assembly

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AssemblerTest : FunSpec({
    test("A命令のアセンブル") {
        assembleText(parseLine("@0")!!, emptyMap()) shouldBe "0000000000000000"
        assembleText(parseLine("@1")!!, emptyMap()) shouldBe "0000000000000001"
        assembleText(parseLine("@2")!!, emptyMap()) shouldBe "0000000000000010"
        assembleText(parseLine("@123")!!, emptyMap()) shouldBe "0000000001111011"
        assembleText(parseLine("@32767")!!, emptyMap()) shouldBe "0111111111111111"
    }

    test("C命令のアセンブル - 計算のみ") {
        assembleText(parseLine("0")!!, emptyMap()) shouldBe "1110101010000000"
        assembleText(parseLine("1")!!, emptyMap()) shouldBe "1110111111000000"
        assembleText(parseLine("-1")!!, emptyMap()) shouldBe "1110111010000000"
        assembleText(parseLine("D")!!, emptyMap()) shouldBe "1110001100000000"
        assembleText(parseLine("A")!!, emptyMap()) shouldBe "1110110000000000"
        assembleText(parseLine("M")!!, emptyMap()) shouldBe "1111110000000000"
        assembleText(parseLine("!D")!!, emptyMap()) shouldBe "1110001101000000"
        assembleText(parseLine("!A")!!, emptyMap()) shouldBe "1110110001000000"
        assembleText(parseLine("!M")!!, emptyMap()) shouldBe "1111110001000000"
        assembleText(parseLine("-D")!!, emptyMap()) shouldBe "1110001111000000"
        assembleText(parseLine("-A")!!, emptyMap()) shouldBe "1110110011000000"
        assembleText(parseLine("-M")!!, emptyMap()) shouldBe "1111110011000000"
        assembleText(parseLine("D+1")!!, emptyMap()) shouldBe "1110011111000000"
        assembleText(parseLine("A+1")!!, emptyMap()) shouldBe "1110110111000000"
        assembleText(parseLine("M+1")!!, emptyMap()) shouldBe "1111110111000000"
        assembleText(parseLine("D-1")!!, emptyMap()) shouldBe "1110001110000000"
        assembleText(parseLine("A-1")!!, emptyMap()) shouldBe "1110110010000000"
        assembleText(parseLine("M-1")!!, emptyMap()) shouldBe "1111110010000000"
        assembleText(parseLine("D+A")!!, emptyMap()) shouldBe "1110000010000000"
        assembleText(parseLine("D+M")!!, emptyMap()) shouldBe "1111000010000000"
        assembleText(parseLine("D-A")!!, emptyMap()) shouldBe "1110010011000000"
        assembleText(parseLine("D-M")!!, emptyMap()) shouldBe "1111010011000000"
        assembleText(parseLine("A-D")!!, emptyMap()) shouldBe "1110000111000000"
        assembleText(parseLine("M-D")!!, emptyMap()) shouldBe "1111000111000000"
        assembleText(parseLine("D&A")!!, emptyMap()) shouldBe "1110000000000000"
        assembleText(parseLine("D&M")!!, emptyMap()) shouldBe "1111000000000000"
        assembleText(parseLine("D|A")!!, emptyMap()) shouldBe "1110010101000000"
        assembleText(parseLine("D|M")!!, emptyMap()) shouldBe "1111010101000000"
    }

    test("C命令のアセンブル - 代入付き") {
        assembleText(parseLine("M=1")!!, emptyMap()) shouldBe "1110111111001000"
        assembleText(parseLine("D=A")!!, emptyMap()) shouldBe "1110110000010000"
        assembleText(parseLine("MD=D+1")!!, emptyMap()) shouldBe "1110011111011000"
        assembleText(parseLine("A=D-1")!!, emptyMap()) shouldBe "1110001110100000"
        assembleText(parseLine("AM=D")!!, emptyMap()) shouldBe "1110001100101000"
        assembleText(parseLine("AD=M")!!, emptyMap()) shouldBe "1111110000110000"
        assembleText(parseLine("AMD=D&A")!!, emptyMap()) shouldBe "1110000000111000"
    }

    test("C命令のアセンブル - ジャンプ付き") {
        assembleText(parseLine("D;JGT")!!, emptyMap()) shouldBe "1110001100000001"
        assembleText(parseLine("D;JEQ")!!, emptyMap()) shouldBe "1110001100000010"
        assembleText(parseLine("D;JGE")!!, emptyMap()) shouldBe "1110001100000011"
        assembleText(parseLine("D;JLT")!!, emptyMap()) shouldBe "1110001100000100"
        assembleText(parseLine("D;JNE")!!, emptyMap()) shouldBe "1110001100000101"
        assembleText(parseLine("D;JLE")!!, emptyMap()) shouldBe "1110001100000110"
        assembleText(parseLine("D;JMP")!!, emptyMap()) shouldBe "1110001100000111"
        assembleText(parseLine("0;JMP")!!, emptyMap()) shouldBe "1110101010000111"
    }

    test("C命令のアセンブル - 代入とジャンプ付き") {
        assembleText(parseLine("M=D+1;JGT")!!, emptyMap()) shouldBe "1110011111001001"
        assembleText(parseLine("D=M;JEQ")!!, emptyMap()) shouldBe "1111110000010010"
        assembleText(parseLine("AMD=D&A;JMP")!!, emptyMap()) shouldBe "1110000000111111"
    }

    test("シンボルを含む命令") {
        val symbolTable = mapOf("LOOP" to 4, "END" to 18)
        assembleText(parseLine("@LOOP")!!, symbolTable) shouldBe "0000000000000100"
        assembleText(parseLine("@END")!!, symbolTable) shouldBe "0000000000010010"
    }

    test("assembleLines - 基本的なアセンブリ") {
        val assembly = sequenceOf(
            "@2",
            "D=A",
            "@3",
            "D=D+A",
            "@0",
            "M=D",
        )

        val result = assembleLines(assembly).toList()

        result shouldBe listOf(
            "0000000000000010",
            "1110110000010000",
            "0000000000000011",
            "1110000010010000",
            "0000000000000000",
            "1110001100001000",
        )
    }

    test("assembleLines - コメントと空行の処理") {
        val assembly = sequenceOf(
            "// This is a comment",
            "@2",
            "D=A  // コメント付き",
            "",
            "   // 空行とコメント",
            "@3",
            "D=D+A",
        )

        val result = assembleLines(assembly).toList()

        result shouldBe listOf(
            "0000000000000010",
            "1110110000010000",
            "0000000000000011",
            "1110000010010000",
        )
    }

    test("assembleLines - ラベルとシンボル") {
        val assembly = """
            @i
            M=1
            @sum
            M=0
        (LOOP)
            @i
            D=M
            @100
            D=D-A
            @END
            D;JGT
            @i
            D=M
            @sum
            M=D+M
            @i
            M=M+1
            @LOOP
            0;JMP
        (END)
            @END
            0;JMP
        """.trimIndent().split("\n").asSequence()
        val result = assembleLines(assembly).toList()

        result shouldBe listOf(
            "0000000000010000",
            "1110111111001000",
            "0000000000010001",
            "1110101010001000",
            "0000000000010000",
            "1111110000010000",
            "0000000001100100",
            "1110010011010000",
            "0000000000010010",
            "1110001100000001",
            "0000000000010000",
            "1111110000010000",
            "0000000000010001",
            "1111000010001000",
            "0000000000010000",
            "1111110111001000",
            "0000000000000100",
            "1110101010000111",
            "0000000000010010",
            "1110101010000111",
        )
    }
})
