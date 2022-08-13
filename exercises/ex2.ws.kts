


/**
 *   # 2. Functional Interfaces
 **/



fun interface Parser<Output> {
    operator fun invoke(input: String): Output?
}












val char = Parser {
    it.firstOrNull()
}

char("Hello Kotlin")
char("1.618")
char("")












val int = Parser {
    it.firstOrNull()?.digitToIntOrNull()
}

int("Hello Kotlin")
int("1.618")
int("")











