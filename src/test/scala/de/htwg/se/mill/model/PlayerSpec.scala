package de.htwg.se.mill.model

import de.htwg.se.mill.model.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {
  "A Player" when {
    "new" should {
      val player = Player("Melanie", "🔴")
      "have a name" in {
        player.name should be("Melanie")
      }
      "have a color" in {
        player.color should be("🔴")
      }
      "have a nice String representation" in {
        player.toString should be("Melanie 🔴")
      }
      "equal a player with the same color and name" in {
        val samePlayer = Player("Melanie", "🔴")
        player should equal(samePlayer)
      }
      "not equal a player with a different color" in {
        val differentPlayer = Player("Melanie", "🔵")
        player should not equal (differentPlayer)
      }
      "not equal a player with a different name" in {
        val differentPlayer = Player("Reyhan", "🔴")
        player should not equal (differentPlayer)
      }
      "not equal a player with a different type" in {
        val differentPlayer = ""
        player should not equal (differentPlayer)
      }
    }
  }
}
