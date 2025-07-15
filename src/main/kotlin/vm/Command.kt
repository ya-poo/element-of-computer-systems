package vm

sealed class Command {
    object Add : Command()

    object Sub : Command()

    object Neg : Command()

    object Eq : Command()

    object Gt : Command()

    object Lt : Command()

    object And : Command()

    object Or : Command()

    object Not : Command()

    data class Push(
        val segment: Segment,
        val index: Int,
    ) : Command()

    data class Pop(
        val segment: Segment,
        val index: Int,
    ) : Command()

    data class Label(
        val symbol: String,
    ) : Command()

    data class Goto(
        val symbol: String,
    ) : Command()

    data class IfGoto(
        val symbol: String,
    ) : Command()

    data class Function(
        val functionName: String,
        val nLocals: Int,
    ) : Command()

    data class Call(
        val functionName: String,
        val nArgs: Int,
    ) : Command()

    object Return : Command()
}

enum class Segment {
    Argument,
    Local,
    Static,
    Constant,
    This,
    That,
    Pointer,
    Temp,
    ;

    companion object {
        val valueMap: Map<String, Segment> = values().associateBy {
            it.name.lowercase()
        }
    }
}
