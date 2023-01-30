package unilang.type

inline fun <T> List<T>.copyUnless(crossinline f: (T) -> Boolean) =
    this.toMutableList()
        .apply { removeIf { f(it) } }

fun <T> List<T>.copyAdd(x: T) =
    this.toMutableList()
        .apply { add(x) }
