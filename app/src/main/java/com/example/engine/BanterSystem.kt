package com.example.engine

import com.example.data.model.PlayerPosition
import kotlin.random.Random

data class BanterBubble(
    val player: PlayerPosition,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

object BanterSystem {

    val QUICK_PHRASES = listOf(
        "Board! ♠",
        "Check the books! 📖",
        "Ain't no cutting here! ✂️",
        "Put some respect on it! 🔥",
        "We got the bags under control! 🎒",
        "Partner, I got your back! 🤝",
        "Reneging check! 🧐",
        "Boston run incoming! 🏆",
        "Straight Nil, no fear! 🎯",
        "You walked right into that! 💥",
        "Count 'em up! 🧮",
        "Who dealt this hand?! 🃏"
    )

    private val NIL_BID_REACTIONS = listOf(
        "Going Nil? I got you covered, partner!",
        "Nil on the table! Watch every lead!",
        "Nil?! We're hunting that book down!"
    )

    private val BIG_CUT_REACTIONS = listOf(
        "Cut it right off! ♠️",
        "Not in my house! ✂️",
        "Boom! Trumped!",
        "Ain't sliding that Ace past me!"
    )

    private val BAG_WARNING_REACTIONS = listOf(
        "Watch the bags! We're running hot! 🎒",
        "Don't take that extra book!",
        "Let them have the trash!"
    )

    private val TRICK_TAKEN_REACTIONS = listOf(
        "Book it! 📚",
        "That's one for the house!",
        "Count that book!",
        "Locked and loaded!"
    )

    fun getRandomBanterForEvent(
        event: GameEvent,
        speaker: PlayerPosition
    ): BanterBubble? {
        val list = when (event) {
            GameEvent.NIL_BID -> NIL_BID_REACTIONS
            GameEvent.CUT_TRUMP -> BIG_CUT_REACTIONS
            GameEvent.BAG_WARNING -> BAG_WARNING_REACTIONS
            GameEvent.TRICK_WON -> TRICK_TAKEN_REACTIONS
        }
        if (list.isEmpty()) return null
        val text = list[Random.nextInt(list.size)]
        return BanterBubble(player = speaker, text = text)
    }
}

enum class GameEvent {
    NIL_BID,
    CUT_TRUMP,
    BAG_WARNING,
    TRICK_WON
}
