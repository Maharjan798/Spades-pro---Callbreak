package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey val id: Int = 1,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val gamesLost: Int = 0,
    val totalTricksWon: Int = 0,
    val nilsAttempted: Int = 0,
    val nilsMade: Int = 0,
    val blindNilsAttempted: Int = 0,
    val blindNilsMade: Int = 0,
    val totalBagsAccumulated: Int = 0,
    val highestScore: Int = 0,
    val bostonsAchieved: Int = 0
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val variant: String,
    val difficulty: String,
    val targetScore: Int,
    val teamNsFinalScore: Int,
    val teamEwFinalScore: Int,
    val isWin: Boolean,
    val totalRounds: Int
)

@Dao
interface SpadesDao {
    @Query("SELECT * FROM player_stats WHERE id = 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlayerStats(stats: PlayerStatsEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 30")
    fun getMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()
}

@Database(
    entities = [PlayerStatsEntity::class, MatchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpadesDatabase : RoomDatabase() {
    abstract fun spadesDao(): SpadesDao
}
