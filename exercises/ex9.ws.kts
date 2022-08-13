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

fun literal(char: Char) = Parser { input ->
    input.wrapped.firstOrNull()
        ?.takeIf { it == char }
        ?.let { input.advance() }
}

val whitespace = literal(' ')

// endregion

/**
 *   # 9. Parsing Something Useful IV – It's all coming together
 *
 *  `[Key]: [Value]`
 *
 *  Examples:
 *   - `targetHeight: 100`
 *   - `maxTemperature: 80`
 **/


fun <A, B, C> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
): Parser<Triple<A, B, C>> = zip(zip(a, b), c)
    .map { (a, c) -> Triple(a.first, a.second, c) }












val separator = zip(
    zeroOrMore(whitespace),
    literal(':'),
    zeroOrMore(whitespace),
).map { Unit }

val key = oneOrMore(letter)
    .map { it.joinToString("") }

val keyValue = zip(
    key,
    separator,
    value,
).map { (key, _, value) ->
    KeyValue(key, value)
}

keyValue("targetHeight: 100")
keyValue("maxTemperature: 80")
keyValue("Hello Kotlin")












fun <A> always(a: Parser<A>): Parser<Unit> = Parser {
    a(it)
    Unit
}

val optionalLineEnding = always(literal('\n'))












val keyValueRow = zip(
    keyValue,
    optionalLineEnding,
).map { (keyValue, _) -> keyValue }

val configParser = zeroOrMore(keyValueRow)

val config = """
targetHeight: 100
maxTemperature: 80
id : 16
""".trimIndent()

configParser(config)












