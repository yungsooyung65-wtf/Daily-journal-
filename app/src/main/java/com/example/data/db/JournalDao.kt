package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettingEntity
import com.example.data.model.JournalDayEntity
import com.example.data.model.StudyEntryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TemplateStudySubjectEntity
import com.example.data.model.TemplateTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    // --- Journal Day ---
    @Query("SELECT * FROM journal_days WHERE dateKey = :dateKey")
    fun getJournalDayFlow(dateKey: String): Flow<JournalDayEntity?>

    @Query("SELECT * FROM journal_days WHERE dateKey = :dateKey")
    suspend fun getJournalDay(dateKey: String): JournalDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertJournalDay(day: JournalDayEntity)

    @Query("SELECT * FROM journal_days WHERE dateKey BETWEEN :startDate AND :endDate ORDER BY dateKey ASC")
    fun getJournalDaysInRangeFlow(startDate: String, endDate: String): Flow<List<JournalDayEntity>>

    @Query("SELECT * FROM journal_days WHERE dateKey BETWEEN :startDate AND :endDate ORDER BY dateKey ASC")
    suspend fun getJournalDaysInRange(startDate: String, endDate: String): List<JournalDayEntity>

    @Query("SELECT * FROM journal_days")
    suspend fun getAllJournalDays(): List<JournalDayEntity>

    // --- Tasks ---
    @Query("SELECT * FROM daily_tasks WHERE dateKey = :dateKey ORDER BY orderIndex ASC, id ASC")
    fun getTasksForDateFlow(dateKey: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM daily_tasks WHERE dateKey = :dateKey ORDER BY orderIndex ASC, id ASC")
    suspend fun getTasksForDate(dateKey: String): List<TaskEntity>

    @Query("SELECT * FROM daily_tasks WHERE dateKey BETWEEN :startDate AND :endDate")
    fun getTasksInRangeFlow(startDate: String, endDate: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM daily_tasks WHERE dateKey BETWEEN :startDate AND :endDate")
    suspend fun getTasksInRange(startDate: String, endDate: String): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE daily_tasks SET isDone = :isDone WHERE id = :id")
    suspend fun setTaskDone(id: Long, isDone: Boolean)

    @Query("UPDATE daily_tasks SET priority = :priority WHERE id = :id")
    suspend fun setTaskPriority(id: Long, priority: String)

    @Query("DELETE FROM daily_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("DELETE FROM daily_tasks WHERE dateKey = :dateKey")
    suspend fun deleteTasksForDate(dateKey: String)

    @Query("SELECT * FROM daily_tasks")
    suspend fun getAllTasks(): List<TaskEntity>

    // --- Study Entries ---
    @Query("SELECT * FROM study_entries WHERE dateKey = :dateKey ORDER BY orderIndex ASC, id ASC")
    fun getStudyEntriesForDateFlow(dateKey: String): Flow<List<StudyEntryEntity>>

    @Query("SELECT * FROM study_entries WHERE dateKey = :dateKey ORDER BY orderIndex ASC, id ASC")
    suspend fun getStudyEntriesForDate(dateKey: String): List<StudyEntryEntity>

    @Query("SELECT * FROM study_entries WHERE dateKey BETWEEN :startDate AND :endDate")
    fun getStudyEntriesInRangeFlow(startDate: String, endDate: String): Flow<List<StudyEntryEntity>>

    @Query("SELECT * FROM study_entries WHERE dateKey BETWEEN :startDate AND :endDate")
    suspend fun getStudyEntriesInRange(startDate: String, endDate: String): List<StudyEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyEntry(entry: StudyEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyEntries(entries: List<StudyEntryEntity>)

    @Update
    suspend fun updateStudyEntry(entry: StudyEntryEntity)

    @Query("UPDATE study_entries SET isDone = :isDone WHERE id = :id")
    suspend fun setStudyEntryDone(id: Long, isDone: Boolean)

    @Query("UPDATE study_entries SET hours = :hours WHERE id = :id")
    suspend fun setStudyEntryHours(id: Long, hours: Double)

    @Query("DELETE FROM study_entries WHERE id = :id")
    suspend fun deleteStudyEntry(id: Long)

    @Query("SELECT * FROM study_entries")
    suspend fun getAllStudyEntries(): List<StudyEntryEntity>

    // --- Templates ---
    @Query("SELECT * FROM template_tasks ORDER BY orderIndex ASC, id ASC")
    fun getTemplateTasksFlow(): Flow<List<TemplateTaskEntity>>

    @Query("SELECT * FROM template_tasks ORDER BY orderIndex ASC, id ASC")
    suspend fun getTemplateTasks(): List<TemplateTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateTask(task: TemplateTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateTasks(tasks: List<TemplateTaskEntity>)

    @Update
    suspend fun updateTemplateTask(task: TemplateTaskEntity)

    @Query("DELETE FROM template_tasks WHERE id = :id")
    suspend fun deleteTemplateTask(id: Long)

    @Query("DELETE FROM template_tasks")
    suspend fun clearTemplateTasks()

    @Query("SELECT * FROM template_study_subjects ORDER BY orderIndex ASC, id ASC")
    fun getTemplateStudySubjectsFlow(): Flow<List<TemplateStudySubjectEntity>>

    @Query("SELECT * FROM template_study_subjects ORDER BY orderIndex ASC, id ASC")
    suspend fun getTemplateStudySubjects(): List<TemplateStudySubjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateStudySubject(subject: TemplateStudySubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateStudySubjects(subjects: List<TemplateStudySubjectEntity>)

    @Update
    suspend fun updateTemplateStudySubject(subject: TemplateStudySubjectEntity)

    @Query("DELETE FROM template_study_subjects WHERE id = :id")
    suspend fun deleteTemplateStudySubject(id: Long)

    @Query("DELETE FROM template_study_subjects")
    suspend fun clearTemplateStudySubjects()

    // --- Settings ---
    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    fun getSettingFlow(key: String): Flow<String?>

    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: AppSettingEntity)

    @Query("SELECT * FROM app_settings")
    suspend fun getAllSettings(): List<AppSettingEntity>

    // --- Data Reset ---
    @Query("DELETE FROM journal_days")
    suspend fun deleteAllJournalDays()

    @Query("DELETE FROM daily_tasks")
    suspend fun deleteAllDailyTasks()

    @Query("DELETE FROM study_entries")
    suspend fun deleteAllStudyEntries()

    @Query("DELETE FROM app_settings")
    suspend fun deleteAllSettings()
}
