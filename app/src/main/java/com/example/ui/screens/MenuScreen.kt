package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AiDifficulty
import com.example.data.model.SpadesVariant
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameMode
import com.example.ui.viewmodel.SpadesViewModel

@Composable
fun MenuScreen(
    viewModel: SpadesViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Emblem / Logo
            Surface(
                modifier = Modifier
                    .size(86.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                color = Color(0xFF6750A4),
                border = BorderStroke(3.dp, Color(0xFFEADDFF))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "♠",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SPADES PRO",
                color = Color(0xFF21005D),
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )

            Text(
                text = "PROFESSIONAL PARTNERSHIP TRICK-TAKING",
                color = Color(0xFF625B71),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Primary Play Buttons
            MenuOptionCard(
                title = "PLAY VS AI",
                subtitle = "Solo match with ${state.difficulty.title} bots (${state.variant.title})",
                icon = Icons.Default.PlayArrow,
                badge = "${state.difficulty.title.uppercase()}",
                accentColor = Color(0xFF6750A4),
                onClick = { viewModel.startNewGame(GameMode.SINGLE_PLAYER_AI) },
                testTag = "play_vs_ai_button"
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuOptionCard(
                title = "ONLINE MULTIPLAYER",
                subtitle = "Ranked matchmaking & private room sync",
                icon = Icons.Default.Public,
                badge = "ONLINE",
                accentColor = Color(0xFF00639B),
                onClick = { viewModel.navigateTo(AppScreen.LOBBY) },
                testTag = "online_multiplayer_button"
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuOptionCard(
                title = "PASS & PLAY",
                subtitle = "Local table for 2 to 4 human players on this device",
                icon = Icons.Default.Group,
                badge = "LOCAL",
                accentColor = Color(0xFF7D5260),
                onClick = { viewModel.startNewGame(GameMode.PASS_AND_PLAY) },
                testTag = "pass_and_play_button"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Secondary Quick Actions (Stats, Rulebook, Settings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallMenuCard(
                    title = "CAREER STATS",
                    icon = Icons.Default.BarChart,
                    color = Color(0xFF6750A4),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.STATS) }
                )

                SmallMenuCard(
                    title = "RULEBOOK",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFF7D5260),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.RULEBOOK) }
                )

                SmallMenuCard(
                    title = "SETTINGS",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF625B71),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Variant Indicator Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF3EDF7),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("♠", fontSize = 24.sp, color = Color(0xFF6750A4))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Current Variant: ${state.variant.title}",
                            color = Color(0xFF1D1B20),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.variant.description,
                            color = Color(0xFF49454F),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MenuOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFFEADDFF), CircleShape)
                    .border(1.dp, Color(0xFF6750A4).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Color(0xFF1D1B20),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEADDFF)
                    ) {
                        Text(
                            text = badge,
                            color = Color(0xFF21005D),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF49454F),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun SmallMenuCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color(0xFF1D1B20),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
