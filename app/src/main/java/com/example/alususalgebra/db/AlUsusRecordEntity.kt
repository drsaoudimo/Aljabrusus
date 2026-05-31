package com.example.alususalgebra.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "al_usus_records")
data class AlUsusRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "NUMBER" or "TEXT"
    val value: String, // e.g., "1260" or textual paragraph
    val resultJson: String, // Stores serialized results mapping
    val timestamp: Long = System.currentTimeMillis()
)
