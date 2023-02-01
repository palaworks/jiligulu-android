package unilang.type

inline fun <T> T?.nullThen(f: () -> Unit) {
    if (this == null)
        f()
}

inline fun <T> T?.notNullThen(f: (T) -> Unit) {
    if (this != null)
        f(this)
}

inline fun <T> T?.orElse(f: () -> T): T = this ?: f()

inline fun <T, R> T?.map(f: (T) -> R): R? = if (this != null) f(this) else null
