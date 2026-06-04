package com.example.ui.dashboard.panels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.components.LRHCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogPanel(viewModel: MainViewModel) {
    val logs by viewModel.logs.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LRHCard(title = "Audit Log") {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(logs) { log ->
                    LogItemRow(log.message, log.tag, log.timestamp)
                    HorizontalDivider(color = LRH_Border)
                }
            }
        }
    }
}

@Composable
fun LogItemRow(message: String, tag: String, timestamp: Long) {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(timeStr, style = MaterialTheme.typography.labelSmall, color = LRH_Text3, modifier = Modifier.width(70.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = LRH_Text2, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Surface(
            color = LRH_Surface2,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
        ) {
            Text(
                tag,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = LRH_Text3
            )
        }
    }
}
