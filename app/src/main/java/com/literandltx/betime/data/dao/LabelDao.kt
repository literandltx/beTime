package com.literandltx.betime.data.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.literandltx.betime.data.model.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Query("SELECT * FROM labels ORDER BY order_index ASC")
    fun getAllLabels(): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE id = :id")
    fun getLabel(id: Int): Flow<Label?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(label: Label)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(labels: List<Label>)

    @Update
    suspend fun update(label: Label)

    @Update
    suspend fun updateLabels(labels: List<Label>)

    @Delete
    suspend fun delete(label: Label)

}
