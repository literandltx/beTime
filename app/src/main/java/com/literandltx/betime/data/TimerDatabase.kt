package com.literandltx.betime.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.withWriteTransaction
import com.literandltx.betime.data.dao.ActivePresetDao
import com.literandltx.betime.data.dao.DatabaseMetadataDao
import com.literandltx.betime.data.dao.LabelDao
import com.literandltx.betime.data.dao.TimerOptionDao
import com.literandltx.betime.data.dao.TimerRecordDao
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.DatabaseMetadata
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.util.DateTimePickModeConverter

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [
        Label::class,
        TimerOption::class,
        ActivePreset::class,
        TimerRecord::class,
        DatabaseMetadata::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@ColumnTypeConverters(DateTimePickModeConverter::class)
abstract class TimerDatabase : RoomDatabase() {

    abstract fun labelDao(): LabelDao
    abstract fun timerOptionDao(): TimerOptionDao
    abstract fun activePresetDao(): ActivePresetDao
    abstract fun timerRecordDao(): TimerRecordDao
    abstract fun databaseMetadataDao(): DatabaseMetadataDao

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
                    .build()
                    .also { Instance = it }
            }
        }

        suspend fun initialize(context: Context) {
            val db = getDatabase(context)

            db.withWriteTransaction {
                if (!db.databaseMetadataDao().isInitialized()) {
                    populateDatabase(
                        labelDao = db.labelDao(),
                        timerOptionDao = db.timerOptionDao(),
                        activePresetDao = db.activePresetDao()
                    )
                    db.databaseMetadataDao().insert(DatabaseMetadata())
                }
            }
        }

        private suspend fun populateDatabase(
            labelDao: LabelDao,
            timerOptionDao: TimerOptionDao,
            activePresetDao: ActivePresetDao
        ) {
            val defaultLabels = listOf(
                Label(name = "Work", colorArgb = Color(0xFF4CAF50).toArgb(), orderIndex = 0),
                Label(name = "Rest", colorArgb = Color(0xFFFFB300).toArgb(), orderIndex = 1),
                Label(name = "Study", colorArgb = Color(0xFFFF7043).toArgb(), orderIndex = 2)
            )
            defaultLabels.forEach { labelDao.insert(it) }

            val defaultTimerOptions = listOf(
                TimerOption(value = 20),
                TimerOption(value = 45),
                TimerOption(value = 60)
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
