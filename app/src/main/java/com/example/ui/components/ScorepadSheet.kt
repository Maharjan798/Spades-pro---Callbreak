package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RoundScoreRecord
import com.example.data.model.TeamType

@Composable
fun ScorepadSheet(
    history: List<RoundScoreRecord>,
    teamNsTotal: Int,
    teamNsBags: Int,
    teamEwTotal: Int,
    teamEwBags: Int,
    targetScore: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            border = BorderStroke(1.5.dp, Color(0xFFCAC4D0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("scorepad_modal")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 OFFICIAL SCOREPAD",
                        color = Color(0xFF6750A4),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF1D1B20))
                    }
                }

                Text(
                    text = "Goal: $targetScore Points • 10 Bags = -100 Pts",
                    color = Color(0xFF49454F),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3EDF7), RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("R#", color = Color(0xFF49454F), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                    Text("US (Bid/Won)", color = Color(0xFF6750A4), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("US Score", color = Color(0xFF21005D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                    Text("THEM (B/W)", color = Color(0xFF625B71), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("THEM Score", color = Color(0xFF1D192B), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No completed rounds yet.\nFirst round in progress!",
                            color = Color(0xFF79747E),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(history) { round ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF7F2FA), RoundedCornerShape(6.dp))
                                    .padding(vertical = 6.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${round.roundNumber}", color = Color(0xFF1D1B20), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                                Text("${round.teamNsBid} / ${round.teamNsWon}", color = Color(0xFF6750A4), fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("${round.teamNsTotalScore} (${if (round.teamNsRoundPoints >= 0) "+${round.teamNsRoundPoints}" else round.teamNsRoundPoints})", color = Color(0xFF21005D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                                Text("${round.teamEwBid} / ${round.teamEwWon}", color = Color(0xFF625B71), fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text("${round.teamEwTotalScore} (${if (round.teamEwRoundPoints >= 0) "+${round.teamEwRoundPoints}" else round.teamEwRoundPoints})", color = Color(0xFF1D192B), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFCAC4D0))
                Spacer(modifier = Modifier.height(12.dp))

                // Current Total Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("US TOTAL", color = Color(0xFF6750A4), fontSize = 12.sp, fontWeight = FontWeight.Black)
                        Text("$teamNsTotal pts", color = Color(0xFF21005D), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("🎒 Bags: $teamNsBags / 10", color = if (teamNsBags >= 7) Color(0xFFB3261E) else Color(0xFF49454F), fontSize = 11.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("THEM TOTAL", color = Color(0xFF625B71), fontSize = 12.sp, fontWeight = FontWeight.Black)
                        Text("$teamEwTotal pts", color = Color(0xFF1D192B), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("🎒 Bags: $teamEwBags / 10", color = if (teamEwBags >= 7) Color(0xFFB3261E) else Color(0xFF49454F), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("BACK TO TABLE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RoundSummaryDialog(
    roundNumber: Int,
    lastRound: RoundScoreRecord?,
    teamNsTotal: Int,
    teamEwTotal: Int,
    isGameOver: Boolean,
    winnerTeam: TeamType?,
    onContinue: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            border = BorderStroke(1.5.dp, Color(0xFFCAC4D0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("round_summary_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isGameOver) "🏆 MATCH OVER! 🏆" else "ROUND $roundNumber COMPLETE",
                    color = Color(0xFF6750A4),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                if (isGameOver && winnerTeam != null) {
                    val isUsWin = winnerTeam == TeamType.TEAM_NS
                    Text(
                        text = if (isUsWin) "🎉 VICTORY! YOUR TEAM WON! 🎉" else "DEFEAT! OPPONENTS WON",
                        color = if (isUsWin) Color(0xFF386A20) else Color(0xFFB3261E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (lastRound != null) {
                    // Team NS box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEADDFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("US (North-South)", color = Color(0xFF21005D), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = if (lastRound.teamNsRoundPoints >= 0) "+${lastRound.teamNsRoundPoints} pts" else "${lastRound.teamNsRoundPoints} pts",
                                    color = if (lastRound.teamNsRoundPoints >= 0) Color(0xFF386A20) else Color(0xFFB3261E),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Bid: ${lastRound.teamNsBid} • Won: ${lastRound.teamNsWon} tricks • Total Bags: ${lastRound.teamNsBags}",
                                color = Color(0xFF49454F),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Team EW box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8DEF8),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("THEM (East-West)", color = Color(0xFF1D192B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = if (lastRound.teamEwRoundPoints >= 0) "+${lastRound.teamEwRoundPoints} pts" else "${lastRound.teamEwRoundPoints} pts",
                                    color = if (lastRound.teamEwRoundPoints >= 0) Color(0xFF386A20) else Color(0xFFB3261E),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Bid: ${lastRound.teamEwBid} • Won: ${lastRound.teamEwWon} tricks • Total Bags: ${lastRound.teamEwBags}",
                                color = Color(0xFF49454F),
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (lastRound.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Round events: ${lastRound.notes}",
                            color = Color(0xFF6750A4),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Running Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("Total US: $teamNsTotal pts", color = Color(0xFF21005D), fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text("Total THEM: $teamEwTotal pts", color = Color(0xFF1D192B), fontSize = 15.sp, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isGameOver) {
                    Button(
                        onClick = onReturnToMenu,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("BACK TO MAIN MENU", color = Color.White, fontWeight = FontWeight.Black)
                    }
                } else {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("DEAL NEXT ROUND", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
