package persistence

import persistence.FileIOAPI
import scala.util.{Try, Success, Failure}

import databaseComponent.Slick.*
import de.htwg.se.mill.databaseComponent.MongoDB.MongoDBDAO

object Persistence {
  // val db = SlickUserDAO()
  val db = MongoDBDAO()
  db.dropTables()

  @main def main: Unit = {
    Try(FileIOAPI(db)) match
      case Success(persistence) => {
        println("Persistance Rest Server is running!")
      }
      case Failure(v) =>
        println(
          "Persistance Server couldn't be started! " + v.getMessage + v.getCause
        )
  }
}
