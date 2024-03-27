package de.htwg.se.mill.aview.gui

import de.htwg.se.mill.model.FieldInterface
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.beans.property.ObjectProperty

final case class Field(
    fieldModel: FieldInterface,
    on: (field: FieldInterface) => Unit
) extends Button {
  minWidth = 50
  maxWidth = 50
  minHeight = 50
  maxHeight = 50
  shape = Circle(25)
  onAction = _ => on(fieldModel)
  style = fieldModel.color match {
    case "🔴" => "-fx-base: Red"
    case "🔵" => "-fx-base: Blue"
    case _    => "-fx-base: Black"
  }
}
