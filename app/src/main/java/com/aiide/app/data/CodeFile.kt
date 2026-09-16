package com.aiide.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_files")
data class CodeFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val content: String,
    val language: String,
    val updatedAt: Long = System.currentTimeMillis()
)
