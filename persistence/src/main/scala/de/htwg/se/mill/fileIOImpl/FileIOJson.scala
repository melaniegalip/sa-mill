package persistence

import persistence.FileIOInterface
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import java.io.*
import scala.io.Source
import scala.xml.PrettyPrinter
import model.GameState

object FileIOJson extends FileIOInterface {
  override def load: JsValue = {
    val source = Source.fromFile("gameState.json")
    val json = Json.parse(source.getLines().mkString)
    source.close()
    json
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
