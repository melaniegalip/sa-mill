package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.ExecutionContext.Implicits.global
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
    val schema =
      player.schema ++ board.schema ++ game.schema ++ field.schema ++ gameState.schema

    database.run(schema.createIfNotExists)
  }

  override def delete(): Future[Unit] = {
    val schema =
      player.schema ++ board.schema ++ game.schema ++ field.schema ++ gameState.schema

    database.run(schema.dropIfExists)
  }

  private def insertPlayers(players: Seq[JsValue]) = {
    val playerRows = players.zipWithIndex.map { (playerJson, index) =>
      val name = (playerJson \ "name").asOpt[String].get
      val color = (playerJson \ "color").asOpt[String].get

      player.insertOrUpdate(index + 2, name, color)
    }

    database.run(DBIO.sequence(playerRows))
  }

  private def insertBoardFields(
      boardId: Int,
      boardFields: Seq[JsValue]
  ) = {
    val fieldRows = boardFields.zipWithIndex.map { (fieldJson, index) =>
      val x = (fieldJson \ "x").asOpt[Int].get
      val y = (fieldJson \ "y").asOpt[Int].get
      val ring = (fieldJson \ "ring").asOpt[Int].get
      val color = (fieldJson \ "color").asOpt[String].get

      field.insertOrUpdate(index + 1, boardId, x, y, ring, color)
    }

    database.run(DBIO.sequence(fieldRows))
  }

  private def insertBoard(size: Int) =
    database.run(board.insertOrUpdate(1, size))

  private def insertGameTable(
      boardId: Int,
      currentPlayerId: Int,
      setStones: Int
  ) = database.run(game.insertOrUpdate(1, boardId, currentPlayerId, setStones))

  private def insertGameState(
      gameStateType: String,
      gameId: Int
  ) = database.run(gameState.insertOrUpdate(1, gameStateType, gameId))

  private def getGameInfo = for {
    gs <- gameState
    g <- game if gs.gameId === g.gameId
    b <- board if g.boardId === b.boardId
    p <- player if g.currentPlayerId === p.playerId
  } yield (
    gs.gameStateType,
    g.setStones,
    b.size,
    p.name,
    p.color,
    b.boardId,
    g.currentPlayerId
  )

  private def getPlayers = for {
    gameInfo <- getGameInfo
    p <- player if p.playerId =!= gameInfo._7
  } yield (p.name, p.color)

  private def getFields = for {
    gameInfo <- getGameInfo
    f <- field if f.boardId === gameInfo._6
  } yield (f.x, f.y, f.ring, f.color)

  private def getCurrentPlayer(name: String, color: String) =
    database.run(
      player.filter((p) => p.name === name && p.color === color).result
    )

  override def load(): Future[Option[String]] = {
    val gameInfoResult = database.run(getGameInfo.result)
    val playersResult = database.run(getPlayers.result)
    val fieldsResult = database.run(getFields.result)

    for {
      gameInfo <- gameInfoResult
      players <- playersResult
      fields <- fieldsResult
    } yield {
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

      Some(
        Json
          .obj(
            "gameState" -> Json.obj(
              "type" -> gameInfo.head._1,
              "game" -> Json.obj(
                "board" -> Json.obj(
                  "fields" -> Json.toJson(fieldsJson),
                  "size" -> gameInfo.head._3
                ),
                "players" -> Json.toJson(playersJson),
                "currentPlayer" -> Json.obj(
                  "name" -> gameInfo.head._4,
                  "color" -> gameInfo.head._5
                ),
                "setStones" -> gameInfo.head._2
              )
            )
          )
          .toString
      )
    }
  }

  override def save(game: String): Future[Int] = {
    Try(Json.parse(game)) match {
      case Failure(exception) =>
        Future.failed(new IllegalArgumentException("Ungültige JSON-Daten"))
      case Success(data) =>
        val gameStateType =
          (data \ "gameState" \ "type").asOpt[String].get
        val boardFields = (data \ "gameState" \ "game" \ "board" \ "fields")
          .asOpt[Seq[JsValue]]
          .get
        val boardSize = (data \ "gameState" \ "game" \ "board" \ "size")
          .asOpt[Int]
          .get
        val players = (data \ "gameState" \ "game" \ "players")
          .asOpt[Seq[JsValue]]
          .get
        val currentPlayerName =
          (data \ "gameState" \ "game" \ "currentPlayer" \ "name")
            .asOpt[String]
            .get
        val currentPlayerColor =
          (data \ "gameState" \ "game" \ "currentPlayer" \ "color")
            .asOpt[String]
            .get
        val setStones =
          (data \ "gameState" \ "game" \ "setStones").asOpt[Int].get

        for {
          boardId <- insertBoard(boardSize)
          _ <- insertBoardFields(boardId, boardFields)
          _ <- insertPlayers(players)
          currentPlayer <- getCurrentPlayer(
            currentPlayerName,
            currentPlayerColor
          )
          gameId <- insertGameTable(boardId, currentPlayer.head._1, setStones)
          _ <- insertGameState(gameStateType, gameId)
        } yield gameId
    }
  }

  override def closeDatabase(): Unit = database.close
}
