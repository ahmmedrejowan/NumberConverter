package com.rejowan.numberconverter.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rejowan.numberconverter.domain.model.NumberBase

@Entity(tableName = "conversion_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val input: String,
    val output: String,
    val fromBase: NumberBase,
    val toBase: NumberBase,
    val timestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
)
