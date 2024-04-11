package persistence

import com.google.inject.Guice
import scala.io.StdIn.readLine
import scala.collection.immutable.LazyList.cons
import persistence.FileIOAPI
import scala.util.{Try,Success,Failure}

object Mill {
  @main def main: Unit = {
    Try(FileIOAPI) match
      case Success(v) => println("Persistance Rest Server is running!")
      case Failure(v) => println("Persistance Server couldn't be started! " + v.getMessage + v.getCause)
  }
}