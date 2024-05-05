package persistence

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

trait FileIOInterface {
  def load(data: String): String
  def save(gameStateAsJson: String): Unit
}
