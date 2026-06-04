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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.LRHCard
import com.example.ui.theme.*

@Composable
fun IntegrationsPanel(viewModel: MainViewModel) {
    val integrations by viewModel.integrations.collectAsState()

    val groups = mapOf(
        "Version Control & Code" to listOf("github", "gitlab"),
        "Documentation & Wiki" to listOf("notion", "googledocs", "confluence"),
        "Communication" to listOf("slack", "discord"),
        "Cloud & Infrastructure" to listOf("do", "aws", "vercel"),
        "Monitoring & CI/CD" to listOf("github_actions", "datadog")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groups.forEach { (groupName, ids) ->
            item {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.labelSmall,
                    color = LRH_Text3,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ids.forEach { id ->
                        val integration = integrations.find { it.id == id }
                            ?: com.example.data.model.Integration(id = id)
                        IntegrationCard(
                            id = id,
                            enabled = integration.enabled,
                            status = integration.status,
                            onToggle = { viewModel.toggleIntegration(id, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IntegrationCard(id: String, enabled: Boolean, status: String, onToggle: (Boolean) -> Unit) {
    val meta = getIntegrationMeta(id)
    LRHCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = LRH_Surface2,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(meta.icon, fontSize = 20.sp)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(meta.name, style = MaterialTheme.typography.titleMedium)
                Text(meta.desc, style = MaterialTheme.typography.bodySmall, color = LRH_Text2)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (enabled && status == "ok") LRH_Accent else if (status == "warn") LRH_Amber else LRH_Text3)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (enabled && status == "ok") "Connected" else if (status == "warn") "Warning" else "Not connected",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (enabled && status == "ok") LRH_Accent else if (status == "warn") LRH_Amber else LRH_Text3
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LRH_Accent,
                    checkedTrackColor = LRH_Accent.copy(alpha = 0.5f),
                    uncheckedThumbColor = LRH_Text3,
                    uncheckedTrackColor = LRH_Surface2
                )
            )
        }
    }
}

data class IntMeta(val name: String, val icon: String, val desc: String)

fun getIntegrationMeta(id: String) = when (id) {
    "github" -> IntMeta("GitHub", "⌥", "Source control & PR webhooks")
    "gitlab" -> IntMeta("GitLab", "🦊", "CI/CD pipeline events")
    "notion" -> IntMeta("Notion", "◻", "Wiki sync & database updates")
    "googledocs" -> IntMeta("Google Docs", "📄", "Document auto-export")
    "confluence" -> IntMeta("Confluence", "📘", "Enterprise knowledge base")
    "slack" -> IntMeta("Slack", "💬", "Alert notifications")
    "discord" -> IntMeta("Discord", "🎮", "Community webhooks")
    "do" -> IntMeta("DigitalOcean", "🌊", "Infrastructure monitoring")
    "aws" -> IntMeta("AWS", "☁", "Cloud metrics & logs")
    "vercel" -> IntMeta("Vercel", "▲", "Deployment triggers")
    "github_actions" -> IntMeta("GH Actions", "⚙", "CI/CD status")
    "datadog" -> IntMeta("Datadog", "🐕", "APM metrics & logs")
    else -> IntMeta(id.uppercase(), "?", "External service integration")
}
