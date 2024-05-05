package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

case class GameTable(tag: Tag)
    extends Table[(Int, Int, Int, Int)](tag, "game") {
  def gameId = column[Int]("game_id", O.PrimaryKey, O.AutoInc)
  def boardId = column[Int]("board_id")
  def currentPlayerId = column[Int]("current_player_id")
  def setStones = column[Int]("set_stones")

  // Projektion, um Tupel in Game zu konvertieren
  def * = (gameId, boardId, currentPlayerId, setStones)

  // ForeignKey-Definitionen
  def board = foreignKey("board_fk", boardId, boardTable)(_.boardId)
  def currentPlayer =
    foreignKey("current_player_fk", currentPlayerId, playerTable)(_.playerId)

  // Zugriff auf BoardTable und PlayerTable
  def boardTable = TableQuery(BoardTable)
  def playerTable = TableQuery(PlayerTable)
}
