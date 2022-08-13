


/**
 *   # 1. Introducing `Parser`
 **/



interface Parser<Output> {
    fun parse(input: String): Output?
}












class CharacterParser : Parser<Char> {

    override fun parse(
        input: String,
    ): Char? = input.firstOrNull()
}

CharacterParser().parse("Hello Kotlin")
CharacterParser().parse("1.618")
CharacterParser().parse("")












class IntegerParser : Parser<Int> {

    override fun parse(
        input: String,
    ): Int? = input.firstOrNull()?.digitToIntOrNull()
}

IntegerParser().parse("Hello Kotlin")
IntegerParser().parse("1.618")
IntegerParser().parse("")












