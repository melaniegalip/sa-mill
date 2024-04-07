package de.htwg.se.mill

import model.Board
import aview.TUI
import scala.util.{Try,Success, Failure}
import controller.Controller
import aview.GUI
import scala.io.StdIn.readLine
import scalafx.application.Platform
import model.BoardInterface
import persistence.FileIOInterface
import com.google.inject.Injector
import com.google.inject.Guice
import controller.ControllerInterface
import persistence.FileIOAPI

object Mill {
  def main(args: Array[String]): Unit = {

    Try(FileIOAPI) match
      case Success(v) => println("Persistance Rest Server is running!")
      case Failure(v) => println("Persistance Server couldn't be started! " + v.getMessage + v.getCause)

    val injector: Injector = Guice.createInjector(new MillModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = TUI(controller)
    //tui.start

    val gui = GUI(controller)
    val guiThread = new Thread(() => {
      gui.main(Array.empty)
      System.exit(0)
    })
    guiThread.setDaemon(true)
    guiThread.start()

    tui.run
  }
}
