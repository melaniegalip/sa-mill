package de.htwg.se.mill

import aview.TUI
import aview.GUI
import scalafx.application.Platform
import com.google.inject.Injector
import com.google.inject.Guice
import controller.ControllerInterface

object Mill {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new MillModule)
    val controller = injector.getInstance(classOf[ControllerInterface])
    val tui = TUI(controller)
    // tui.start
    Platform.startup(() => {
      Platform.implicitExit_=(false);
    });

    val gui = GUI(controller)

    gui.main(Array())

    // tui.runTUI
  }
}
