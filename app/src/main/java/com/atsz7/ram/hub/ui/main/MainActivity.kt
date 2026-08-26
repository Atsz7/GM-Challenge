package com.atsz7.ram.hub.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atsz7.ram.hub.common.ui.theme.RamHubTheme
import com.atsz7.ram.hub.ui.main.navigation.MainNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RamHubTheme {
                MainNavGraph()
            }
        }
    }
}
