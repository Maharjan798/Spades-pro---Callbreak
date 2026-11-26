package com.example.data.model

enum class Suit(val symbol: String, val displayName: String, val isRed: Boolean) {
    SPADES("♠", "Spades", false),
    HEARTS("♥", "Hearts", true),
    DIAMONDS("♦", "Diamonds", true),
    CLUBS("♣", "Clubs", false),
    JOKER("★", "Joker", false)
}

enum class Rank(val symbol: String, val value: Int) {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13),
    ACE("A", 14),
    LITTLE_JOKER("LJ", 15),
    BIG_JOKER("BJ", 16)
}

data class Card(
    val suit: Suit,
    val rank: Rank,
    val id: String = "${suit.name}_${rank.name}"
) {
    val isSpadeOrTrump: Boolean
        get() = suit == Suit.SPADES || suit == Suit.JOKER || (suit == Suit.DIAMONDS && rank == Rank.TWO)

    fun getDisplayName(isBostonsVariant: Boolean = false): String {
        return when {
            rank == Rank.BIG_JOKER -> "Big Joker"
            rank == Rank.LITTLE_JOKER -> "Little Joker"
            isBostonsVariant && suit == Suit.SPADES && rank == Rank.TWO -> "Big Deuce (2♠)"
            isBostonsVariant && suit == Suit.DIAMONDS && rank == Rank.TWO -> "Little Deuce (2♦)"
            else -> "${rank.symbol}${suit.symbol}"
        }
    }
}

enum class PlayerPosition(val displayName: String) {
    SOUTH("South (You)"),
    WEST("West (Marcus)"),
    NORTH("North (Jordan - Partner)"),
    EAST("East (Aaliyah)");

    val partnerPosition: PlayerPosition
        get() = when (this) {
            SOUTH -> NORTH
            NORTH -> SOUTH
            WEST -> EAST
            EAST -> WEST
        }

    val team: TeamType
        get() = if (this == SOUTH || this == NORTH) TeamType.TEAM_NS else TeamType.TEAM_EW

    fun next(): PlayerPosition = when (this) {
        SOUTH -> WEST
        WEST -> NORTH
        NORTH -> EAST
        EAST -> SOUTH
    }
}

enum class TeamType(val displayName: String) {
    TEAM_NS("Us (South & North)"),
    TEAM_EW("Them (West & East)")
}

enum class SpadesVariant(val title: String, val description: String) {
    STANDARD("Standard (52 Cards)", "Spades trump, Aces high, 2 low. Standard tournament rules."),
    BOSTONS_JOKERS("Bostons with Jokers", "Big Joker, Little Joker, 2♠ (Big Deuce), 2♦ (Little Deuce) as top 4 trumps.")
}

enum class AiDifficulty(val title: String, val description: String) {
    CASUAL("Casual", "Relaxed play with basic strategy"),
    STRATEGIC("Strategic", "Understands nil protection, cuts & bag counting"),
    MASTER("Master", "Advanced card counting, opponent setting & finesses")
}

data class PlayedCard(
    val player: PlayerPosition,
    val card: Card,
    val timestamp: Long = System.currentTimeMillis()
)

data class Trick(
    val id: Int,
    val plays: List<PlayedCard> = emptyList(),
    val leadSuit: Suit? = null,
    val winningPlay: PlayedCard? = null
)

data class RoundScoreRecord(
    val roundNumber: Int,
    val teamNsBid: Int,
    val teamNsWon: Int,
    val teamNsRoundPoints: Int,
    val teamNsBags: Int,
    val teamNsTotalScore: Int,
    val teamEwBid: Int,
    val teamEwWon: Int,
    val teamEwRoundPoints: Int,
    val teamEwBags: Int,
    val teamEwTotalScore: Int,
    val notes: String = ""
)
