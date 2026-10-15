package com.example.data.model

import java.time.LocalDate

data class PeriodSummary(
    val label: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalDays: Int,
    val activeDaysCount: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val taskCompletionPercent: Int,
    val totalStudyHours: Double,
    val studyHoursBySubject: Map<String, Double>,
    val averageWaterGlasses: Double,
    val moodCounts: Map<String, Int>,
    val dominantMood: String?
)

data class PeriodComparison(
    val current: PeriodSummary,
    val previous: PeriodSummary,
    val taskCompletionDiffPercent: Int,
    val studyHoursDiff: Double,
    val waterDiff: Double,
    val tasksDoneDiff: Int
)

data class YearlyMonthlyBreakdown(
    val monthNumber: Int,
    val monthName: String,
    val taskCompletionPercent: Int,
    val studyHours: Double,
    val totalTasksDone: Int
)
