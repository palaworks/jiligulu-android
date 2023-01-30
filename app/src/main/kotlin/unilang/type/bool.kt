package unilang.type

inline fun Boolean?.then(f: () -> Unit) = if (this == true) f() else Unit