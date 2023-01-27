package data.db

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unilang.alias.i32
import unilang.alias.i64
import unilang.type.none
import unilang.type.some

@Entity(tableName = "app_setting")
data class AppSetting(
    @PrimaryKey val setting_id: i32,
    @ColumnInfo(name = "grpc_host") val grpcHost: String?,
    @ColumnInfo(name = "grpc_port") val grpcPort: i32?,
    @ColumnInfo(name = "pilipala_uid") val pilipalaUid: i64?,
    @ColumnInfo(name = "pilipala_pws") val pilipalaPwd: String?,
)

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_setting WHERE setting_id = 0")
    suspend fun maybe(): AppSetting?

    @Query("SELECT * FROM app_setting WHERE setting_id = 0")
    suspend fun get(): AppSetting

    @Insert
    suspend fun insert(appSetting: AppSetting)

    @Update
    suspend fun update(appSetting: AppSetting)
}

//@TypeConverters(U16Converter::class)
@Database(entities = [AppSetting::class], version = 1)
abstract class AppSettingDatabase : RoomDatabase() {
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        private var db = none<AppSettingDatabase>()

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        suspend fun getDatabase(ctx: Context) =
            withContext(Dispatchers.IO) {
                if (db.isEmpty)
                    synchronized(this) {
                        db =
                            Room.databaseBuilder(
                                ctx,
                                AppSettingDatabase::class.java,
                                "app_setting_database"
                            )
                                .build()
                                .some()
                    }

                val dao = db.get().appSettingDao()
                if (dao.maybe() == null)
                    dao.insert(
                        AppSetting(
                            0,
                            null,
                            null,
                            null,
                            null
                        )
                    )

                db.get()
            }
    }
}
