package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

import play.api.libs.json._
import play.api.libs.functional.syntax._

import persistence.*

class SlickUserDAO extends DBDAO {

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "tbl")
  private val databaseUser: String =
    sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String =
    sys.env.getOrElse("POSTGRES_PASSWORD", "postgres")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
  private val databaseHost: String =
    sys.env.getOrElse("POSTGRES_HOST", "localhost")
  private val databaseUrl =
    s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val player = TableQuery(PlayerTable)
  val board = TableQuery(BoardTable)
  val game = TableQuery(GameTable)
  val field = TableQuery(FieldTable)
  val gameState = TableQuery(GameStateTable)

  override def create(): Future[Unit] = {

    val createPlayerTableAction = player.schema.createIfNotExists
    val createBoardTableAction = board.schema.createIfNotExists
    val createGameTableAction = game.schema.createIfNotExists
    val createFieldTableAction = field.schema.createIfNotExists
    val createGameStateTableAction = gameState.schema.createIfNotExists

    val combinedAction = for {
      _ <- createPlayerTableAction
      _ <- createBoardTableAction
      _ <- createGameTableAction
      _ <- createFieldTableAction
      _ <- createGameStateTableAction
    } yield ()

    database.run(combinedAction)
  }

  override def delete(): Future[Unit] = {
    val dropGameStateTableAction = gameState.schema.dropIfExists
    val dropFieldTableAction = field.schema.dropIfExists
    val dropGameTableAction = game.schema.dropIfExists
    val dropPlayerTableAction = player.schema.dropIfExists
    val dropBoardTableAction = board.schema.dropIfExists

    val combinedAction = for {
      _ <- dropGameStateTableAction
      _ <- dropFieldTableAction
      _ <- dropGameTableAction
      _ <- dropPlayerTableAction
      _ <- dropBoardTableAction
    } yield ()

    database.run(combinedAction)
  }

  def insertPlayer(name: String, color: String): Future[Int] = {
    val newPlayer = (0, name, color)
    database.run((player returning player.map(_.playerId)) += newPlayer)
  }

  def insertPlayers(
      currentPlayerId: Int,
      players: Seq[JsValue]
  ): Future[Unit] = {
    val playerInsertions = players.map { playerJson =>
      val name = (playerJson \ "name").asOpt[String].getOrElse("")
      val color = (playerJson \ "color").asOpt[String].getOrElse("")
      (0, name, color)
    }
    database.run(player ++= playerInsertions).map(_ => ())
  }

  def insertBoardFields(
      boardId: Int,
      boardFields: Seq[JsValue]
  ): Future[Unit] = {
    val fieldInsertions = boardFields.map { fieldJson =>
      val x = (fieldJson \ "x").asOpt[Int].getOrElse(0)
      val y = (fieldJson \ "y").asOpt[Int].getOrElse(0)
      val ring = (fieldJson \ "ring").asOpt[Int].getOrElse(0)
      val color = (fieldJson \ "color").asOpt[String].getOrElse("")
      (0, boardId, x, y, ring, color)
    }
    database.run(field ++= fieldInsertions).map(_ => ())
  }

  def insertBoard(size: Int): Future[Int] = {
    val newBoard = (0, size)
    database.run((board returning board.map(_.boardId)) += newBoard)
  }

  def insertGame(gameStateJson: String): Future[Int] = {
    Try(Json.parse(gameStateJson)) match {
      case Failure(exception) =>
        println(s"Fehler beim Parsen der JSON-Daten: $exception")
        Future.failed(new IllegalArgumentException("Ungültige JSON-Daten"))
      case Success(data) =>
        val gameStateType =
          (data \ "gameState" \ "type").asOpt[String].getOrElse("")
        val boardFields = (data \ "gameState" \ "game" \ "board" \ "fields")
          .asOpt[Seq[JsValue]]
          .getOrElse(Seq.empty)
        val boardSize = (data \ "gameState" \ "game" \ "board" \ "size")
          .asOpt[Int]
          .getOrElse(0)
        val players = (data \ "gameState" \ "game" \ "players")
          .asOpt[Seq[JsValue]]
          .getOrElse(Seq.empty)
        val currentPlayerName =
          (data \ "gameState" \ "game" \ "currentPlayer" \ "name")
            .asOpt[String]
            .getOrElse("")
        val currentPlayerColor =
          (data \ "gameState" \ "game" \ "currentPlayer" \ "color")
            .asOpt[String]
            .getOrElse("")
        val setStones =
          (data \ "gameState" \ "game" \ "setStones").asOpt[Int].getOrElse(0)

        for {
          boardId <- insertBoard(boardSize)
          _ <- insertBoardFields(boardId, boardFields)
          currentPlayerId <- insertPlayer(currentPlayerName, currentPlayerColor)
          _ <- insertPlayers(currentPlayerId, players)
          gameId <- insertGameTable(boardId, currentPlayerId, setStones)
          _ <- insertGameState(gameStateType, gameId)
        } yield gameId
    }
  }

  private def insertGameTable(
      boardId: Int,
      currentPlayerId: Int,
      setStones: Int
  ): Future[Int] = {
    database.run(
      (game returning game.map(
        _.gameId
      )) += (0, boardId, currentPlayerId, setStones)
    )
  }

  def insertField(boardId: Int, fieldJson: String): Future[Int] = {
    Try(Json.parse(fieldJson)) match {
      case Failure(exception) =>
        println(s"Fehler beim Parsen der JSON-Daten: $exception")
        Future.failed(new IllegalArgumentException("Ungültige JSON-Daten"))
      case Success(data) =>
        val x = (data \ "x").asOpt[Int].getOrElse(0)
        val y = (data \ "y").asOpt[Int].getOrElse(0)
        val ring = (data \ "ring").asOpt[Int].getOrElse(0)
        val color = (data \ "color").asOpt[String].getOrElse("")
        database.run(
          (field returning field.map(
            _.fieldId
          )) += (0, boardId, x, y, ring, color)
        )
    }
  }

  def insertGameState(gameStateType: String, gameId: Int): Future[Int] = {
    database.run(
      (gameState returning gameState.map(
        _.gameStateId
      )) += (0, gameStateType, gameId)
    )
  }

  override def load(): Future[Option[String]] = {

    val gameInfoQuery = for {
      gs <- gameState
      g <- game if gs.gameId === g.gameId
      b <- board if g.boardId === b.boardId
      p <- player if g.currentPlayerId === p.playerId
    } yield (gs.gameStateType, g.setStones, b.size, p.name, p.color)

    val gameInfoResult = database.run(gameInfoQuery.result)

    val playerGameQuery = for {
      (player, game) <- player join game on (_.playerId =!= _.currentPlayerId)
    } yield (player.name, player.color)

    val playersResult = database.run(playerGameQuery.result)

    val fieldQuery = for {
      (((f, _), g), gs) <- field
        .join(board)
        .on(_.boardId === _.boardId)
        .join(game)
        .on(_._2.boardId === _.boardId)
        .join(gameState)
        .on(_._2.gameId === _.gameId)
    } yield (f.x, f.y, f.ring, f.color)

    val fieldsResult = database.run(fieldQuery.result)

    val combinedResult: Future[Option[String]] = for {
      gameInfo <- gameInfoResult
      players <- playersResult
      fields <- fieldsResult
    } yield {
      val gameStateJson = gameInfo.map {
        case (
              gameStateType,
              setStones,
              boardSize,
              currentPlayerName,
              currentPlayerColor
            ) =>
          val fieldsJson = fields.map { case (x, y, ring, color) =>
            Json.obj(
              "x" -> x,
              "y" -> y,
              "ring" -> ring,
              "color" -> color
            )
          }
          val playersJson = players.map { case (name, color) =>
            Json.obj(
              "name" -> name,
              "color" -> color
            )
          }
          Json.obj(
            "gameState" -> Json.obj(
              "type" -> gameStateType,
              "game" -> Json.obj(
                "board" -> Json.obj(
                  "fields" -> Json.toJson(fieldsJson),
                  "size" -> boardSize
                ),
                "players" -> Json.toJson(playersJson),
                "currentPlayer" -> Json.obj(
                  "name" -> currentPlayerName,
                  "color" -> currentPlayerColor
                ),
                "setStones" -> setStones
              )
            )
          )
      }
      gameStateJson.headOption.map(_.toString())
    }
    combinedResult
  }

  override def save(game: String): Future[Int] = {
    insertGame(game)
  }

  override def closeDatabase(): Unit = {
    database.close()
  }

}
