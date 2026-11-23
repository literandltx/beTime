package com.literandltx.betime.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.literandltx.betime.data.dao.ActivePresetDao
import com.literandltx.betime.data.dao.LabelDao
import com.literandltx.betime.data.dao.TimerOptionDao
import com.literandltx.betime.data.dao.TimerRecordDao
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.model.TimerRecord

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [
        Label::class,
        TimerOption::class,
        ActivePreset::class,
        TimerRecord::class
    ],
    version = 8,
    exportSchema = true,
    autoMigrations = []
)
abstract class TimerDatabase : RoomDatabase() {

    abstract fun labelDao(): LabelDao
    abstract fun timerOptionDao(): TimerOptionDao
    abstract fun activePresetDao(): ActivePresetDao
    abstract fun timerRecordDao(): TimerRecordDao

    companion object {
        @Volatile
        private var Instance: TimerDatabase? = null

        fun getDatabase(context: Context): TimerDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TimerDatabase::class.java,
                    "timer_database"
                )
                    .addCallback(TimerDatabaseCallback())
                    .build()
                    .also { Instance = it }
            }
        }

        private class TimerDatabaseCallback : Callback() {
            override suspend fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)
                val db = Instance ?: return
                populateDatabase(
                    labelDao = db.labelDao(),
                    timerOptionDao = db.timerOptionDao(),
                    activePresetDao = db.activePresetDao()
                )
            }

            private suspend fun populateDatabase(
                labelDao: LabelDao,
                timerOptionDao: TimerOptionDao,
                activePresetDao: ActivePresetDao
            ) {
                val defaultLabels = listOf(
                    Label(name = "Work", colorArgb = Color(0xFF4CAF50).toArgb(), orderIndex = 0),
                    Label(name = "Rest", colorArgb = Color(0xFFFFB300).toArgb(), orderIndex = 1),
                    Label(name = "Study", colorArgb = Color(0xFFFF7043).toArgb(), orderIndex = 2),
                )
                defaultLabels.forEach { labelDao.insert(it) }

                val defaultTimerOptions = listOf(
                    TimerOption(value = 20),
                    TimerOption(value = 45),
                    TimerOption(value = 60),
                )
                defaultTimerOptions.forEach { timerOptionDao.insert(it) }

                activePresetDao.setActivePreset(
                    ActivePreset(
                        id = 1,
                        labelId = 1,
                        timerOptionId = 1
                    )
                )
            }
        }
    }
}
