package de.htwg.se.mill.model

trait FileIOInterface {
  def load: GameState
  def save(gameState: GameState): Unit
}
