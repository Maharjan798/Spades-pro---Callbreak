package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerPosition
import com.example.data.model.TeamType
import com.example.ui.viewmodel.PlayerState

@Composable
fun PlayerSeatView(
    playerState: PlayerState,
    isTurn: Boolean,
    isDealer: Boolean,
    banterText: String? = null,
    modifier: Modifier = Modifier
) {
    val isTeamNs = playerState.position.team == TeamType.TEAM_NS
    val teamBadgeColor = if (isTeamNs) Color(0xFF6750A4) else Color(0xFF625B71)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTurn) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier.testTag("seat_${playerState.position.name}"),
        contentAlignment = Alignment.Center
    ) {
        // Speech Bubble if active
        AnimatedVisibility(
            visible = banterText != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-42).dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF3EDF7),
                border = BorderStroke(1.dp, Color(0xFF6750A4)),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = banterText ?: "",
                    color = Color(0xFF21005D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Avatar & Info container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(pulseScale)
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(if (isTurn) 10.dp else 4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (isTurn) listOf(Color(0xFFEADDFF), Color(0xFF6750A4))
                            else if (isTeamNs) listOf(Color(0xFF6750A4), Color(0xFF381E72))
                            else listOf(Color(0xFF49454F), Color(0xFF1D1B20))
                        )
                    )
                    .border(
                        width = if (isTurn) 2.5.dp else 1.5.dp,
                        color = if (isTurn) Color(0xFFD0E4FF) else teamBadgeColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val avatarEmoji = when (playerState.position) {
                    PlayerPosition.SOUTH -> "👤"
                    PlayerPosition.WEST -> "🧔🏾"
                    PlayerPosition.NORTH -> "🤝"
                    PlayerPosition.EAST -> "👩🏾"
                }
                Text(text = avatarEmoji, fontSize = 22.sp)

                if (isDealer) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .background(Color(0xFF6750A4), CircleShape)
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("D", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player Name and Bid / Won pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xDD000000),
                border = BorderStroke(1.dp, if (isTurn) Color(0xFFD0E4FF) else Color(0x33FFFFFF))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = playerState.name.substringBefore(" ("),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val bidText = when {
                            playerState.isBlindNil -> "Blind Nil"
                            playerState.bid == 0 -> "Nil"
                            playerState.bid != null -> "Bid: ${playerState.bid}"
                            else -> "Bidding..."
                        }

                        Text(
                            text = "$bidText • Won: ${playerState.tricksWon}",
                            color = if (playerState.isNil) Color(0xFFFFD8E4) else Color(0xFFEADDFF),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
