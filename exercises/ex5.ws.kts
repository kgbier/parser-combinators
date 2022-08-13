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

// endregion


/**
 *   # 5. Composing Parsers III – Repetition
 **/


fun <Output> oneOrMore(parser: Parser<Output>) = Parser<List<Output>> {
    val matches = mutableListOf<Output>()
    while (true) {
        val match = parser(it) ?: break
        matches.add(match)
    }

    matches.takeIf { it.isNotEmpty() }
}












val characters = oneOrMore(char)

characters("Hello Kotlin")
characters("abc123")

val digits = oneOrMore(int)

digits("Hello Kotlin")
digits("16.18")












