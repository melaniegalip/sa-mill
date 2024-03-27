package de.htwg.se.mill.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Game
import de.htwg.se.mill.model.Board
import de.htwg.se.mill.model.GameState
import de.htwg.se.mill.model.Field
import de.htwg.se.mill.controller.Controller
import java.io.ByteArrayInputStream
import de.htwg.se.mill.model.MovingState
import de.htwg.se.mill.model.SettingState
import de.htwg.se.mill.model.RemovingState
import de.htwg.se.mill.model.FlyingState
import de.htwg.se.mill.model.GameEvent
import java.io.ByteArrayOutputStream
import de.htwg.se.mill.util.Messages

class TUISpec extends AnyWordSpec with Matchers {
  "A new TUI" when {
    val melanie = Player("Melanie", "🔴")
    val reyhan = Player("Reyhan", "🔵")
    val controller = Controller(Board.withSize().get)
    controller.addFirstPlayer(melanie.name)
    controller.addSecondPlayer(reyhan.name)
    controller.newGame
    val firstField = Field(0, 0, 0)
    val gameState = controller.gameState.get
    val game = gameState.game
    val tui = TUI(controller)

    "processing input" should {
      "quit the game on entering q" in {
        // tui.onInput("q") should be(true)
      }
      "start a new game on entering n" in {
        controller.gameState = Some(MovingState(game))
        tui.onInput("n")
        controller.gameState.get.equals(SettingState(game))
      }
      "fail if the game state is either Setting or Removing on entering an invalid command" in {
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          controller.gameState = Some(SettingState(game))
          tui.onInput("1")
          out.toString should be(
            Messages.wrongSettingOrRemovingCommandMessage + "\n"
          )
          out.reset
          tui.onInput("121 123")
          out.toString should be(
            Messages.wrongSettingOrRemovingCommandMessage + "\n"
          )
          out.reset
          controller.gameState = Some(RemovingState(game))
          tui.onInput("1")
          out.toString should be(
            Messages.wrongSettingOrRemovingCommandMessage + "\n"
          )
          out.reset
          tui.onInput("121 123")
          out.toString should be(
            Messages.wrongSettingOrRemovingCommandMessage + "\n"
          )
        }
      }
      "fail if the game state is either Moving or Flying on entering an invalid command" in {
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          controller.gameState = Some(MovingState(game))
          tui.onInput("111")
          out.toString should be(
            Messages.wrongMovingOrFlyingCommandMessage + "\n"
          )
          out.reset
          tui.onInput("11 123")
          out.toString should be(
            Messages.wrongMovingOrFlyingCommandMessage + "\n"
          )
          out.reset
          controller.gameState = Some(FlyingState(game))
          tui.onInput("111")
          out.toString should be(
            Messages.wrongMovingOrFlyingCommandMessage + "\n"
          )
          out.reset
          tui.onInput("11 123")
          out.toString should be(
            Messages.wrongMovingOrFlyingCommandMessage + "\n"
          )
        }
      }
      "fail if the field in the first command part could not be found" in {
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          controller.gameState = Some(SettingState(game))
          tui.onInput("221")
          out.toString should be(
            Messages.wrongFieldPositionMessage + "\n"
          )
        }
      }
      "fail if the field in the second command part could not be found" in {
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          controller.gameState = Some(MovingState(game))
          tui.onInput("112 221")
          out.toString should be(
            Messages.wrongTargetFieldPositionMessage + "\n"
          )
        }
      }
      "fail setting a piece" in {
        controller.gameState = Some(
          SettingState(
            game.copy(
              board = Board(
                game.board.fields.updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                ),
                game.board.size
              ),
              currentPlayer = melanie
            )
          )
        )
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          tui.onInput("111")
          out.toString should be(
            Messages.invalidSetFieldAlreadyInUseMessage + "\n"
          )
        }
      }
      "fail moving a piece" in {
        controller.gameState = Some(
          MovingState(
            game.copy(
              board = Board(
                game.board.fields.updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = reyhan.color)
                ),
                game.board.size
              ),
              currentPlayer = melanie
            )
          )
        )
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          tui.onInput("111 211")
          out.toString should be(
            Messages.movedOtherPieceMessage + "\n"
          )
        }
      }
      "fail removing a piece" in {
        controller.gameState = Some(
          RemovingState(
            game.copy(
              board = Board(
                game.board.fields.updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                ),
                game.board.size
              ),
              currentPlayer = melanie
            )
          )
        )
        val out = new ByteArrayOutputStream
        Console.withOut(out) {
          tui.onInput("111")
          out.toString should be(
            Messages.invalidRemoveFieldOwnPieceMessage + "\n"
          )
        }
      }
      "move a piece successfully" in {
        controller.gameState = Some(
          MovingState(
            game.copy(
              board = Board(
                game.board.fields.updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = melanie.color)
                ),
                game.board.size
              ),
              currentPlayer = melanie
            )
          )
        )
        val out = new ByteArrayOutputStream
        tui.onInput("111 211")
      }
      "set a piece successfully" in {
        controller.gameState = Some(SettingState(game))
        tui.onInput("111")
      }
      "remove a piece successfully" in {
        controller.gameState = Some(
          RemovingState(
            game.copy(
              board = Board(
                game.board.fields.updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = reyhan.color)
                ),
                game.board.size
              ),
              currentPlayer = melanie
            )
          )
        )
        tui.onInput("111")
      }
      "win" in {
        controller.gameState = Some(
          RemovingState(
            game.copy(
              board = Board(
                game.board.fields
                  .updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(1, 0, 2)),
                    Field(1, 0, 2).copy(color = reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(2, 0, 0)),
                    Field(2, 0, 0).copy(color = reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(0, 0, 1)),
                    Field(0, 0, 1).copy(color = melanie.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(1, 0, 1)),
                    Field(1, 0, 1).copy(color = melanie.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(2, 0, 1)),
                    Field(2, 0, 1).copy(color = melanie.color)
                  ),
                game.board.size
              ),
              currentPlayer = melanie,
              setStones =
                Math.pow(game.board.size, 2).toInt * game.players.length
            )
          )
        )
        tui.onInput("111")
        controller.gameState.get.equals(gameState) should be(true)
      }
      "run and quit successfully" in {
        val controller = Controller(Board.withSize().get)
        val tui = TUI(controller)
        Console.withIn(
          new ByteArrayInputStream(("melanie\nreyhan\n111\nq").getBytes())
        ) {
          tui.run
        }
      }
    }
  }
}
