package persistence

import persistence.FileIOInterface
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import java.io.*
import scala.io.Source
import scala.xml.PrettyPrinter

object FileIOJson extends FileIOInterface {
  override def load(data: String): String = {
    val file = scala.io.Source.fromFile("gameState.json")
    try file.mkString
    finally file.close()
    data
  }

  override def save(gameStateAsJson: String): Unit = {
    val pw = new PrintWriter(new File("gameState.json"))

    pw.write(gameStateAsJson)
    pw.close()
  }
}
