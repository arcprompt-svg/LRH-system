package com.example.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DomainMonitor
import com.example.ui.MainViewModel
import com.example.ui.components.LRHBadge
import com.example.ui.components.LRHCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DomainPanel(viewModel: MainViewModel) {
    val domains by viewModel.domains.collectAsState()
    var showDnsAssistant by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LRHCard(title = "Domain & SSL Monitoring") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    domains.forEach { domain ->
                        DomainItemRow(domain)
                    }
                }
            }
        }

        item {
            LRHCard(title = "DNS Verification Assistant") {
                Column {
                    Text(
                        "Generate instructions for adding TXT records (e.g. for Let's Encrypt or Domain Verification).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LRH_Text2
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showDnsAssistant = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LRH_Surface2, contentColor = LRH_Accent),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Accent.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.Info, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Open DNS Assistant")
                    }
                }
            }
        }
    }

    if (showDnsAssistant) {
        DnsAssistantDialog(onDismiss = { showDnsAssistant = false })
    }
}

@Composable
fun DomainItemRow(monitor: DomainMonitor) {
    val sslColor = when (monitor.sslStatus) {
        "valid" -> LRH_Accent
        "warning" -> LRH_Amber
        else -> LRH_Red
    }

    val expiryDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(monitor.sslExpiryTimestamp))
    val daysLeft = ((monitor.sslExpiryTimestamp - System.currentTimeMillis()) / (24 * 60 * 60 * 1000L)).coerceAtLeast(0)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = LRH_Surface2,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, null, tint = sslColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(monitor.domain, style = MaterialTheme.typography.titleMedium, color = LRH_Text)
                Spacer(Modifier.weight(1f))
                LRHBadge(
                    text = if (monitor.isDnsValid) "DNS OK" else "DNS ERROR",
                    containerColor = if (monitor.isDnsValid) LRH_Accent else LRH_Red,
                    contentColor = if (monitor.isDnsValid) LRH_Accent else LRH_Red
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("SSL STATUS", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                    Text(
                        monitor.sslStatus.uppercase(),
                        color = sslColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("EXPIRY", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                    Text("$expiryDate ($daysLeft days)", style = MaterialTheme.typography.bodyMedium, color = LRH_Text2)
                }
            }

            if (monitor.sslStatus == "warning") {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = LRH_Amber.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Amber.copy(alpha = 0.2f))
                ) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = LRH_Amber, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Certificate expires in less than 30 days. Renewal recommended.",
                            style = MaterialTheme.typography.labelSmall,
                            color = LRH_Amber
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DnsAssistantDialog(onDismiss: () -> Unit) {
    var domain by remember { mutableStateOf("wiki.supachai.org") }
    var recordName by remember { mutableStateOf("_acme-challenge") }
    var value by remember { mutableStateOf("random-verification-string-12345") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LRH_Surface,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        title = { Text("DNS Verification Assistant", color = LRH_Text) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = domain,
                    onValueChange = { domain = it },
                    label = { Text("Domain") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = recordName,
                    onValueChange = { recordName = it },
                    label = { Text("Record Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Verification Value") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("Instructions:", style = MaterialTheme.typography.titleSmall, color = LRH_Text)
                Surface(
                    color = LRH_Bg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("1. Log in to your DNS provider (e.g. DigitalOcean, Cloudflare).", fontSize = 12.sp, color = LRH_Text2)
                        Text("2. Add a new record of type TXT.", fontSize = 12.sp, color = LRH_Text2)
                        Text("3. Use Name: $recordName.$domain", fontSize = 12.sp, color = LRH_Accent, fontWeight = FontWeight.Bold)
                        Text("4. Use Value: $value", fontSize = 12.sp, color = LRH_Accent, fontWeight = FontWeight.Bold)
                        Text("5. Set TTL to 3600 (or lowest possible).", fontSize = 12.sp, color = LRH_Text2)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE", color = LRH_Accent) }
        }
    )
}
