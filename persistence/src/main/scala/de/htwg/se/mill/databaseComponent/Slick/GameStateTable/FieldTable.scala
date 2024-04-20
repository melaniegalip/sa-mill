package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

class FieldTable(tag: Tag) extends Table[(Int, Int, Int, Int, Int, String)](tag, "field") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def boardId = column[Int]("board_id")
    def x = column[Int]("x")
    def y = column[Int]("y")
    def ring = column[Int]("ring")
    def color = column[String]("color")

    // Projektion, um Tupel in Field zu konvertieren
    def * = (id, boardId, x, y, ring, color)

    // ForeignKey-Definition
    def board = foreignKey("board_fk", boardId, boardTable)(_.id)

    // Zugriff auf BoardTable
    def boardTable = TableQuery[BoardTable]
}