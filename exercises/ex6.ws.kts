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

// endregion

/**
 *   # 6. Parsing Something Useful I – Parsers of Parsers of Parsers
 *
 *  `[Key]: [Value]`
 *
 *  Examples:
 *   - `targetHeight: 100`
 *   - `maxTemperature: 80`
 **/

val nonDigit = Parser { input ->
    input.wrapped.firstOrNull()
        ?.takeIf { !it.isDigit() }
        ?.also { input.advance() }
}












val key = oneOrMore(nonDigit)
val value = oneOrMore(int)

val keyValue = zip(key, value)

keyValue("targetHeight: 100")
keyValue("maxTemperature: 80")
keyValue("Hello Kotlin")
keyValue("abc123")
keyValue("16.18")












