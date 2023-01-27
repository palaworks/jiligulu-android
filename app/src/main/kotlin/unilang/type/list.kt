package unilang.type

fun <T> List<T>.copyUnless(f: (T) -> Boolean) =
    this.toMutableList()
        .apply { this.removeIf { f(it) } }