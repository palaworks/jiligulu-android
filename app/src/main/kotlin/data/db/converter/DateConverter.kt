package data.db.converter

import androidx.room.TypeConverter
import unilang.alias.i64
import java.util.Date

class DateConverter {
    @TypeConverter
    fun from(i64: i64?): Date? = if (i64 == null) null else Date(i64)

    @TypeConverter
    fun into(date: Date?): i64? = date?.time
}