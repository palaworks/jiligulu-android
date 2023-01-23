package unilang.type

infix fun <T> T?.nullThen(f: () -> Unit) {
    if (this == null)
        f()
}

infix fun <T> T?.notNullThen(f: (T) -> Unit) {
    if (this != null)
        f(this)
}
