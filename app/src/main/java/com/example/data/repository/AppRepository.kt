package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    val allTools: Flow<List<Tool>> = dao.getAllTools()
    val allIntegrations: Flow<List<Integration>> = dao.getAllIntegrations()
    val recentFeed: Flow<List<FeedItem>> = dao.getRecentFeed()
    val recentLogs: Flow<List<LogEntry>> = dao.getRecentLogs()
    val allDomains: Flow<List<DomainMonitor>> = dao.getAllDomainMonitors()

    suspend fun insertTool(tool: Tool) = dao.insertTool(tool)
    suspend fun deleteTool(tool: Tool) = dao.deleteTool(tool)
    suspend fun getToolById(id: Long) = dao.getToolById(id)

    suspend fun insertIntegration(integration: Integration) = dao.insertIntegration(integration)

    suspend fun insertDomainMonitor(monitor: DomainMonitor) = dao.insertDomainMonitor(monitor)

    suspend fun insertFeedItem(item: FeedItem) = dao.insertFeedItem(item)
    suspend fun clearFeed() = dao.clearFeed()

    suspend fun insertLog(log: LogEntry) = dao.insertLog(log)
    suspend fun clearLogs() = dao.clearLogs()
}
