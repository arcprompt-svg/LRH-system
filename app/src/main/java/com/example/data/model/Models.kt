package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class KPI(
    val k: String,
    val v: String
)

@Entity(tableName = "tools")
@Serializable
data class Tool(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val efficiency: Int,
    val status: String,
    val stack: String,
    val notes: String = "",
    val kpisJson: String = "[]" // Store as JSON string for simplicity
)

@Entity(tableName = "integrations")
data class Integration(
    @PrimaryKey val id: String, // e.g. "github"
    val enabled: Boolean = false,
    val status: String = "off",
    val token: String = "",
    val url: String = ""
)

@Entity(tableName = "feed_items")
data class FeedItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String,
    val dot: String, // "green", "blue", "amber", "red", "purple"
    val type: String
)

@Entity(tableName = "domain_monitors")
data class DomainMonitor(
    @PrimaryKey val domain: String,
    val isDnsValid: Boolean = true,
    val sslStatus: String = "valid", // "valid", "warning", "expired"
    val sslExpiryTimestamp: Long,
    val lastChecked: Long = System.currentTimeMillis()
)

@Entity(tableName = "log_entries")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String,
    val tag: String
)
