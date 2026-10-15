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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TemplateStudySubjectEntity
import com.example.data.model.TemplateTaskEntity
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.QuicksandFontFamily
import java.util.Locale

@Composable
fun TemplateSettingsDialog(
    templateTasks: List<TemplateTaskEntity>,
    templateStudySubjects: List<TemplateStudySubjectEntity>,
    onAddTemplateTask: (title: String, icon: String, priority: String) -> Unit,
    onUpdateTemplateTask: (TemplateTaskEntity) -> Unit,
    onDeleteTemplateTask: (Long) -> Unit,
    onAddTemplateStudySubject: (subject: String, colorHex: String, defaultHours: Double) -> Unit,
    onUpdateTemplateStudySubject: (TemplateStudySubjectEntity) -> Unit,
    onDeleteTemplateStudySubject: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddStudyDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TemplateTaskEntity?>(null) }
    var studyToEdit by remember { mutableStateOf<TemplateStudySubjectEntity?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Edit Daily Templates 📋",
                    fontFamily = PatrickHandFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PastelPrimary
                )
                Text(
                    text = "These default tasks & subjects automatically load for each new day.",
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
                    .heightIn(max = 420.dp)
            ) {
                // Tabs: Default Tasks | Default Study
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PastelPinkBackground,
                    contentColor = PastelPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Default Tasks (${templateTasks.size})",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Study Subjects (${templateStudySubjects.size})",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTab == 0) {
                    // Tasks Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PastelPrimary,
                            modifier = Modifier.clickable { showAddTaskDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Default Task", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        templateTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = task.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = task.title,
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 13.sp,
                                    color = CuteDarkText,
                                    modifier = Modifier.weight(1f)
                                )
                                PriorityBadge(priority = task.priority) {
                                    val next = when (task.priority) {
                                        "High" -> "Medium"
                                        "Medium" -> "Low"
                                        else -> "High"
                                    }
                                    onUpdateTemplateTask(task.copy(priority = next))
                                }
                                IconButton(
                                    onClick = { taskToEdit = task },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CuteSubtleText, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteTemplateTask(task.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Study Subjects Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PastelPrimary,
                            modifier = Modifier.clickable { showAddStudyDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Default Subject", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        templateStudySubjects.forEach { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(parseHexColor(s.colorHex))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = s.subject,
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 13.sp,
                                    color = CuteDarkText,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = String.format(Locale.US, "%.1fh", s.defaultHours),
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 12.sp,
                                    color = CuteSubtleText
                                )
                                IconButton(
                                    onClick = { studyToEdit = s },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CuteSubtleText, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { onDeleteTemplateStudySubject(s.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
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
                Text("Done")
            }
        }
    )

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, icon, priority, _ ->
                onAddTemplateTask(title, icon, priority)
                showAddTaskDialog = false
            }
        )
    }

    if (showAddStudyDialog) {
        AddStudySubjectDialog(
            onDismiss = { showAddStudyDialog = false },
            onConfirm = { subject, colorHex, hours, _ ->
                onAddTemplateStudySubject(subject, colorHex, hours)
                showAddStudyDialog = false
            }
        )
    }

    taskToEdit?.let { task ->
        var editTitle by remember { mutableStateOf(task.title) }
        var editIcon by remember { mutableStateOf(task.icon) }
        var editPriority by remember { mutableStateOf(task.priority) }

        AlertDialog(
            onDismissRequest = { taskToEdit = null },
            title = { Text("Edit Template Task", fontFamily = PatrickHandFontFamily, fontSize = 20.sp) },
            text = {
                Column {
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Title") })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Icon:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("🎬", "📸", "🧹", "🍳", "🚶", "📖", "💰", "⭐").forEach { em ->
                            Surface(
                                shape = CircleShape,
                                color = if (editIcon == em) PastelPrimary.copy(alpha = 0.2f) else PastelPinkBackground,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { editIcon = em }
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(em) }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank()) {
                            onUpdateTemplateTask(task.copy(title = editTitle.trim(), icon = editIcon, priority = editPriority))
                        }
                        taskToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { taskToEdit = null }) { Text("Cancel") }
            }
        )
    }

    studyToEdit?.let { subject ->
        var editName by remember { mutableStateOf(subject.subject) }
        var editColor by remember { mutableStateOf(subject.colorHex) }
        var editHoursStr by remember { mutableStateOf(subject.defaultHours.toString()) }

        AlertDialog(
            onDismissRequest = { studyToEdit = null },
            title = { Text("Edit Template Subject", fontFamily = PatrickHandFontFamily, fontSize = 20.sp) },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Subject Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editHoursStr, onValueChange = { editHoursStr = it }, label = { Text("Default Hours") })
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Color Dot:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        studyColorPalette.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .clickable { editColor = hex }
                            ) {
                                if (editColor == hex) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White).align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            val h = editHoursStr.toDoubleOrNull() ?: subject.defaultHours
                            onUpdateTemplateStudySubject(subject.copy(subject = editName.trim(), colorHex = editColor, defaultHours = h))
                        }
                        studyToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { studyToEdit = null }) { Text("Cancel") }
            }
        )
    }
}
