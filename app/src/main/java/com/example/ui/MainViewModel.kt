package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.worker.DomainCheckWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

sealed class AuthScreen {
    object Login : AuthScreen()
    object MfaSelect : AuthScreen()
    object MfaOtp : AuthScreen()
    object Success : AuthScreen()
    object Authenticated : AuthScreen()
}

sealed class DashboardPanel {
    object Overview : DashboardPanel()
    object Tools : DashboardPanel()
    object Feed : DashboardPanel()
    object Integrations : DashboardPanel()
    object Update : DashboardPanel()
    object DomainMonitor : DashboardPanel()
    object Workers : DashboardPanel()
    object Templates : DashboardPanel()
    object ApiConfig : DashboardPanel()
    object AuditLog : DashboardPanel()
}

class MainViewModel(application: Application, private val repository: AppRepository) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    // Auth State
    private val _authScreen = MutableStateFlow<AuthScreen>(AuthScreen.Login)
    val authScreen: StateFlow<AuthScreen> = _authScreen

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail

    private val _selectedMfa = MutableStateFlow("totp")
    val selectedMfa: StateFlow<String> = _selectedMfa

    // Dashboard State
    private val _currentPanel = MutableStateFlow<DashboardPanel>(DashboardPanel.Overview)
    val currentPanel: StateFlow<DashboardPanel> = _currentPanel

    // Data
    val tools = repository.allTools.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val integrations = repository.allIntegrations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val feed = repository.recentFeed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val logs = repository.recentLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val domains = repository.allDomains.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Effects/Simulations
    private val _isWsConnected = MutableStateFlow(false)
    val isWsConnected: StateFlow<Boolean> = _isWsConnected

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        seedInitialData()
        startWsSimulation()
        startDomainMonitor()
        scheduleBackgroundWorkers()
    }

    private fun scheduleBackgroundWorkers() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val domainWorkRequest = PeriodicWorkRequestBuilder<DomainCheckWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag("domain_check")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DomainCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            domainWorkRequest
        )
        
        viewModelScope.launch {
            insertLog("Scheduled Background Worker: DomainCheck (1hr interval)", "worker")
        }
    }

    private fun seedInitialData() {
        viewModelScope.launch {
            repository.allTools.first().ifEmpty {
                val initialTools = listOf(
                    Tool(name = "Wiki Knowledge Hub", category = "Research & Wiki", efficiency = 72, status = "active", stack = "Next.js · PostgreSQL", kpisJson = Json.encodeToString(listOf(KPI("Page Load", "1.2s"), KPI("Articles", "47")))),
                    Tool(name = "Interactive Sandbox", category = "Coding & Development", efficiency = 65, status = "testing", stack = "Express.js · WebSocket", kpisJson = Json.encodeToString(listOf(KPI("Experiments", "23"), KPI("Latency", "340ms")))),
                    Tool(name = "Gamification Dashboard", category = "Gamification", efficiency = 80, status = "stable", stack = "React · Chart.js", kpisJson = Json.encodeToString(listOf(KPI("Active Users", "34"), KPI("Reports", "120")))),
                    Tool(name = "Blueprint Viewer", category = "Cloud & Infrastructure", efficiency = 58, status = "review", stack = "Mermaid.js · D3.js", kpisJson = Json.encodeToString(listOf(KPI("Diagrams", "12"))))
                )
                initialTools.forEach { repository.insertTool(it) }

                val initialIntegrations = listOf("github", "notion", "slack", "do", "vercel").map {
                    Integration(id = it, enabled = true, status = "ok")
                }
                initialIntegrations.forEach { repository.insertIntegration(it) }

                // Seed domain monitor: Expiring in 25 days (within 30 day warning)
                val thirtyDaysInMs = 30L * 24 * 60 * 60 * 1000L
                val expiry = System.currentTimeMillis() + (25L * 24 * 60 * 60 * 1000L)
                repository.insertDomainMonitor(
                    DomainMonitor(
                        domain = "wiki.supachai.org",
                        isDnsValid = true,
                        sslStatus = "warning",
                        sslExpiryTimestamp = expiry
                    )
                )
            }
        }
    }

    private fun startDomainMonitor() {
        viewModelScope.launch {
            while (true) {
                checkDomains()
                delay(60000) // Check every minute (simulated)
            }
        }
    }

    private suspend fun checkDomains() {
        val allDomains = repository.allDomains.first()
        val now = System.currentTimeMillis()
        val warningThreshold = 30L * 24 * 60 * 60 * 1000L

        allDomains.forEach { monitor ->
            val remaining = monitor.sslExpiryTimestamp - now
            if (remaining < warningThreshold && monitor.sslStatus != "warning") {
                // Update status and notify
                val updated = monitor.copy(sslStatus = "warning", lastChecked = now)
                repository.insertDomainMonitor(updated)
                
                val msg = "ALERT: ${monitor.domain} SSL expires in ${remaining / (24 * 60 * 60 * 1000L)} days!"
                insertFeedItem(msg, "amber", "domain_alert")
                insertLog(msg, "domain_monitor")
                sendEmailNotification(msg) // Simulated
            }
        }
    }

    private fun sendEmailNotification(msg: String) {
        // Log the email notification
        viewModelScope.launch {
            insertLog("Email sent to admin@example.com: $msg", "notification")
        }
    }

    private fun startWsSimulation() {
        viewModelScope.launch {
            delay(2000)
            _isWsConnected.value = true
            insertLog("WebSocket connected to backend", "system")
            insertFeedItem("WebSocket connected to backend", "green", "system")

            while (true) {
                delay(8000)
                simulateUpdate()
            }
        }
    }

    private suspend fun simulateUpdate() {
        val currentTools = tools.value
        if (currentTools.isNotEmpty()) {
            val tool = currentTools.random()
            val delta = (-5..10).random()
            val newEff = (tool.efficiency + delta).coerceIn(0, 100)
            repository.insertTool(tool.copy(efficiency = newEff))
            
            val msg = "${tool.name}: efficiency updated to $newEff%"
            val dot = if (delta >= 0) "green" else "red"
            insertFeedItem(msg, dot, "kpi_update")
            insertLog(msg, "kpi_update")
        }
    }

    // Actions
    fun login(email: String) {
        _userEmail.value = email
        _authScreen.value = AuthScreen.MfaSelect
    }

    fun selectMfa(method: String) {
        _selectedMfa.value = method
    }

    fun goToOtp() {
        _authScreen.value = AuthScreen.MfaOtp
    }

    fun verifyOtp(otp: String) {
        if (otp == "123456" || otp.length == 6) {
            viewModelScope.launch {
                _authScreen.value = AuthScreen.Success
                delay(1500)
                _authScreen.value = AuthScreen.Authenticated
                insertLog("User authenticated: ${_userEmail.value}", "auth")
            }
        }
    }

    fun logout() {
        _authScreen.value = AuthScreen.Login
    }

    fun setPanel(panel: DashboardPanel) {
        _currentPanel.value = panel
    }

    fun toggleIntegration(id: String, enabled: Boolean) {
        viewModelScope.launch {
            val current = integrations.value.find { it.id == id } ?: Integration(id)
            repository.insertIntegration(current.copy(enabled = enabled, status = if (enabled) "ok" else "off"))
            insertLog("Integration $id ${if (enabled) "enabled" else "disabled"}", "integration")
        }
    }

    fun addTool(tool: Tool) {
        viewModelScope.launch {
            repository.insertTool(tool)
            insertFeedItem("New template added: ${tool.name}", "blue", "create")
            insertLog("Tool created: ${tool.name}", "create")
        }
    }

    fun deleteTool(tool: Tool) {
        viewModelScope.launch {
            repository.deleteTool(tool)
            insertLog("Tool deleted: ${tool.name}", "delete")
        }
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            _isRefreshing.value = true
            insertLog("Manual refresh triggered", "system")
            insertFeedItem("Refreshing system metrics...", "blue", "system")
            delay(1000) // Artificial delay for visual feedback
            simulateUpdate()
            checkDomains()
            insertLog("Manual refresh complete", "system")
            _isRefreshing.value = false
        }
    }

    private suspend fun insertLog(message: String, tag: String) {
        repository.insertLog(LogEntry(message = message, tag = tag))
    }

    private suspend fun insertFeedItem(message: String, dot: String, type: String) {
        repository.insertFeedItem(FeedItem(message = message, dot = dot, type = type))
    }
}
