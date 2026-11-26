package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PlayerStatsEntity
import com.example.data.local.SpadesRepository
import com.example.data.model.AiDifficulty
import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.PlayerPosition
import com.example.data.model.Rank
import com.example.data.model.RoundScoreRecord
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit
import com.example.data.model.TeamType
import com.example.data.model.Trick
import com.example.engine.BanterBubble
import com.example.engine.BanterSystem
import com.example.engine.DeckFactory
import com.example.engine.GameEvent
import com.example.engine.SpadesAi
import com.example.engine.TrickEvaluator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class SpadesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpadesRepository(application)
    private val _uiState = MutableStateFlow(SpadesUiState())
    val uiState: StateFlow<SpadesUiState> = _uiState.asStateFlow()

    private var aiTurnJob: Job? = null

    init {
        viewModelScope.launch {
            repository.playerStats.collect { stats ->
                _uiState.update { it.copy(playerStats = stats ?: PlayerStatsEntity()) }
            }
        }
        viewModelScope.launch {
            repository.matchHistory.collect { history ->
                _uiState.update { it.copy(matchHistory = history) }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun updateVariant(variant: SpadesVariant) {
        _uiState.update { it.copy(variant = variant) }
    }

    fun updateDifficulty(difficulty: AiDifficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun updateTargetScore(score: Int) {
        _uiState.update { it.copy(targetScore = score) }
    }

    fun updateTableTheme(theme: TableTheme) {
        _uiState.update { it.copy(tableTheme = theme) }
    }

    fun toggleSound() {
        _uiState.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    }

    fun dismissError() {
        _uiState.update { it.copy(lastErrorMessage = null) }
    }

    fun sendBanter(text: String, sender: PlayerPosition = PlayerPosition.SOUTH) {
        val bubble = BanterBubble(player = sender, text = text)
        _uiState.update { state ->
            val updated = (state.activeBanterBubbles + bubble).takeLast(4)
            state.copy(activeBanterBubbles = updated)
        }
        // Auto dismiss after 3.5s
        viewModelScope.launch {
            delay(3500)
            _uiState.update { state ->
                state.copy(activeBanterBubbles = state.activeBanterBubbles.filter { it != bubble })
            }
        }
    }

    fun toggleScorepad() {
        _uiState.update { it.copy(showScorepadSheet = !it.showScorepadSheet) }
    }

    // --- GAME ENGINE FLOW ---

    fun startNewGame(
        mode: GameMode = GameMode.SINGLE_PLAYER_AI,
        customVariant: SpadesVariant? = null,
        customDifficulty: AiDifficulty? = null,
        customTargetScore: Int? = null
    ) {
        aiTurnJob?.cancel()

        val variant = customVariant ?: _uiState.value.variant
        val difficulty = customDifficulty ?: _uiState.value.difficulty
        val targetScore = customTargetScore ?: _uiState.value.targetScore

        val isPassAndPlay = mode == GameMode.PASS_AND_PLAY

        val initialPlayers = mapOf(
            PlayerPosition.SOUTH to PlayerState(position = PlayerPosition.SOUTH, isHuman = true, name = "South (You)"),
            PlayerPosition.WEST to PlayerState(position = PlayerPosition.WEST, isHuman = isPassAndPlay, name = "West (Marcus)"),
            PlayerPosition.NORTH to PlayerState(position = PlayerPosition.NORTH, isHuman = isPassAndPlay, name = "North (Jordan)"),
            PlayerPosition.EAST to PlayerState(position = PlayerPosition.EAST, isHuman = isPassAndPlay, name = "East (Aaliyah)")
        )

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GAME_TABLE,
                gameMode = mode,
                variant = variant,
                difficulty = difficulty,
                targetScore = targetScore,
                roundNumber = 1,
                teamNsTotalScore = 0,
                teamNsBags = 0,
                teamEwTotalScore = 0,
                teamEwBags = 0,
                roundScoreHistory = emptyList(),
                winnerTeam = null,
                players = initialPlayers,
                dealerPosition = PlayerPosition.EAST,
                activeBanterBubbles = emptyList()
            )
        }

        dealNewRound()
    }

    fun dealNewRound() {
        aiTurnJob?.cancel()
        val state = _uiState.value
        val deck = DeckFactory.createDeck(state.variant)

        // Deal 13 cards to each player
        val pSouthCards = sortHand(deck.subList(0, 13), state.variant)
        val pWestCards = sortHand(deck.subList(13, 26), state.variant)
        val pNorthCards = sortHand(deck.subList(26, 39), state.variant)
        val pEastCards = sortHand(deck.subList(39, 52), state.variant)

        val nextDealer = if (state.gamePhase == GamePhase.IDLE) PlayerPosition.EAST else state.dealerPosition.next()
        val firstBidder = nextDealer.next()

        val updatedPlayers = state.players.mapValues { (pos, player) ->
            val hand = when (pos) {
                PlayerPosition.SOUTH -> pSouthCards
                PlayerPosition.WEST -> pWestCards
                PlayerPosition.NORTH -> pNorthCards
                PlayerPosition.EAST -> pEastCards
            }
            player.copy(
                hand = hand,
                bid = null,
                isBlindNil = false,
                tricksWon = 0
            )
        }

        _uiState.update {
            it.copy(
                gamePhase = GamePhase.DEALING,
                dealerPosition = nextDealer,
                activeTurn = firstBidder,
                spadesBroken = false,
                players = updatedPlayers,
                currentTrickNumber = 1,
                currentTrick = Trick(id = 1),
                lastCompletedTrick = null,
                showBiddingDialog = false,
                showExchangeDialog = false,
                exchangeSelectedCards = emptySet()
            )
        }

        viewModelScope.launch {
            delay(1200) // Dealing animation pause
            _uiState.update { it.copy(gamePhase = GamePhase.BIDDING) }
            proceedBiddingFlow()
        }
    }

    private fun sortHand(cards: List<Card>, variant: SpadesVariant): List<Card> {
        val suitOrder = mapOf(
            Suit.SPADES to 4,
            Suit.JOKER to 4, // Group with spades in Bostons
            Suit.HEARTS to 3,
            Suit.CLUBS to 2,
            Suit.DIAMONDS to 1
        )
        return cards.sortedWith { a, b ->
            val sA = suitOrder[a.suit] ?: 0
            val sB = suitOrder[b.suit] ?: 0
            if (sA != sB) {
                sB - sA
            } else {
                b.rank.value - a.rank.value
            }
        }
    }

    private fun proceedBiddingFlow() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.BIDDING) return

        val bidder = state.activeTurn
        val player = state.players[bidder] ?: return

        if (player.isHuman) {
            _uiState.update { it.copy(showBiddingDialog = true) }
        } else {
            // AI Player bids
            _uiState.update { it.copy(showBiddingDialog = false) }
            aiTurnJob = viewModelScope.launch {
                delay(800)
                val partnerBid = state.players[bidder.partnerPosition]?.bid
                val currentBags = if (bidder.team == TeamType.TEAM_NS) state.teamNsBags else state.teamEwBags
                val aiBid = SpadesAi.calculateBid(
                    hand = player.hand,
                    difficulty = state.difficulty,
                    variant = state.variant,
                    partnerBid = partnerBid,
                    currentTeamBags = currentBags
                )
                submitBid(bidder, aiBid, isBlindNil = false)
            }
        }
    }

    fun submitBid(position: PlayerPosition, bidValue: Int, isBlindNil: Boolean) {
        val state = _uiState.value
        val player = state.players[position] ?: return
        val updatedPlayer = player.copy(bid = bidValue, isBlindNil = isBlindNil)
        val updatedPlayers = state.players + (position to updatedPlayer)

        // Trigger reaction if Nil
        if (bidValue == 0 || isBlindNil) {
            BanterSystem.getRandomBanterForEvent(GameEvent.NIL_BID, position.partnerPosition)?.let {
                sendBanter(it.text, it.player)
            }
        }

        // Check if all 4 players have bid
        val allBid = updatedPlayers.values.all { it.bid != null }

        if (allBid) {
            // Check if any player bid Blind Nil
            val hasBlindNil = updatedPlayers.values.any { it.isBlindNil }
            if (hasBlindNil) {
                _uiState.update {
                    it.copy(
                        players = updatedPlayers,
                        showBiddingDialog = false,
                        gamePhase = GamePhase.CARD_EXCHANGE,
                        activeTurn = state.dealerPosition.next()
                    )
                }
                handleBlindNilExchange()
            } else {
                val firstTrickLeader = state.dealerPosition.next()
                _uiState.update {
                    it.copy(
                        players = updatedPlayers,
                        showBiddingDialog = false,
                        gamePhase = GamePhase.PLAYING,
                        activeTurn = firstTrickLeader
                    )
                }
                checkNextTurn()
            }
        } else {
            val nextBidder = position.next()
            _uiState.update {
                it.copy(
                    players = updatedPlayers,
                    activeTurn = nextBidder,
                    showBiddingDialog = false
                )
            }
            proceedBiddingFlow()
        }
    }

    private fun handleBlindNilExchange() {
        // Blind nil card exchange between partners
        // For simplicity & smooth flow: exchange lowest 2 cards of non-nil partner with highest 2 cards of nil partner
        val state = _uiState.value
        val updatedPlayers = state.players.toMutableMap()

        for (team in listOf(TeamType.TEAM_NS, TeamType.TEAM_EW)) {
            val teamPlayers = updatedPlayers.values.filter { it.position.team == team }
            val nilPlayer = teamPlayers.find { it.isBlindNil }
            val partner = teamPlayers.find { !it.isBlindNil }

            if (nilPlayer != null && partner != null) {
                val nilHighCards = nilPlayer.hand.sortedByDescending { it.rank.value }.take(2)
                val partnerLowCards = partner.hand.sortedBy { it.rank.value }.take(2)

                val newNilHand = sortHand(nilPlayer.hand - nilHighCards.toSet() + partnerLowCards, state.variant)
                val newPartnerHand = sortHand(partner.hand - partnerLowCards.toSet() + nilHighCards, state.variant)

                updatedPlayers[nilPlayer.position] = nilPlayer.copy(hand = newNilHand)
                updatedPlayers[partner.position] = partner.copy(hand = newPartnerHand)
            }
        }

        val firstLeader = state.dealerPosition.next()
        viewModelScope.launch {
            delay(1000)
            _uiState.update {
                it.copy(
                    players = updatedPlayers,
                    gamePhase = GamePhase.PLAYING,
                    activeTurn = firstLeader
                )
            }
            checkNextTurn()
        }
    }

    fun playCard(position: PlayerPosition, card: Card) {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.PLAYING) return
        if (state.activeTurn != position) return

        val player = state.players[position] ?: return
        val currentPlays = state.currentTrick.plays

        // Validate play legality
        val playableCards = TrickEvaluator.getPlayableCards(
            hand = player.hand,
            currentTrickPlays = currentPlays,
            spadesBroken = state.spadesBroken,
            variant = state.variant
        )

        if (!playableCards.contains(card)) {
            val leadSuit = currentPlays.firstOrNull()?.card?.suit?.displayName ?: "Spades"
            val errorMsg = if (currentPlays.isEmpty()) {
                "Cannot lead Spades until broken or holding only Spades!"
            } else {
                "Must follow lead suit ($leadSuit)!"
            }
            _uiState.update { it.copy(lastErrorMessage = errorMsg) }
            return
        }

        _uiState.update { it.copy(lastErrorMessage = null) }

        // Execute play
        val updatedHand = player.hand - card
        val updatedPlayer = player.copy(hand = updatedHand)
        val newPlay = PlayedCard(player = position, card = card)
        val updatedPlays = currentPlays + newPlay

        val isTrumpCard = TrickEvaluator.isTrump(card, state.variant)
        val leadSuit = state.currentTrick.leadSuit ?: TrickEvaluator.getEffectiveSuit(card, state.variant)
        val isCut = currentPlays.isNotEmpty() && leadSuit != Suit.SPADES && isTrumpCard

        val spadesBrokenNow = state.spadesBroken || isTrumpCard

        val updatedTrick = state.currentTrick.copy(
            plays = updatedPlays,
            leadSuit = leadSuit
        )

        val updatedPlayers = state.players + (position to updatedPlayer)

        if (isCut) {
            BanterSystem.getRandomBanterForEvent(GameEvent.CUT_TRUMP, position)?.let {
                sendBanter(it.text, it.player)
            }
        }

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                currentTrick = updatedTrick,
                spadesBroken = spadesBrokenNow
            )
        }

        // Check if trick completed (4 plays)
        if (updatedPlays.size == 4) {
            resolveCompletedTrick(updatedTrick, updatedPlayers, spadesBrokenNow)
        } else {
            val nextTurn = position.next()
            _uiState.update { it.copy(activeTurn = nextTurn) }
            checkNextTurn()
        }
    }

    private fun resolveCompletedTrick(
        completedTrick: Trick,
        currentPlayers: Map<PlayerPosition, PlayerState>,
        spadesBroken: Boolean
    ) {
        val state = _uiState.value
        val winningPlay = TrickEvaluator.evaluateWinner(completedTrick.plays, state.variant) ?: completedTrick.plays.first()
        val winningPlayerPos = winningPlay.player

        val winnerState = currentPlayers[winningPlayerPos] ?: return
        val updatedWinnerState = winnerState.copy(tricksWon = winnerState.tricksWon + 1)
        val updatedPlayers = currentPlayers + (winningPlayerPos to updatedWinnerState)

        _uiState.update {
            it.copy(
                gamePhase = GamePhase.TRICK_RESOLVING,
                currentTrick = completedTrick.copy(winningPlay = winningPlay),
                players = updatedPlayers
            )
        }

        viewModelScope.launch {
            delay(1300) // Highlight winning trick card
            val trickNum = state.currentTrickNumber

            if (trickNum >= 13) {
                // All 13 tricks played, calculate round score!
                finishRound(updatedPlayers)
            } else {
                _uiState.update {
                    it.copy(
                        gamePhase = GamePhase.PLAYING,
                        currentTrickNumber = trickNum + 1,
                        currentTrick = Trick(id = trickNum + 1),
                        lastCompletedTrick = completedTrick.copy(winningPlay = winningPlay),
                        activeTurn = winningPlayerPos
                    )
                }
                checkNextTurn()
            }
        }
    }

    private fun checkNextTurn() {
        val state = _uiState.value
        if (state.gamePhase != GamePhase.PLAYING) return

        val active = state.activeTurn
        val player = state.players[active] ?: return

        if (!player.isHuman) {
            aiTurnJob?.cancel()
            aiTurnJob = viewModelScope.launch {
                delay(750) // Natural card play delay
                val pState = _uiState.value
                val currentP = pState.players[active] ?: return@launch
                val partner = pState.players[active.partnerPosition] ?: return@launch

                val teamBid = (currentP.bid ?: 0) + (partner.bid ?: 0)
                val teamWon = currentP.tricksWon + partner.tricksWon
                val teamBags = if (active.team == TeamType.TEAM_NS) pState.teamNsBags else pState.teamEwBags

                val chosenCard = SpadesAi.chooseCardToPlay(
                    hand = currentP.hand,
                    currentTrickPlays = pState.currentTrick.plays,
                    spadesBroken = pState.spadesBroken,
                    difficulty = pState.difficulty,
                    variant = pState.variant,
                    myPosition = active,
                    partnerPosition = active.partnerPosition,
                    partnerIsNil = partner.isNil,
                    myBid = currentP.bid ?: 0,
                    myTricksWon = currentP.tricksWon,
                    teamBid = teamBid,
                    teamTricksWon = teamWon,
                    teamBags = teamBags
                )

                playCard(active, chosenCard)
            }
        }
    }

    private fun finishRound(finalPlayers: Map<PlayerPosition, PlayerState>) {
        val state = _uiState.value

        // NS calculations
        val pSouth = finalPlayers[PlayerPosition.SOUTH]!!
        val pNorth = finalPlayers[PlayerPosition.NORTH]!!
        val nsTricks = pSouth.tricksWon + pNorth.tricksWon

        // EW calculations
        val pWest = finalPlayers[PlayerPosition.WEST]!!
        val pEast = finalPlayers[PlayerPosition.EAST]!!
        val ewTricks = pWest.tricksWon + pEast.tricksWon

        val nsScoreResult = calculateTeamRoundScore(
            p1 = pSouth,
            p2 = pNorth,
            totalTeamWon = nsTricks,
            currentBags = state.teamNsBags
        )

        val ewScoreResult = calculateTeamRoundScore(
            p1 = pWest,
            p2 = pEast,
            totalTeamWon = ewTricks,
            currentBags = state.teamEwBags
        )

        val newNsScore = state.teamNsTotalScore + nsScoreResult.roundPoints
        val newEwScore = state.teamEwTotalScore + ewScoreResult.roundPoints

        val record = RoundScoreRecord(
            roundNumber = state.roundNumber,
            teamNsBid = nsScoreResult.combinedBid,
            teamNsWon = nsTricks,
            teamNsRoundPoints = nsScoreResult.roundPoints,
            teamNsBags = nsScoreResult.newBags,
            teamNsTotalScore = newNsScore,
            teamEwBid = ewScoreResult.combinedBid,
            teamEwWon = ewTricks,
            teamEwRoundPoints = ewScoreResult.roundPoints,
            teamEwBags = ewScoreResult.newBags,
            teamEwTotalScore = newEwScore,
            notes = nsScoreResult.notes + (if (ewScoreResult.notes.isNotEmpty()) " | " + ewScoreResult.notes else "")
        )

        val updatedHistory = state.roundScoreHistory + record

        // Check victory
        val isGameOver = newNsScore >= state.targetScore || newEwScore >= state.targetScore ||
                newNsScore <= -200 || newEwScore <= -200

        val winner = if (isGameOver) {
            if (newNsScore > newEwScore) TeamType.TEAM_NS else TeamType.TEAM_EW
        } else null

        _uiState.update {
            it.copy(
                gamePhase = if (isGameOver) GamePhase.GAME_OVER else GamePhase.ROUND_SUMMARY,
                teamNsTotalScore = newNsScore,
                teamNsBags = nsScoreResult.newBags,
                teamEwTotalScore = newEwScore,
                teamEwBags = ewScoreResult.newBags,
                roundScoreHistory = updatedHistory,
                winnerTeam = winner
            )
        }

        if (isGameOver && winner != null) {
            saveGameResultsToDatabase(
                isWin = winner == TeamType.TEAM_NS,
                finalNsScore = newNsScore,
                finalEwScore = newEwScore,
                rounds = state.roundNumber,
                pSouthTricks = pSouth.tricksWon,
                pSouthNilAttempted = if (pSouth.bid == 0) 1 else 0,
                pSouthNilMade = if (pSouth.bid == 0 && pSouth.tricksWon == 0) 1 else 0,
                pSouthBlindNilAttempted = if (pSouth.isBlindNil) 1 else 0,
                pSouthBlindNilMade = if (pSouth.isBlindNil && pSouth.tricksWon == 0) 1 else 0,
                bags = nsScoreResult.newBags
            )
        }
    }

    private data class TeamRoundCalc(
        val combinedBid: Int,
        val roundPoints: Int,
        val newBags: Int,
        val notes: String
    )

    private fun calculateTeamRoundScore(
        p1: PlayerState,
        p2: PlayerState,
        totalTeamWon: Int,
        currentBags: Int
    ): TeamRoundCalc {
        var points = 0
        var bagsAccum = currentBags
        val noteList = mutableListOf<String>()

        val p1Nil = p1.isNil
        val p2Nil = p2.isNil

        // Calculate non-nil bids
        val nonNilBid1 = if (p1Nil) 0 else (p1.bid ?: 0)
        val nonNilBid2 = if (p2Nil) 0 else (p2.bid ?: 0)
        val teamContract = nonNilBid1 + nonNilBid2

        // Nil scoring for Player 1
        if (p1Nil) {
            val isBlind = p1.isBlindNil
            val reward = if (isBlind) 100 else 50
            if (p1.tricksWon == 0) {
                points += reward
                noteList.add("${p1.name} Nil (+${reward})")
            } else {
                points -= reward
                noteList.add("${p1.name} Nil Set (-${reward})")
            }
        }

        // Nil scoring for Player 2
        if (p2Nil) {
            val isBlind = p2.isBlindNil
            val reward = if (isBlind) 100 else 50
            if (p2.tricksWon == 0) {
                points += reward
                noteList.add("${p2.name} Nil (+${reward})")
            } else {
                points -= reward
                noteList.add("${p2.name} Nil Set (-${reward})")
            }
        }

        // Contract scoring
        if (teamContract > 0) {
            // If partner is nil, non-nil player must make the contract alone
            val nonNilTricks = when {
                p1Nil && p2Nil -> totalTeamWon
                p1Nil -> p2.tricksWon
                p2Nil -> p1.tricksWon
                else -> totalTeamWon
            }

            if (nonNilTricks >= teamContract) {
                val overtricks = nonNilTricks - teamContract
                points += (teamContract * 10) + overtricks
                bagsAccum += overtricks

                if (overtricks > 0) {
                    noteList.add("+$overtricks bags")
                }
            } else {
                points -= (teamContract * 10)
                noteList.add("Set (-${teamContract * 10})")
            }
        }

        // 10 Bags penalty check (-100 pts)
        if (bagsAccum >= 10) {
            points -= 100
            bagsAccum -= 10
            noteList.add("BAG PENALTY (-100)")
        }

        return TeamRoundCalc(
            combinedBid = teamContract,
            roundPoints = points,
            newBags = bagsAccum,
            notes = noteList.joinToString(", ")
        )
    }

    fun continueToNextRound() {
        _uiState.update {
            it.copy(
                roundNumber = it.roundNumber + 1
            )
        }
        dealNewRound()
    }

    private fun saveGameResultsToDatabase(
        isWin: Boolean,
        finalNsScore: Int,
        finalEwScore: Int,
        rounds: Int,
        pSouthTricks: Int,
        pSouthNilAttempted: Int,
        pSouthNilMade: Int,
        pSouthBlindNilAttempted: Int,
        pSouthBlindNilMade: Int,
        bags: Int
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            repository.recordMatchEnd(
                isWin = isWin,
                variant = state.variant.title,
                difficulty = state.difficulty.title,
                targetScore = state.targetScore,
                teamNsScore = finalNsScore,
                teamEwScore = finalEwScore,
                rounds = rounds,
                tricksWonByPlayer = pSouthTricks,
                nilsAttempted = pSouthNilAttempted,
                nilsMade = pSouthNilMade,
                blindNilsAttempted = pSouthBlindNilAttempted,
                blindNilsMade = pSouthBlindNilMade,
                bagsAccumulated = bags
            )

            // Update stats
            val current = state.playerStats ?: PlayerStatsEntity()
            val updated = current.copy(
                gamesPlayed = current.gamesPlayed + 1,
                gamesWon = current.gamesWon + (if (isWin) 1 else 0),
                gamesLost = current.gamesLost + (if (!isWin) 1 else 0),
                totalTricksWon = current.totalTricksWon + pSouthTricks,
                nilsAttempted = current.nilsAttempted + pSouthNilAttempted,
                nilsMade = current.nilsMade + pSouthNilMade,
                blindNilsAttempted = current.blindNilsAttempted + pSouthBlindNilAttempted,
                blindNilsMade = current.blindNilsMade + pSouthBlindNilMade,
                totalBagsAccumulated = current.totalBagsAccumulated + bags,
                highestScore = max(current.highestScore, finalNsScore)
            )
            repository.savePlayerStats(updated)
        }
    }
}
