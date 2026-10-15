package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.JournalMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PastelPinkBackground
import com.example.ui.viewmodel.JournalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PastelPinkBackground
                ) {
                    val viewModel: JournalViewModel = viewModel()
                    JournalMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
