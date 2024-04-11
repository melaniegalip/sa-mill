package persistence

import persistence.FileIOAPI
import scala.util.{Try, Success, Failure}

object Persistence {
  @main def main: Unit = {
    Try(FileIOAPI) match
      case Success(v) => println("Persistance Rest Server is running!")
      case Failure(v) =>
        println(
          "Persistance Server couldn't be started! " + v.getMessage + v.getCause
        )
  }
}
