package com.example.ui.dashboard.panels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.Tool
import com.example.ui.MainViewModel
import com.example.ui.components.LRHButton
import com.example.ui.components.LRHCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePanel(viewModel: MainViewModel) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Coding & Development") }
    var efficiency by remember { mutableStateOf("80") }
    var stack by remember { mutableStateOf("") }
    var kpis by remember { mutableStateOf("") }

    val categories = listOf(
        "Coding & Development", "API Usage", "Security & Privacy",
        "Cloud & Infrastructure", "Automation", "Gamification",
        "UI/UX & Design", "Research & Wiki"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LRHCard(title = "เพิ่ม Template ใหม่") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ชื่อเครื่องมือ") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("หมวดหมู่") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = efficiency,
                        onValueChange = { if (it.all { char -> char.isDigit() }) efficiency = it },
                        label = { Text("ประสิทธิภาพ (0-100)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stack,
                        onValueChange = { stack = it },
                        label = { Text("Stack") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g. Next.js") }
                    )
                }

                OutlinedTextField(
                    value = kpis,
                    onValueChange = { kpis = it },
                    label = { Text("KPI (คั่นด้วย , )") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Uptime, Latency, Errors") }
                )

                Spacer(Modifier.height(8.dp))
                LRHButton("⊞ เพิ่มเข้าระบบ", onClick = {
                    if (name.isNotEmpty()) {
                        viewModel.addTool(
                            Tool(
                                name = name,
                                category = category,
                                efficiency = efficiency.toIntOrNull() ?: 0,
                                status = "testing",
                                stack = stack,
                                kpisJson = "[]" // In a real app, parse the kpis string
                            )
                        )
                        name = ""; stack = ""; kpis = ""
                    }
                })
            }
        }
    }
}
