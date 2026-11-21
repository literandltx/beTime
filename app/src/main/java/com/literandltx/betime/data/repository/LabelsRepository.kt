package com.literandltx.betime.data.repository

import com.literandltx.betime.data.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelsRepository {

    fun getAllLabelsStream(): Flow<List<Label>>

    fun getLabelStream(id: Int): Flow<Label?>

    suspend fun insertLabel(label: Label)

    suspend fun insertAll(labels: List<Label>)

    suspend fun deleteLabel(label: Label)

    suspend fun updateLabel(label: Label)

    suspend fun updateLabels(labels: List<Label>)

}
