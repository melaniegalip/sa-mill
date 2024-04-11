package persistence

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

trait FileIOInterface {
  def load: String
  def save(gameStateAsJson: String): Unit
}
