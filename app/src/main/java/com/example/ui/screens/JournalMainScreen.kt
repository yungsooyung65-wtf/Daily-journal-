package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.JournalDayEntity
import com.example.data.model.StudyEntryEntity
import com.example.data.model.TaskEntity
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.FooterQuoteSection
import com.example.ui.components.MoodSection
import com.example.ui.components.NotesSection
import com.example.ui.components.PastelHeader
import com.example.ui.components.PinLockScreen
import com.example.ui.components.ProudOfSection
import com.example.ui.components.SettingsBackupDialog
import com.example.ui.components.StudySection
import com.example.ui.components.TasksSection
import com.example.ui.components.TemplateSettingsDialog
import com.example.ui.components.WeekDaySelector
import com.example.ui.components.ProgressComparisonDialog
import com.example.ui.components.WeeklySummaryDialog
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.viewmodel.JournalViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JournalMainScreen(viewModel: JournalViewModel) {
    val context = LocalContext.current

    val isPinLocked by viewModel.isPinLocked.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val currentPin by viewModel.currentPin.collectAsState()

    if (isPinLocked) {
        PinLockScreen(
            onUnlockAttempt = { pin -> viewModel.unlockWithPin(pin) }
        )
        return
    }

    val selectedDate by viewModel.selectedLocalDate.collectAsState()
    val journalDay by viewModel.currentJournalDay.collectAsState()
    val tasks by viewModel.currentTasks.collectAsState()
    val studyEntries by viewModel.currentStudyEntries.collectAsState()
    val templateTasks by viewModel.templateTasks.collectAsState()
    val templateStudySubjects by viewModel.templateStudySubjects.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()

    val comparisonTimeframe by viewModel.comparisonTimeframe.collectAsState()
    val comparisonOffset by viewModel.comparisonOffset.collectAsState()
    val activeComparison by viewModel.activeComparison.collectAsState()
    val yearlyBreakdown by viewModel.yearlyBreakdown.collectAsState()
    val isComparisonLoading by viewModel.isComparisonLoading.collectAsState()

    var showComparisonDialog by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.celebrationEvent.collect {
            showCelebration = true
        }
    }

    val completedTasksCount = tasks.count { it.isDone }
    val day = journalDay ?: JournalDayEntity(dateKey = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

    Scaffold(
        containerColor = PastelPinkBackground,
        modifier = Modifier.testTag("journal_main_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (Title, Tagline, Date switcher, Progress bar)
                PastelHeader(
                    selectedDate = selectedDate,
                    totalTasks = tasks.size,
                    completedTasks = completedTasksCount,
                    onPreviousDay = { viewModel.goToPreviousDay() },
                    onNextDay = { viewModel.goToNextDay() },
                    onDateSelected = { date -> viewModel.selectDate(date) },
                    onGoToToday = { viewModel.goToToday() },
                    onOpenCompareProgress = {
                        viewModel.loadComparisonData()
                        showComparisonDialog = true
                    },
                    onOpenTemplateSettings = { showTemplateDialog = true },
                    onOpenSettings = { showSettingsDialog = true }
                )

                // Day Selector: (M T W T F S S)
                WeekDaySelector(
                    days = viewModel.getWeekDayItems(),
                    onDayClick = { date -> viewModel.selectDate(date) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Main Tasks (Non-Study)
                TasksSection(
                    tasks = tasks,
                    onToggleDone = { task -> viewModel.toggleTaskDone(task) },
                    onCyclePriority = { task -> viewModel.cycleTaskPriority(task) },
                    onUpdateTask = { task -> viewModel.updateTask(task) },
                    onDeleteTask = { id -> viewModel.deleteTask(id) },
                    onMoveTask = { task, up -> viewModel.moveTask(task, up) },
                    onAddTask = { title, icon, priority, addToTemplate ->
                        viewModel.addTask(title, icon, priority, addToTemplate)
                    }
                )

                // Study Tracker
                StudySection(
                    entries = studyEntries,
                    onToggleDone = { entry -> viewModel.toggleStudyDone(entry) },
                    onChangeHours = { entry, delta -> viewModel.changeStudyHours(entry, delta) },
                    onSetHours = { entry, hrs -> viewModel.setStudyHours(entry, hrs) },
                    onUpdateEntry = { entry -> viewModel.updateStudyEntry(entry) },
                    onDeleteEntry = { id -> viewModel.deleteStudyEntry(id) },
                    onAddEntry = { subject, colorHex, hours, addToTemplate ->
                        viewModel.addStudyEntry(subject, colorHex, hours, addToTemplate)
                    }
                )

                // Water Intake
                com.example.ui.components.WaterIntakeSection(
                    intake = day.waterIntake,
                    goal = day.waterGoal,
                    onSetIntake = { intake -> viewModel.setWaterIntake(intake) },
                    onSetGoal = { goal -> viewModel.setWaterGoal(goal) }
                )

                // Mood Today
                MoodSection(
                    selectedMood = day.moodEmoji,
                    moodNote = day.moodNote,
                    onSelectMood = { emoji -> viewModel.setMood(emoji) },
                    onMoodNoteChange = { note -> viewModel.setMoodNote(note) }
                )

                // Notes / Reminders
                NotesSection(
                    notes = day.notes,
                    onNotesChange = { notes -> viewModel.setNotes(notes) }
                )

                // Today I'm Proud Of...
                ProudOfSection(
                    proudOfText = day.proudOf,
                    onProudOfChange = { proudOf -> viewModel.setProudOf(proudOf) }
                )

                // Footer Quote
                FooterQuoteSection(
                    quote = day.footerQuote,
                    onQuoteChange = { quote -> viewModel.setFooterQuote(quote) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Confetti Overlay
            ConfettiOverlay(
                isActive = showCelebration,
                onAnimationEnd = { showCelebration = false }
            )
        }
    }

    // Progress Comparison Dialog (Weekly, Monthly, Yearly)
    if (showComparisonDialog) {
        ProgressComparisonDialog(
            timeframe = comparisonTimeframe,
            offset = comparisonOffset,
            comparison = activeComparison,
            yearlyBreakdown = yearlyBreakdown,
            isLoading = isComparisonLoading,
            onTimeframeChange = { tf -> viewModel.setComparisonTimeframe(tf) },
            onOffsetChange = { delta -> viewModel.changeComparisonOffset(delta) },
            onResetOffset = { viewModel.resetComparisonOffset() },
            onDismiss = { showComparisonDialog = false }
        )
    }

    // Template Settings Dialog
    if (showTemplateDialog) {
        TemplateSettingsDialog(
            templateTasks = templateTasks,
            templateStudySubjects = templateStudySubjects,
            onAddTemplateTask = { title, icon, priority -> viewModel.addTemplateTask(title, icon, priority) },
            onUpdateTemplateTask = { task -> viewModel.updateTemplateTask(task) },
            onDeleteTemplateTask = { id -> viewModel.deleteTemplateTask(id) },
            onAddTemplateStudySubject = { subject, color, hours -> viewModel.addTemplateStudySubject(subject, color, hours) },
            onUpdateTemplateStudySubject = { subject -> viewModel.updateTemplateStudySubject(subject) },
            onDeleteTemplateStudySubject = { id -> viewModel.deleteTemplateStudySubject(id) },
            onDismiss = { showTemplateDialog = false }
        )
    }

    // Settings & Backup Dialog
    if (showSettingsDialog) {
        SettingsBackupDialog(
            isPinEnabled = isPinEnabled,
            currentPin = currentPin,
            onSetPin = { pin, enabled -> viewModel.setPin(pin, enabled) },
            onDisablePin = { viewModel.disablePin() },
            onExportBackup = { viewModel.exportBackupJson() },
            onImportBackup = { json -> viewModel.importBackupJson(json) },
            onDeleteAllData = { viewModel.deleteAllDataAndReset() },
            onShareDaySummary = {
                shareDaySummary(context, selectedDate, day, tasks, studyEntries)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

private fun shareDaySummary(
    context: Context,
    date: LocalDate,
    day: JournalDayEntity,
    tasks: List<TaskEntity>,
    study: List<StudyEntryEntity>
) {
    val dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"))
    val totalTasks = tasks.size
    val doneTasks = tasks.count { it.isDone }
    val totalStudyHours = study.sumOf { it.hours }

    val sb = StringBuilder()
    sb.appendLine("🌸 Daily Task Journal ♥ 🌸")
    sb.appendLine("📅 $dateStr")
    sb.appendLine("\"Small steps ♥ Big dreams\"")
    sb.appendLine()
    sb.appendLine("✨ Tasks Progress: $doneTasks / $totalTasks completed")
    tasks.forEach { t ->
        val status = if (t.isDone) "[✓]" else "[ ]"
        sb.appendLine("$status ${t.icon} ${t.title} (${t.priority})")
    }
    sb.appendLine()

    if (study.isNotEmpty()) {
        sb.appendLine("📚 Study Tracker (Total: ${String.format(Locale.US, "%.1f", totalStudyHours)} hrs):")
        study.forEach { s ->
            val status = if (s.isDone) "[✓]" else "[ ]"
            sb.appendLine("$status ${s.subject}: ${String.format(Locale.US, "%.1f", s.hours)} hrs")
        }
        sb.appendLine()
    }

    sb.appendLine("💧 Water Intake: ${day.waterIntake} / ${day.waterGoal} glasses")

    if (day.moodEmoji.isNotBlank()) {
        sb.appendLine("✨ Mood: ${day.moodEmoji} ${if (day.moodNote.isNotBlank()) "(${day.moodNote})" else ""}")
    }

    if (day.notes.isNotBlank()) {
        sb.appendLine()
        sb.appendLine("📝 Notes / Reminders:")
        sb.appendLine(day.notes)
    }

    if (day.proudOf.isNotBlank()) {
        sb.appendLine()
        sb.appendLine("💖 Today I'm Proud Of:")
        sb.appendLine(day.proudOf)
    }

    sb.appendLine()
    sb.appendLine("♥ ${day.footerQuote.ifBlank { "You're doing better than you think ♥" }}")

    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Share Daily Task Journal"))
}
