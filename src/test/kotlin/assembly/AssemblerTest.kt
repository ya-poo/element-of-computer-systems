package me.yapoo.computer.assembly

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AssemblerTest : FunSpec({
    test("A命令のアセンブル") {
        assembleText("@0") shouldBe "0000000000000000"
        assembleText("@1") shouldBe "0000000000000001"
        assembleText("@2") shouldBe "0000000000000010"
        assembleText("@123") shouldBe "0000000001111011"
        assembleText("@32767") shouldBe "0111111111111111"
    }

    test("C命令のアセンブル - 計算のみ") {
        assembleText("0") shouldBe "1110101010000000"
        assembleText("1") shouldBe "1110111111000000"
        assembleText("-1") shouldBe "1110111010000000"
        assembleText("D") shouldBe "1110001100000000"
        assembleText("A") shouldBe "1110110000000000"
        assembleText("M") shouldBe "1111110000000000"
        assembleText("!D") shouldBe "1110001101000000"
        assembleText("!A") shouldBe "1110110001000000"
        assembleText("!M") shouldBe "1111110001000000"
        assembleText("-D") shouldBe "1110001111000000"
        assembleText("-A") shouldBe "1110110011000000"
        assembleText("-M") shouldBe "1111110011000000"
        assembleText("D+1") shouldBe "1110011111000000"
        assembleText("A+1") shouldBe "1110110111000000"
        assembleText("M+1") shouldBe "1111110111000000"
        assembleText("D-1") shouldBe "1110001110000000"
        assembleText("A-1") shouldBe "1110110010000000"
        assembleText("M-1") shouldBe "1111110010000000"
        assembleText("D+A") shouldBe "1110000010000000"
        assembleText("D+M") shouldBe "1111000010000000"
        assembleText("D-A") shouldBe "1110010011000000"
        assembleText("D-M") shouldBe "1111010011000000"
        assembleText("A-D") shouldBe "1110000111000000"
        assembleText("M-D") shouldBe "1111000111000000"
        assembleText("D&A") shouldBe "1110000000000000"
        assembleText("D&M") shouldBe "1111000000000000"
        assembleText("D|A") shouldBe "1110010101000000"
        assembleText("D|M") shouldBe "1111010101000000"
    }

    test("C命令のアセンブル - 代入付き") {
        assembleText("M=1") shouldBe "1110111111001000"
        assembleText("D=A") shouldBe "1110110000010000"
        assembleText("MD=D+1") shouldBe "1110011111011000"
        assembleText("A=D-1") shouldBe "1110001110100000"
        assembleText("AM=D") shouldBe "1110001100101000"
        assembleText("AD=M") shouldBe "1111110000110000"
        assembleText("AMD=D&A") shouldBe "1110000000111000"
    }

    test("C命令のアセンブル - ジャンプ付き") {
        assembleText("D;JGT") shouldBe "1110001100000001"
        assembleText("D;JEQ") shouldBe "1110001100000010"
        assembleText("D;JGE") shouldBe "1110001100000011"
        assembleText("D;JLT") shouldBe "1110001100000100"
        assembleText("D;JNE") shouldBe "1110001100000101"
        assembleText("D;JLE") shouldBe "1110001100000110"
        assembleText("D;JMP") shouldBe "1110001100000111"
        assembleText("0;JMP") shouldBe "1110101010000111"
    }

    test("C命令のアセンブル - 代入とジャンプ付き") {
        assembleText("M=D+1;JGT") shouldBe "1110011111001001"
        assembleText("D=M;JEQ") shouldBe "1111110000010010"
        assembleText("AMD=D&A;JMP") shouldBe "1110000000111111"
    }

    test("空行とコメント") {
        assembleText("") shouldBe null
        assembleText("   ") shouldBe null
        assembleText("// コメント") shouldBe null
        assembleText("   // コメント") shouldBe null
    }

    test("コメント付きの命令") {
        assembleText("@123 // A命令") shouldBe "0000000001111011"
        assembleText("D=A // レジスタ代入") shouldBe "1110110000010000"
        assembleText("D;JMP // 無条件ジャンプ") shouldBe "1110001100000111"
    }

    test("スペースを含む命令") {
        assembleText("@ 123") shouldBe "0000000001111011"
        assembleText("M = 1") shouldBe "1110111111001000"
        assembleText("D ; JGT") shouldBe "1110001100000001"
        assembleText("M = D + 1 ; JGT") shouldBe "1110011111001001"
    }
})
