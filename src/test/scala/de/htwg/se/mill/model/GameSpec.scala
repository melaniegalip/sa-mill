package de.htwg.se.mill.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameSpec extends AnyWordSpec with Matchers {
  "A new Game" when {
    "created with a board and two players" should {
      val board = Board.withSize().get
      val melanie = Player("Melanie", "🔴")
      val players = Array(melanie, Player("Reyhan", "🔵"))
      val game = Game(board, players, melanie)
      "have the board" in {
        game.board should be(board)
      }
      "have the players" in {
        game.players should be(players)
      }
      "not equal a game with a different board" in {
        game.equals(
          game.copy(board =
            Board(
              board.fields.updated(
                board.fields.indexOf(Field(0, 0, 0)),
                Field(3, 0, 0, melanie.color)
              ),
              board.size
            )
          )
        ) should be(false)
      }
      "not equal a game with different players" in {
        game.equals(
          game.copy(players = Array(melanie, Player("max", melanie.color)))
        ) should be(false)
      }
      "not equal a different object" in {
        game.equals("") should be(false)
      }
      "equal the same game" in {
        game.equals(game) should be(true)
      }
    }
  }
  "A game" when {
    val board = Board.withSize().get
    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val players = Array(melanie, reyhan)
    val game = Game(board, players, melanie)
    val firstField = Field(0, 0, 0)
    "is set a piece correctly" should {
      "be not valid if its horizontal location is outside of the board" in {
        game.isValidSet(Field(3, 0, 0)) should be(false)
        game.isValidSet(Field(-1, 0, 0)) should be(false)
      }
      "be not valid if its vertical location is outside of the board" in {
        game.isValidSet(Field(0, 3, 0)) should be(false)
        game.isValidSet(Field(0, -1, 0)) should be(false)
      }

      "be not valid if its ring location is outside of the board" in {
        game.isValidSet(Field(0, 0, 3)) should be(false)
        game.isValidSet(Field(0, 0, -1)) should be(false)
      }
      "be not valid if its set on a field that is already in use" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              firstField.copy(color = reyhan.color)
            ),
            game.board.size
          ),
          players,
          melanie
        ).isValidSet(firstField) should be(false)
      }
      "be valid if its set on a correct field" in {
        Game(
          Board(
            game.board.fields.updated(
              game.board.fields.indexOf(
                firstField
              ),
              firstField.copy(color = melanie.color)
            ),
            game.board.size
          ),
          players,
          melanie
        ).isValidSet(Field(1, 0, 0)) should be(true)
      }
    }
    "is moved a piece correctly" should {
      "be not valid if there is a difference in x or y or ring" in {
        game.isValidMove(firstField, Field(-1, 0, 1)) should be(false)
        game.isValidMove(firstField, Field(1, 1, 1)) should be(false)
        game.isValidMove(firstField, Field(1, 1, 0)) should be(false)
        game.isValidMove(firstField, Field(1, 0, 1)) should be(false)
        game.isValidMove(firstField, Field(0, 1, 1)) should be(false)
        game.isValidMove(firstField, firstField) should be(false)
      }
      "be valid if there is only one difference in either x or y or ring" in {
        game.isValidMove(firstField, Field(1, 0, 0)) should be(true)
        game.isValidMove(firstField, Field(0, 1, 0)) should be(true)
        game.isValidMove(firstField, Field(0, 0, 1)) should be(true)
      }
    }
    "is moved a piece to a mill correctly" should {
      val boardWithMillOnFirstRow = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              firstField
            ),
            firstField.copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 0)
            ),
            Field(1, 0, 0).copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(2, 0, 0)
            ),
            Field(2, 0, 0).copy(color = melanie.color)
          ),
        game.board.size
      )
      val boardWithMillOnFirstColumn = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              firstField
            ),
            firstField.copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(0, 1, 0)
            ),
            Field(0, 1, 0).copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(0, 2, 0)
            ),
            Field(0, 2, 0).copy(color = melanie.color)
          ),
        game.board.size
      )
      val boardWithMillOnFirstRing = Board(
        game.board.fields
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 0)
            ),
            Field(1, 0, 0).copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 1)
            ),
            Field(1, 0, 1).copy(color = melanie.color)
          )
          .updated(
            game.board.fields.indexOf(
              Field(1, 0, 2)
            ),
            Field(1, 0, 2).copy(color = melanie.color)
          ),
        game.board.size
      )

      "be invalid if there is no mill" in {
        game.isMill(
          firstField.copy(color = melanie.color)
        ) should be(false)
      }
      "be invalid if there is a mill but the target field is wrong" in {
        game
          .copy(board = boardWithMillOnFirstRow)
          .isMill(
            Field(1, 0, 1, melanie.color)
          ) should be(false)
      }
      "be valid if there is a mill on a row and its a middle point" in {
        game
          .copy(board = boardWithMillOnFirstRow)
          .isMill(
            Field(1, 0, 0, melanie.color)
          ) should be(true)
      }
      "be valid if there is a mill on a column and its a middle point" in {
        game
          .copy(board = boardWithMillOnFirstColumn)
          .isMill(
            Field(0, 1, 0, melanie.color)
          ) should be(true)
      }
      "be valid if there is a mill on a row" in {
        game
          .copy(board = boardWithMillOnFirstRow)
          .isMill(
            Field(0, 0, 0, melanie.color)
          ) should be(true)
      }
      "be valid if there is a mill on a column" in {
        game
          .copy(board = boardWithMillOnFirstColumn)
          .isMill(
            Field(0, 0, 0, melanie.color)
          ) should be(true)
      }
      "be valid if there is a mill on a ring" in {
        game
          .copy(board = boardWithMillOnFirstRing)
          .isMill(
            Field(1, 0, 1, melanie.color)
          ) should be(true)
      }
    }
    "every player has set its stones" should {
      "be invalid if one player has not set its stones" in {
        game.copy(setStones = 1).everyPlayerHasSetItsStones should be(false)
      }
      "be invalid if both players have set too many stones" in {
        game
          .copy(setStones =
            Math.pow(game.board.size, 2).toInt * game.players.length + 1
          )
          .everyPlayerHasSetItsStones should be(false)
      }
      "be valid if both players have set their stones" in {
        game
          .copy(setStones =
            Math.pow(game.board.size, 2).toInt * game.players.length
          )
          .everyPlayerHasSetItsStones should be(true)
      }
    }
  }
}
