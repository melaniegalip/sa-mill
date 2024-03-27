package de.htwg.se.mill.model

import de.htwg.se.mill.model.Field
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FieldSpec extends AnyWordSpec with Matchers {
  "A Field" when {
    "new" should {
      val field = Field(0, 0, 0, "🔴")
      "have a x" in {
        field.x should be(0)
      }
      "have a y" in {
        field.y should be(0)
      }
      "have a ring" in {
        field.ring should be(0)
      }
      "have a player color" in {
        field.color should be("🔴")
      }
      "have a nice String representation" in {
        field.toString should be("🔴")
      }
      "have a unset field color" in {
        field.unsetFieldColor should be("⚫")
      }
      "equal a field with the same coordinate and ring" in {
        val sameField = Field(0, 0, 0, "🔴")
        field should equal(sameField)
      }
      "not equal a field with a different x coordinate" in {
        val differentField = Field(1, 0, 0, "🔴")
        field should not equal (differentField)
      }
      "not equal a field with a different y coordinate" in {
        val differentField = Field(0, 1, 0, "🔴")
        field should not equal (differentField)
      }
      "not equal a field with a different ring" in {
        val differentField = Field(0, 0, 1, "🔴")
        field should not equal (differentField)
      }
      "not equal a field with a different type" in {
        val differentField = ""
        field should not equal (differentField)
      }
    }
  }
}
