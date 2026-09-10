package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val initialPrompt: String,
    val htmlContent: String,
    val cssContent: String,
    val jsContent: String,
    val manifestContent: String,
    val thinkingLog: String,
    val currentVersion: Int,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_versions",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ProjectVersionEntity(
    @PrimaryKey(autoGenerate = true) val versionId: Long = 0,
    val projectId: String,
    val versionNumber: Int,
    val prompt: String,
    val htmlContent: String,
    val cssContent: String,
    val jsContent: String,
    val manifestContent: String,
    val thinkingSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
