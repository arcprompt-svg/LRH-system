package com.example.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardPanel
import com.example.ui.MainViewModel
import com.example.ui.dashboard.panels.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardShell(viewModel: MainViewModel) {
    val currentPanel by viewModel.currentPanel.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val isWsConnected by viewModel.isWsConnected.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val rotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_rotation"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = LRH_Surface,
                drawerContentColor = LRH_Text,
                modifier = Modifier.width(300.dp)
            ) {
                SidebarContent(
                    viewModel = viewModel,
                    userEmail = userEmail,
                    isWsConnected = isWsConnected,
                    currentPanel = currentPanel,
                    onPanelSelect = {
                        viewModel.setPanel(it)
                        scope.launch { drawerState.close() }
                    },
                    onLogout = { viewModel.logout(context) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = getPanelTitle(currentPanel),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LRH_Bg,
                        titleContentColor = LRH_Text,
                        navigationIconContentColor = LRH_Text
                    ),
                    actions = {
                        IconButton(onClick = { if (!isRefreshing) viewModel.refreshMetrics() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = if (isRefreshing) LRH_Accent else LRH_Text,
                                modifier = if (isRefreshing) Modifier.graphicsLayer { rotationZ = rotation } else Modifier
                            )
                        }
                        if (currentPanel == DashboardPanel.Overview) {
                            Text(
                                "Live",
                                style = MaterialTheme.typography.labelSmall,
                                color = LRH_Accent,
                                modifier = Modifier.padding(end = 16.dp, start = 8.dp)
                            )
                        }
                    }
                )
            },
            containerColor = LRH_Bg
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(
                    targetState = currentPanel,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { panel ->
                    when (panel) {
                        DashboardPanel.Overview -> OverviewPanel(viewModel)
                        DashboardPanel.Tools -> ToolsPanel(viewModel)
                        DashboardPanel.Feed -> FeedPanel(viewModel)
                        DashboardPanel.Integrations -> IntegrationsPanel(viewModel)
                        DashboardPanel.Update -> UpdatePanel(viewModel)
                        DashboardPanel.DomainMonitor -> DomainPanel(viewModel)
                        DashboardPanel.Workers -> WorkersPanel(viewModel)
                        DashboardPanel.AuditLog -> LogPanel(viewModel)
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Panel Not Implemented: $panel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarContent(
    viewModel: MainViewModel,
    userEmail: String,
    isWsConnected: Boolean,
    currentPanel: DashboardPanel,
    onPanelSelect: (DashboardPanel) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Logo
        Column(Modifier.padding(8.dp)) {
            Text("LRH System", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text("Living Reference Handbook", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
            Spacer(Modifier.height(8.dp))
            Surface(
                color = if (isWsConnected) LRH_Accent.copy(alpha = 0.1f) else LRH_Amber.copy(alpha = 0.1f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isWsConnected) LRH_Accent else LRH_Amber)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(if (isWsConnected) LRH_Accent else LRH_Amber))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (isWsConnected) "Connected" else "Connecting...",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isWsConnected) LRH_Accent else LRH_Amber
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // User row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = LRH_Surface2,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(LRH_Accent.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, LRH_Accent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(userEmail.take(2).uppercase(), color = LRH_Accent, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(userEmail.split("@")[0], style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("Admin", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
                }
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = LRH_Red, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("DASHBOARD", style = MaterialTheme.typography.labelSmall, color = LRH_Text3, modifier = Modifier.padding(start = 8.dp))
        Spacer(Modifier.height(8.dp))
        NavItem("ภาพรวม", Icons.Default.Dashboard, currentPanel == DashboardPanel.Overview) { onPanelSelect(DashboardPanel.Overview) }
        NavItem("เครื่องมือ", Icons.Default.Hexagon, currentPanel == DashboardPanel.Tools) { onPanelSelect(DashboardPanel.Tools) }
        NavItem("WS Feed", Icons.AutoMirrored.Filled.Chat, currentPanel == DashboardPanel.Feed) { onPanelSelect(DashboardPanel.Feed) }
        NavItem("Domain Monitor", Icons.Default.Security, currentPanel == DashboardPanel.DomainMonitor) { onPanelSelect(DashboardPanel.DomainMonitor) }
        NavItem("Cloud Workers", Icons.Default.Bolt, currentPanel == DashboardPanel.Workers) { onPanelSelect(DashboardPanel.Workers) }

        Spacer(Modifier.height(16.dp))
        Text("จัดการ", style = MaterialTheme.typography.labelSmall, color = LRH_Text3, modifier = Modifier.padding(start = 8.dp))
        Spacer(Modifier.height(8.dp))
        NavItem("Integrations", Icons.Default.Link, currentPanel == DashboardPanel.Integrations) { onPanelSelect(DashboardPanel.Integrations) }
        NavItem("อัปเดตผล", Icons.Default.Add, currentPanel == DashboardPanel.Update) { onPanelSelect(DashboardPanel.Update) }
        NavItem("Audit Log", Icons.Default.ListAlt, currentPanel == DashboardPanel.AuditLog) { onPanelSelect(DashboardPanel.AuditLog) }

        Spacer(Modifier.weight(1f))
        Text("Last sync: 23:14:02", style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
    }
}

@Composable
fun NavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        color = if (isSelected) LRH_Accent.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = if (isSelected) LRH_Accent else LRH_Text2)
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = if (isSelected) LRH_Accent else LRH_Text2)
        }
    }
}

fun getPanelTitle(panel: DashboardPanel): String = when (panel) {
    DashboardPanel.Overview -> "ภาพรวมระบบ"
    DashboardPanel.Tools -> "เครื่องมือทั้งหมด"
    DashboardPanel.Feed -> "WebSocket Feed"
    DashboardPanel.Integrations -> "Integrations"
    DashboardPanel.Update -> "อัปเดตผล"
    DashboardPanel.DomainMonitor -> "Domain Monitor"
    DashboardPanel.Workers -> "Workers Monitoring"
    DashboardPanel.Templates -> "Templates"
    DashboardPanel.ApiConfig -> "API Config"
    DashboardPanel.AuditLog -> "Audit Log"
}
