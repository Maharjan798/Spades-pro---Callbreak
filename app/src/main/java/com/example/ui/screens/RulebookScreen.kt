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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SpadesViewModel

@Composable
fun RulebookScreen(
    viewModel: SpadesViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFDF7FF))
            .padding(16.dp)
            .testTag("rulebook_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                    text = "OFFICIAL RULEBOOK & VARIANTS",
                    color = Color(0xFF6750A4),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            RuleSectionCard(
                title = "1. OBJECTIVE & TEAMS",
                content = "Spades is a strategic trick-taking game played by 4 players in 2 fixed partnerships (North-South vs East-West). Partners sit opposite each other and combine their individual bids to form the team goal. The goal is to reach 500 points before your opponents."
            )

            Spacer(modifier = Modifier.height(12.dp))

            RuleSectionCard(
                title = "2. TRUMP RANK & BOSTONS JOKERS",
                content = "Spades are always the trump suit and outrank all other suits.\n\n" +
                        "• Standard Deck: Spades Ace (high) down to 2 of Spades (low).\n" +
                        "• Bostons with Jokers: 52-card deck featuring top 4 trumps:\n" +
                        "  1. Big Joker (👑 Top Trump)\n" +
                        "  2. Little Joker (🃏 2nd Trump)\n" +
                        "  3. 2 of Spades (Big Deuce, 3rd Trump)\n" +
                        "  4. 2 of Diamonds (Little Deuce, 4th Trump)\n" +
                        "  5. Ace of Spades, King of Spades, etc."
            )

            Spacer(modifier = Modifier.height(12.dp))

            RuleSectionCard(
                title = "3. BIDDING & CONTRACTS",
                content = "Each player bids the number of tricks ('books') they expect to win. Partner bids are summed into a Team Contract.\n\n" +
                        "• Nil (0 books): If successful = +50 points. If taken a single trick = -50 points.\n" +
                        "• Blind Nil (Bid before looking): If successful = +100 points. If set = -100 points. Partners swap 2 cards.\n" +
                        "• Table Board: Minimum combined team bid is typically 4 books."
            )

            Spacer(modifier = Modifier.height(12.dp))

            RuleSectionCard(
                title = "4. SANDBAGS & OVERTRICK PENALTY",
                content = "Every extra trick taken beyond your team's contract is counted as 1 overtrick ('bag') and worth 1 point. However, accumulating 10 bags triggers a severe -100 point penalty, resetting the bag counter back down."
            )

            Spacer(modifier = Modifier.height(12.dp))

            RuleSectionCard(
                title = "5. RENEGING (REVOKING) PREVENTION",
                content = "A player MUST follow the suit led if they hold any cards of that suit. If void, the player can cut with a trump or discard off-suit. Spades cannot be led until Spades have been 'broken' (played on an off-suit trick) or the player has only Spades left in hand."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RuleSectionCard(
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color(0xFF6750A4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                color = Color(0xFF49454F),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
