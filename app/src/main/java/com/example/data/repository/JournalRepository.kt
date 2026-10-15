package com.example.data.repository

import com.example.data.db.JournalDao
import com.example.data.model.AppSettingEntity
import com.example.data.model.JournalDayEntity
import com.example.data.model.StudyEntryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TemplateStudySubjectEntity
import com.example.data.model.TemplateTaskEntity
import com.example.data.model.PeriodComparison
import com.example.data.model.PeriodSummary
import com.example.data.model.YearlyMonthlyBreakdown
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class JournalRepository(private val dao: JournalDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getJournalDayFlow(dateKey: String): Flow<JournalDayEntity?> =
        dao.getJournalDayFlow(dateKey)

    fun getTasksForDateFlow(dateKey: String): Flow<List<TaskEntity>> =
        dao.getTasksForDateFlow(dateKey)

    fun getStudyEntriesForDateFlow(dateKey: String): Flow<List<StudyEntryEntity>> =
        dao.getStudyEntriesForDateFlow(dateKey)

    fun getTemplateTasksFlow(): Flow<List<TemplateTaskEntity>> =
        dao.getTemplateTasksFlow()

    fun getTemplateStudySubjectsFlow(): Flow<List<TemplateStudySubjectEntity>> =
        dao.getTemplateStudySubjectsFlow()

    fun getJournalDaysInRangeFlow(startDate: String, endDate: String): Flow<List<JournalDayEntity>> =
        dao.getJournalDaysInRangeFlow(startDate, endDate)

    fun getTasksInRangeFlow(startDate: String, endDate: String): Flow<List<TaskEntity>> =
        dao.getTasksInRangeFlow(startDate, endDate)

    fun getStudyEntriesInRangeFlow(startDate: String, endDate: String): Flow<List<StudyEntryEntity>> =
        dao.getStudyEntriesInRangeFlow(startDate, endDate)

    fun getSettingFlow(key: String): Flow<String?> =
        dao.getSettingFlow(key)

    suspend fun getSetting(key: String): String? =
        dao.getSetting(key)

    suspend fun setSetting(key: String, value: String) {
        dao.setSetting(AppSettingEntity(key = key, value = value))
    }

    /**
     * Initializes a day's records if they don't exist yet, copying from templates.
     */
    suspend fun ensureDayInitialized(dateKey: String) {
        var day = dao.getJournalDay(dateKey)
        if (day == null) {
            day = JournalDayEntity(dateKey = dateKey)
            dao.upsertJournalDay(day)
        }

        val existingTasks = dao.getTasksForDate(dateKey)
        if (existingTasks.isEmpty()) {
            val templateTasks = dao.getTemplateTasks()
            if (templateTasks.isNotEmpty()) {
                val tasks = templateTasks.map { t ->
                    TaskEntity(
                        dateKey = dateKey,
                        title = t.title,
                        icon = t.icon,
                        priority = t.priority,
                        isDone = false,
                        orderIndex = t.orderIndex
                    )
                }
                dao.insertTasks(tasks)
            }
        }

        val existingStudy = dao.getStudyEntriesForDate(dateKey)
        if (existingStudy.isEmpty()) {
            val templateStudy = dao.getTemplateStudySubjects()
            if (templateStudy.isNotEmpty()) {
                val entries = templateStudy.map { s ->
                    StudyEntryEntity(
                        dateKey = dateKey,
                        subject = s.subject,
                        colorHex = s.colorHex,
                        hours = s.defaultHours,
                        isDone = false,
                        orderIndex = s.orderIndex
                    )
                }
                dao.insertStudyEntries(entries)
            }
        }
    }

    suspend fun updateJournalDay(day: JournalDayEntity) {
        dao.upsertJournalDay(day)
    }

    suspend fun updateWaterIntake(dateKey: String, intake: Int) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(waterIntake = intake, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateWaterGoal(dateKey: String, goal: Int) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(waterGoal = goal, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateMood(dateKey: String, emoji: String) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        val newEmoji = if (current.moodEmoji == emoji) "" else emoji
        dao.upsertJournalDay(current.copy(moodEmoji = newEmoji, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateMoodNote(dateKey: String, note: String) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(moodNote = note, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateNotes(dateKey: String, notes: String) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(notes = notes, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateProudOf(dateKey: String, proudOf: String) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(proudOf = proudOf, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun updateFooterQuote(dateKey: String, quote: String) {
        val current = dao.getJournalDay(dateKey) ?: JournalDayEntity(dateKey = dateKey)
        dao.upsertJournalDay(current.copy(footerQuote = quote, lastUpdated = System.currentTimeMillis()))
    }

    // --- Task actions ---
    suspend fun setTaskDone(taskId: Long, isDone: Boolean) {
        dao.setTaskDone(taskId, isDone)
    }

    suspend fun cycleTaskPriority(task: TaskEntity) {
        val nextPriority = when (task.priority) {
            "High" -> "Medium"
            "Medium" -> "Low"
            else -> "High"
        }
        dao.setTaskPriority(task.id, nextPriority)
    }

    suspend fun updateTask(task: TaskEntity) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteTask(taskId)
    }

    suspend fun addTask(
        dateKey: String,
        title: String,
        icon: String,
        priority: String,
        addToTemplate: Boolean
    ) {
        val tasks = dao.getTasksForDate(dateKey)
        val maxOrder = tasks.maxOfOrNull { it.orderIndex } ?: -1
        val newTask = TaskEntity(
            dateKey = dateKey,
            title = title,
            icon = icon,
            priority = priority,
            isDone = false,
            orderIndex = maxOrder + 1
        )
        dao.insertTask(newTask)

        if (addToTemplate) {
            val templates = dao.getTemplateTasks()
            val maxTemplateOrder = templates.maxOfOrNull { it.orderIndex } ?: -1
            dao.insertTemplateTask(
                TemplateTaskEntity(
                    title = title,
                    icon = icon,
                    priority = priority,
                    orderIndex = maxTemplateOrder + 1
                )
            )
        }
    }

    suspend fun moveTask(task: TaskEntity, directionUp: Boolean) {
        val tasks = dao.getTasksForDate(task.dateKey).toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index == -1) return
        val targetIndex = if (directionUp) index - 1 else index + 1
        if (targetIndex in 0 until tasks.size) {
            val currentOrder = tasks[index].orderIndex
            val targetOrder = tasks[targetIndex].orderIndex
            dao.updateTask(tasks[index].copy(orderIndex = targetOrder))
            dao.updateTask(tasks[targetIndex].copy(orderIndex = currentOrder))
        }
    }

    // --- Study entries actions ---
    suspend fun setStudyDone(id: Long, isDone: Boolean) {
        dao.setStudyEntryDone(id, isDone)
    }

    suspend fun updateStudyHours(id: Long, hours: Double) {
        val rounded = String.format(java.util.Locale.US, "%.1f", maxOf(0.0, hours)).toDoubleOrNull() ?: 0.0
        dao.setStudyEntryHours(id, rounded)
    }

    suspend fun updateStudyEntry(entry: StudyEntryEntity) {
        dao.updateStudyEntry(entry)
    }

    suspend fun deleteStudyEntry(id: Long) {
        dao.deleteStudyEntry(id)
    }

    suspend fun addStudyEntry(
        dateKey: String,
        subject: String,
        colorHex: String,
        hours: Double,
        addToTemplate: Boolean
    ) {
        val entries = dao.getStudyEntriesForDate(dateKey)
        val maxOrder = entries.maxOfOrNull { it.orderIndex } ?: -1
        dao.insertStudyEntry(
            StudyEntryEntity(
                dateKey = dateKey,
                subject = subject,
                colorHex = colorHex,
                hours = hours,
                isDone = false,
                orderIndex = maxOrder + 1
            )
        )

        if (addToTemplate) {
            val templates = dao.getTemplateStudySubjects()
            val maxTemplateOrder = templates.maxOfOrNull { it.orderIndex } ?: -1
            dao.insertTemplateStudySubject(
                TemplateStudySubjectEntity(
                    subject = subject,
                    colorHex = colorHex,
                    defaultHours = hours,
                    orderIndex = maxTemplateOrder + 1
                )
            )
        }
    }

    // --- Template CRUD ---
    suspend fun addTemplateTask(title: String, icon: String, priority: String) {
        val templates = dao.getTemplateTasks()
        val maxOrder = templates.maxOfOrNull { it.orderIndex } ?: -1
        dao.insertTemplateTask(
            TemplateTaskEntity(
                title = title,
                icon = icon,
                priority = priority,
                orderIndex = maxOrder + 1
            )
        )
    }

    suspend fun updateTemplateTask(task: TemplateTaskEntity) {
        dao.updateTemplateTask(task)
    }

    suspend fun deleteTemplateTask(id: Long) {
        dao.deleteTemplateTask(id)
    }

    suspend fun addTemplateStudySubject(subject: String, colorHex: String, defaultHours: Double) {
        val templates = dao.getTemplateStudySubjects()
        val maxOrder = templates.maxOfOrNull { it.orderIndex } ?: -1
        dao.insertTemplateStudySubject(
            TemplateStudySubjectEntity(
                subject = subject,
                colorHex = colorHex,
                defaultHours = defaultHours,
                orderIndex = maxOrder + 1
            )
        )
    }

    suspend fun updateTemplateStudySubject(subject: TemplateStudySubjectEntity) {
        dao.updateTemplateStudySubject(subject)
    }

    suspend fun deleteTemplateStudySubject(id: Long) {
        dao.deleteTemplateStudySubject(id)
    }

    // --- Backup & Restore ---
    suspend fun exportDataAsJson(): String {
        val root = JSONObject()
        root.put("app", "DailyTaskJournal")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val daysArray = JSONArray()
        for (day in dao.getAllJournalDays()) {
            val obj = JSONObject()
            obj.put("dateKey", day.dateKey)
            obj.put("waterIntake", day.waterIntake)
            obj.put("waterGoal", day.waterGoal)
            obj.put("moodEmoji", day.moodEmoji)
            obj.put("moodNote", day.moodNote)
            obj.put("notes", day.notes)
            obj.put("proudOf", day.proudOf)
            obj.put("footerQuote", day.footerQuote)
            obj.put("lastUpdated", day.lastUpdated)
            daysArray.put(obj)
        }
        root.put("journalDays", daysArray)

        val tasksArray = JSONArray()
        for (t in dao.getAllTasks()) {
            val obj = JSONObject()
            obj.put("dateKey", t.dateKey)
            obj.put("title", t.title)
            obj.put("icon", t.icon)
            obj.put("priority", t.priority)
            obj.put("isDone", t.isDone)
            obj.put("orderIndex", t.orderIndex)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val studyArray = JSONArray()
        for (s in dao.getAllStudyEntries()) {
            val obj = JSONObject()
            obj.put("dateKey", s.dateKey)
            obj.put("subject", s.subject)
            obj.put("colorHex", s.colorHex)
            obj.put("hours", s.hours)
            obj.put("isDone", s.isDone)
            obj.put("orderIndex", s.orderIndex)
            studyArray.put(obj)
        }
        root.put("studyEntries", studyArray)

        val templateTasksArray = JSONArray()
        for (tt in dao.getTemplateTasks()) {
            val obj = JSONObject()
            obj.put("title", tt.title)
            obj.put("icon", tt.icon)
            obj.put("priority", tt.priority)
            obj.put("orderIndex", tt.orderIndex)
            templateTasksArray.put(obj)
        }
        root.put("templateTasks", templateTasksArray)

        val templateStudyArray = JSONArray()
        for (ts in dao.getTemplateStudySubjects()) {
            val obj = JSONObject()
            obj.put("subject", ts.subject)
            obj.put("colorHex", ts.colorHex)
            obj.put("defaultHours", ts.defaultHours)
            obj.put("orderIndex", ts.orderIndex)
            templateStudyArray.put(obj)
        }
        root.put("templateStudySubjects", templateStudyArray)

        return root.toString(2)
    }

    suspend fun importDataFromJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)

            // Import journal days
            val daysArray = root.optJSONArray("journalDays")
            if (daysArray != null) {
                for (i in 0 until daysArray.length()) {
                    val obj = daysArray.getJSONObject(i)
                    val day = JournalDayEntity(
                        dateKey = obj.getString("dateKey"),
                        waterIntake = obj.optInt("waterIntake", 0),
                        waterGoal = obj.optInt("waterGoal", 8),
                        moodEmoji = obj.optString("moodEmoji", ""),
                        moodNote = obj.optString("moodNote", ""),
                        notes = obj.optString("notes", ""),
                        proudOf = obj.optString("proudOf", ""),
                        footerQuote = obj.optString("footerQuote", "You're doing better than you think ♥"),
                        lastUpdated = obj.optLong("lastUpdated", System.currentTimeMillis())
                    )
                    dao.upsertJournalDay(day)
                }
            }

            // Import tasks
            val tasksArray = root.optJSONArray("tasks")
            if (tasksArray != null) {
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    val task = TaskEntity(
                        dateKey = obj.getString("dateKey"),
                        title = obj.getString("title"),
                        icon = obj.optString("icon", "⭐"),
                        priority = obj.optString("priority", "Medium"),
                        isDone = obj.optBoolean("isDone", false),
                        orderIndex = obj.optInt("orderIndex", i)
                    )
                    dao.insertTask(task)
                }
            }

            // Import study entries
            val studyArray = root.optJSONArray("studyEntries")
            if (studyArray != null) {
                for (i in 0 until studyArray.length()) {
                    val obj = studyArray.getJSONObject(i)
                    val entry = StudyEntryEntity(
                        dateKey = obj.getString("dateKey"),
                        subject = obj.getString("subject"),
                        colorHex = obj.optString("colorHex", "#B388FF"),
                        hours = obj.optDouble("hours", 1.0),
                        isDone = obj.optBoolean("isDone", false),
                        orderIndex = obj.optInt("orderIndex", i)
                    )
                    dao.insertStudyEntry(entry)
                }
            }

            // Import templates if present
            val templateTasksArray = root.optJSONArray("templateTasks")
            if (templateTasksArray != null && templateTasksArray.length() > 0) {
                dao.clearTemplateTasks()
                for (i in 0 until templateTasksArray.length()) {
                    val obj = templateTasksArray.getJSONObject(i)
                    dao.insertTemplateTask(
                        TemplateTaskEntity(
                            title = obj.getString("title"),
                            icon = obj.optString("icon", "⭐"),
                            priority = obj.optString("priority", "Medium"),
                            orderIndex = obj.optInt("orderIndex", i)
                        )
                    )
                }
            }

            val templateStudyArray = root.optJSONArray("templateStudySubjects")
            if (templateStudyArray != null && templateStudyArray.length() > 0) {
                dao.clearTemplateStudySubjects()
                for (i in 0 until templateStudyArray.length()) {
                    val obj = templateStudyArray.getJSONObject(i)
                    dao.insertTemplateStudySubject(
                        TemplateStudySubjectEntity(
                            subject = obj.getString("subject"),
                            colorHex = obj.optString("colorHex", "#B388FF"),
                            defaultHours = obj.optDouble("defaultHours", 1.0),
                            orderIndex = obj.optInt("orderIndex", i)
                        )
                    )
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAllData() {
        resetAllWithDefaults()
    }

    suspend fun resetAllWithDefaults() {
        dao.deleteAllJournalDays()
        dao.deleteAllDailyTasks()
        dao.deleteAllStudyEntries()
        dao.deleteAllSettings()
        dao.clearTemplateTasks()
        dao.clearTemplateStudySubjects()

        val defaultTasks = listOf(
            TemplateTaskEntity(title = "Generate business videos", icon = "🎬", priority = "High", orderIndex = 0),
            TemplateTaskEntity(title = "Sort business files", icon = "📁", priority = "Medium", orderIndex = 1),
            TemplateTaskEntity(title = "Post on Instagram", icon = "📸", priority = "High", orderIndex = 2),
            TemplateTaskEntity(title = "House chores", icon = "🧹", priority = "Medium", orderIndex = 3),
            TemplateTaskEntity(title = "Cooking/meal prep", icon = "🍳", priority = "Medium", orderIndex = 4),
            TemplateTaskEntity(title = "Exercise/walk", icon = "🚶", priority = "High", orderIndex = 5),
            TemplateTaskEntity(title = "Read (non-academic)", icon = "📖", priority = "Low", orderIndex = 6),
            TemplateTaskEntity(title = "Financial tasks", icon = "💰", priority = "High", orderIndex = 7),
            TemplateTaskEntity(title = "Family time/calls", icon = "📞", priority = "Medium", orderIndex = 8),
            TemplateTaskEntity(title = "Personal care", icon = "💅", priority = "Medium", orderIndex = 9),
            TemplateTaskEntity(title = "Hobbies/creative work (dress designing)", icon = "👗", priority = "Low", orderIndex = 10),
            TemplateTaskEntity(title = "Other", icon = "⭐", priority = "Low", orderIndex = 11)
        )
        dao.insertTemplateTasks(defaultTasks)

        val defaultStudy = listOf(
            TemplateStudySubjectEntity(subject = "Fashion Design & Styling", colorHex = "#FFB7C5", defaultHours = 1.5, orderIndex = 0),
            TemplateStudySubjectEntity(subject = "Business & Marketing", colorHex = "#B388FF", defaultHours = 1.0, orderIndex = 1),
            TemplateStudySubjectEntity(subject = "Creative Skills & Tutorials", colorHex = "#80D8FF", defaultHours = 1.0, orderIndex = 2),
            TemplateStudySubjectEntity(subject = "Reading & Self Development", colorHex = "#A7F3D0", defaultHours = 0.5, orderIndex = 3)
        )
        dao.insertTemplateStudySubjects(defaultStudy)
    }

    suspend fun getPeriodSummary(startDate: LocalDate, endDate: LocalDate, label: String): PeriodSummary {
        val startKey = startDate.format(dateFormatter)
        val endKey = endDate.format(dateFormatter)

        val days = dao.getJournalDaysInRange(startKey, endKey)
        val tasks = dao.getTasksInRange(startKey, endKey)
        val studies = dao.getStudyEntriesInRange(startKey, endKey)

        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
        val activeDaysCount = days.count {
            it.waterIntake > 0 || it.moodEmoji.isNotBlank() || it.notes.isNotBlank() || it.proudOf.isNotBlank()
        } + tasks.map { it.dateKey }.distinct().count { dateKey -> days.none { it.dateKey == dateKey } }

        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.isDone }
        val taskCompletionPercent = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0

        val totalStudy = studies.sumOf { it.hours }
        val studyBySubject = studies.groupBy { it.subject }
            .mapValues { entry -> entry.value.sumOf { it.hours } }

        val avgWater = if (days.isNotEmpty()) {
            val sum = days.sumOf { it.waterIntake }.toDouble()
            sum / maxOf(1, totalDays).toDouble()
        } else 0.0

        val moodCounts = days.filter { it.moodEmoji.isNotBlank() }
            .groupBy { it.moodEmoji }
            .mapValues { it.value.size }
        val dominantMood = moodCounts.maxByOrNull { it.value }?.key

        return PeriodSummary(
            label = label,
            startDate = startDate,
            endDate = endDate,
            totalDays = totalDays,
            activeDaysCount = activeDaysCount,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            taskCompletionPercent = taskCompletionPercent,
            totalStudyHours = totalStudy,
            studyHoursBySubject = studyBySubject,
            averageWaterGlasses = avgWater,
            moodCounts = moodCounts,
            dominantMood = dominantMood
        )
    }

    suspend fun getPeriodComparison(
        currentStart: LocalDate,
        currentEnd: LocalDate,
        currentLabel: String,
        prevStart: LocalDate,
        prevEnd: LocalDate,
        prevLabel: String
    ): PeriodComparison {
        val curr = getPeriodSummary(currentStart, currentEnd, currentLabel)
        val prev = getPeriodSummary(prevStart, prevEnd, prevLabel)

        return PeriodComparison(
            current = curr,
            previous = prev,
            taskCompletionDiffPercent = curr.taskCompletionPercent - prev.taskCompletionPercent,
            studyHoursDiff = curr.totalStudyHours - prev.totalStudyHours,
            waterDiff = curr.averageWaterGlasses - prev.averageWaterGlasses,
            tasksDoneDiff = curr.completedTasks - prev.completedTasks
        )
    }

    suspend fun getYearlyMonthlyBreakdown(year: Int): List<YearlyMonthlyBreakdown> {
        return (1..12).map { month ->
            val start = LocalDate.of(year, month, 1)
            val end = start.plusMonths(1).minusDays(1)
            val summary = getPeriodSummary(start, end, start.month.name.take(3))
            YearlyMonthlyBreakdown(
                monthNumber = month,
                monthName = start.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                taskCompletionPercent = summary.taskCompletionPercent,
                studyHours = summary.totalStudyHours,
                totalTasksDone = summary.completedTasks
            )
        }
    }
}
