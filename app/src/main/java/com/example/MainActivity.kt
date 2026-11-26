package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.GameTableScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.RulebookScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpadesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SpadesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = Color(0xFFFDF7FF)
                ) {
                    SpadesApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SpadesApp(viewModel: SpadesViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state.currentScreen) {
        AppScreen.MENU -> MenuScreen(viewModel = viewModel)
        AppScreen.GAME_TABLE -> GameTableScreen(viewModel = viewModel)
        AppScreen.LOBBY -> LobbyScreen(viewModel = viewModel)
        AppScreen.STATS -> StatsScreen(viewModel = viewModel)
        AppScreen.RULEBOOK -> RulebookScreen(viewModel = viewModel)
        AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
    }
}
