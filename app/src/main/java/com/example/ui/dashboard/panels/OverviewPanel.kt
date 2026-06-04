package com.example.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.LRHCard
import com.example.ui.components.PulseDot
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverviewPanel(viewModel: MainViewModel) {
    val tools by viewModel.tools.collectAsState()
    val feed by viewModel.feed.collectAsState()
    val integrations by viewModel.integrations.collectAsState()

    val avgEff = if (tools.isNotEmpty()) tools.map { it.efficiency }.average().toInt() else 0
    val stableCount = tools.count { it.status == "stable" }
    val connectedInts = integrations.count { it.enabled && it.status == "ok" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("เครื่องมือ", tools.size.toString(), LRH_Blue, Modifier.weight(1f))
                MetricCard("ประสิทธิภาพ", "$avgEff%", LRH_Accent, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Stable", stableCount.toString(), LRH_Amber, Modifier.weight(1f))
                MetricCard("Integrations", connectedInts.toString(), LRH_Purple, Modifier.weight(1f))
            }
        }

        item {
            LRHCard(title = "ประสิทธิภาพรายเครื่องมือ") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tools.forEach { tool ->
                        EfficiencyRow(tool.name, tool.efficiency)
                    }
                }
            }
        }

        item {
            LRHCard(
                title = "Live WebSocket Feed",
                headerExtra = {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PulseDot(LRH_Accent)
                            Spacer(Modifier.width(4.dp))
                            Text("LIVE", style = MaterialTheme.typography.labelSmall, color = LRH_Accent)
                        }
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    feed.take(5).forEach { item ->
                        FeedRow(item.message, item.dot, item.timestamp)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        color = LRH_Surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(Modifier.fillMaxWidth().height(2.dp).background(color))
            Spacer(Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
            Text(value, style = MaterialTheme.typography.displayMedium, color = LRH_Text)
        }
    }
}

@Composable
fun EfficiencyRow(name: String, efficiency: Int) {
    val color = when {
        efficiency >= 80 -> LRH_Accent
        efficiency >= 60 -> LRH_Amber
        else -> LRH_Red
    }
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.bodyMedium, color = LRH_Text2)
            Text("$efficiency%", style = MaterialTheme.typography.labelSmall, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { efficiency / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = LRH_Surface2
        )
    }
}

@Composable
fun FeedRow(message: String, dotStr: String, timestamp: Long) {
    val color = when (dotStr) {
        "green" -> LRH_Accent
        "blue" -> LRH_Blue
        "amber" -> LRH_Amber
        "red" -> LRH_Red
        "purple" -> LRH_Purple
        else -> LRH_Text3
    }
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.padding(top = 6.dp).size(6.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(12.dp))
        Text(timeStr, style = MaterialTheme.typography.labelSmall, color = LRH_Text3, modifier = Modifier.width(60.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = LRH_Text2, modifier = Modifier.weight(1f))
    }
}
