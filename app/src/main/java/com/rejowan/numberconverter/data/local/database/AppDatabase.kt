package com.rejowan.numberconverter.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rejowan.numberconverter.data.local.database.converter.NumberBaseConverter
import com.rejowan.numberconverter.data.local.database.dao.HistoryDao
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity

@Database(
    entities = [HistoryEntity::class],
    version = 3,
    // Schemas are exported to app/schemas and committed. This has to be on
    // *before* a version ships: without the schema JSON for the released
    // version there is nothing to migrate from, and a real Migration can never
    // be written or tested for it afterwards.
    exportSchema = true
)
@TypeConverters(NumberBaseConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "number_converter_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
