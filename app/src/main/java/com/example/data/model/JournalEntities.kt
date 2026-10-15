package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_days")
data class JournalDayEntity(
    @PrimaryKey
    val dateKey: String, // "YYYY-MM-DD"
    val waterIntake: Int = 0,
    val waterGoal: Int = 8,
    val moodEmoji: String = "",
    val moodNote: String = "",
    val notes: String = "",
    val proudOf: String = "",
    val footerQuote: String = "You're doing better than you think ♥",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String, // "YYYY-MM-DD"
    val title: String,
    val icon: String = "⭐",
    val priority: String = "Medium", // "High", "Medium", "Low"
    val isDone: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "study_entries")
data class StudyEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String, // "YYYY-MM-DD"
    val subject: String,
    val colorHex: String = "#B388FF",
    val hours: Double = 1.0,
    val isDone: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "template_tasks")
data class TemplateTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val icon: String,
    val priority: String = "Medium",
    val orderIndex: Int = 0
)

@Entity(tableName = "template_study_subjects")
data class TemplateStudySubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val colorHex: String = "#B388FF",
    val defaultHours: Double = 1.0,
    val orderIndex: Int = 0
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
