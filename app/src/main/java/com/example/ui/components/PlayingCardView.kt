package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Card
import com.example.data.model.Rank
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit

@Composable
fun PlayingCardView(
    card: Card,
    modifier: Modifier = Modifier,
    isPlayable: Boolean = true,
    isSelected: Boolean = false,
    isFaceUp: Boolean = true,
    isBostonsVariant: Boolean = false,
    width: Dp = 68.dp,
    height: Dp = 96.dp,
    onClick: (() -> Unit)? = null
) {
    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) -16f else if (isPlayable && onClick != null) 0f else 0f,
        label = "cardOffset"
    )

    val isTopTrump = isBostonsVariant && (
        card.rank == Rank.BIG_JOKER ||
        card.rank == Rank.LITTLE_JOKER ||
        (card.suit == Suit.SPADES && card.rank == Rank.TWO) ||
        (card.suit == Suit.DIAMONDS && card.rank == Rank.TWO)
    )

    val borderColor = when {
        isSelected -> Color(0xFF6750A4)
        isTopTrump -> Color(0xFFE5A93C)
        isPlayable && onClick != null -> Color(0xFF6750A4).copy(alpha = 0.5f)
        else -> Color(0xFFCAC4D0)
    }

    val borderWidth = if (isSelected || isTopTrump) 2.dp else 1.dp
    val elevation = if (isSelected) 12.dp else if (isPlayable) 6.dp else 2.dp

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .width(width)
            .height(height)
            .alpha(if (isPlayable || onClick == null) 1f else 0.45f)
            .shadow(elevation, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = isPlayable && onClick != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color(0xFF6750A4)),
                onClick = { onClick?.invoke() }
            )
            .testTag("card_${card.id}")
    ) {
        if (isFaceUp) {
            FaceUpCardContent(
                card = card,
                isTopTrump = isTopTrump,
                isBostonsVariant = isBostonsVariant,
                borderColor = borderColor,
                borderWidth = borderWidth
            )
        } else {
            FaceDownCardBack(
                borderColor = borderColor,
                borderWidth = borderWidth
            )
        }
    }
}

@Composable
private fun FaceUpCardContent(
    card: Card,
    isTopTrump: Boolean,
    isBostonsVariant: Boolean,
    borderColor: Color,
    borderWidth: Dp
) {
    val suitColor = if (card.suit.isRed) Color(0xFFD32F2F) else Color(0xFF1E293B)
    val cardBg = if (isTopTrump) {
        Brush.verticalGradient(listOf(Color(0xFFFFFDF5), Color(0xFFFFF7DD)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cardBg)
            .padding(borderWidth)
    ) {
        // Border frame
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(6.dp),
            color = Color.Transparent,
            border = BorderStroke(borderWidth, borderColor)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(3.dp)) {
                // Top-left rank & suit
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    val rankText = when (card.rank) {
                        Rank.BIG_JOKER -> "BJ"
                        Rank.LITTLE_JOKER -> "LJ"
                        else -> card.rank.symbol
                    }
                    Text(
                        text = rankText,
                        color = suitColor,
                        fontSize = if (card.rank == Rank.BIG_JOKER || card.rank == Rank.LITTLE_JOKER) 10.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = if (card.suit == Suit.JOKER) "★" else card.suit.symbol,
                        color = suitColor,
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                }

                // Center Icon/Graphic
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        card.rank == Rank.BIG_JOKER -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👑", fontSize = 18.sp)
                                Text("BIG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }
                        card.rank == Rank.LITTLE_JOKER -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🃏", fontSize = 18.sp)
                                Text("LIL", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                            }
                        }
                        isBostonsVariant && card.suit == Suit.SPADES && card.rank == Rank.TWO -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("♠", fontSize = 20.sp, color = Color(0xFF0F172A))
                                Text("BIG 2", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                            }
                        }
                        isBostonsVariant && card.suit == Suit.DIAMONDS && card.rank == Rank.TWO -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("♦", fontSize = 20.sp, color = Color(0xFFDC2626))
                                Text("LIL 2", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                            }
                        }
                        else -> {
                            Text(
                                text = card.suit.symbol,
                                color = suitColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom-right upside-down rank & suit
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .rotate(180f)
                ) {
                    val rankText = when (card.rank) {
                        Rank.BIG_JOKER -> "BJ"
                        Rank.LITTLE_JOKER -> "LJ"
                        else -> card.rank.symbol
                    }
                    Text(
                        text = rankText,
                        color = suitColor,
                        fontSize = if (card.rank == Rank.BIG_JOKER || card.rank == Rank.LITTLE_JOKER) 10.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 12.sp
                    )
                    Text(
                        text = if (card.suit == Suit.JOKER) "★" else card.suit.symbol,
                        color = suitColor,
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FaceDownCardBack(
    borderColor: Color,
    borderWidth: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), Color(0xFF1E3A8A))
                )
            )
            .padding(borderWidth)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(6.dp),
            color = Color(0x33000000),
            border = BorderStroke(1.dp, Color(0x44FFFFFF))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "♠",
                    color = Color(0x88F59E0B),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
