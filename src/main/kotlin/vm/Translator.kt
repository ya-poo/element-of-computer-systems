package vm

fun translateLines(
    filename: String,
    lines: Sequence<String>,
): Sequence<String> {
    val normalizedLines = lines.map { line ->
        line.replace(Regex("\\s+"), " ").split("//")[0]
    }.filter { it.isNotBlank() }

    return normalizedLines.map { line ->
        val command = parseLine(line)
        command.toHack(filename)
    }
}
