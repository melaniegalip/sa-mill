package persistence

import persistence.FileIOAPI
import scala.util.{Try, Success, Failure}
import databaseComponent.Slick.SlickUserDAO

import scala.concurrent.Await
import scala.concurrent.duration._

import databaseComponent.Slick.*



object Persistence {
  @main def main: Unit = {

    Try(FileIOAPI) match
      case Success(v) => println("Persistance Rest Server is running!")
      case Failure(v) =>
        println(
          "Persistance Server couldn't be started! " + v.getMessage + v.getCause
        )

    val userDAO = SlickUserDAO()

    Await.result(userDAO.createTables(), Duration.Inf)
    userDAO.closeDatabase()
  }
}
