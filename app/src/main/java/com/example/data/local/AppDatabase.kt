package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Tools
    @Query("SELECT * FROM tools ORDER BY id DESC")
    fun getAllTools(): Flow<List<Tool>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: Tool)

    @Delete
    suspend fun deleteTool(tool: Tool)

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getToolById(id: Long): Tool?

    // Integrations
    @Query("SELECT * FROM integrations")
    fun getAllIntegrations(): Flow<List<Integration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegration(integration: Integration)

    // Feed
    @Query("SELECT * FROM feed_items ORDER BY timestamp DESC LIMIT 100")
    fun getRecentFeed(): Flow<List<FeedItem>>

    @Insert
    suspend fun insertFeedItem(item: FeedItem)

    @Query("DELETE FROM feed_items")
    suspend fun clearFeed()

    // Logs
    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC LIMIT 500")
    fun getRecentLogs(): Flow<List<LogEntry>>

    @Insert
    suspend fun insertLog(log: LogEntry)

    @Query("DELETE FROM log_entries")
    suspend fun clearLogs()

    // Domain Monitors
    @Query("SELECT * FROM domain_monitors")
    fun getAllDomainMonitors(): Flow<List<DomainMonitor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomainMonitor(monitor: DomainMonitor)
}

@Database(
    entities = [Tool::class, Integration::class, FeedItem::class, LogEntry::class, DomainMonitor::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "lrh-database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
