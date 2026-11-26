package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.PlayerPosition
import com.example.data.model.SpadesVariant
import com.example.engine.BanterSystem
import com.example.engine.TrickEvaluator
import com.example.ui.components.BiddingDialog
import com.example.ui.components.FeltTableSurface
import com.example.ui.components.PlayerSeatView
import com.example.ui.components.PlayingCardView
import com.example.ui.components.RoundSummaryDialog
import com.example.ui.components.ScorepadSheet
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GamePhase
import com.example.ui.viewmodel.SpadesViewModel

@Composable
fun GameTableScreen(
    viewModel: SpadesViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showBanterMenu by remember { mutableStateOf(false) }

    val southPlayer = state.players[PlayerPosition.SOUTH]
    val westPlayer = state.players[PlayerPosition.WEST]
    val northPlayer = state.players[PlayerPosition.NORTH]
    val eastPlayer = state.players[PlayerPosition.EAST]

    val activeBanterMap = remember(state.activeBanterBubbles) {
        state.activeBanterBubbles.associate { it.player to it.text }
    }

    FeltTableSurface(
        tableTheme = state.tableTheme,
        spadesBroken = state.spadesBroken,
        teamNsScore = state.teamNsTotalScore,
        teamNsBags = state.teamNsBags,
        teamEwScore = state.teamEwTotalScore,
        teamEwBags = state.teamEwBags,
        targetScore = state.targetScore,
        roundNumber = state.roundNumber,
        modifier = modifier
    ) {
        // --- TOP PARTNER SEAT (NORTH) ---
        if (northPlayer != null) {
            PlayerSeatView(
                playerState = northPlayer,
                isTurn = state.activeTurn == PlayerPosition.NORTH && state.gamePhase == GamePhase.PLAYING,
                isDealer = state.dealerPosition == PlayerPosition.NORTH,
                banterText = activeBanterMap[PlayerPosition.NORTH],
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 50.dp)
            )
        }

        // --- LEFT OPPONENT SEAT (WEST) ---
        if (westPlayer != null) {
            PlayerSeatView(
                playerState = westPlayer,
                isTurn = state.activeTurn == PlayerPosition.WEST && state.gamePhase == GamePhase.PLAYING,
                isDealer = state.dealerPosition == PlayerPosition.WEST,
                banterText = activeBanterMap[PlayerPosition.WEST],
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 2.dp)
            )
        }

        // --- RIGHT OPPONENT SEAT (EAST) ---
        if (eastPlayer != null) {
            PlayerSeatView(
                playerState = eastPlayer,
                isTurn = state.activeTurn == PlayerPosition.EAST && state.gamePhase == GamePhase.PLAYING,
                isDealer = state.dealerPosition == PlayerPosition.EAST,
                banterText = activeBanterMap[PlayerPosition.EAST],
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 2.dp)
            )
        }

        // --- CENTER TRICK PILE ---
        CenterTrickArea(
            plays = state.currentTrick.plays,
            winningPlay = state.currentTrick.winningPlay,
            isBostonsVariant = state.variant == SpadesVariant.BOSTONS_JOKERS,
            modifier = Modifier.align(Alignment.Center)
        )

        // --- ERROR / RULE VIOLATION BANNER ---
        AnimatedVisibility(
            visible = state.lastErrorMessage != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 195.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xEE7F1D1D),
                border = BorderStroke(1.dp, Color(0xFFF87171)),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = state.lastErrorMessage ?: "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // --- SOUTH (USER) AREA ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isMyTurn = state.activeTurn == PlayerPosition.SOUTH && state.gamePhase == GamePhase.PLAYING

            // "Your Turn" Polish Alert Banner
            AnimatedVisibility(
                visible = isMyTurn,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFD0E4FF),
                    border = BorderStroke(1.dp, Color(0xFF72A7FF)),
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ YOUR TURN TO PLAY",
                            color = Color(0xFF001D36),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // South Seat Info Badge
            if (southPlayer != null) {
                PlayerSeatView(
                    playerState = southPlayer,
                    isTurn = isMyTurn,
                    isDealer = state.dealerPosition == PlayerPosition.SOUTH,
                    banterText = activeBanterMap[PlayerPosition.SOUTH],
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Cards in Hand
            if (southPlayer != null) {
                val playableCards = remember(
                    southPlayer.hand,
                    state.currentTrick.plays,
                    state.spadesBroken,
                    state.variant
                ) {
                    TrickEvaluator.getPlayableCards(
                        hand = southPlayer.hand,
                        currentTrickPlays = state.currentTrick.plays,
                        spadesBroken = state.spadesBroken,
                        variant = state.variant
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    items(southPlayer.hand, key = { it.id }) { card ->
                        val canPlay = isMyTurn && playableCards.contains(card)
                        PlayingCardView(
                            card = card,
                            isPlayable = canPlay,
                            isBostonsVariant = state.variant == SpadesVariant.BOSTONS_JOKERS,
                            width = 62.dp,
                            height = 90.dp,
                            onClick = {
                                if (isMyTurn) {
                                    viewModel.playCard(PlayerPosition.SOUTH, card)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Action Controls Container (Matching Professional Polish Theme)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF3EDF7),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back to Menu
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEADDFF),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0))
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.MENU) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Menu", tint = Color(0xFF21005D), modifier = Modifier.size(18.dp))
                        }
                    }

                    // Banter quick phrase launcher
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE8DEF8),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        modifier = Modifier.clickable { showBanterMenu = !showBanterMenu }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ChatBubble, contentDescription = "Banter", tint = Color(0xFF1D192B), modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TALK SMACK", color = Color(0xFF1D192B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Scorepad Sheet
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF6750A4),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { viewModel.toggleScorepad() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Scorepad", tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SCOREPAD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- BANTER POPUP SELECTOR ---
        AnimatedVisibility(
            visible = showBanterMenu,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "COMMUNITY PHRASES & TAUNTS",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BanterSystem.QUICK_PHRASES.forEach { phrase ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0x44FFFFFF)),
                                modifier = Modifier.clickable {
                                    viewModel.sendBanter(phrase, PlayerPosition.SOUTH)
                                    showBanterMenu = false
                                }
                            ) {
                                Text(
                                    text = phrase,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- MODALS & DIALOGS ---

        // 1. Human Bidding Dialog
        if (state.showBiddingDialog && state.activeTurn == PlayerPosition.SOUTH) {
            val partnerBid = state.players[PlayerPosition.NORTH]?.bid
            BiddingDialog(
                position = PlayerPosition.SOUTH,
                partnerBid = partnerBid,
                onBidSelected = { bid, isBlindNil ->
                    viewModel.submitBid(PlayerPosition.SOUTH, bid, isBlindNil)
                }
            )
        }

        // 2. Scorepad Modal
        if (state.showScorepadSheet) {
            ScorepadSheet(
                history = state.roundScoreHistory,
                teamNsTotal = state.teamNsTotalScore,
                teamNsBags = state.teamNsBags,
                teamEwTotal = state.teamEwTotalScore,
                teamEwBags = state.teamEwBags,
                targetScore = state.targetScore,
                onDismiss = { viewModel.toggleScorepad() }
            )
        }

        // 3. Round Summary or Game Over Dialog
        if (state.gamePhase == GamePhase.ROUND_SUMMARY || state.gamePhase == GamePhase.GAME_OVER) {
            RoundSummaryDialog(
                roundNumber = state.roundNumber,
                lastRound = state.roundScoreHistory.lastOrNull(),
                teamNsTotal = state.teamNsTotalScore,
                teamEwTotal = state.teamEwTotalScore,
                isGameOver = state.gamePhase == GamePhase.GAME_OVER,
                winnerTeam = state.winnerTeam,
                onContinue = { viewModel.continueToNextRound() },
                onReturnToMenu = { viewModel.navigateTo(AppScreen.MENU) }
            )
        }
    }
}

@Composable
private fun CenterTrickArea(
    plays: List<PlayedCard>,
    winningPlay: PlayedCard?,
    isBostonsVariant: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(210.dp)
            .background(Color(0x22000000), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (plays.isEmpty()) {
            Text(
                text = "♠ LEAD TRICK ♠",
                color = Color(0x44FFFFFF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        } else {
            plays.forEach { play ->
                val (offsetX, offsetY, rotation) = when (play.player) {
                    PlayerPosition.SOUTH -> Triple(0.dp, 36.dp, 0f)
                    PlayerPosition.WEST -> Triple((-46).dp, 0.dp, 12f)
                    PlayerPosition.NORTH -> Triple(0.dp, (-36).dp, 0f)
                    PlayerPosition.EAST -> Triple(46.dp, 0.dp, -12f)
                }

                val isWinner = winningPlay?.player == play.player

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .rotate(rotation)
                ) {
                    PlayingCardView(
                        card = play.card,
                        isPlayable = true,
                        isSelected = isWinner,
                        isBostonsVariant = isBostonsVariant,
                        width = 54.dp,
                        height = 78.dp
                    )
                }
            }
        }
    }
}
