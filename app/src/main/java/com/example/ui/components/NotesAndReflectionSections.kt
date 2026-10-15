package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PaperBgColor
import com.example.ui.theme.PaperLineColor
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PastelTertiary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily

@Composable
fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("notes_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notes / Reminders",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "📝", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lined notepad container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaperBgColor)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 28.dp)
                ) {
                    val lineHeight = 28.dp.toPx()
                    var y = lineHeight
                    while (y < size.height) {
                        drawLine(
                            color = PaperLineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += lineHeight
                    }
                }

                TextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = {
                        Text(
                            text = "Jot down tasks, links, reminders, or quick notes...",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 13.sp,
                            color = CuteSubtleText.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("notes_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = QuicksandFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 28.sp,
                        color = CuteDarkText
                    )
                )
            }
        }
    }
}

@Composable
fun ProudOfSection(
    proudOfText: String,
    onProudOfChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("proud_of_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today I'm Proud Of...",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Love",
                    tint = PastelPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelPinkBackground.copy(alpha = 0.45f))
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 28.dp)
                ) {
                    val lineHeight = 28.dp.toPx()
                    var y = lineHeight
                    while (y < size.height) {
                        drawLine(
                            color = Color(0xFFF1D8DF),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += lineHeight
                    }
                }

                TextField(
                    value = proudOfText,
                    onValueChange = onProudOfChange,
                    placeholder = {
                        Text(
                            text = "Write a win, a kind moment, or progress you made...",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 13.sp,
                            color = CuteSubtleText.copy(alpha = 0.8f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("proud_of_input"),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = QuicksandFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 28.sp,
                        color = CuteDarkText
                    )
                )
            }
        }
    }
}

@Composable
fun FooterQuoteSection(
    quote: String,
    onQuoteChange: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PastelPinkBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { showEditDialog = true }
            .testTag("footer_quote_section")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = quote.ifBlank { "You're doing better than you think ♥" },
                fontFamily = PatrickHandFontFamily,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Quote",
                tint = PastelPrimary.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }

    if (showEditDialog) {
        var inputQuote by remember { mutableStateOf(quote) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Personalize Footer Quote ✨",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputQuote,
                        onValueChange = { inputQuote = it },
                        label = { Text("Daily Inspiration Quote") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputQuote.isNotBlank()) {
                            onQuoteChange(inputQuote.trim())
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
