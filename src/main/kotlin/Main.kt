import vm.translate
import java.io.File

fun main() {
    val path = File("src/main/resources/08/BasicLoop").absolutePath
    translate(path, "BasicLoop.asm")
}
