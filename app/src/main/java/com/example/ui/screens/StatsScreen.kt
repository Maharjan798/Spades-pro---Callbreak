package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpadesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    viewModel: SpadesViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val stats = state.playerStats

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7FF))
            .padding(16.dp)
            .testTag("stats_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.MENU) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B20))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CAREER STATS & RECORDS",
                    color = Color(0xFF6750A4),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Overall Win Rate Banner
            val gamesPlayed = stats?.gamesPlayed ?: 0
            val gamesWon = stats?.gamesWon ?: 0
            val winRate = if (gamesPlayed > 0) (gamesWon * 100) / gamesPlayed else 0

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                border = BorderStroke(1.5.dp, Color(0xFFCAC4D0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("WIN RATE", color = Color(0xFF625B71), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("$winRate%", color = Color(0xFF386A20), fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Text("$gamesWon Wins / $gamesPlayed Matches", color = Color(0xFF49454F), fontSize = 12.sp)
                    }

                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFF6750A4),
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Stat Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(
                    title = "Total Books",
                    value = "${stats?.totalTricksWon ?: 0}",
                    color = Color(0xFF6750A4),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Highest Score",
                    value = "${stats?.highestScore ?: 0}",
                    color = Color(0xFF7D5260),
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Nil Success",
                    value = "${stats?.nilsMade ?: 0}/${stats?.nilsAttempted ?: 0}",
                    color = Color(0xFF386A20),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "MATCH HISTORY",
                color = Color(0xFF49454F),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.matchHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved match history yet.\nPlay a game to record stats!",
                        color = Color(0xFF79747E),
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.matchHistory) { match ->
                        val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
                        val dateString = dateFormat.format(Date(match.timestamp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFFFFF),
                            border = BorderStroke(1.dp, if (match.isWin) Color(0xFF386A20) else Color(0xFFB3261E)),
                            shadowElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (match.isWin) "VICTORY (US WON)" else "DEFEAT (THEM WON)",
                                        color = if (match.isWin) Color(0xFF386A20) else Color(0xFFB3261E),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${match.variant} • ${match.totalRounds} Rounds",
                                        color = Color(0xFF625B71),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = dateString,
                                        color = Color(0xFF79747E),
                                        fontSize = 10.sp
                                    )
                                }

                                Text(
                                    text = "${match.teamNsFinalScore} - ${match.teamEwFinalScore}",
                                    color = Color(0xFF1D1B20),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color(0xFF625B71), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}
