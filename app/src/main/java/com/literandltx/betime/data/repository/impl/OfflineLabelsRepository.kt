package com.literandltx.betime.data.repository.impl

import com.literandltx.betime.data.dao.LabelDao
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.repository.LabelsRepository
import kotlinx.coroutines.flow.Flow

class OfflineLabelsRepository(
    private val labelDao: LabelDao
) : LabelsRepository {

    override fun getAllLabelsStream(): Flow<List<Label>> = labelDao.getAllLabels()

    override fun getLabelStream(id: Int): Flow<Label?> = labelDao.getLabel(id)

    override suspend fun insertLabel(label: Label) = labelDao.insert(label)

    override suspend fun insertAll(labels: List<Label>) = labelDao.insertAll(labels)

    override suspend fun deleteLabel(label: Label) = labelDao.delete(label)

    override suspend fun updateLabel(label: Label) = labelDao.update(label)

    override suspend fun updateLabels(labels: List<Label>) = labelDao.updateLabels(labels)
}
