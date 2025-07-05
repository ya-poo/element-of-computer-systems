package me.yapoo.computer.assembly

import java.io.File

fun assemble(path: String) {
    val outputPath = "${path.substringBeforeLast(".")}.hack"
    File(outputPath).bufferedWriter().use { writer ->
        File(path).useLines { lines ->
            lines.forEach { line ->
                val machineCode = assembleText(line)
                if (machineCode != null) {
                    writer.write(machineCode)
                    writer.newLine()
                }
            }
        }
    }
}

fun assembleText(line: String): String? {
    val command = parseLine(line)
    return command?.let {
        when (command) {
            is Command.A -> command.toHack()
            is Command.C -> command.toHack()
        }
    }
}
