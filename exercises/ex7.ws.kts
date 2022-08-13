// region Initial Setup

data class MString(var wrapped: String) {

    fun advance(stride: Int = 1) {
        wrapped = wrapped.drop(stride)
    }
}

fun interface Parser<Output> {
    operator fun invoke(input: MString): Output?
}

operator fun <T> Parser<T>.invoke(input: String) = invoke(MString(input))

val char = Parser { input ->
    input.wrapped.firstOrNull()
        ?.also { input.advance() }
}

val int = Parser { input ->
    input.wrapped.firstOrNull()?.digitToIntOrNull()
        ?.also { input.advance() }
}

fun <A, B> zip(a: Parser<A>, b: Parser<B>): Parser<Pair<A, B>> = Parser {
    val originalState = it.wrapped

    val resultA = a(it)
    val resultB = b(it)

    if (resultA != null && resultB != null) {
        Pair(resultA, resultB)
    } else {
        it.wrapped = originalState
        null
    }
}

fun <Output> oneOrMore(parser: Parser<Output>) = Parser<List<Output>> {
    val matches = mutableListOf<Output>()
    while (true) {
        val match = parser(it) ?: break
        matches.add(match)
    }

    matches.takeIf { it.isNotEmpty() }
}

val nonDigit = Parser { input ->
    input.wrapped.firstOrNull()
        ?.takeIf { !it.isDigit() }
        ?.also { input.advance() }
}

// endregion

/**
 *   # 7. Parsing Something Useful II – Typesafety
 *
 *  `[Key]: [Value]`
 *
 *  Examples:
 *   - `targetHeight: 100`
 *   - `maxTemperature: 80`
 **/

inline fun <A, B> Parser<A>.map(
    crossinline transform: (A) -> B,
): Parser<B> = Parser { invoke(it)?.let(transform) }












val key = oneOrMore(nonDigit)
    .map { it.joinToString("") }

key("Hello Kotlin")

val value = oneOrMore(int)
    .map { it.joinToString("").toInt() }

value("100")












data class KeyValue(val key: String, val value: Int)

val keyValue = zip(key, value)
    .map { (key, value) ->
        KeyValue(key, value)
    }

keyValue("targetHeight: 100")
keyValue("maxTemperature: 80")
keyValue("Hello Kotlin")
keyValue("abc123")
keyValue("16.18")












