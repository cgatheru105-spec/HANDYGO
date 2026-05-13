package com.example.handygo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.handygo.navigation.AppNavHost
import com.example.handygo.ui.theme.HANDYGOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CloudinaryManager.init(this)
        setContent {
            HANDYGOTheme {
                AppNavHost()
            }
        }
    }
}
