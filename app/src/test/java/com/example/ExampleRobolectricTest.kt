package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Card
import com.example.data.model.PlayedCard
import com.example.data.model.PlayerPosition
import com.example.data.model.Rank
import com.example.data.model.SpadesVariant
import com.example.data.model.Suit
import com.example.engine.TrickEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Spades Pro", appName)
  }

  @Test
  fun `trick evaluator standard spades trump wins`() {
    val plays = listOf(
      PlayedCard(PlayerPosition.SOUTH, Card(Suit.HEARTS, Rank.ACE)),
      PlayedCard(PlayerPosition.WEST, Card(Suit.HEARTS, Rank.KING)),
      PlayedCard(PlayerPosition.NORTH, Card(Suit.SPADES, Rank.TWO)), // Cut with 2 of Spades
      PlayedCard(PlayerPosition.EAST, Card(Suit.HEARTS, Rank.QUEEN))
    )
    val winner = TrickEvaluator.evaluateWinner(plays, SpadesVariant.STANDARD)
    assertNotNull(winner)
    assertEquals(PlayerPosition.NORTH, winner?.player)
  }

  @Test
  fun `trick evaluator bostons big joker beats big deuce`() {
    val plays = listOf(
      PlayedCard(PlayerPosition.SOUTH, Card(Suit.SPADES, Rank.TWO)), // Big Deuce
      PlayedCard(PlayerPosition.WEST, Card(Suit.JOKER, Rank.BIG_JOKER)), // Big Joker
      PlayedCard(PlayerPosition.NORTH, Card(Suit.JOKER, Rank.LITTLE_JOKER)),
      PlayedCard(PlayerPosition.EAST, Card(Suit.DIAMONDS, Rank.TWO)) // Little Deuce
    )
    val winner = TrickEvaluator.evaluateWinner(plays, SpadesVariant.BOSTONS_JOKERS)
    assertNotNull(winner)
    assertEquals(PlayerPosition.WEST, winner?.player)
  }
}
