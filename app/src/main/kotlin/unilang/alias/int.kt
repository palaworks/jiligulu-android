package unilang.alias

typealias i8 = Byte
typealias i16 = Short
typealias i32 = Int
typealias i64 = Long

fun i16(n: i8): i16 = n.toShort()
fun i32(n: i8): i32 = n.toInt()
fun i32(n: i16): i32 = n.toInt()
fun i64(n: i8): i64 = n.toLong()
fun i64(n: i16): i64 = n.toLong()
fun i64(n: i32): i64 = n.toLong()

typealias u8 = UByte
typealias u16 = UShort
typealias u32 = UInt
typealias u64 = ULong

fun u16(n: u8): u16 = n.toUShort()
fun u32(n: u8): u32 = n.toUInt()
fun u32(n: u16): u32 = n.toUInt()
fun u64(n: u8): u64 = n.toULong()
fun u64(n: u16): u64 = n.toULong()
fun u64(n: u32): u64 = n.toULong()

fun i16(n: u8): i16 = n.toShort()
fun i32(n: u16): i32 = n.toInt()
fun i64(n: u32): i64 = n.toLong()
