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

fun <Output> zeroOrMore(parser: Parser<Output>) = Parser<List<Output>> {
    val matches = mutableListOf<Output>()
    while (true) {
        val match = parser(it) ?: break
        matches.add(match)
    }

    matches
}

fun <Output> oneOrMore(parser: Parser<Output>) = Parser<List<Output>> {
    val matches = mutableListOf<Output>()
    while (true) {
        val match = parser(it) ?: break
        matches.add(match)
    }

    matches.takeIf { it.isNotEmpty() }
}

val letter = Parser { input ->
    input.wrapped.firstOrNull()
        ?.takeIf { it.isLetter() }
        ?.also { input.advance() }
}

inline fun <A, B> Parser<A>.map(
    crossinline transform: (A) -> B,
): Parser<B> = Parser { invoke(it)?.let(transform) }

data class KeyValue(val key: String, val value: Int)

val value = oneOrMore(int).map { it.joinToString("").toInt() }

// endregion

/**
 *   # 8. Parsing Something Useful III – Parsers of Parsers of Parsers of Parsers
 *
 *  `[Key]: [Value]`
 *
 *  Examples:
 *   - `targetHeight: 100`
 *   - `maxTemperature: 80`
 **/

fun literal(char: Char) = Parser { input ->
    input.wrapped.firstOrNull()
        ?.takeIf { it == char }
        ?.also { input.advance() }
}












val whitesquare = literal('⬜')

whitesquare("Hello⬜Kotlin")
whitesquare("⬜Kotlin")
whitesquare("Hello⬜")










val whitespace = literal(' ')

val separator = zip(
    literal(':'),
    zeroOrMore(whitespace),
).map { Unit }

separator(" ") == Unit
separator("Hello Kotlin") == Unit
separator(":") == Unit
separator(": ") == Unit
separator(":   ") == Unit












val key = zip(
    oneOrMore(letter),
    separator,
).map { (letters, _) ->
    letters.joinToString("")
}

key("height:")
key("targetHeight: ")
key("targetHeight:    ")
key("Hello Kotlin")
key(": 1.618")












val keyValue = zip(key, value)
    .map { (key, value) ->
        KeyValue(key, value)
    }

keyValue("targetHeight: 100")
keyValue("maxTemperature: 80")
keyValue("Hello Kotlin")
keyValue("height:")
keyValue("1.618")












