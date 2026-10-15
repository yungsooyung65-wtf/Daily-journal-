package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.JournalDayEntity
import com.example.data.model.PeriodComparison
import com.example.data.model.PeriodSummary
import com.example.data.model.StudyEntryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TemplateStudySubjectEntity
import com.example.data.model.TemplateTaskEntity
import com.example.data.model.YearlyMonthlyBreakdown
import com.example.data.repository.JournalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class DayTabItem(
    val date: LocalDate,
    val dateKey: String,
    val dayLetter: String, // "M", "T", "W", "T", "F", "S", "S"
    val dayNumber: Int,
    val isSelected: Boolean,
    val isToday: Boolean
)

data class WeeklyStats(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalTasks: Int,
    val completedTasks: Int,
    val taskCompletionPercent: Int,
    val totalStudyHours: Double,
    val studyHoursBySubject: Map<String, Double>,
    val averageWaterGlasses: Double,
    val moodCounts: Map<String, Int>
)

enum class ComparisonTimeframe {
    WEEKLY,
    MONTHLY,
    YEARLY
}

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedLocalDate = MutableStateFlow(LocalDate.now())
    val selectedLocalDate: StateFlow<LocalDate> = _selectedLocalDate.asStateFlow()

    private val _selectedDateKey = MutableStateFlow(LocalDate.now().format(dateFormatter))
    val selectedDateKey: StateFlow<String> = _selectedDateKey.asStateFlow()

    // PIN Lock
    private val _isPinLocked = MutableStateFlow(false)
    val isPinLocked: StateFlow<Boolean> = _isPinLocked.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(false)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _currentPin = MutableStateFlow("")
    val currentPin: StateFlow<String> = _currentPin.asStateFlow()

    // Confetti Event
    private val _celebrationEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val celebrationEvent: SharedFlow<Unit> = _celebrationEvent.asSharedFlow()

    private var previousHighTasksAllDone = false

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = JournalRepository(database.journalDao())

        // Ensure today is initialized
        val todayStr = LocalDate.now().format(dateFormatter)
        viewModelScope.launch {
            repository.ensureDayInitialized(todayStr)
            loadPinSettings()
        }
    }

    private suspend fun loadPinSettings() {
        val enabled = repository.getSetting("pin_enabled") == "true"
        val code = repository.getSetting("pin_code") ?: ""
        _isPinEnabled.value = enabled
        _currentPin.value = code
        if (enabled && code.isNotEmpty()) {
            _isPinLocked.value = true
        }
    }

    // Reactive streams for current selected date
    val currentJournalDay: StateFlow<JournalDayEntity?> = _selectedDateKey
        .flatMapLatest { key -> repository.getJournalDayFlow(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentTasks: StateFlow<List<TaskEntity>> = _selectedDateKey
        .flatMapLatest { key -> repository.getTasksForDateFlow(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudyEntries: StateFlow<List<StudyEntryEntity>> = _selectedDateKey
        .flatMapLatest { key -> repository.getStudyEntriesForDateFlow(key) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templateTasks: StateFlow<List<TemplateTaskEntity>> = repository.getTemplateTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templateStudySubjects: StateFlow<List<TemplateStudySubjectEntity>> = repository.getTemplateStudySubjectsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly summary stats flow
    private val _weeklyStats = MutableStateFlow<WeeklyStats?>(null)
    val weeklyStats: StateFlow<WeeklyStats?> = _weeklyStats.asStateFlow()

    // Progress Comparison
    private val _comparisonTimeframe = MutableStateFlow(ComparisonTimeframe.WEEKLY)
    val comparisonTimeframe: StateFlow<ComparisonTimeframe> = _comparisonTimeframe.asStateFlow()

    private val _comparisonOffset = MutableStateFlow(0)
    val comparisonOffset: StateFlow<Int> = _comparisonOffset.asStateFlow()

    private val _activeComparison = MutableStateFlow<PeriodComparison?>(null)
    val activeComparison: StateFlow<PeriodComparison?> = _activeComparison.asStateFlow()

    private val _yearlyBreakdown = MutableStateFlow<List<YearlyMonthlyBreakdown>>(emptyList())
    val yearlyBreakdown: StateFlow<List<YearlyMonthlyBreakdown>> = _yearlyBreakdown.asStateFlow()

    private val _isComparisonLoading = MutableStateFlow(false)
    val isComparisonLoading: StateFlow<Boolean> = _isComparisonLoading.asStateFlow()

    fun setComparisonTimeframe(timeframe: ComparisonTimeframe) {
        _comparisonTimeframe.value = timeframe
        _comparisonOffset.value = 0
        loadComparisonData()
    }

    fun changeComparisonOffset(delta: Int) {
        _comparisonOffset.value += delta
        loadComparisonData()
    }

    fun resetComparisonOffset() {
        _comparisonOffset.value = 0
        loadComparisonData()
    }

    fun loadComparisonData() {
        viewModelScope.launch {
            _isComparisonLoading.value = true
            val timeframe = _comparisonTimeframe.value
            val offset = _comparisonOffset.value
            val today = LocalDate.now()

            when (timeframe) {
                ComparisonTimeframe.WEEKLY -> {
                    val refDate = today.plusWeeks(offset.toLong())
                    val currStart = refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val currEnd = currStart.plusDays(6)
                    val prevStart = currStart.minusWeeks(1)
                    val prevEnd = prevStart.plusDays(6)

                    val currLabel = if (offset == 0) "This Week" else "${currStart.format(DateTimeFormatter.ofPattern("MMM d"))} – ${currEnd.format(DateTimeFormatter.ofPattern("MMM d"))}"
                    val prevLabel = if (offset == 0) "Last Week" else "${prevStart.format(DateTimeFormatter.ofPattern("MMM d"))} – ${prevEnd.format(DateTimeFormatter.ofPattern("MMM d"))}"

                    _activeComparison.value = repository.getPeriodComparison(
                        currStart, currEnd, currLabel,
                        prevStart, prevEnd, prevLabel
                    )
                    _yearlyBreakdown.value = emptyList()
                }

                ComparisonTimeframe.MONTHLY -> {
                    val refDate = today.plusMonths(offset.toLong())
                    val currStart = refDate.withDayOfMonth(1)
                    val currEnd = currStart.plusMonths(1).minusDays(1)
                    val prevStart = currStart.minusMonths(1)
                    val prevEnd = currStart.minusDays(1)

                    val monthFmt = DateTimeFormatter.ofPattern("MMMM yyyy")
                    val currLabel = currStart.format(monthFmt)
                    val prevLabel = prevStart.format(monthFmt)

                    _activeComparison.value = repository.getPeriodComparison(
                        currStart, currEnd, currLabel,
                        prevStart, prevEnd, prevLabel
                    )
                    _yearlyBreakdown.value = emptyList()
                }

                ComparisonTimeframe.YEARLY -> {
                    val refYear = today.year + offset
                    val currStart = LocalDate.of(refYear, 1, 1)
                    val currEnd = LocalDate.of(refYear, 12, 31)
                    val prevStart = LocalDate.of(refYear - 1, 1, 1)
                    val prevEnd = LocalDate.of(refYear - 1, 12, 31)

                    _activeComparison.value = repository.getPeriodComparison(
                        currStart, currEnd, "$refYear",
                        prevStart, prevEnd, "${refYear - 1}"
                    )
                    _yearlyBreakdown.value = repository.getYearlyMonthlyBreakdown(refYear)
                }
            }
            _isComparisonLoading.value = false
        }
    }

    fun selectDate(date: LocalDate) {
        val key = date.format(dateFormatter)
        _selectedLocalDate.value = date
        _selectedDateKey.value = key
        previousHighTasksAllDone = false
        viewModelScope.launch {
            repository.ensureDayInitialized(key)
        }
    }

    fun goToPreviousDay() {
        selectDate(_selectedLocalDate.value.minusDays(1))
    }

    fun goToNextDay() {
        selectDate(_selectedLocalDate.value.plusDays(1))
    }

    fun goToToday() {
        selectDate(LocalDate.now())
    }

    fun getWeekDayItems(): List<DayTabItem> {
        val current = _selectedLocalDate.value
        val monday = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val today = LocalDate.now()

        val letters = listOf("M", "T", "W", "T", "F", "S", "S")
        return (0..6).map { offset ->
            val d = monday.plusDays(offset.toLong())
            val key = d.format(dateFormatter)
            DayTabItem(
                date = d,
                dateKey = key,
                dayLetter = letters[offset],
                dayNumber = d.dayOfMonth,
                isSelected = d == current,
                isToday = d == today
            )
        }
    }

    // --- Journal Day Edits ---
    fun setWaterIntake(count: Int) {
        viewModelScope.launch {
            repository.updateWaterIntake(_selectedDateKey.value, count)
        }
    }

    fun setWaterGoal(goal: Int) {
        viewModelScope.launch {
            repository.updateWaterGoal(_selectedDateKey.value, goal)
        }
    }

    fun setMood(emoji: String) {
        viewModelScope.launch {
            repository.updateMood(_selectedDateKey.value, emoji)
        }
    }

    fun setMoodNote(note: String) {
        viewModelScope.launch {
            repository.updateMoodNote(_selectedDateKey.value, note)
        }
    }

    fun setNotes(notes: String) {
        viewModelScope.launch {
            repository.updateNotes(_selectedDateKey.value, notes)
        }
    }

    fun setProudOf(proudOf: String) {
        viewModelScope.launch {
            repository.updateProudOf(_selectedDateKey.value, proudOf)
        }
    }

    fun setFooterQuote(quote: String) {
        viewModelScope.launch {
            repository.updateFooterQuote(_selectedDateKey.value, quote)
        }
    }

    // --- Task Actions ---
    fun toggleTaskDone(task: TaskEntity) {
        viewModelScope.launch {
            val newDone = !task.isDone
            repository.setTaskDone(task.id, newDone)
            checkHighPriorityCelebration(task.id, newDone)
        }
    }

    private fun checkHighPriorityCelebration(updatedTaskId: Long, newDone: Boolean) {
        val currentList = currentTasks.value
        val highTasks = currentList.filter { it.priority.equals("High", ignoreCase = true) }
        if (highTasks.isEmpty()) return

        val allHighDone = highTasks.all {
            if (it.id == updatedTaskId) newDone else it.isDone
        }

        if (allHighDone && !previousHighTasksAllDone) {
            _celebrationEvent.tryEmit(Unit)
        }
        previousHighTasksAllDone = allHighDone
    }

    fun cycleTaskPriority(task: TaskEntity) {
        viewModelScope.launch {
            repository.cycleTaskPriority(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun moveTask(task: TaskEntity, directionUp: Boolean) {
        viewModelScope.launch {
            repository.moveTask(task, directionUp)
        }
    }

    fun addTask(title: String, icon: String, priority: String, addToTemplate: Boolean) {
        viewModelScope.launch {
            repository.addTask(_selectedDateKey.value, title, icon, priority, addToTemplate)
        }
    }

    // --- Study Actions ---
    fun toggleStudyDone(entry: StudyEntryEntity) {
        viewModelScope.launch {
            repository.setStudyDone(entry.id, !entry.isDone)
        }
    }

    fun changeStudyHours(entry: StudyEntryEntity, delta: Double) {
        viewModelScope.launch {
            val newHours = maxOf(0.0, entry.hours + delta)
            repository.updateStudyHours(entry.id, newHours)
        }
    }

    fun setStudyHours(entry: StudyEntryEntity, hours: Double) {
        viewModelScope.launch {
            repository.updateStudyHours(entry.id, hours)
        }
    }

    fun updateStudyEntry(entry: StudyEntryEntity) {
        viewModelScope.launch {
            repository.updateStudyEntry(entry)
        }
    }

    fun deleteStudyEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteStudyEntry(id)
        }
    }

    fun addStudyEntry(subject: String, colorHex: String, hours: Double, addToTemplate: Boolean) {
        viewModelScope.launch {
            repository.addStudyEntry(_selectedDateKey.value, subject, colorHex, hours, addToTemplate)
        }
    }

    // --- Template Management ---
    fun addTemplateTask(title: String, icon: String, priority: String) {
        viewModelScope.launch {
            repository.addTemplateTask(title, icon, priority)
        }
    }

    fun updateTemplateTask(task: TemplateTaskEntity) {
        viewModelScope.launch {
            repository.updateTemplateTask(task)
        }
    }

    fun deleteTemplateTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTemplateTask(id)
        }
    }

    fun addTemplateStudySubject(subject: String, colorHex: String, defaultHours: Double) {
        viewModelScope.launch {
            repository.addTemplateStudySubject(subject, colorHex, defaultHours)
        }
    }

    fun updateTemplateStudySubject(subject: TemplateStudySubjectEntity) {
        viewModelScope.launch {
            repository.updateTemplateStudySubject(subject)
        }
    }

    fun deleteTemplateStudySubject(id: Long) {
        viewModelScope.launch {
            repository.deleteTemplateStudySubject(id)
        }
    }

    // --- PIN Management ---
    fun unlockWithPin(pin: String): Boolean {
        return if (pin == _currentPin.value) {
            _isPinLocked.value = false
            true
        } else {
            false
        }
    }

    fun setPin(code: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setSetting("pin_code", code)
            repository.setSetting("pin_enabled", if (enabled) "true" else "false")
            _currentPin.value = code
            _isPinEnabled.value = enabled
            _isPinLocked.value = false
        }
    }

    fun disablePin() {
        viewModelScope.launch {
            repository.setSetting("pin_enabled", "false")
            _isPinEnabled.value = false
            _isPinLocked.value = false
        }
    }

    // --- Weekly Stats Calculation ---
    fun loadWeeklyStats() {
        val current = _selectedLocalDate.value
        val monday = current.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)
        val startKey = monday.format(dateFormatter)
        val endKey = sunday.format(dateFormatter)

        viewModelScope.launch {
            val daysList = repository.getJournalDaysInRangeFlow(startKey, endKey)
            val tasksList = repository.getTasksInRangeFlow(startKey, endKey)
            val studyList = repository.getStudyEntriesInRangeFlow(startKey, endKey)

            // Collect single snapshot
            kotlinx.coroutines.flow.combine(daysList, tasksList, studyList) { days, tasks, studies ->
                val totalT = tasks.size
                val doneT = tasks.count { it.isDone }
                val taskPercent = if (totalT > 0) (doneT * 100) / totalT else 0

                val totalStudy = studies.sumOf { it.hours }
                val studyMap = studies.groupBy { it.subject }
                    .mapValues { entry -> entry.value.sumOf { it.hours } }

                val avgWater = if (days.isNotEmpty()) {
                    days.sumOf { it.waterIntake }.toDouble() / 7.0
                } else 0.0

                val moodMap = days.filter { it.moodEmoji.isNotBlank() }
                    .groupBy { it.moodEmoji }
                    .mapValues { it.value.size }

                WeeklyStats(
                    startDate = monday,
                    endDate = sunday,
                    totalTasks = totalT,
                    completedTasks = doneT,
                    taskCompletionPercent = taskPercent,
                    totalStudyHours = totalStudy,
                    studyHoursBySubject = studyMap,
                    averageWaterGlasses = avgWater,
                    moodCounts = moodMap
                )
            }.collect { stats ->
                _weeklyStats.value = stats
            }
        }
    }

    // --- Backup & Restore ---
    suspend fun exportBackupJson(): String {
        return repository.exportDataAsJson()
    }

    suspend fun importBackupJson(json: String): Boolean {
        val success = repository.importDataFromJson(json)
        if (success) {
            repository.ensureDayInitialized(_selectedDateKey.value)
        }
        return success
    }

    fun deleteAllDataAndReset() {
        viewModelScope.launch {
            repository.resetAllWithDefaults()
            repository.ensureDayInitialized(_selectedDateKey.value)
            _isPinEnabled.value = false
            _isPinLocked.value = false
            _currentPin.value = ""
        }
    }
}
