package de.htwg.se.mill

import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.Controller
import model.GameInterface
import model.Game
import model.Player
import model.PlayerInterface
import model.BoardInterface
import model.FieldInterface
import model.Field
import model.Board
import model.FileIOJson
import model.FileIOInterface
import model.FileIOXml

class MillModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[BoardInterface]).toInstance(Board.withSize(3).get)
    // bind(classOf[FileIOInterface]).toInstance(FileIOJson)
    bind(classOf[FileIOInterface]).toInstance(FileIOXml)
  }
}
