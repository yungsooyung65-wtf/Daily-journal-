package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelLavenderAccent
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily
import com.example.ui.viewmodel.WeeklyStats
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeeklySummaryDialog(
    stats: WeeklyStats?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Weekly Summary 📊",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
                if (stats != null) {
                    val fmt = DateTimeFormatter.ofPattern("MMM d")
                    Text(
                        text = "${stats.startDate.format(fmt)} – ${stats.endDate.format(fmt)}",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 12.sp,
                        color = CuteSubtleText
                    )
                }
            }
        },
        text = {
            if (stats == null) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading weekly stats...", fontFamily = QuicksandFontFamily)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Task Completion Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelPinkBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Tasks Completion",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CuteDarkText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${stats.completedTasks} of ${stats.totalTasks} tasks done",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 13.sp,
                                    color = CuteDarkText
                                )
                                Text(
                                    text = "${stats.taskCompletionPercent}%",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PastelPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Study Hours Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = PastelLavenderAccent.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Study Hours",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CuteDarkText
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1f hrs", stats.totalStudyHours),
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF6741D9)
                                )
                            }

                            if (stats.studyHoursBySubject.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                stats.studyHoursBySubject.forEach { (subject, hrs) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• $subject",
                                            fontFamily = QuicksandFontFamily,
                                            fontSize = 12.sp,
                                            color = CuteDarkText
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.1f hrs", hrs),
                                            fontFamily = QuicksandFontFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = CuteDarkText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Water intake average
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE3FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💧", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Avg Water Intake",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0C8599)
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "%.1f glasses / day", stats.averageWaterGlasses),
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0C8599)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mood Trend
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFF9DB),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Mood Trend ✨",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF8F5B00)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            if (stats.moodCounts.isEmpty()) {
                                Text(
                                    text = "No mood logged this week yet.",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 12.sp,
                                    color = CuteSubtleText
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    stats.moodCounts.forEach { (emoji, count) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = emoji, fontSize = 22.sp)
                                            Text(
                                                text = "${count}d",
                                                fontFamily = QuicksandFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF8F5B00)
                                            )
                                        }
                                    }
                                }
                            }
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
                Text("Close")
            }
        }
    )
}
