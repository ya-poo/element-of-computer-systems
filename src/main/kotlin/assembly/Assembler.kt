package assembly

import java.io.File

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

fun assembleLines(lines: Sequence<String>): Sequence<String> {
    val normalizedLines = lines.map { line ->
        line.replace(" ", "").split("//")[0]
    }.filter { it.isNotBlank() }

    val commands = normalizedLines.map { line ->
        parseLine(line)
    }

    val symbolTable = createSymbolTable(commands)

    return normalizedLines.mapNotNull { line ->
        val command = parseLine(line)
        assembleText(command, symbolTable)
    }
}

fun createSymbolTable(commands: Sequence<Command>): Map<String, Int> {
    return buildMap {
        var nextCommandAddress = 0
        var variables = 16

        commands.forEach { command ->
            if (
                command is Command.L &&
                !this.containsKey(command.symbol)
            ) {
                this.put(
                    key = command.symbol,
                    value = nextCommandAddress,
                )
            }

            if (command !is Command.L) {
                nextCommandAddress++
            }
        }
        commands.forEach { command ->
            if (
                command is Command.A &&
                command.symbol != null &&
                !this.containsKey(command.symbol) &&
                !defaultSymbolTable.containsKey(command.symbol)
            ) {
                this.put(
                    key = command.symbol,
                    value = variables,
                )
                variables++
            }
        }
        putAll(defaultSymbolTable)
    }
}

private val defaultSymbolTable: Map<String, Int> = buildMap {
    putAll(
        mapOf(
            "SP" to 0,
            "LCL" to 1,
            "ARG" to 2,
            "THIS" to 3,
            "THAT" to 4,
            "SCREEN" to 16384,
            "KBD" to 24576,
        ),
    )
    (0..15).forEach {
        put("R$it", it)
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
