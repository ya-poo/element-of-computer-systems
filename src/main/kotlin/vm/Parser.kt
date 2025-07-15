package vm

fun parseLine(line: String): Command {
    return parseSingleCommand(line)
        ?: parseCommandWithOneArg(line)
        ?: parseCommandWithTwoArgs(line)
        ?: throw Exception("Invalid vm code: $line")
}

fun parseSingleCommand(line: String): Command? {
    return when (line) {
        "add" -> Command.Add
        "sub" -> Command.Sub
        "neg" -> Command.Neg
        "eq" -> Command.Eq
        "gt" -> Command.Gt
        "lt" -> Command.Lt
        "and" -> Command.And
        "or" -> Command.Or
        "not" -> Command.Not
        "return" -> Command.Return
        else -> null
    }
}

fun parseCommandWithOneArg(line: String): Command? {
    val args = line.split(" ")
    if (args.size != 2) {
        return null
    }
    return when (args[0]) {
        "label" -> Command.Label(args[1])
        "goto" -> Command.Goto(args[1])
        "if-goto" -> Command.IfGoto(args[1])
        else -> null
    }
}

fun parseCommandWithTwoArgs(line: String): Command? {
    val args = line.split(" ")
    if (args.size != 3) {
        return null
    }
    return when (args[0]) {
        "push" -> {
            val segment = Segment.valueMap[args[1]] ?: return null
            val index = args[2].toIntOrNull() ?: return null
            Command.Push(segment, index)
        }

        "pop" -> {
            val segment = Segment.valueMap[args[1]] ?: return null
            val index = args[2].toIntOrNull() ?: return null
            Command.Pop(segment, index)
        }

        "function" -> {
            val nLocals = args[2].toIntOrNull() ?: return null
            Command.Function(args[1], nLocals)
        }

        "call" -> {
            val nArgs = args[2].toIntOrNull() ?: return null
            Command.Call(args[1], nArgs)
        }

        else -> null
    }
}
