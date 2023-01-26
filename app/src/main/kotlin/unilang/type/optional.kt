package unilang.type

import java.util.Optional

fun <T : Any> T.some(): Optional<T> = Optional.of(this)

fun <T : Any> none(): Optional<T> = Optional.empty()

fun <T : Any> T?.optional() = this?.some() ?: none()
