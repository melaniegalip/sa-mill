package de.htwg.se.mill

import de.htwg.se.mill.model.Board
import de.htwg.se.mill.aview.TUI
import scala.util.{Success, Failure}
import de.htwg.se.mill.controller.Controller
import de.htwg.se.mill.aview.GUI
import scala.io.StdIn.readLine
import scalafx.application.Platform
import de.htwg.se.mill.model.BoardInterface
import com.google.inject.Injector
import com.google.inject.Guice
import de.htwg.se.mill.controller.ControllerInterface

object Mill {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new MillModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = TUI(controller)
    // tui.start

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
