package com.rejowan.numberconverter.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rejowan.numberconverter.data.local.database.converter.NumberBaseConverter
import com.rejowan.numberconverter.data.local.database.dao.HistoryDao
import com.rejowan.numberconverter.data.local.database.dao.ProgressDao
import com.rejowan.numberconverter.data.local.database.entity.HistoryEntity
import com.rejowan.numberconverter.data.local.database.entity.ProgressEntity

@Database(
    entities = [HistoryEntity::class, ProgressEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(NumberBaseConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun progressDao(): ProgressDao

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
