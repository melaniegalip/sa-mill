package de.htwg.se.mill

import com.google.inject.AbstractModule
import controller.ControllerInterface
import controller.Controller
import model.BoardInterface
import model.Board

class MillModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[BoardInterface]).toInstance(Board.withSize(3).get)
  }
}
