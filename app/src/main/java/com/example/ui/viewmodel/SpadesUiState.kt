package com.example.ui.viewmodel

import com.example.data.local.MatchHistoryEntity
import com.example.data.local.PlayerStatsEntity
import com.example.data.model.AiDifficulty
import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.PlayerPosition
import com.example.data.model.RoundScoreRecord
import com.example.data.model.SpadesVariant
import com.example.data.model.TeamType
import com.example.data.model.Trick
import com.example.engine.BanterBubble

enum class AppScreen {
    MENU,
    GAME_TABLE,
    LOBBY,
    STATS,
    RULEBOOK,
    SETTINGS
}

enum class GamePhase {
    IDLE,
    DEALING,
    BIDDING,
    CARD_EXCHANGE,
    PLAYING,
    TRICK_RESOLVING,
    ROUND_SUMMARY,
    GAME_OVER
}

enum class GameMode {
    SINGLE_PLAYER_AI,
    PASS_AND_PLAY,
    ONLINE_SIMULATION
}

enum class TableTheme(val displayName: String, val primaryColorHex: Long, val secondaryColorHex: Long) {
    EMERALD_CLASSIC("Emerald Classic", 0xFF0D3823, 0xFF061A10),
    ROYAL_BLUE("Royal Blue", 0xFF0D2342, 0xFF061120),
    MIDNIGHT_VELVET("Midnight Velvet", 0xFF24142E, 0xFF120A17),
    CRIMSON_GOLD("Crimson & Gold", 0xFF3D0E14, 0xFF1F070A)
}

data class PlayerState(
    val position: PlayerPosition,
    val isHuman: Boolean = true,
    val name: String,
    val hand: List<Card> = emptyList(),
    val bid: Int? = null,
    val isBlindNil: Boolean = false,
    val tricksWon: Int = 0
) {
    val isNil: Boolean
        get() = bid == 0 || isBlindNil
}

data class SpadesUiState(
    val currentScreen: AppScreen = AppScreen.MENU,
    val gameMode: GameMode = GameMode.SINGLE_PLAYER_AI,
    val gamePhase: GamePhase = GamePhase.IDLE,
    val variant: SpadesVariant = SpadesVariant.STANDARD,
    val difficulty: AiDifficulty = AiDifficulty.STRATEGIC,
    val targetScore: Int = 500,
    val tableTheme: TableTheme = TableTheme.EMERALD_CLASSIC,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,

    // Match state
    val roundNumber: Int = 1,
    val dealerPosition: PlayerPosition = PlayerPosition.EAST,
    val activeTurn: PlayerPosition = PlayerPosition.SOUTH,
    val spadesBroken: Boolean = false,
    val players: Map<PlayerPosition, PlayerState> = emptyMap(),

    // Current Trick
    val currentTrickNumber: Int = 1,
    val currentTrick: Trick = Trick(id = 1),
    val lastCompletedTrick: Trick? = null,

    // Scoring & Bags
    val teamNsTotalScore: Int = 0,
    val teamNsBags: Int = 0,
    val teamEwTotalScore: Int = 0,
    val teamEwBags: Int = 0,
    val roundScoreHistory: List<RoundScoreRecord> = emptyList(),

    // UI overlays & Dialogs
    val showBiddingDialog: Boolean = false,
    val showScorepadSheet: Boolean = false,
    val showExchangeDialog: Boolean = false,
    val exchangeSelectedCards: Set<Card> = emptySet(),
    val lastErrorMessage: String? = null,
    val activeBanterBubbles: List<BanterBubble> = emptyList(),

    // Online Lobby Simulation
    val roomCode: String = "SPADES-7842",
    val pingMs: Int = 24,
    val lobbyPlayersReady: Map<PlayerPosition, Boolean> = mapOf(
        PlayerPosition.SOUTH to true,
        PlayerPosition.WEST to true,
        PlayerPosition.NORTH to true,
        PlayerPosition.EAST to true
    ),

    // Database & Stats
    val playerStats: PlayerStatsEntity? = null,
    val matchHistory: List<MatchHistoryEntity> = emptyList(),
    val winnerTeam: TeamType? = null
)
