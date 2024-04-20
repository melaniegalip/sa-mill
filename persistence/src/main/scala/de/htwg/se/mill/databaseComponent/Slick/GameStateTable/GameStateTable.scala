package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

class GameStateTable(tag: Tag) extends Table[(Int, String, Int)](tag, "game_state") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def `type` = column[String]("type")
  def gameId = column[Int]("game_id")

  // Projektion, um Tupel in GameState zu konvertieren
  def * = (id, `type`, gameId)

  // ForeignKey-Definition
  def game = foreignKey("game_fk", gameId, gameTable)(_.id)

  // Zugriff auf GameTable
  def gameTable = TableQuery[GameTable]
}
