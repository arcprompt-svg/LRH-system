package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.model.DomainMonitor
import com.example.data.model.FeedItem
import com.example.data.model.LogEntry
import kotlinx.coroutines.flow.first
import java.util.*

class DomainCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.dao()
        val allDomains: List<DomainMonitor> = dao.getAllDomainMonitors().first()
        val now = System.currentTimeMillis()
        val warningThreshold = 30L * 24 * 60 * 60 * 1000L

        allDomains.forEach { monitor ->
            val remaining = monitor.sslExpiryTimestamp - now
            if (remaining < warningThreshold && monitor.sslStatus != "warning") {
                // Update status and notify
                val updated = monitor.copy(sslStatus = "warning", lastChecked = now)
                dao.insertDomainMonitor(updated)
                
                val msg = "BACKGROUND ALERT: ${monitor.domain} SSL expires in ${remaining / (24 * 60 * 60 * 1000L)} days!"
                dao.insertFeedItem(FeedItem(message = msg, type = "domain_alert", dot = "amber"))
                dao.insertLog(LogEntry(message = msg, tag = "worker_monitor"))
                
                // Log simulated email
                dao.insertLog(LogEntry(message = "Email sent to admin@example.com (triggered by Background Worker)", tag = "notification"))
            }
        }

        return Result.success()
    }
}
