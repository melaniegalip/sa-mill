    package de.htwg.se.mill.model

    import org.scalatest.wordspec.AnyWordSpec
    import org.scalatest.matchers.should.Matchers

    class GameStateSpec extends AnyWordSpec with Matchers {
      "A new Game State" when {
        val board = Board.withSize().get
        val melanie = Player("Melanie", "🔴")
        val reyhan = Player("Reyhan", "🔵")
        val players = Array(melanie, reyhan)
        val game = Game(board, players, melanie)
        val firstField = Field(0, 0, 0)
        "is setting a piece" should {
          "be invalid if game state is not SettingState" in {
            MovingState(game)
              .handle(GameEvent.OnSetting, (firstField, None))
              .isFailure should be(true)
          }
          "be invalid if the field to be set is already in use" in {
            SettingState(
              game
                .copy(board =
                  Board(
                    game.board.fields.updated(
                      game.board.fields.indexOf(firstField),
                      firstField.copy(color = melanie.color)
                    ),
                    game.board.size
                  )
                )
            )
              .handle(GameEvent.OnSetting, (firstField, None))
              .isFailure should be(true)
          }
          "be valid with a new game state of Removing" in {
            val boardWithMillOnFirstRow = Board(
              game.board.fields
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
            val nextState = SettingState(
              game.copy(board = boardWithMillOnFirstRow, currentPlayer = melanie)
            )
              .handle(GameEvent.OnSetting, (firstField, None))
            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[RemovingState] should be(true)
          }
          "be valid with a new game state of Moving" in {
            val nextState = SettingState(
              game
                .copy(setStones =
                  Math.pow(game.board.size, 2).toInt * game.players.length - 1
                )
            )
              .handle(GameEvent.OnSetting, (firstField, None))
            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[MovingState] should be(true)
          }
          "be valid with the same game state of Setting" in {
            val nextState =
              SettingState(game).handle(GameEvent.OnSetting, (firstField, None))
            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[SettingState] should be(true)
          }
        }
        "a piece is moved to a field" should {
          "fail if the game state is Setting" in {
            SettingState(
              game.copy(
                Board(
                  game.board.fields.updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = melanie.color)
                  ),
                  game.board.size
                )
              )
            ).handle(GameEvent.OnMoving, (firstField, Some(Field(1, 0, 0))))
              .isFailure should be(true)
          }
          "fail if the piece moved was not the players in turn" in {
            MovingState(
              game.copy(
                Board(
                  game.board.fields.updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = reyhan.color)
                  ),
                  game.board.size
                )
              )
            ).handle(GameEvent.OnMoving, (firstField, Some(Field(1, 0, 0))))
              .isFailure should be(true)
          }
          "fail if the game state is Moving but the move is not valid" in {
            MovingState(
              game.copy(
                Board(
                  game.board.fields.updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = melanie.color)
                  ),
                  game.board.size
                )
              )
            ).handle(GameEvent.OnMoving, (firstField, Some(Field(2, 0, 0))))
              .isFailure should be(true)
          }
          "fail if the game state is Flying but setting the piece is not valid" in {
            FlyingState(
              game.copy(
                Board(
                  game.board.fields.updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = melanie.color)
                  ),
                  game.board.size
                )
              )
            ).handle(GameEvent.OnFlying, (firstField, Some(Field(-1, 0, 0))))
              .isFailure should be(true)
          }
          "succeed with a state of Removing if it is a mill" in {
            val boardWithMillOnFirstRow = Board(
              game.board.fields
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
            val nextState = MovingState(
              game
                .copy(board = boardWithMillOnFirstRow, currentPlayer = melanie)
            )
              .handle(
                GameEvent.OnMoving,
                (Field(0, 1, 0, melanie.color), Some(firstField))
              )

            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[RemovingState] should be(true)
          }
          "succeed with a state of Moving if the piece is moved correctly" in {

            val nextState = MovingState(game).handle(
              GameEvent.OnMoving,
              (firstField.copy(color = melanie.color), Some(Field(1, 0, 0)))
            )
            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[MovingState] should be(true)
          }
        }
        "a piece is removed from a field" should {
          "fail if the game state is not Removing" in {
            RemovingState(game)
              .handle(GameEvent.OnRemoving, (firstField, None))
              .isFailure should be(true)
          }
          "fail if the removed field has the same color as the player in turn" in {
            RemovingState(
              game
                .copy(
                  board = Board(
                    game.board.fields.updated(
                      game.board.fields.indexOf(firstField),
                      firstField.copy(color = melanie.color)
                    ),
                    game.board.size
                  )
                )
            ).handle(GameEvent.OnRemoving, (firstField, None)).isFailure should be(
              true
            )
          }
          "fail if the removed field is unset" in {
            RemovingState(game)
              .handle(GameEvent.OnRemoving, (firstField, None))
              .isFailure should be(true)
          }
          "fail if the field is part of a mill" in {
            val boardWithMillOnFirstRow = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(
                    Field(1, 0, 0)
                  ),
                  Field(1, 0, 0).copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(
                    Field(2, 0, 0)
                  ),
                  Field(2, 0, 0).copy(color = reyhan.color)
                ),
              game.board.size
            )
            RemovingState(game.copy(board = boardWithMillOnFirstRow))
              .handle(GameEvent.OnRemoving, (firstField, None))
              .isFailure should be(true)
          }
          "succeed with a state of Moving if every player has set its stones " +
            "and the other players pieces are more than the board size" in {
              val board = Board(
                game.board.fields
                  .updated(
                    game.board.fields.indexOf(firstField),
                    firstField.copy(color = reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(1, 0, 0)),
                    Field(1, 0, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(2, 0, 0)),
                    Field(2, 0, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(0, 1, 0)),
                    Field(0, 1, 0, reyhan.color)
                  )
                  .updated(
                    game.board.fields.indexOf(Field(0, 2, 0)),
                    Field(0, 2, 0, reyhan.color)
                  ),
                game.board.size
              )
              val nextState = RemovingState(
                game.copy(
                  board = board,
                  setStones =
                    Math.pow(game.board.size, 2).toInt * game.players.length
                )
              ).handle(
                GameEvent.OnRemoving,
                (firstField.copy(color = reyhan.color), None)
              )
              nextState.isSuccess should be(true)
              nextState.get.isInstanceOf[MovingState] should be(true)
            }
          "succeed with a state of Flying if the other players pieces are equal to the board size" in {
            val board = Board(
              game.board.fields
                .updated(
                  game.board.fields.indexOf(firstField),
                  firstField.copy(color = reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(1, 0, 0)),
                  Field(1, 0, 0, reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(2, 0, 0)),
                  Field(2, 0, 0, reyhan.color)
                )
                .updated(
                  game.board.fields.indexOf(Field(0, 1, 0)),
                  Field(0, 1, 0, reyhan.color)
                ),
              game.board.size
            )
            val nextState = RemovingState(
              game
                .copy(
                  board = board,
                  setStones =
                    Math.pow(game.board.size, 2).toInt * game.players.length
                )
            )
              .handle(
                GameEvent.OnRemoving,
                (firstField.copy(color = reyhan.color), None)
              )
            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[FlyingState] should be(true)
          }
          "succeed with a state of Setting if the piece is removed correctly" in {
            val nextState = RemovingState(
              game.copy(board =
                Board(
                  game.board.fields
                    .updated(
                      game.board.fields.indexOf(firstField),
                      firstField.copy(color = reyhan.color)
                    ),
                  game.board.size
                )
              )
            ).handle(
              GameEvent.OnRemoving,
              (firstField.copy(color = reyhan.color), None)
            )

            nextState.isSuccess should be(true)
            nextState.get.isInstanceOf[SettingState] should be(true)
          }
        }
      }
    }
