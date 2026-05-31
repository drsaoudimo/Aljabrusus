package com.example.alususalgebra.db

import kotlinx.coroutines.flow.Flow

class AlUsusRepository(private val dao: AlUsusRecordDao) {
    val allRecords: Flow<List<AlUsusRecordEntity>> = dao.getAllRecords()

    fun getRecordsByType(type: String): Flow<List<AlUsusRecordEntity>> {
        return dao.getRecordsByType(type)
    }

    suspend fun insert(record: AlUsusRecordEntity) {
        dao.insertRecord(record)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteRecordById(id)
    }

    suspend fun clearAll() {
        dao.deleteAllRecords()
    }
}
