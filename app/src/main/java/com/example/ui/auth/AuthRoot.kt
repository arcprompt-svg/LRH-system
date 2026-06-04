package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthScreen
import com.example.ui.MainViewModel
import com.example.ui.components.LRHButton
import com.example.ui.theme.*

@Composable
fun AuthRoot(viewModel: MainViewModel) {
    val screen by viewModel.authScreen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LRH_Bg)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(LRH_Accent.copy(alpha = 0.05f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(800f, 200f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogoSection()
            Spacer(Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                color = LRH_Surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LRH_Border)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (screen) {
                        AuthScreen.Login -> LoginScreen(viewModel)
                        AuthScreen.MfaSelect -> MfaSelectScreen(viewModel)
                        AuthScreen.MfaOtp -> MfaOtpScreen(viewModel)
                        AuthScreen.Success -> SuccessScreen(viewModel)
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "LRH System",
            style = MaterialTheme.typography.displayMedium,
            color = LRH_Text
        )
        Text(
            text = "Living Reference Handbook · v2.0",
            style = MaterialTheme.typography.labelSmall,
            color = LRH_Text3
        )
    }
}

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var email by remember { mutableStateOf("admin@lrh.dev") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text("เข้าสู่ระบบ", style = MaterialTheme.typography.headlineSmall)
        Text("เลือกวิธีการเข้าสู่ระบบ", style = MaterialTheme.typography.bodyMedium, color = LRH_Text2)
        Spacer(Modifier.height(24.dp))

        // OAuth Mocks
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OAuthButton("GitHub", Modifier.weight(1f))
            OAuthButton("Google", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OAuthButton("Notion", Modifier.weight(1f))
            OAuthButton("Slack", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.weight(1f), color = LRH_Border)
            Text("หรือ", modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelSmall, color = LRH_Text3)
            HorizontalDivider(Modifier.weight(1f), color = LRH_Border)
        }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("อีเมล", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LRH_Surface2,
                focusedContainerColor = LRH_Surface2,
                unfocusedTextColor = LRH_Text,
                focusedTextColor = LRH_Text,
                focusedIndicatorColor = LRH_Accent
            )
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("รหัสผ่าน", style = MaterialTheme.typography.labelSmall) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LRH_Surface2,
                focusedContainerColor = LRH_Surface2,
                unfocusedTextColor = LRH_Text,
                focusedTextColor = LRH_Text,
                focusedIndicatorColor = LRH_Accent
            )
        )

        Spacer(Modifier.height(24.dp))
        LRHButton("เข้าสู่ระบบ →", onClick = { viewModel.login(email) })
    }
}

@Composable
fun OAuthButton(label: String, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = {},
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = LRH_Text)
    ) {
        Text(label, fontSize = 12.sp)
    }
}

@Composable
fun MfaSelectScreen(viewModel: MainViewModel) {
    val selected by viewModel.selectedMfa.collectAsState()

    Column {
        Text("เลือกวิธียืนยันตัวตน", style = MaterialTheme.typography.headlineSmall)
        Text("เลือก 1 วิธีเพื่อรับรหัส OTP", style = MaterialTheme.typography.bodyMedium, color = LRH_Text2)
        Spacer(Modifier.height(24.dp))

        MfaOption("Authenticator", "🔐", selected == "totp") { viewModel.selectMfa("totp") }
        Spacer(Modifier.height(8.dp))
        MfaOption("Email OTP", "📧", selected == "email") { viewModel.selectMfa("email") }
        Spacer(Modifier.height(8.dp))
        MfaOption("SMS", "📱", selected == "sms") { viewModel.selectMfa("sms") }

        Spacer(Modifier.height(24.dp))
        LRHButton("ส่งรหัส OTP →", onClick = { viewModel.goToOtp() })
    }
}

@Composable
fun MfaOption(label: String, emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) LRH_Accent.copy(alpha = 0.1f) else LRH_Surface2,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) LRH_Accent else LRH_Border
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = if (isSelected) LRH_Accent else LRH_Text)
        }
    }
}

@Composable
fun MfaOtpScreen(viewModel: MainViewModel) {
    var otp by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("กรอกรหัส OTP", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.align(Alignment.Start))
        Text("กรอกรหัส 6 หลักจาก Authenticator App", style = MaterialTheme.typography.bodyMedium, color = LRH_Text2, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            modifier = Modifier.width(200.dp),
            textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LRH_Surface2,
                focusedContainerColor = LRH_Surface2,
                focusedIndicatorColor = LRH_Accent
            )
        )

        Spacer(Modifier.height(32.dp))
        LRHButton("ยืนยัน OTP →", onClick = { viewModel.verifyOtp(otp) })
    }
}

@Composable
fun SuccessScreen(viewModel: MainViewModel) {
    val email by viewModel.userEmail.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(LRH_Accent.copy(alpha = 0.1f), CircleShape)
                .border(2.dp, LRH_Accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = LRH_Accent, fontSize = 32.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text("เข้าสู่ระบบสำเร็จ", style = MaterialTheme.typography.headlineSmall)
        Text(email, style = MaterialTheme.typography.bodyMedium, color = LRH_Text2)
    }
}
