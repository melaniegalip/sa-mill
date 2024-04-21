package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

class GameStateTable(tag: Tag) extends Table[(Int, String, Int)](tag, "game_state") {
  def gameStateId = column[Int]("game_state_id", O.PrimaryKey, O.AutoInc)
  def gameStateType = column[String]("type")
  def gameId = column[Int]("game_id")

  // Projektion, um Tupel in GameState zu konvertieren
  def * = (gameStateId, gameStateType, gameId)

  // ForeignKey-Definition
  def game = foreignKey("game_fk", gameId, gameTable)(_.gameId)

  // Zugriff auf GameTable
  def gameTable = TableQuery[GameTable]
}
