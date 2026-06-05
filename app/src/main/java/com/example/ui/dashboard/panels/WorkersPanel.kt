package com.example.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.LRHBadge
import com.example.ui.components.LRHCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkersPanel(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = LRH_Bg,
            contentColor = LRH_Accent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = LRH_Accent
                )
            },
            divider = { HorizontalDivider(color = LRH_Border) }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Android Workers", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Cloudflare Workers", modifier = Modifier.padding(16.dp))
            }
        }

        if (selectedTab == 0) {
            AndroidWorkersList()
        } else {
            CloudflareWorkersView()
        }
    }
}

@Composable
fun AndroidWorkersList() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LRHCard(title = "Local Task Scheduler") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WorkerItem(
                        name = "DomainCheckWork",
                        status = "RUNNING",
                        interval = "1 Hour",
                        lastRun = "12:45:00",
                        nextRun = "13:45:00"
                    )
                    WorkerItem(
                        name = "TelemetryAggregation",
                        status = "ENQUEUED",
                        interval = "15 Minutes",
                        lastRun = "12:55:00",
                        nextRun = "13:10:00"
                    )
                }
            }
        }
    }
}

@Composable
fun CloudflareWorkersView() {
    Column(Modifier.padding(16.dp)) {
        LRHCard(title = "Cloudflare Workers Monitoring") {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, null, tint = LRH_Accent)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("supachai-org-dns-handler", style = MaterialTheme.typography.titleMedium)
                        Text("Status: Active · Route: /*", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                    }
                    Spacer(Modifier.weight(1f))
                    LRHBadge("LIVE", LRH_Accent, LRH_Accent)
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Code snippet visualization
                Surface(
                    color = Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "export default {\n  async scheduled(event, env, ctx) {\n    console.log(event.scheduledTime)\n  }\n}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFFD4D4D4)
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text("Recent Executions", style = MaterialTheme.typography.titleSmall, color = LRH_Text)
                Spacer(Modifier.height(8.dp))
                
                repeat(3) { i ->
                    ExecutionLogRow(
                        time = "12:50:00",
                        event = "scheduled",
                        duration = "${(100..500).random()}ms",
                        status = "Success"
                    )
                    if (i < 2) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WorkerItem(name: String, status: String, interval: String, lastRun: String, nextRun: String) {
    Surface(
        color = LRH_Surface2,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SettingsBackupRestore, null, tint = LRH_Accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(12.dp))
                Text(name, fontWeight = FontWeight.Bold, color = LRH_Text)
                Spacer(Modifier.weight(1f))
                Text(status, style = MaterialTheme.typography.labelSmall, color = if (status == "RUNNING") LRH_Accent else LRH_Text3)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("INTERVAL", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                    Text(interval, style = MaterialTheme.typography.bodySmall, color = LRH_Text2)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("LAST / NEXT", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                    Text("$lastRun / $nextRun", style = MaterialTheme.typography.bodySmall, color = LRH_Text2)
                }
            }
        }
    }
}

@Composable
fun ExecutionLogRow(time: String, event: String, duration: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(time, style = MaterialTheme.typography.labelSmall, color = LRH_Text3, modifier = Modifier.width(60.dp))
        Text(event, style = MaterialTheme.typography.bodySmall, color = LRH_Accent, modifier = Modifier.weight(1f))
        Text(duration, style = MaterialTheme.typography.bodySmall, color = LRH_Text3)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(6.dp).clip(CircleShape).background(LRH_Accent))
    }
}
