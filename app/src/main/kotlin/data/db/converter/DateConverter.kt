package data.db.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?) = if (dateLong == null) null else Date(dateLong)

    @TypeConverter
    fun fromDate(date: Date?) = date?.time
}