package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppSettingEntity
import com.example.data.model.JournalDayEntity
import com.example.data.model.StudyEntryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TemplateStudySubjectEntity
import com.example.data.model.TemplateTaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        JournalDayEntity::class,
        TaskEntity::class,
        StudyEntryEntity::class,
        TemplateTaskEntity::class,
        TemplateStudySubjectEntity::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_journal_db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialTemplates(database.journalDao())
                    }
                }
            }

            suspend fun populateInitialTemplates(dao: JournalDao) {
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
        }
    }
}
