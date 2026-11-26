package com.example.engine

import com.example.data.model.AiDifficulty
import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.PlayerPosition
import com.example.data.model.Rank
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit
import kotlin.math.max
import kotlin.math.min

object SpadesAi {

    /**
     * Estimates an appropriate bid for an AI player based on difficulty and hand composition.
     */
    fun calculateBid(
        hand: List<Card>,
        difficulty: AiDifficulty,
        variant: SpadesVariant,
        partnerBid: Int?,
        currentTeamBags: Int
    ): Int {
        val trumps = hand.filter { TrickEvaluator.isTrump(it, variant) }
        val nonTrumps = hand.filter { !TrickEvaluator.isTrump(it, variant) }

        var expectedTricks = 0.0

        // High trumps
        when (variant) {
            SpadesVariant.STANDARD -> {
                if (trumps.any { it.rank == Rank.ACE }) expectedTricks += 1.0
                if (trumps.any { it.rank == Rank.KING } && trumps.size >= 2) expectedTricks += 0.8
                if (trumps.any { it.rank == Rank.QUEEN } && trumps.size >= 3) expectedTricks += 0.6
                // Extra trumps length
                val extraTrumps = max(0, trumps.size - 3)
                expectedTricks += extraTrumps * 0.7
            }
            SpadesVariant.BOSTONS_JOKERS -> {
                if (trumps.any { it.rank == Rank.BIG_JOKER }) expectedTricks += 1.0
                if (trumps.any { it.rank == Rank.LITTLE_JOKER }) expectedTricks += 0.95
                if (trumps.any { it.suit == Suit.SPADES && it.rank == Rank.TWO }) expectedTricks += 0.9
                if (trumps.any { it.suit == Suit.DIAMONDS && it.rank == Rank.TWO }) expectedTricks += 0.85
                if (trumps.any { it.suit == Suit.SPADES && it.rank == Rank.ACE }) expectedTricks += 0.8
                val extraTrumps = max(0, trumps.size - 3)
                expectedTricks += extraTrumps * 0.7
            }
        }

        // Side suit winners & void cuts
        val suits = listOf(Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        for (suit in suits) {
            val suitCards = nonTrumps.filter { it.suit == suit }
            val hasAce = suitCards.any { it.rank == Rank.ACE }
            val hasKing = suitCards.any { it.rank == Rank.KING }
            val hasQueen = suitCards.any { it.rank == Rank.QUEEN }

            if (hasAce) {
                expectedTricks += 0.95
            }
            if (hasKing && suitCards.size >= 2) {
                expectedTricks += if (hasAce) 0.6 else 0.7
            }
            if (hasQueen && suitCards.size >= 3 && hasKing && hasAce) {
                expectedTricks += 0.5
            }

            // Short suits / Voids allow ruffing if holding trumps
            if (suitCards.isEmpty() && trumps.isNotEmpty()) {
                expectedTricks += 0.6
            } else if (suitCards.size == 1 && trumps.size >= 2) {
                expectedTricks += 0.3
            }
        }

        var bid = expectedTricks.toInt()

        // Nil potential check
        val isExtremelyLow = trumps.size <= 1 && nonTrumps.none { it.rank.value >= Rank.KING.value }
        if (isExtremelyLow && (difficulty == AiDifficulty.STRATEGIC || difficulty == AiDifficulty.MASTER)) {
            // Check if partner has already bid a strong hand to cover
            if (partnerBid != null && partnerBid >= 4) {
                return 0 // Go for Nil!
            }
        }

        // Under-bidding or over-bidding adjustments based on difficulty
        when (difficulty) {
            AiDifficulty.CASUAL -> {
                // Casual bids slightly loose
                bid = max(1, min(7, bid))
            }
            AiDifficulty.STRATEGIC -> {
                // Adjust if team is close to 10 bags
                if (currentTeamBags >= 7) {
                    bid = max(1, min(7, bid)) // bid tighter to avoid bag penalty
                } else {
                    bid = max(1, min(8, bid))
                }
            }
            AiDifficulty.MASTER -> {
                // Master accounts for partner's bid
                if (partnerBid != null) {
                    val combined = partnerBid + bid
                    if (combined < 4) {
                        bid = max(1, 4 - partnerBid) // At least table board
                    }
                }
                bid = max(1, min(9, bid))
            }
        }

        return max(1, bid)
    }

    /**
     * Selects the best card to play given the current game state and difficulty.
     */
    fun chooseCardToPlay(
        hand: List<Card>,
        currentTrickPlays: List<PlayedCard>,
        spadesBroken: Boolean,
        difficulty: AiDifficulty,
        variant: SpadesVariant,
        myPosition: PlayerPosition,
        partnerPosition: PlayerPosition,
        partnerIsNil: Boolean,
        myBid: Int,
        myTricksWon: Int,
        teamBid: Int,
        teamTricksWon: Int,
        teamBags: Int
    ): Card {
        val playable = TrickEvaluator.getPlayableCards(hand, currentTrickPlays, spadesBroken, variant)
        if (playable.size == 1) return playable.first()

        val currentWinner = TrickEvaluator.evaluateWinner(currentTrickPlays, variant)
        val partnerIsWinning = currentWinner != null && currentWinner.player == partnerPosition
        val partnerNeedsProtection = partnerIsNil

        // 1. Casual AI: straightforward heuristic
        if (difficulty == AiDifficulty.CASUAL) {
            return if (partnerIsWinning) {
                // Partner has it, slough lowest playable card
                playable.minByOrNull { TrickEvaluator.calculateCardPower(it, currentTrickPlays.firstOrNull()?.card?.suit ?: it.suit, variant) }
                    ?: playable.first()
            } else {
                // Try to win if possible, else dump lowest
                val winningCards = playable.filter { card ->
                    val simPlays = currentTrickPlays + PlayedCard(myPosition, card)
                    TrickEvaluator.evaluateWinner(simPlays, variant)?.player == myPosition
                }
                winningCards.minByOrNull { it.rank.value }
                    ?: playable.minByOrNull { it.rank.value }
                    ?: playable.first()
            }
        }

        // 2. Leading the trick (plays is empty)
        if (currentTrickPlays.isEmpty()) {
            return chooseLeadCard(playable, partnerNeedsProtection, teamTricksWon >= teamBid, difficulty, variant)
        }

        // 3. Following in an existing trick
        val leadCard = currentTrickPlays.first().card
        val leadSuit = TrickEvaluator.getEffectiveSuit(leadCard, variant)
        val highestPowerSoFar = currentTrickPlays.maxOfOrNull {
            TrickEvaluator.calculateCardPower(it.card, leadSuit, variant)
        } ?: 0

        // If partner bid Nil and is currently winning or at risk
        if (partnerNeedsProtection) {
            // If partner has played a card, we MUST overtake it if partner is winning to prevent them from taking the trick!
            val partnerPlay = currentTrickPlays.find { it.player == partnerPosition }
            if (partnerPlay != null && partnerIsWinning) {
                // Partner is winning the trick - OVERTAKE THEM with our highest available card
                val overtakes = playable.filter {
                    TrickEvaluator.calculateCardPower(it, leadSuit, variant) > highestPowerSoFar
                }
                if (overtakes.isNotEmpty()) {
                    return overtakes.maxByOrNull { TrickEvaluator.calculateCardPower(it, leadSuit, variant) }!!
                }
            }
        }

        // Partner is currently winning and partner didn't bid Nil
        if (partnerIsWinning && !partnerNeedsProtection) {
            // Is partner's winning play secure? (e.g. Master level checks if opponent plays next)
            val opponentsRemaining = 3 - currentTrickPlays.size
            if (opponentsRemaining == 0 || difficulty == AiDifficulty.STRATEGIC || difficulty == AiDifficulty.MASTER) {
                // Partner already secured the trick, slough lowest non-trump or dangerous high card
                val nonTrumps = playable.filter { !TrickEvaluator.isTrump(it, variant) }
                return if (nonTrumps.isNotEmpty()) {
                    // Slough lowest card of non-trump
                    nonTrumps.minByOrNull { it.rank.value }!!
                } else {
                    playable.minByOrNull { it.rank.value }!!
                }
            }
        }

        // We need to decide whether to try to win the trick
        val candidateWinners = playable.filter {
            TrickEvaluator.calculateCardPower(it, leadSuit, variant) > highestPowerSoFar
        }

        val teamNeedsTricks = teamTricksWon < teamBid
        val dangerOfBagging = !teamNeedsTricks && teamBags >= 7 && (difficulty == AiDifficulty.MASTER)

        return if (candidateWinners.isNotEmpty()) {
            if (dangerOfBagging) {
                // Avoid taking unnecessary extra trick if bags are dangerous
                playable.minByOrNull { TrickEvaluator.calculateCardPower(it, leadSuit, variant) }!!
            } else {
                // Win the trick with the cheapest winning card (efficiency)
                candidateWinners.minByOrNull { TrickEvaluator.calculateCardPower(it, leadSuit, variant) }!!
            }
        } else {
            // Cannot win the trick, dump lowest card to conserve power
            playable.minByOrNull { TrickEvaluator.calculateCardPower(it, leadSuit, variant) }!!
        }
    }

    private fun chooseLeadCard(
        playable: List<Card>,
        partnerNeedsProtection: Boolean,
        contractMade: Boolean,
        difficulty: AiDifficulty,
        variant: SpadesVariant
    ): Card {
        val nonTrumps = playable.filter { !TrickEvaluator.isTrump(it, variant) }

        if (partnerNeedsProtection) {
            // Lead low off-suits to let partner duck safely under opponents
            if (nonTrumps.isNotEmpty()) {
                return nonTrumps.minByOrNull { it.rank.value }!!
            }
        }

        if (contractMade && difficulty == AiDifficulty.MASTER) {
            // Avoid leading high cards that might accidentally bag us
            if (nonTrumps.isNotEmpty()) {
                return nonTrumps.minByOrNull { it.rank.value }!!
            }
        }

        // Standard lead strategy: Lead guaranteed side-suit Aces
        val sideAces = nonTrumps.filter { it.rank == Rank.ACE }
        if (sideAces.isNotEmpty()) {
            return sideAces.first()
        }

        // Lead low from long side suit to establish or cut
        if (nonTrumps.isNotEmpty()) {
            return nonTrumps.minByOrNull { it.rank.value } ?: playable.first()
        }

        // Only trumps left
        return playable.minByOrNull { it.rank.value } ?: playable.first()
    }
}
