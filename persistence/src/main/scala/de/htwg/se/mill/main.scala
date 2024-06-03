package persistence

import persistence.FileIOAPI
import scala.util.{Try, Success, Failure}

import databaseComponent.Slick.*
import databaseComponent.MongoDB.*

import com.google.inject.Injector
import com.google.inject.Guice

object Persistence {
  @main def main: Unit = {
    val injector: Injector = Guice.createInjector(new PersistenceModule)
    val fileIOAPI = injector.getInstance(classOf[FileIOAPI])
    Try(fileIOAPI) match
      case Success(persistence) => {
        println("Persistance Rest Server is running!")
      }
      case Failure(v) =>
        println(
          "Persistance Server couldn't be started! " + v.getMessage + v.getCause
        )
  }
}
