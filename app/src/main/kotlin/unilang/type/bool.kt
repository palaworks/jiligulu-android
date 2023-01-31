package unilang.type

inline fun Boolean?.then(f: () -> Unit) = if (this == true) f() else Unit

inline fun Boolean?.whenFalse(f: () -> Unit) = if (this == false) f() else Unit
