package assembly

fun parseLine(line: String): Command {
    return parseCommandA(line)
        ?: parseCommandL(line)
        ?: parseCommandC(line)
}

private fun parseCommandA(command: String): Command.A? {
    if (!command.startsWith("@")) {
        return null
    }
    val valueOrSymbol = command.substring(1)
    val value = command.substring(1).toIntOrNull()
    if (value == null) {
        require(validateSymbol(valueOrSymbol)) {
            "不正な形式のシンボルです: $command"
        }
        return Command.A(
            value = null,
            symbol = valueOrSymbol,
        )
    }
    return Command.A(
        value = value,
        symbol = null,
    )
}

private fun parseCommandL(command: String): Command.L? {
    if (
        !command.startsWith("(") ||
        !command.endsWith(")")
    ) {
        return null
    }

    val symbol = command.substring(1, command.length - 1)
    require(validateSymbol(symbol)) {
        "不正な形式のシンボルです: $command"
    }

    return Command.L(symbol)
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

private val symbolRegex = Regex("[a-zA-Z0-9_.\$:]+")

private fun validateSymbol(symbol: String): Boolean {
    if (!symbol.matches(symbolRegex)) {
        return false
    }
    if (symbol[0].digitToIntOrNull() != null) {
        return false
    }
    return true
}
