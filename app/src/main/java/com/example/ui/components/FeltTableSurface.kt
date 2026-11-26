package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.M3OnPrimaryContainer
import com.example.ui.theme.M3OnSecondaryContainer
import com.example.ui.theme.M3Outline
import com.example.ui.theme.M3Primary
import com.example.ui.theme.M3PrimaryContainer
import com.example.ui.theme.M3SecondaryContainer
import com.example.ui.theme.M3Surface
import com.example.ui.viewmodel.TableTheme

@Composable
fun FeltTableSurface(
    tableTheme: TableTheme,
    spadesBroken: Boolean,
    teamNsScore: Int,
    teamNsBags: Int,
    teamEwScore: Int,
    teamEwBags: Int,
    targetScore: Int,
    roundNumber: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val innerFeltBrush = Brush.linearGradient(
        listOf(
            Color(tableTheme.primaryColorHex),
            Color(tableTheme.secondaryColorHex)
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7FF))
    ) {
        // --- TOP PROFESSIONAL POLISH HEADER ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = M3Surface,
            border = BorderStroke(1.dp, M3Outline),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Table title & scores
                Column {
                    Text(
                        text = "TOURNAMENT TABLE • ROUND $roundNumber",
                        color = M3Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WE Badge (NS)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = M3PrimaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "WE: $teamNsScore",
                                    color = M3OnPrimaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " • ",
                                    color = M3OnPrimaryContainer.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Bags: $teamNsBags",
                                    color = M3OnPrimaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // THEY Badge (EW)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = M3SecondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "THEY: $teamEwScore",
                                    color = M3OnSecondaryContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " • ",
                                    color = M3OnSecondaryContainer.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Bags: $teamEwBags",
                                    color = M3OnSecondaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Spades broken status indicator & user initials avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (spadesBroken) Color(0xFFFFD8E4) else Color(0xFFEADDFF)
                    ) {
                        Text(
                            text = if (spadesBroken) "♠ BROKEN" else "♠ INTACT",
                            color = if (spadesBroken) Color(0xFF633B48) else M3OnPrimaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Avatar badge
                    Surface(
                        shape = CircleShape,
                        color = M3Primary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "JD",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- FELT TABLE MAIN CANVAS ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(16.dp, RoundedCornerShape(44.dp))
                    .clip(RoundedCornerShape(44.dp))
                    .background(innerFeltBrush)
                    .border(
                        BorderStroke(6.dp, Color(0xFF2E2E2E)),
                        RoundedCornerShape(44.dp)
                    )
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }
}
