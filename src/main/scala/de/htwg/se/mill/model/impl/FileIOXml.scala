package de.htwg.se.mill.model.impl

import de.htwg.se.mill.model.FileIOInterface
import java.io.{File, PrintWriter}
import scala.xml.{Elem, Node, PrettyPrinter}
import de.htwg.se.mill.model.GameState

object FileIOXml extends FileIOInterface {
  override def load: GameState = {
    val xml = scala.xml.XML.loadFile("gameState.xml")
    GameState.fromXml(xml)
  }

  override def save(gameState: GameState): Unit = {
    val pw = new PrintWriter(new File("gameState.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gameState.toXml)
    pw.write(xml)
    pw.close()
  }
}
