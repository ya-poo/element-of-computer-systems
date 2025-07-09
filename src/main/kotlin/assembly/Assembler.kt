package me.yapoo.computer.assembly

import java.io.File

fun assembleLines(lines: Sequence<String>): Sequence<String> {
    val normalizedLines = lines.map { line ->
        line.replace(" ", "").split("//")[0]
    }.filter { it.isNotBlank() }

    val commands = normalizedLines.mapNotNull { line ->
        parseLine(line)
    }

    val symbolTable = createSymbolTable(commands)

    return normalizedLines.mapNotNull { line ->
        parseLine(line)?.let { command ->
            assembleText(command, symbolTable)
        }
    }
}

fun assemble(path: String) {
    val outputPath = "${path.substringBeforeLast(".")}.hack"

    File(outputPath).bufferedWriter().use { writer ->
        File(path).useLines { lines ->
            assembleLines(lines).forEach { machineCode ->
                writer.write(machineCode)
                writer.newLine()
            }
        }
    }
}

fun createSymbolTable(commands: Sequence<Command>): Map<String, Int> {
    return buildMap {
        var nextCommandAddress = 0
        commands.forEach { command ->
            if (
                command is Command.A &&
                command.symbol != null &&
                !this.containsKey(command.symbol)
            ) {
                this.put(
                    key = command.symbol,
                    value = this.keys.size + 16,
                )
            } else if (command is Command.L) {
                this.put(
                    key = command.symbol,
                    value = nextCommandAddress,
                )
            }

            if (command !is Command.L) {
                nextCommandAddress++
            }
        }
    }
}

fun assembleText(
    command: Command,
    symbolTable: Map<String, Int>,
): String? {
    return when (command) {
        is Command.A -> command.toHack(symbolTable)
        is Command.C -> command.toHack()
        is Command.L -> null
    }
}
