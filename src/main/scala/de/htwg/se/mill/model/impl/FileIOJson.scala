package de.htwg.se.mill.model.impl

import de.htwg.se.mill.model.FileIOInterface
import de.htwg.se.mill.model.GameState
import play.api.libs.json.Json
import java.io.*
import scala.io.Source
import scala.xml.PrettyPrinter

object FileIOJson extends FileIOInterface {
  override def load: GameState = {
    val source = Source.fromFile("gameState.json")
    val json = Json.parse(source.getLines().mkString)
    source.close()
    GameState.fromJson((json \ "gameState").get)
  }

  override def save(gameState: GameState): Unit = {
    val pw = new PrintWriter(new File("gameState.json"))
    val save = Json.obj(
      "gameState" -> Json.toJson(gameState.toJson)
    )
    pw.write(Json.prettyPrint(save))
    pw.close()
  }
}
