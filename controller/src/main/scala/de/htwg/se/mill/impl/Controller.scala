package controller

import scalafx.application.Platform
import model.Game
import model.Board
import model.Player
import scala.util.{Try, Success, Failure}
import model.WinStrategy
import model.GameState
import model.GameEvent
import model.{
  SettingState,
  RemovingState,
  MovingState,
  FlyingState
}
import util.Event
import model.FieldInterface
import model.BoardInterface
import model.PlayerInterface
import model.GameInterface
import com.google.inject.Inject
import com.google.inject.Guice
import persistence.FileIOInterface
import util.Messages

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.*
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import play.api.libs.json.Json
import scala.concurrent.Future
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.marshalling.Marshaller

import play.api.libs.json.{JsValue, Json}

import java.io._

class Controller @Inject() (private val board: BoardInterface)
    extends ControllerInterface {
  private val twoPlayers = new Array[PlayerInterface](2)
  private val winStrategy = WinStrategy.classicStrategy
  private var previousTurn: Option[Try[GameState]] = None
  private var undoCommand = new UndoCommand()
  private val persistenceServer = "http://localhost:8080/persistence"
  var gameState: Option[GameState] = None
  var fromField: Option[FieldInterface] = None

  def undo: Option[Throwable] = undoCommand.undoStep
  def redo: Option[Throwable] = undoCommand.redoStep

  def addFirstPlayer(playerName: String, playerColor: String = "🔴") = {
    twoPlayers(0) = Player(playerName, playerColor)
  }
  def addSecondPlayer(playerName: String, playerColor: String = "🔵") = {
    twoPlayers(1) = Player(playerName, playerColor)
  }
  def newGame = {
    // delete command history
    undoCommand = new UndoCommand()
    gameState = Some(
      SettingState(
        Game(
          board,
          twoPlayers,
          twoPlayers(0)
        )
      )
    )
    previousTurn = Some(Success(gameState.get))
    notifyObservers(None, Event.PLAY)
  }

  def quit = notifyObservers(None, Event.QUIT)

  private def createSnapshot: Snapshot = {
    val snapshot = new Snapshot(this, previousTurn)
    return snapshot
  }

  // Memento
  private class Snapshot(
      val controller: Controller,
      val previousTurn: Option[Try[GameState]]
  ) {
    def restore: Option[Throwable] =
      controller.previousTurn = previousTurn
      previousTurn.get match {
        case Success(state: GameState) => {
          state match {
            case RemovingState(game: GameInterface) => {
              controller.gameState = Some(state)
            }
            case SettingState(game: GameInterface) => {
              controller.gameState = Some(state)
            }
            case FlyingState(game: GameInterface) => {
              controller.gameState = Some(state)
            }
            case MovingState(game: GameInterface) => {
              controller.gameState = Some(state)
            }
          }
          controller.notifyObservers(None, Event.PLAY)

          return None
        }
        case Failure(error) => {
          controller.notifyObservers(Some(error.getMessage()), Event.PLAY)

          return Some(error)
        }
      }
  }

  // Command
  private class UndoCommand {
    private var undoStack: List[Snapshot] = Nil
    private var redoStack: List[Snapshot] = Nil
    def backup(snapshot: Snapshot): Unit = {
      undoStack = snapshot :: undoStack
    }
    def undoStep: Option[Throwable] =
      undoStack match {
        case Nil => None
        case head :: stack => {
          val result = head.restore
          undoStack = stack
          redoStack = head :: redoStack
          result
        }
      }
    def redoStep: Option[Throwable] =
      redoStack match {
        case Nil => None
        case head :: stack => {
          val result = head.restore
          redoStack = stack
          undoStack = head :: undoStack
          result
        }
      }
  }

  def setPiece(to: FieldInterface): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnSetting,
        (to, None)
      )
    )
  }

  def movePiece(from: FieldInterface, to: FieldInterface): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnMoving,
        (from, Some(to))
      )
    )
  }

  def removePiece(field: FieldInterface): Option[Throwable] = {
    undoCommand.backup(createSnapshot)
    doTurn(
      gameState.get.handle(
        GameEvent.OnRemoving,
        (field, None)
      )
    )
  }

  def currentGameState = gameState.get match {
    case FlyingState(game: GameInterface)   => "Flying Pieces"
    case MovingState(game: GameInterface)   => "Moving Pieces"
    case SettingState(game: GameInterface)  => "Setting Pieces"
    case RemovingState(game: GameInterface) => "Removing Pieces"
  }

  def isSetting = gameState.get.isInstanceOf[SettingState]
  def isRemoving = gameState.get.isInstanceOf[RemovingState]

  def isMovingOrFlying =
    gameState.get.isInstanceOf[MovingState] || gameState.get
      .isInstanceOf[FlyingState]

  
  def save: Unit = {
    implicit val system:ActorSystem[Any] = ActorSystem(Behaviors.empty, "my-system")

    val executionContext: ExecutionContextExecutor = system.executionContext
    given ExecutionContextExecutor = executionContext

    
    val save = Json.obj(
      "gameState" -> Json.toJson(previousTurn.get.get.toJson)
    )

    val responseFuture: Future[HttpResponse] = Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = persistenceServer + "/save",
          entity = HttpEntity(ContentTypes.`application/json`, Json.prettyPrint(save).toString)
        )
    )
  }

  def load: Unit = {
    implicit val system:ActorSystem[Any] = ActorSystem(Behaviors.empty, "my-system")

    val executionContext: ExecutionContextExecutor = system.executionContext
    given ExecutionContextExecutor = executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(uri = persistenceServer + "/load"))

    responseFuture
      .onComplete {
        case Failure(_) => sys.error("Failed getting Json")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(unmarshalledValue) => {
              val json = Json.parse(unmarshalledValue)
              Platform.runLater(
                doTurn(
                  Success(
                    GameState.fromJson((json \ "gameState").get)
                  )
                )
              )
            }
          }
        }
      }
  }
 
  private def doTurn(turn: Try[GameState]): Option[Throwable] = {
    previousTurn = Some(turn)
    turn match {
      case Success(state: GameState) => {
        var currentGame: Option[GameInterface] = None
        state match {
          case RemovingState(game: GameInterface) => {
            gameState = Some(state)
            currentGame = Some(game)
          }
          case SettingState(game: GameInterface) => {
            gameState = Some(
              SettingState(
                game.copyCurrentPlayer(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
          case FlyingState(game: GameInterface) => {
            gameState = Some(
              FlyingState(
                game.copyCurrentPlayer(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
          case MovingState(game: GameInterface) => {
            gameState = Some(
              MovingState(
                game.copyCurrentPlayer(currentPlayer =
                  twoPlayers.find(p => !p.equals(game.currentPlayer)).get
                )
              )
            )
            currentGame = Some(game)
          }
        }

        if (winStrategy(currentGame.get)) {
          notifyObservers(
            Some(
              s"Congratulations! ${currentGame.get.currentPlayer} has won the game!\nStarting new game."
            ),
            Event.PLAY
          )

          newGame
        } else {
          notifyObservers(None, Event.PLAY)

        }
        return None
      }
      case Failure(error) => {
        notifyObservers(Some(error.getMessage()), Event.PLAY)

        return Some(error)
      }
    }
  }
}
