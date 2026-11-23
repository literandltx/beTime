package com.literandltx.betime.data.repository.impl

import com.literandltx.betime.data.dao.LabelDao
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.repository.LabelsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OfflineLabelsRepository(
    private val labelDao: LabelDao
) : LabelsRepository {

    override fun getAllLabelsStream(): Flow<List<Label>> = labelDao.getAllLabels()

    override fun getRootLabelsStream(): Flow<List<Label>> = labelDao.getRootLabels()

    override fun getSubLabelsStream(parentId: Int): Flow<List<Label>> = labelDao.getSubLabels(parentId)

    override fun getLabelStream(id: Int): Flow<Label?> = labelDao.getLabel(id)

    override suspend fun insertLabel(label: Label) {
        validateHierarchy(label)
        labelDao.insert(label)
    }

    override suspend fun insertAll(labels: List<Label>) {
        labels.forEach { validateHierarchy(it) }
        labelDao.insertAll(labels)
    }

    override suspend fun deleteLabel(label: Label) = labelDao.delete(label)

    override suspend fun updateLabel(label: Label) {
        validateHierarchy(label)
        labelDao.update(label)
    }

    override suspend fun updateLabels(labels: List<Label>) {
        labels.forEach { validateHierarchy(it) }
        labelDao.updateLabels(labels)
    }

    private suspend fun validateHierarchy(label: Label) {
        val targetParentId = label.parentId ?: return

        if (label.id != 0 && label.id == targetParentId) {
            throw IllegalArgumentException("Label cannot be its own parent")
        }

        val parent = labelDao.getLabel(targetParentId).firstOrNull()
            ?: throw IllegalArgumentException("Parent label with id=$targetParentId does not exist")

        if (parent.parentId != null) {
            throw IllegalArgumentException("Only a single level of sub-labels is allowed")
        }

        if (label.id != 0 && labelDao.hasSubLabels(label.id)) {
            throw IllegalArgumentException("Label with existing sub-labels cannot become a sub-label")
        }
    }
}
