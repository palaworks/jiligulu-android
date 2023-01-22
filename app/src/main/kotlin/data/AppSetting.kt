package data

import android.content.Context
import androidx.room.*

@Entity(tableName = "app_setting")
data class AppSetting(
    @PrimaryKey val setting_id: Int,
    @ColumnInfo(name = "grpc_host") val grpcHost: String?,
    @ColumnInfo(name = "grpc_port") val grpcPort: String?,
    @ColumnInfo(name = "pilipala_uid") val pilipalaUid: String?,
    @ColumnInfo(name = "pilipala_pws") val pilipalaPwd: String?,
)

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_setting WHERE setting_id = 0")
    fun get(): AppSetting

    @Query("SELECT * FROM app_setting WHERE setting_id = 0")
    fun maybe(): AppSetting?

    @Insert
    fun insert(appSetting: AppSetting)

    @Update
    fun update(appSetting: AppSetting)
}

@Database(entities = [AppSetting::class], version = 1)
abstract class AppSettingDatabase : RoomDatabase() {
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        private var INSTANCE: AppSettingDatabase? = null
        fun getDatabase(ctx: Context): AppSettingDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            ctx,
                            AppSettingDatabase::class.java,
                            "app_setting_database"
                        )
                            //TODO remove this for better performance
                            .allowMainThreadQueries()
                            .build()
                }
            }

            val dao = INSTANCE!!.appSettingDao()
            if (dao.maybe() == null)
                dao.insert(AppSetting(0, null, null, null, null))

            return INSTANCE!!
        }
    }
}
