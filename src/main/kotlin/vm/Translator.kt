package vm

import java.io.File

fun translate(
    path: String,
    outputFilename: String,
) {
    val directory = File(path)
    val vmFiles = directory.listFiles { file ->
        file.extension == "vm"
    } ?: emptyArray()

    val outputFile = File(directory, outputFilename)
    outputFile.bufferedWriter().use { writer ->
        vmFiles.forEach { vmFile ->
            val filename = vmFile.nameWithoutExtension
            val lines = vmFile.bufferedReader().lineSequence()
            translateLines(filename, lines).forEach { assemblyLine ->
                writer.write(assemblyLine)
                writer.newLine()
            }
        }
    }
}

fun translateLines(
    filename: String,
    lines: Sequence<String>,
): Sequence<String> {
    val normalizedLines = lines.map { line ->
        line
            .replace(Regex("\\s+"), " ")
            .split("//")[0]
            .trim()
    }.filter { it.isNotBlank() }

    return normalizedLines.map { line ->
        val command = parseLine(line)
        command.toHack(filename)
    }
}
