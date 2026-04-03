package com.example.stadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stadmin.navigation.NavigationFlow
import com.example.stadmin.ui.theme.STAdminTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STAdminTheme {
                NavigationFlow()
            }
        }
    }
}