// region Initial Setup

fun interface Parser<Output> {
    operator fun invoke(input: String): Output?
}

val char = Parser {
    it.firstOrNull()
}

val int = Parser {
    it.firstOrNull()?.digitToIntOrNull()
}

// endregion


/**
 *   # 3. Composing Parsers I – Zip
 **/


fun <A, B> zip(a: Parser<A>, b: Parser<B>): Parser<Pair<A, B>> = Parser {
    val resultA = a(it)
    val resultB = b(it.drop(1))
    if (resultA != null && resultB != null) {
        Pair(resultA, resultB)
    } else null
}












val id = zip(char, int)

id("a1")
id("23")
id("c")
id("4")
id("Hello Kotlin")
id("")












