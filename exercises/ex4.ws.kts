// region Initial Setup

fun interface Parser<Output> {
    operator fun invoke(input: MString): Output?
}

// endregion


/**
 *   # 4. Composing Parsers II – Recording progress
 **/



data class MString(var wrapped: String) {

    fun advance(stride: Int) {
        wrapped = wrapped.drop(stride)
    }
}












val char = Parser { input ->
    input.wrapped.firstOrNull()
        ?.also { input.advance(1) }
}


val int = Parser { input ->
    input.wrapped.firstOrNull()?.digitToIntOrNull()
        ?.also { input.advance(1) }
}












fun <A, B> zip(a: Parser<A>, b: Parser<B>): Parser<Pair<A, B>> = Parser {
    val originalState = it.wrapped // <=== Record state

    val resultA = a(it)
    val resultB = b(it)

    if (resultA != null && resultB != null) {
        Pair(resultA, resultB)
    } else {
        it.wrapped = originalState // <=== Restore if parsing failed
        null
    }
}












val id = zip(char, int)

operator fun <T> Parser<T>.invoke(input: String) = invoke(MString(input))

id("a1")
id("23")
id("c")
id("4")
id("Hello Kotlin")
id("")












