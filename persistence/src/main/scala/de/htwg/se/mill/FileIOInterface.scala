package persistence

import model.GameState
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

trait FileIOInterface {
  def load: JsValue
  def save(gameState: GameState): Unit
}
