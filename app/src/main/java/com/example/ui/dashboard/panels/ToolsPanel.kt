package com.example.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KPI
import com.example.data.model.Tool
import com.example.ui.MainViewModel
import com.example.ui.components.LRHBadge
import com.example.ui.components.LRHCard
import com.example.ui.theme.*
import kotlinx.serialization.json.Json

@Composable
fun ToolsPanel(viewModel: MainViewModel) {
    val tools by viewModel.tools.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tools) { tool ->
            ToolItemRow(tool, onDelete = { viewModel.deleteTool(tool) })
        }
    }
}

@Composable
fun ToolItemRow(tool: Tool, onDelete: () -> Unit) {
    val color = when {
        tool.efficiency >= 80 -> LRH_Accent
        tool.efficiency >= 60 -> LRH_Amber
        else -> LRH_Red
    }

    LRHCard {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(tool.name, style = MaterialTheme.typography.titleMedium, color = LRH_Text)
                Text("${tool.category} · ${tool.stack}", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                StatusBadge(tool.status)
                Text("${tool.efficiency}%", style = MaterialTheme.typography.headlineSmall, color = color)
            }
        }
        
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { tool.efficiency / 100f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = LRH_Surface2
        )
        
        Spacer(Modifier.height(12.dp))
        val kpis = remember(tool.kpisJson) {
            try { Json.decodeFromString<List<KPI>>(tool.kpisJson) } catch (e: Exception) { emptyList() }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            kpis.forEach { kpi ->
                KpiChip(kpi.k, kpi.v)
            }
        }
        
        if (tool.notes.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LRH_Surface2,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border.copy(alpha = 0.5f))
            ) {
                Text(
                    text = tool.notes,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LRH_Text2
                )
            }
        }
        
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = LRH_Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "active" -> "Active" to LRH_Blue
        "stable" -> "Stable" to LRH_Accent
        "testing" -> "Testing" to LRH_Amber
        "review" -> "Needs Review" to LRH_Red
        "deploying" -> "Deploying" to LRH_Purple
        else -> status to LRH_Text2
    }
    
    LRHBadge(text = text, containerColor = color, contentColor = color)
}

@Composable
fun KpiChip(label: String, value: String) {
    Surface(
        color = LRH_Surface2,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
    ) {
        Text(
            text = "$label: $value",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = LRH_Text3
        )
    }
}
