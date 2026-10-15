package com.example.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyEntryEntity
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelLavenderAccent
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily
import java.util.Locale

val studyColorPalette = listOf(
    "#FFB7C5", // Pastel Pink
    "#B388FF", // Pastel Purple
    "#80D8FF", // Pastel Cyan
    "#A7F3D0", // Pastel Mint
    "#FFD180", // Pastel Peach
    "#FFF3BF", // Pastel Yellow
    "#FF8A80", // Coral
    "#CFD8DC"  // Lavender grey
)

fun parseHexColor(hex: String, defaultColor: Color = Color(0xFFB388FF)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16).toInt()
        if (clean.length == 6) {
            Color(colorInt or -0x1000000)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun StudySection(
    entries: List<StudyEntryEntity>,
    onToggleDone: (StudyEntryEntity) -> Unit,
    onChangeHours: (StudyEntryEntity, Double) -> Unit,
    onSetHours: (StudyEntryEntity, Double) -> Unit,
    onUpdateEntry: (StudyEntryEntity) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onAddEntry: (subject: String, colorHex: String, hours: Double, addToTemplate: Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<StudyEntryEntity?>(null) }

    val totalHours = entries.sumOf { it.hours }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("study_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Study Tracker",
                        fontFamily = PatrickHandFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CuteDarkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📖",
                        fontSize = 18.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PastelLavenderAccent.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { showAddDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Subject",
                            tint = CuteDarkText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CuteDarkText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No study subjects added yet! Tap '+ Add' to track 📚",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 13.sp,
                        color = CuteSubtleText
                    )
                }
            } else {
                entries.forEachIndexed { index, entry ->
                    StudyRowItem(
                        entry = entry,
                        onToggleDone = { onToggleDone(entry) },
                        onMinus = { onChangeHours(entry, -0.5) },
                        onPlus = { onChangeHours(entry, 0.5) },
                        onEdit = { entryToEdit = entry },
                        onDelete = { onDeleteEntry(entry.id) }
                    )
                    if (index < entries.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.8.dp)
                                .background(Color(0xFFF5EDF0))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Auto-calculated Total Study Time banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PastelPinkBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Study Time",
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CuteDarkText
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f hrs", totalHours),
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PastelPrimary
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddStudySubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { subject, colorHex, hours, addToTemplate ->
                onAddEntry(subject, colorHex, hours, addToTemplate)
                showAddDialog = false
            }
        )
    }

    entryToEdit?.let { entry ->
        EditStudySubjectDialog(
            entry = entry,
            onDismiss = { entryToEdit = null },
            onConfirm = { updated ->
                onUpdateEntry(updated)
                entryToEdit = null
            },
            onDelete = {
                onDeleteEntry(entry.id)
                entryToEdit = null
            }
        )
    }
}

@Composable
private fun StudyRowItem(
    entry: StudyEntryEntity,
    onToggleDone: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored dot
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(parseHexColor(entry.colorHex))
                .clickable { onEdit() }
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Subject name
        Text(
            text = entry.subject,
            fontFamily = QuicksandFontFamily,
            fontWeight = if (entry.isDone) FontWeight.Normal else FontWeight.Medium,
            fontSize = 14.sp,
            color = if (entry.isDone) CuteSubtleText else CuteDarkText,
            textDecoration = if (entry.isDone) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit() }
        )

        // Time quick stepper
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PastelPinkBackground.copy(alpha = 0.6f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = onMinus,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Less hours",
                    tint = CuteDarkText,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = String.format(Locale.US, "%.1fh", entry.hours),
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = CuteDarkText,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(
                onClick = onPlus,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "More hours",
                    tint = CuteDarkText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Done Checkbox
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (entry.isDone) PastelPrimary else Color.Transparent,
            border = BorderStroke(1.5.dp, if (entry.isDone) PastelPrimary else Color(0xFFD4C7CC)),
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToggleDone() }
                .testTag("study_check_${entry.id}")
        ) {
            if (entry.isDone) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // More Menu
        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = CuteSubtleText,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit / Color") },
                    onClick = {
                        showMenu = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = Color(0xFFD32F2F)) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
fun AddStudySubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (subject: String, colorHex: String, hours: Double, addToTemplate: Boolean) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(studyColorPalette[0]) }
    var hoursStr by remember { mutableStateOf("1.0") }
    var addToTemplate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Study Subject 📚",
                fontFamily = PatrickHandFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Topic") },
                    placeholder = { Text("e.g. Graphic Design") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = hoursStr,
                    onValueChange = { hoursStr = it },
                    label = { Text("Hours spent") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pick Dot Color:",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    studyColorPalette.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .clickable { selectedColor = hex }
                        ) {
                            if (selectedColor == hex) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addToTemplate = !addToTemplate }
                ) {
                    Checkbox(
                        checked = addToTemplate,
                        onCheckedChange = { addToTemplate = it },
                        colors = CheckboxDefaults.colors(checkedColor = PastelPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Recurring (add to daily template)",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        val hours = hoursStr.toDoubleOrNull() ?: 1.0
                        onConfirm(subject.trim(), selectedColor, hours, addToTemplate)
                    }
                },
                enabled = subject.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditStudySubjectDialog(
    entry: StudyEntryEntity,
    onDismiss: () -> Unit,
    onConfirm: (StudyEntryEntity) -> Unit,
    onDelete: () -> Unit
) {
    var subject by remember { mutableStateOf(entry.subject) }
    var selectedColor by remember { mutableStateOf(entry.colorHex) }
    var hoursStr by remember { mutableStateOf(entry.hours.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Subject",
                fontFamily = PatrickHandFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hoursStr,
                    onValueChange = { hoursStr = it },
                    label = { Text("Hours") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Color Dot:",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    studyColorPalette.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .clickable { selectedColor = hex }
                        ) {
                            if (selectedColor == hex) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        val hours = hoursStr.toDoubleOrNull() ?: entry.hours
                        onConfirm(entry.copy(subject = subject.trim(), colorHex = selectedColor, hours = hours))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
