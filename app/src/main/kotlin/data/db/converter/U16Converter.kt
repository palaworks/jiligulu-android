package data.db.converter

import androidx.room.TypeConverter
import unilang.alias.i64
import unilang.alias.u16

//TODO this converter can not be compiled successfully, a bug of kapt?
class U16Converter {
    @TypeConverter
    fun from(i64: i64?): u16? = i64?.toUShort()

    @TypeConverter
    fun into(u16: u16?): i64? = u16?.toLong()
}