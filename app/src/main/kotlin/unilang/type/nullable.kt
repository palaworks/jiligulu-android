package unilang.type

infix fun <T> T?.nullThen(f: () -> Unit) {
    if (this == null)
        f()
}

infix fun <T> T?.notNullThen(f: (T) -> Unit) {
    if (this != null)
        f(this)
}

infix fun <T> T?.or(v: T): T = this ?: v

infix fun <T> T?.orElse(f: () -> T): T = this ?: f()

infix fun <T, R> T?.map(f: (T) -> R): R? = if (this != null) f(this) else null
