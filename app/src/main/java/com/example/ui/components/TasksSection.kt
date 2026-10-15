package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
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
import com.example.data.model.TaskEntity
import com.example.ui.theme.CuteDarkText
import com.example.ui.theme.CuteSubtleText
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.theme.PastelPrimary
import com.example.ui.theme.PatrickHandFontFamily
import com.example.ui.theme.PriorityHighBg
import com.example.ui.theme.PriorityHighText
import com.example.ui.theme.PriorityLowBg
import com.example.ui.theme.PriorityLowText
import com.example.ui.theme.PriorityMediumBg
import com.example.ui.theme.PriorityMediumText
import com.example.ui.theme.QuicksandFontFamily

@Composable
fun TasksSection(
    tasks: List<TaskEntity>,
    onToggleDone: (TaskEntity) -> Unit,
    onCyclePriority: (TaskEntity) -> Unit,
    onUpdateTask: (TaskEntity) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onMoveTask: (TaskEntity, Boolean) -> Unit,
    onAddTask: (title: String, icon: String, priority: String, addToTemplate: Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToChangeEmoji by remember { mutableStateOf<TaskEntity?>(null) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("tasks_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Main Tasks (Non-Study)",
                        fontFamily = PatrickHandFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CuteDarkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = PastelPinkBackground,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "${tasks.count { it.isDone }}/${tasks.size}",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PastelPrimary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PastelPinkBackground,
                    modifier = Modifier.clickable { showAddDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = PastelPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PastelPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet! Tap '+ Add' to plan your day ✨",
                        fontFamily = QuicksandFontFamily,
                        fontSize = 13.sp,
                        color = CuteSubtleText
                    )
                }
            } else {
                tasks.forEachIndexed { index, task ->
                    TaskRowItem(
                        task = task,
                        isFirst = index == 0,
                        isLast = index == tasks.size - 1,
                        onToggleDone = { onToggleDone(task) },
                        onCyclePriority = { onCyclePriority(task) },
                        onEmojiClick = { taskToChangeEmoji = task },
                        onEditClick = { taskToEdit = task },
                        onDeleteClick = { onDeleteTask(task.id) },
                        onMoveUp = { onMoveTask(task, true) },
                        onMoveDown = { onMoveTask(task, false) }
                    )
                    if (index < tasks.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.8.dp)
                                .background(Color(0xFFF5EDF0))
                        )
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, icon, priority, addToTemplate ->
                onAddTask(title, icon, priority, addToTemplate)
                showAddDialog = false
            }
        )
    }

    // Edit Task Dialog
    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onConfirm = { updatedTask ->
                onUpdateTask(updatedTask)
                taskToEdit = null
            },
            onDelete = {
                onDeleteTask(task.id)
                taskToEdit = null
            }
        )
    }

    // Emoji Picker Dialog
    taskToChangeEmoji?.let { task ->
        EmojiPickerDialog(
            currentEmoji = task.icon,
            onDismiss = { taskToChangeEmoji = null },
            onSelect = { newEmoji ->
                onUpdateTask(task.copy(icon = newEmoji))
                taskToChangeEmoji = null
            }
        )
    }
}

@Composable
private fun TaskRowItem(
    task: TaskEntity,
    isFirst: Boolean,
    isLast: Boolean,
    onToggleDone: () -> Unit,
    onCyclePriority: () -> Unit,
    onEmojiClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon / Emoji button
        Surface(
            shape = CircleShape,
            color = PastelPinkBackground,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .clickable { onEmojiClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = task.icon.ifBlank { "⭐" },
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Title
        Text(
            text = task.title,
            fontFamily = QuicksandFontFamily,
            fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Medium,
            fontSize = 14.sp,
            color = if (task.isDone) CuteSubtleText else CuteDarkText,
            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier
                .weight(1f)
                .clickable { onEditClick() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Priority Badge (Tappable to cycle!)
        PriorityBadge(
            priority = task.priority,
            onClick = onCyclePriority
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Done Checkbox (Custom styled)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (task.isDone) PastelPrimary else Color.Transparent,
            border = BorderStroke(1.5.dp, if (task.isDone) PastelPrimary else Color(0xFFD4C7CC)),
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToggleDone() }
                .testTag("task_check_${task.id}")
        ) {
            if (task.isDone) {
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

        // More options dropdown
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
                    text = { Text("Rename / Edit") },
                    onClick = {
                        showMenu = false
                        onEditClick()
                    }
                )
                if (!isFirst) {
                    DropdownMenuItem(
                        text = { Text("Move Up ⬆") },
                        onClick = {
                            showMenu = false
                            onMoveUp()
                        }
                    )
                }
                if (!isLast) {
                    DropdownMenuItem(
                        text = { Text("Move Down ⬇") },
                        onClick = {
                            showMenu = false
                            onMoveDown()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Delete", color = Color(0xFFD32F2F)) },
                    onClick = {
                        showMenu = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(
    priority: String,
    onClick: () -> Unit
) {
    val (bg, textColor) = when (priority) {
        "High" -> Pair(PriorityHighBg, PriorityHighText)
        "Medium" -> Pair(PriorityMediumBg, PriorityMediumText)
        else -> Pair(PriorityLowBg, PriorityLowText)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = priority,
            fontFamily = QuicksandFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

val commonEmojis = listOf("🎬", "📁", "📸", "🧹", "🍳", "🚶", "📖", "💰", "📞", "💅", "👗", "⭐", "☕", "💻", "🧘", "🎨", "🛒", "🌿", "💡", "🎯")

@Composable
fun EmojiPickerDialog(
    currentEmoji: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pick an Emoji",
                fontFamily = PatrickHandFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    commonEmojis.take(5).forEach { emoji ->
                        EmojiOption(emoji, emoji == currentEmoji) { onSelect(emoji) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    commonEmojis.drop(5).take(5).forEach { emoji ->
                        EmojiOption(emoji, emoji == currentEmoji) { onSelect(emoji) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    commonEmojis.drop(10).take(5).forEach { emoji ->
                        EmojiOption(emoji, emoji == currentEmoji) { onSelect(emoji) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    commonEmojis.drop(15).take(5).forEach { emoji ->
                        EmojiOption(emoji, emoji == currentEmoji) { onSelect(emoji) }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EmojiOption(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) PastelPrimary.copy(alpha = 0.2f) else PastelPinkBackground,
        border = if (isSelected) BorderStroke(1.5.dp, PastelPrimary) else null,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = emoji, fontSize = 20.sp)
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, icon: String, priority: String, addToTemplate: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("⭐") }
    var priority by remember { mutableStateOf("Medium") }
    var addToTemplate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Task ✨",
                fontFamily = PatrickHandFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task name") },
                    placeholder = { Text("e.g. Plan grocery list") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pick Icon:",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("🎬", "📸", "🧹", "🍳", "🚶", "📖", "💰", "⭐").forEach { emoji ->
                        EmojiOption(emoji, emoji == selectedIcon) { selectedIcon = emoji }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Priority:",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CuteDarkText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("High", "Medium", "Low").forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (priority == p) PastelPrimary else PastelPinkBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { priority = p }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p,
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (priority == p) Color.White else CuteDarkText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

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
                        fontSize = 12.sp,
                        color = CuteDarkText
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), selectedIcon, priority, addToTemplate)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PastelPrimary)
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditTaskDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onConfirm: (TaskEntity) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var priority by remember { mutableStateOf(task.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Task",
                fontFamily = PatrickHandFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PastelPrimary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Priority:",
                    fontFamily = QuicksandFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("High", "Medium", "Low").forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (priority == p) PastelPrimary else PastelPinkBackground,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { priority = p }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p,
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (priority == p) Color.White else CuteDarkText
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
                    if (title.isNotBlank()) {
                        onConfirm(task.copy(title = title.trim(), priority = priority))
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
