package utilities

import kotlin.random.Random

private val PREFIX_CHAR_POOL = ('A'..'Z') + ('a'..'z')

private val CHAR_POOL =
    ('0'..'9') + ('A'..'Z') + ('a'..'z')

fun createRandomSymbol(length: Int = 20): String {
    return listOf(
        listOf(PREFIX_CHAR_POOL.random(Random.Default)),
        List(length - 1) { CHAR_POOL.random(Random.Default) },
    ).flatten().joinToString("")
}
