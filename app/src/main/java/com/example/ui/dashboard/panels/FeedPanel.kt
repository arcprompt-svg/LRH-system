package com.example.ui.dashboard.panels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.components.LRHCard
import com.example.ui.theme.*

@Composable
fun FeedPanel(viewModel: MainViewModel) {
    val feed by viewModel.feed.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LRHCard(
            title = "Event Stream",
            headerExtra = {
                Text(
                    "${feed.size} events",
                    modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.CenterEnd),
                    style = MaterialTheme.typography.labelSmall,
                    color = LRH_Text3
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(feed) { item ->
                    FeedRow(item.message, item.dot, item.timestamp)
                }
            }
        }
    }
}
