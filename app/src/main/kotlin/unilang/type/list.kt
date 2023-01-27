package unilang.type

fun <T> List<T>.copyUnless(f: (T) -> Boolean) =
    this.toMutableList()
        .apply { removeIf { f(it) } }

fun <T> List<T>.copyAdd(x: T) =
    this.toMutableList()
        .apply { add(x) }
