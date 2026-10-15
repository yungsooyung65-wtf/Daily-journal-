package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily

@Composable
fun WaterIntakeSection(
    intake: Int,
    goal: Int,
    onSetIntake: (Int) -> Unit,
    onSetGoal: (Int) -> Unit
) {
    var showGoalDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("water_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Goal (editable)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Water Intake",
                        fontFamily = PatrickHandFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CuteDarkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "💧", fontSize = 18.sp)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3FAFC),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showGoalDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Goal: $goal glasses",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1098AD)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Goal",
                            tint = Color(0xFF1098AD),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 10 Water Glasses (Two rows of 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..5) {
                    val isFilled = i <= intake
                    WaterGlassItem(
                        glassNumber = i,
                        isFilled = isFilled,
                        onClick = {
                            if (intake == i) {
                                onSetIntake(i - 1)
                            } else {
                                onSetIntake(i)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 6..10) {
                    val isFilled = i <= intake
                    WaterGlassItem(
                        glassNumber = i,
                        isFilled = isFilled,
                        onClick = {
                            if (intake == i) {
                                onSetIntake(i - 1)
                            } else {
                                onSetIntake(i)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress text
            val safeGoal = if (goal <= 0) 8 else goal
            val percent = (intake * 100) / safeGoal
            val encouragement = when {
                intake >= safeGoal -> "🎉 Goal achieved! Super hydrated!"
                intake >= safeGoal / 2 -> "✨ Halfway there! Keep sipping!"
                intake > 0 -> "💧 Good start! Grab another glass!"
                else -> "Tap the glasses to track your hydration!"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$intake / $safeGoal glasses ($percent%)",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF0C8599)
                )

                Text(
                    text = encouragement,
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = CuteSubtleText
                )
            }
        }
    }

    if (showGoalDialog) {
        var inputGoal by remember { mutableStateOf(goal.toString()) }

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = "Set Water Goal 💧",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "How many glasses would you like to drink daily?",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 13.sp,
                        color = CuteDarkText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputGoal,
                        onValueChange = { inputGoal = it },
                        label = { Text("Glasses") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(6, 8, 10, 12).forEach { g ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (inputGoal == g.toString()) PastelPrimary else PastelPinkBackground,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { inputGoal = g.toString() }
                            ) {
                                Text(
                                    text = "$g",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (inputGoal == g.toString()) Color.White else CuteDarkText,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = inputGoal.toIntOrNull() ?: goal
                        if (parsed in 1..20) {
                            onSetGoal(parsed)
                        }
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun WaterGlassItem(
    glassNumber: Int,
    isFilled: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isFilled) Color(0xFF3BC9DB) else Color(0xFFF1F3F5),
        label = "glass_bg"
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, if (isFilled) Color(0xFF15AABF) else Color(0xFFDEE2E6)),
        modifier = Modifier
            .size(46.dp, 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("water_glass_$glassNumber")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Glass $glassNumber",
                tint = if (isFilled) Color.White else Color(0xFFADB5BD),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "$glassNumber",
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isFilled) Color.White else Color(0xFF868E96)
            )
        }
    }
}
