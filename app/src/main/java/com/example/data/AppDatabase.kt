package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.dao.AlarmDao
import com.example.data.dao.ReminderDao
import com.example.data.dao.WorldCityDao
import com.example.data.model.AlarmEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderSoundType
import com.example.data.model.WorldCityEntity

class Converters {
    @TypeConverter
    fun fromReminderSoundType(value: ReminderSoundType): String = value.name

    @TypeConverter
    fun toReminderSoundType(value: String): ReminderSoundType = runCatching {
        ReminderSoundType.valueOf(value)
    }.getOrDefault(ReminderSoundType.AI_VOICE)
}

@Database(
    entities = [
        AlarmEntity::class,
        WorldCityEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun worldCityDao(): WorldCityDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clock_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
