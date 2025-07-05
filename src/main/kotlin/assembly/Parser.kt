package me.yapoo.computer.assembly

fun parseLine(line: String): Command? {
    val command = line
        .replace(" ", "")
        .split("//")[0]

    if (command.isBlank()) {
        return null
    }

    return parseCommandA(command)
        ?: parseCommandC(command)
}

private fun parseCommandA(command: String): Command.A? {
    if (!command.startsWith("@")) {
        return null
    }
    val value = command.substring(1).toIntOrNull()
    if (value == null) {
        throw Exception("invalid command A: $value")
    }
    return Command.A(value)
}

private fun parseCommandC(command: String): Command.C {
    val dest = if (command.contains("=")) {
        command.split("=")[0]
    } else {
        null
    }?.let(Dest::valueOf)

    val compAndJump = if (command.contains("=")) {
        command.split("=")[1]
    } else {
        command
    }

    val comp = if (compAndJump.contains(";")) {
        compAndJump.split(";")[0]
    } else {
        compAndJump
    }.let(Comp::valueOf)

    val jump = if (compAndJump.contains(";")) {
        compAndJump.split(";")[1]
    } else {
        null
    }?.let(Jump::valueOf)

    return Command.C(dest, comp, jump)
}
