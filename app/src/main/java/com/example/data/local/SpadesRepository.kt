package com.example.data.local

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class SpadesRepository(context: Context) {
    private val database: SpadesDatabase = Room.databaseBuilder(
        context.applicationContext,
        SpadesDatabase::class.java,
        "spades_database.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = database.spadesDao()

    val playerStats: Flow<PlayerStatsEntity?> = dao.getPlayerStats()
    val matchHistory: Flow<List<MatchHistoryEntity>> = dao.getMatchHistory()

    suspend fun recordMatchEnd(
        isWin: Boolean,
        variant: String,
        difficulty: String,
        targetScore: Int,
        teamNsScore: Int,
        teamEwScore: Int,
        rounds: Int,
        tricksWonByPlayer: Int,
        nilsAttempted: Int,
        nilsMade: Int,
        blindNilsAttempted: Int,
        blindNilsMade: Int,
        bagsAccumulated: Int
    ) {
        dao.insertMatchHistory(
            MatchHistoryEntity(
                variant = variant,
                difficulty = difficulty,
                targetScore = targetScore,
                teamNsFinalScore = teamNsScore,
                teamEwFinalScore = teamEwScore,
                isWin = isWin,
                totalRounds = rounds
            )
        )

        // Update aggregated stats
        // We'll update the singleton row with updated stats
    }

    suspend fun savePlayerStats(stats: PlayerStatsEntity) {
        dao.updatePlayerStats(stats)
    }

    suspend fun clearAllHistory() {
        dao.clearHistory()
    }
}
