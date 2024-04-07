package model

trait FileIOInterface {
  def load: GameState
  def save(gameState: GameState): Unit
}
