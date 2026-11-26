package com.example.engine

import com.example.data.model.Card
import com.example.data.model.Rank
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit

object DeckFactory {
    fun createDeck(variant: SpadesVariant): List<Card> {
        return when (variant) {
            SpadesVariant.STANDARD -> createStandardDeck()
            SpadesVariant.BOSTONS_JOKERS -> createBostonsDeck()
        }
    }

    private fun createStandardDeck(): List<Card> {
        val suits = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        val ranks = listOf(
            Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN,
            Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE
        )
        val deck = mutableListOf<Card>()
        for (suit in suits) {
            for (rank in ranks) {
                deck.add(Card(suit = suit, rank = rank))
            }
        }
        return deck.shuffled()
    }

    /**
     * Bostons variant standard deck with 52 cards:
     * - Big Joker (top trump)
     * - Little Joker (2nd trump)
     * - 2 of Spades (Big Deuce, 3rd trump)
     * - 2 of Diamonds (Little Deuce, 4th trump)
     * - Remove 2 of Clubs and 2 of Hearts so the deck remains exactly 52 cards for 4 players (13 each).
     */
    private fun createBostonsDeck(): List<Card> {
        val deck = mutableListOf<Card>()

        // Add Jokers
        deck.add(Card(suit = Suit.JOKER, rank = Rank.BIG_JOKER))
        deck.add(Card(suit = Suit.JOKER, rank = Rank.LITTLE_JOKER))

        val suits = listOf(Suit.SPADES, Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS)
        val ranks = listOf(
            Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN,
            Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE
        )

        for (suit in suits) {
            for (rank in ranks) {
                // In 52-card Bostons, omit 2 of Clubs and 2 of Hearts to accommodate the 2 Jokers
                if (rank == Rank.TWO && (suit == Suit.CLUBS || suit == Suit.HEARTS)) {
                    continue
                }
                deck.add(Card(suit = suit, rank = rank))
            }
        }

        return deck.shuffled()
    }
}
