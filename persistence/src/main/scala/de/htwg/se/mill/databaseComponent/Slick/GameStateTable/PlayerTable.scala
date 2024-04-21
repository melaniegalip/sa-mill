package databaseComponent.Slick

import slick.jdbc.PostgresProfile.api.*

class PlayerTable(tag: Tag) extends Table[(Int, String, String)](tag, "player") {
    def id = column[Int]("player_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def color = column[String]("color")

    // Projektion, um Tupel in Player zu konvertieren
    def * = (id, name, color)
}