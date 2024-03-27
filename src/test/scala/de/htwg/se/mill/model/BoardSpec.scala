package de.htwg.se.mill.model

import de.htwg.se.mill.model.Board
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BoardSpec extends AnyWordSpec with Matchers {
  "A Board" when {
    val nineMensMorrisFields = List(
      Field(0, 0, 0),
      Field(1, 0, 0),
      Field(2, 0, 0),
      Field(0, 1, 0),
      Field(2, 1, 0),
      Field(0, 2, 0),
      Field(1, 2, 0),
      Field(2, 2, 0),
      Field(0, 0, 1),
      Field(1, 0, 1),
      Field(2, 0, 1),
      Field(0, 1, 1),
      Field(2, 1, 1),
      Field(0, 2, 1),
      Field(1, 2, 1),
      Field(2, 2, 1),
      Field(0, 0, 2),
      Field(1, 0, 2),
      Field(2, 0, 2),
      Field(0, 1, 2),
      Field(2, 1, 2),
      Field(0, 2, 2),
      Field(1, 2, 2),
      Field(2, 2, 2)
    )
    "created with size" should {
      "result in a failure when the size is less than three" in {
        Board.withSize(2).isFailure should be(true)
      }
      "result in a failure when the size is even" in {
        Board.withSize(4).isFailure should be(true)
      }
      "result in a failure when the size is larger than nine" in {
        Board.withSize(11).isFailure should be(true)
      }
      "result in a success when the size is correct" in {
        Board.withSize().isSuccess should be(true)
      }
      val board = Board.withSize().get
      "have initialized fields" in {
        board.fields should be(nineMensMorrisFields)
      }
      "have a size of three" in {
        board.size should be(3)
      }
      "have a nine mens morris string representation" in {
        board.toString should be("""
⚫――――――――――――⚫――――――――――――⚫
│   ⚫――――――――⚫――――――――⚫   │
│   │   ⚫――――⚫――――⚫   │   │
│   │   │            │   │   │
⚫――⚫――⚫         ⚫――⚫――⚫
│   │   │            │   │   │
│   │   ⚫――――⚫――――⚫   │   │
│   ⚫――――――――⚫――――――――⚫   │
⚫――――――――――――⚫――――――――――――⚫""")
      }
      "have a fields dump with correct fields representation" in {
        board.fieldsDump should be(
          "(0, 0, 0),(1, 0, 0),(2, 0, 0),(0, 1, 0),(2, 1, 0),(0, 2, 0),(1, 2, 0),(2, 2, 0),(0, 0, 1),(1, 0, 1),(2, 0, 1),(0, 1, 1),(2, 1, 1),(0, 2, 1),(1, 2, 1),(2, 2, 1),(0, 0, 2),(1, 0, 2),(2, 0, 2),(0, 1, 2),(2, 1, 2),(0, 2, 2),(1, 2, 2),(2, 2, 2)"
        )
      }
      "equal to a board with the same size and the same fields" in {
        board should be(Board(nineMensMorrisFields, board.size))
      }
    }
    "created with apply i.e. default constructor with " +
      "nineMensMorris fields and size of 3" should {
        val board = Board(nineMensMorrisFields, 3)
        "have initialized fields" in {
          board.fields should be(nineMensMorrisFields)
        }
        "have a size of three" in {
          board.size should be(3)
        }
      }
  }
}
