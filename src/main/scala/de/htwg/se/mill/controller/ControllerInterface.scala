package de.htwg.se.mill.controller

import de.htwg.se.mill.util.Observable
import de.htwg.se.mill.model.GameState
import de.htwg.se.mill.model.FieldInterface

trait ControllerInterface extends Observable {
  var gameState: Option[GameState]
  var fromField: Option[FieldInterface]
  def addFirstPlayer(playerName: String, playerColor: String = "🔴"): Unit
  def addSecondPlayer(playerName: String, playerColor: String = "🔵"): Unit
  def newGame: Unit
  def undo: Option[Throwable]
  def redo: Option[Throwable]
  def save: Unit
  def load: Unit
  def quit: Unit
  def setPiece(to: FieldInterface): Option[Throwable]
  def movePiece(from: FieldInterface, to: FieldInterface): Option[Throwable]
  def removePiece(field: FieldInterface): Option[Throwable]
  def currentGameState: String
  def isSetting: Boolean
  def isRemoving: Boolean
  def isMovingOrFlying: Boolean
}
