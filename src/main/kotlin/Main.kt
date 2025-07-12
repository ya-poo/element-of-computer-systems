import assembly.assemble
import java.io.File

fun main() {
    val path = File("src/main/resources/hack/Add.asm").absolutePath
    assemble(path)
}
