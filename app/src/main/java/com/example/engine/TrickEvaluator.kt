package com.example.engine

import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.Rank
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit

object TrickEvaluator {

    /**
     * Determines whether a card is treated as a Trump in the given variant.
     */
    fun isTrump(card: Card, variant: SpadesVariant): Boolean {
        return when (variant) {
            SpadesVariant.STANDARD -> card.suit == Suit.SPADES
            SpadesVariant.BOSTONS_JOKERS -> {
                card.suit == Suit.SPADES ||
                        card.suit == Suit.JOKER ||
                        (card.suit == Suit.DIAMONDS && card.rank == Rank.TWO)
            }
        }
    }

    /**
     * Returns the effective effective suit of a card for following-suit rules.
     * In Bostons, Jokers and 2♦ act as Spades/Trump for following suit.
     */
    fun getEffectiveSuit(card: Card, variant: SpadesVariant): Suit {
        return if (isTrump(card, variant)) {
            Suit.SPADES
        } else {
            card.suit
        }
    }

    /**
     * Computes the numerical power of a card in the context of a trick.
     * Trump cards score highest (500+).
     * Cards of the lead suit score 10..100.
     * Cards of other off-suits score 0.
     */
    fun calculateCardPower(card: Card, leadSuit: Suit, variant: SpadesVariant): Int {
        if (isTrump(card, variant)) {
            return when (variant) {
                SpadesVariant.STANDARD -> 500 + card.rank.value
                SpadesVariant.BOSTONS_JOKERS -> {
                    when {
                        card.rank == Rank.BIG_JOKER -> 1000
                        card.rank == Rank.LITTLE_JOKER -> 900
                        card.suit == Suit.SPADES && card.rank == Rank.TWO -> 800
                        card.suit == Suit.DIAMONDS && card.rank == Rank.TWO -> 700
                        card.suit == Suit.SPADES -> 500 + card.rank.value
                        else -> 500
                    }
                }
            }
        }

        // Card matches the led suit
        if (getEffectiveSuit(card, variant) == leadSuit) {
            return 50 + card.rank.value
        }

        // Off-suit, cannot win trick
        return 0
    }

    /**
     * Finds the winning play among cards played so far in a trick.
     */
    fun evaluateWinner(plays: List<PlayedCard>, variant: SpadesVariant): PlayedCard? {
        if (plays.isEmpty()) return null
        val leadSuit = getEffectiveSuit(plays.first().card, variant)

        var bestPlay = plays.first()
        var highestPower = calculateCardPower(bestPlay.card, leadSuit, variant)

        for (i in 1 until plays.size) {
            val play = plays[i]
            val power = calculateCardPower(play.card, leadSuit, variant)
            if (power > highestPower) {
                highestPower = power
                bestPlay = play
            }
        }
        return bestPlay
    }

    /**
     * Returns a list of legal cards the player can play from their hand.
     * Enforces:
     * 1. Must follow lead suit if hand contains cards of the lead suit.
     * 2. If leading (plays is empty), cannot lead Spades/Trumps unless spades are broken or hand only contains trumps.
     */
    fun getPlayableCards(
        hand: List<Card>,
        currentTrickPlays: List<PlayedCard>,
        spadesBroken: Boolean,
        variant: SpadesVariant
    ): List<Card> {
        if (hand.isEmpty()) return emptyList()

        // 1. Leading a new trick
        if (currentTrickPlays.isEmpty()) {
            if (spadesBroken) {
                return hand
            }
            val nonTrumps = hand.filter { !isTrump(it, variant) }
            return nonTrumps.ifEmpty {
                // Player only has trumps, allowed to lead spades
                hand
            }
        }

        // 2. Following suit in an existing trick
        val leadSuit = getEffectiveSuit(currentTrickPlays.first().card, variant)
        val matchingSuitCards = hand.filter { getEffectiveSuit(it, variant) == leadSuit }

        return if (matchingSuitCards.isNotEmpty()) {
            matchingSuitCards
        } else {
            // Player is void in lead suit: can play ANY card (cut with spade or slough off-suit)
            hand
        }
    }
}
