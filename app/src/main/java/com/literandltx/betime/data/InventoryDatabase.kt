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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    version = 7,
    exportSchema = false
)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun labelDao(): LabelDao
    abstract fun timerOptionDao(): TimerOptionDao
    abstract fun activePresetDao(): ActivePresetDao
    abstract fun timerRecordDao(): TimerRecordDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "timer_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .addCallback(InventoryDatabaseCallback())
                    .build()
                    .also { Instance = it }
            }
        }

        private class InventoryDatabaseCallback : Callback() {
            override suspend fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(
                            labelDao = database.labelDao(),
                            timerOptionDao = database.timerOptionDao(),
                            activePresetDao = database.activePresetDao()
                        )
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
                    Label(name = "Study", colorArgb = Color(0xFFFF7043).toArgb(), orderIndex = 2),
                    Label(
                        name = "Uncertain",
                        colorArgb = Color(0xFF90A4AE).toArgb(),
                        orderIndex = 3,
                        isUncertainOnly = true
                    )
                )
                for (label in defaultLabels) {
                    labelDao.insert(label)
                }

                val defaultTimerOptions = listOf(
                    TimerOption(value = 20),
                    TimerOption(value = 45),
                    TimerOption(value = 60),
                )
                for (option in defaultTimerOptions) {
                    timerOptionDao.insert(option)
                }

                val defaultPreset = ActivePreset(
                    id = 1,
                    labelId = 1,
                    timerOptionId = 1
                )
                activePresetDao.setActivePreset(defaultPreset)
            }
        }
    }
}
