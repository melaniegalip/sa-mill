package persistence

import persistence.FileIOInterface
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import java.io.*
import scala.io.Source
import scala.xml.PrettyPrinter

object FileIOJson extends FileIOInterface {
  override def load: String = {
    /*
    val source = Source.fromFile("gameState.json")
    val json = Json.parse(source.getLines().mkString)
    source.close()
    json
     */

    val file = scala.io.Source.fromFile("gameState.json")
    try file.mkString
    finally file.close()
  }

  override def save(gameStateAsJson: String): Unit = {
    val pw = new PrintWriter(new File("gameState.json"))
    /*
    val save = Json.obj(
      "gameState" -> Json.toJson(gameState.toJson)
    )
    Json.prettyPrint(save)
     */
    pw.write(gameStateAsJson)
    pw.close()
  }
}
