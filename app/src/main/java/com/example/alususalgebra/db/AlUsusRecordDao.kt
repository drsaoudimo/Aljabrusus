package com.example.alususalgebra.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlUsusRecordDao {
    @Query("SELECT * FROM al_usus_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<AlUsusRecordEntity>>

    @Query("SELECT * FROM al_usus_records WHERE type = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: String): Flow<List<AlUsusRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AlUsusRecordEntity)

    @Query("DELETE FROM al_usus_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)

    @Query("DELETE FROM al_usus_records")
    suspend fun deleteAllRecords()
}
