package unilang.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

val yyMdHmm = "yy-M-d h:mm"

@SuppressLint("SimpleDateFormat")
fun Date.format(format: String) = SimpleDateFormat(format).format(this)
