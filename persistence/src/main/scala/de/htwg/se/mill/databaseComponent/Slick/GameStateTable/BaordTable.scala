package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

class BoardTable(tag: Tag) extends Table[(Int, Int)](tag, "board") {
    def id = column[Int]("board_id", O.PrimaryKey, O.AutoInc)
    def size = column[Int]("size")

    // Projektion, um Tupel in Board zu konvertieren
    def * = (id, size)
}