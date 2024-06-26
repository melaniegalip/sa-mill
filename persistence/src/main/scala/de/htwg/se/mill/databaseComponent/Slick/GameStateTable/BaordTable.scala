package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

case class BoardTable(tag: Tag) extends Table[(Int, Int)](tag, "board") {
  def boardId = column[Int]("board_id", O.PrimaryKey, O.AutoInc)
  def size = column[Int]("size")

  // Projektion, um Tupel in Board zu konvertieren
  def * = (boardId, size)
}
