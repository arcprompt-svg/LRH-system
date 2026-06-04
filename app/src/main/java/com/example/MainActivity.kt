package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.AuthScreen
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.auth.AuthRoot
import com.example.ui.dashboard.DashboardShell
import com.example.ui.theme.LRHSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "lrh-database"
        ).build()
        val repository = AppRepository(db.dao())

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository)
            )
            val authState by viewModel.authScreen.collectAsState()

            LRHSystemTheme {
                if (authState == AuthScreen.Authenticated) {
                    DashboardShell(viewModel)
                } else {
                    AuthRoot(viewModel)
                }
            }
        }
    }
}
