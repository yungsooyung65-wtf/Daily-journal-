package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PeriodComparison
import com.example.data.model.PeriodSummary
import com.example.data.model.YearlyMonthlyBreakdown
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelLavenderAccent
import com.example.ui.theme.PastelMintAccent
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily
import com.example.ui.viewmodel.ComparisonTimeframe
import java.util.Locale

@Composable
fun ProgressComparisonDialog(
    timeframe: ComparisonTimeframe,
    offset: Int,
    comparison: PeriodComparison?,
    yearlyBreakdown: List<YearlyMonthlyBreakdown>,
    isLoading: Boolean,
    onTimeframeChange: (ComparisonTimeframe) -> Unit,
    onOffsetChange: (Int) -> Unit,
    onResetOffset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("progress_comparison_dialog"),
        title = {
            Column {
                Text(
                    text = "Compare Progress 📊",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
                Text(
                    text = "Weekly, monthly & yearly growth comparison",
                    fontFamily = QuicksandFontFamily,
                    fontSize = 12.sp,
                    color = CuteSubtleText
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                // Timeframe Tabs (Weekly | Monthly | Yearly)
                TabRow(
                    selectedTabIndex = timeframe.ordinal,
                    containerColor = PastelPinkBackground,
                    contentColor = PastelPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = timeframe == ComparisonTimeframe.WEEKLY,
                        onClick = { onTimeframeChange(ComparisonTimeframe.WEEKLY) },
                        text = {
                            Text(
                                "Weekly",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = timeframe == ComparisonTimeframe.MONTHLY,
                        onClick = { onTimeframeChange(ComparisonTimeframe.MONTHLY) },
                        text = {
                            Text(
                                "Monthly",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = timeframe == ComparisonTimeframe.YEARLY,
                        onClick = { onTimeframeChange(ComparisonTimeframe.YEARLY) },
                        text = {
                            Text(
                                "Yearly",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Period Navigator (< Label >)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onOffsetChange(-1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Earlier Period",
                                tint = CuteDarkText,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val currLabel = comparison?.current?.label ?: "Current"
                            val prevLabel = comparison?.previous?.label ?: "Previous"
                            Text(
                                text = "$currLabel  vs  $prevLabel",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CuteDarkText
                            )
                            if (offset != 0) {
                                Text(
                                    text = "Tap to return to current",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 10.sp,
                                    color = PastelPrimary,
                                    modifier = Modifier.clickable { onResetOffset() }
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOffsetChange(1) },
                            enabled = offset < 0,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Later Period",
                                tint = if (offset < 0) CuteDarkText else Color(0xFFCED4DA),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PastelPrimary, modifier = Modifier.size(32.dp))
                    }
                } else if (comparison != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Task Completion Card
                        ComparisonMetricCard(
                            title = "Tasks Completion",
                            icon = Icons.Default.CheckCircle,
                            iconColor = PastelPrimary,
                            currentValue = "${comparison.current.taskCompletionPercent}%",
                            currentSubtitle = "${comparison.current.completedTasks}/${comparison.current.totalTasks} done",
                            previousValue = "${comparison.previous.taskCompletionPercent}%",
                            previousSubtitle = "${comparison.previous.completedTasks}/${comparison.previous.totalTasks} done",
                            diffText = formatPercentDiff(comparison.taskCompletionDiffPercent),
                            isPositive = comparison.taskCompletionDiffPercent >= 0
                        )

                        // 2. Study Time Card
                        ComparisonMetricCard(
                            title = "Total Study Hours",
                            icon = Icons.Default.MenuBook,
                            iconColor = Color(0xFF7048E8),
                            currentValue = String.format(Locale.US, "%.1f hrs", comparison.current.totalStudyHours),
                            currentSubtitle = "${comparison.current.studyHoursBySubject.size} subjects",
                            previousValue = String.format(Locale.US, "%.1f hrs", comparison.previous.totalStudyHours),
                            previousSubtitle = "${comparison.previous.studyHoursBySubject.size} subjects",
                            diffText = formatHoursDiff(comparison.studyHoursDiff),
                            isPositive = comparison.studyHoursDiff >= 0
                        )

                        // Subject Breakdown if present
                        if (comparison.current.studyHoursBySubject.isNotEmpty() || comparison.previous.studyHoursBySubject.isNotEmpty()) {
                            StudySubjectsComparisonList(
                                currentSubjects = comparison.current.studyHoursBySubject,
                                previousSubjects = comparison.previous.studyHoursBySubject
                            )
                        }

                        // 3. Hydration Card
                        val waterDiff = comparison.waterDiff
                        ComparisonMetricCard(
                            title = "Daily Water Intake",
                            icon = Icons.Default.WaterDrop,
                            iconColor = Color(0xFF1098AD),
                            currentValue = String.format(Locale.US, "%.1f gls/day", comparison.current.averageWaterGlasses),
                            currentSubtitle = "Avg glasses",
                            previousValue = String.format(Locale.US, "%.1f gls/day", comparison.previous.averageWaterGlasses),
                            previousSubtitle = "Avg glasses",
                            diffText = formatGlassesDiff(waterDiff),
                            isPositive = waterDiff >= 0
                        )

                        // 4. Active Days & Consistency Card
                        ComparisonMetricCard(
                            title = "Journaling Consistency",
                            icon = Icons.Default.CalendarToday,
                            iconColor = Color(0xFFF59F00),
                            currentValue = "${comparison.current.activeDaysCount} days",
                            currentSubtitle = "of ${comparison.current.totalDays} days",
                            previousValue = "${comparison.previous.activeDaysCount} days",
                            previousSubtitle = "of ${comparison.previous.totalDays} days",
                            diffText = "${if (comparison.current.activeDaysCount >= comparison.previous.activeDaysCount) "+" else ""}${comparison.current.activeDaysCount - comparison.previous.activeDaysCount} days",
                            isPositive = comparison.current.activeDaysCount >= comparison.previous.activeDaysCount
                        )

                        // 5. Mood Comparison Card
                        MoodComparisonCard(
                            currentSummary = comparison.current,
                            previousSummary = comparison.previous
                        )

                        // 6. For Yearly: Monthly breakdown visual chart
                        if (timeframe == ComparisonTimeframe.YEARLY && yearlyBreakdown.isNotEmpty()) {
                            YearlyMonthlyChartCard(breakdown = yearlyBreakdown)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun ComparisonMetricCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    currentValue: String,
    currentSubtitle: String,
    previousValue: String,
    previousSubtitle: String,
    diffText: String,
    isPositive: Boolean
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CuteDarkText
                    )
                }

                // Diff Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPositive) Color(0xFFE6FCF5) else Color(0xFFFFF5F5)
                ) {
                    Text(
                        text = diffText,
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isPositive) Color(0xFF0CA678) else Color(0xFFE03131),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-column side-by-side comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Current
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PastelPinkBackground,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                        Text(
                            text = currentValue,
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PastelPrimary
                        )
                        Text(
                            text = currentSubtitle,
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Previous
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8F9FA),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Previous",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                        Text(
                            text = previousValue,
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CuteDarkText
                        )
                        Text(
                            text = previousSubtitle,
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudySubjectsComparisonList(
    currentSubjects: Map<String, Double>,
    previousSubjects: Map<String, Double>
) {
    val allSubjectNames = (currentSubjects.keys + previousSubjects.keys).toList()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = PastelLavenderAccent.copy(alpha = 0.25f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Subject Breakdown Comparison",
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF5F3DC4)
            )
            Spacer(modifier = Modifier.height(6.dp))

            allSubjectNames.forEach { subject ->
                val currH = currentSubjects[subject] ?: 0.0
                val prevH = previousSubjects[subject] ?: 0.0
                val diffH = currH - prevH

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $subject",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 11.sp,
                        color = CuteDarkText,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format(Locale.US, "%.1fh vs %.1fh", currH, prevH),
                            fontFamily = QuicksandFontFamily,
                            fontSize = 11.sp,
                            color = CuteSubtleText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (diffH >= 0) "+${String.format(Locale.US, "%.1fh", diffH)}" else String.format(Locale.US, "%.1fh", diffH),
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (diffH >= 0) Color(0xFF2B8A3E) else Color(0xFFC92A2A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodComparisonCard(
    currentSummary: PeriodSummary,
    previousSummary: PeriodSummary
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Mood,
                    contentDescription = null,
                    tint = Color(0xFFF59F00),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mood & Wellbeing",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CuteDarkText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Current Mood
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF9DB),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Top Mood",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = Color(0xFF8F5B00)
                        )
                        Text(
                            text = currentSummary.dominantMood ?: "None",
                            fontSize = 24.sp
                        )
                        val daysCount = currentSummary.moodCounts.values.sum()
                        Text(
                            text = "$daysCount days recorded",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = Color(0xFF8F5B00)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Previous Mood
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8F9FA),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Previous Top Mood",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                        Text(
                            text = previousSummary.dominantMood ?: "None",
                            fontSize = 24.sp
                        )
                        val prevDays = previousSummary.moodCounts.values.sum()
                        Text(
                            text = "$prevDays days recorded",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 10.sp,
                            color = CuteSubtleText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearlyMonthlyChartCard(breakdown: List<YearlyMonthlyBreakdown>) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "12-Month Progress Trend",
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CuteDarkText
            )
            Text(
                text = "Monthly task completion rate %",
                fontFamily = QuicksandFontFamily,
                fontSize = 11.sp,
                color = CuteSubtleText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 12-month mini bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                breakdown.forEach { item ->
                    val percent = item.taskCompletionPercent
                    val barHeightFraction = (percent.toFloat() / 100f).coerceIn(0.08f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (percent > 0) "$percent%" else "-",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (percent > 0) PastelPrimary else CuteSubtleText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .fillMaxHeight(fraction = barHeightFraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (percent > 0) PastelPrimary else Color(0xFFE9ECEF))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.monthName.take(1),
                            fontFamily = QuicksandFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CuteDarkText
                        )
                    }
                }
            }
        }
    }
}

private fun formatPercentDiff(diff: Int): String {
    return when {
        diff > 0 -> "+$diff% 📈"
        diff < 0 -> "$diff% 📉"
        else -> "0% —"
    }
}

private fun formatHoursDiff(diff: Double): String {
    return when {
        diff > 0.05 -> "+${String.format(Locale.US, "%.1f", diff)} hrs 📚"
        diff < -0.05 -> "${String.format(Locale.US, "%.1f", diff)} hrs"
        else -> "0.0 hrs —"
    }
}

private fun formatGlassesDiff(diff: Double): String {
    return when {
        diff > 0.05 -> "+${String.format(Locale.US, "%.1f", diff)} gls 💧"
        diff < -0.05 -> "${String.format(Locale.US, "%.1f", diff)} gls"
        else -> "0.0 gls —"
    }
}
