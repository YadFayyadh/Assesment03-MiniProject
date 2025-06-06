package com.fayyadh0093.miniproject3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fayyadh0093.miniproject3.ui.theme.MiniProject3Theme
import com.fayyadh0093.miniproject3.ui.theme.screen.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           MiniProject3Theme {
                 MainScreen()
           }
        }
    }
}