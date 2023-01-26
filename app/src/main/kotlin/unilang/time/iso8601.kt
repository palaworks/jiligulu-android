package unilang.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

data class Iso8601(val value: String)

fun Iso8601.toDate(): Date {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val accessor = formatter.parse(this.value)
    return Date.from(Instant.from(accessor))
}

@SuppressLint("SimpleDateFormat")
fun Date.toIso8601(): Iso8601 {
    val tz = TimeZone.getTimeZone("UTC")
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.timeZone = tz
    return Iso8601(df.format(this))
}
