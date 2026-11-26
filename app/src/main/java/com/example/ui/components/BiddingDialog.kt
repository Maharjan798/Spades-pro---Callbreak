package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PlayerPosition

@Composable
fun BiddingDialog(
    position: PlayerPosition,
    partnerBid: Int?,
    onBidSelected: (Int, Boolean) -> Unit
) {
    var selectedBid by remember { mutableIntStateOf(3) }
    var isBlindNilSelected by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* Modal: must make a bid */ },
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
                .testTag("bidding_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "♠ ENTER YOUR BID ♠",
                    color = Color(0xFF6750A4),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "${position.displayName}",
                    color = Color(0xFF625B71),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                if (partnerBid != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFEADDFF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Partner bid: ${if (partnerBid == 0) "NIL" else partnerBid} books",
                            color = Color(0xFF21005D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Special Nil and Blind Nil buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Regular Nil
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedBid = 0
                                isBlindNilSelected = false
                            },
                        color = if (selectedBid == 0 && !isBlindNilSelected) Color(0xFFE8DEF8) else Color(0xFFF3EDF7),
                        border = BorderStroke(
                            1.5.dp,
                            if (selectedBid == 0 && !isBlindNilSelected) Color(0xFF6750A4) else Color(0xFFCAC4D0)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NIL (0)",
                                color = Color(0xFF1D1B20),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+50 / -50 pts",
                                color = Color(0xFF625B71),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Blind Nil
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedBid = 0
                                isBlindNilSelected = true
                            },
                        color = if (isBlindNilSelected) Color(0xFFFFD8E4) else Color(0xFFF3EDF7),
                        border = BorderStroke(
                            1.5.dp,
                            if (isBlindNilSelected) Color(0xFFB3261E) else Color(0xFFCAC4D0)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BLIND NIL",
                                color = if (isBlindNilSelected) Color(0xFF633B48) else Color(0xFF1D1B20),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+100 / -100 (Swap 2)",
                                color = if (isBlindNilSelected) Color(0xFFB3261E) else Color(0xFF625B71),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Or Bid Books (1 – 13):",
                    color = Color(0xFF49454F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Grid of 1..13
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(150.dp)
                ) {
                    items((1..13).toList()) { num ->
                        val isSelected = selectedBid == num && !isBlindNilSelected
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Color(0xFF6750A4) else Color(0xFFF3EDF7)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF21005D) else Color(0xFFCAC4D0)
                                    ),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedBid = num
                                    isBlindNilSelected = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$num",
                                color = if (isSelected) Color.White else Color(0xFF1D1B20),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Bid Action
                Button(
                    onClick = {
                        onBidSelected(selectedBid, isBlindNilSelected)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_bid_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val label = when {
                        isBlindNilSelected -> "LOCK IN BLIND NIL (+100)"
                        selectedBid == 0 -> "LOCK IN NIL (+50)"
                        else -> "CONFIRM BID: $selectedBid BOOKS"
                    }
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
