package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

data class MoodOption(
    val emoji: String,
    val label: String,
    val color: Color
)

val moodOptions = listOf(
    MoodOption("🤩", "Great", Color(0xFFFFD43B)),
    MoodOption("😊", "Happy", Color(0xFF69DB7C)),
    MoodOption("😐", "Okay", Color(0xFF74C0FC)),
    MoodOption("😔", "Tired", Color(0xFFFFA94D)),
    MoodOption("😫", "Stressed", Color(0xFFFF8787))
)

@Composable
fun MoodSection(
    selectedMood: String,
    moodNote: String,
    onSelectMood: (String) -> Unit,
    onMoodNoteChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("mood_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Today",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "✨", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5 Mood Emojis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                moodOptions.forEach { option ->
                    val isSelected = selectedMood == option.emoji
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        label = "mood_scale"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .scale(scale)
                            .clickable { onSelectMood(option.emoji) }
                            .testTag("mood_btn_${option.label}")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) option.color.copy(alpha = 0.25f) else Color(0xFFF8F9FA),
                            border = if (isSelected) BorderStroke(2.dp, option.color) else BorderStroke(1.dp, Color(0xFFE9ECEF)),
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = option.emoji,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = option.label,
                            fontFamily = QuicksandFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = if (isSelected) CuteDarkText else CuteSubtleText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Small Mood Notes Box
            OutlinedTextField(
                value = moodNote,
                onValueChange = onMoodNoteChange,
                placeholder = {
                    Text(
                        text = "Why do you feel this way? (optional note)...",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 12.sp,
                        color = CuteSubtleText
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mood_note_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = PastelPinkBackground.copy(alpha = 0.4f),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFF1E3E7),
                    focusedBorderColor = PastelPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )
        }
    }
}
