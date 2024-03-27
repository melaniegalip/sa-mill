package de.htwg.se.mill.aview.gui

import de.htwg.se.mill.model.FieldInterface
import scalafx.scene.layout.GridPane
import scalafx.geometry.Insets
import scalafx.scene.layout.ColumnConstraints
import scalafx.geometry.HPos
import scalafx.scene.layout.RowConstraints
import scalafx.scene.layout.Border
import scalafx.scene.layout.BorderStroke
import scalafx.scene.paint.Color
import scalafx.scene.layout.BorderStrokeStyle
import scalafx.scene.layout.CornerRadii
import scalafx.scene.layout.BorderWidths
import scalafx.geometry.VPos

final case class Ring(
    fields: List[FieldInterface],
    size: Int,
    onAction: (field: FieldInterface) => Unit,
    level: Int = 0
) extends GridPane {
  border = new Border(
    new BorderStroke(
      Color.Black,
      BorderStrokeStyle.Solid,
      CornerRadii.Empty,
      new BorderWidths(5)
    )
  )
  columnConstraints = (0 until size)
    .map(n => new ColumnConstraints { percentWidth = 100 })
  rowConstraints = (0 until size)
    .map(n => new RowConstraints { percentHeight = 100 })
  children ++=
    fields
      .filter(f => f.y == 0)
      .zipWithIndex
      .map((f, i) => {
        val field = new Field(f, onAction)
        GridPane.setConstraints(field, i, 0)
        if (i == 0) {
          GridPane.setHalignment(field, HPos.Left)
        } else if (i == size - 1) {
          GridPane.setHalignment(field, HPos.Right)
        } else {
          GridPane.setHalignment(field, HPos.Center)
        }
        GridPane.setValignment(field, VPos.Top)
        field
      })
  val middleSection = fields
    .filter(field => field.y > 0 && field.y < (size - 1))
    .sortBy(_.x)
  val middleLeftField = new Field(middleSection(0), onAction)
  val middleRightField = new Field(middleSection(1), onAction)
  GridPane.setConstraints(middleLeftField, 0, 1)
  GridPane.setHalignment(middleLeftField, HPos.Left)
  GridPane.setConstraints(middleRightField, 2, 1)
  GridPane.setHalignment(middleRightField, HPos.Right)
  GridPane.setValignment(middleLeftField, VPos.Center)
  GridPane.setValignment(middleRightField, VPos.Center)
  children ++= Seq(middleLeftField, middleRightField)
  children ++= fields
    .filter(f => f.y == size - 1)
    .zipWithIndex
    .map((f, i) => {
      val field = new Field(f, onAction)
      GridPane.setConstraints(field, i, size - 1)
      if (i == 0) {
        GridPane.setHalignment(field, HPos.Left)
      } else if (i == size - 1) {
        GridPane.setHalignment(field, HPos.Right)
      } else {
        GridPane.setHalignment(field, HPos.Center)
      }
      GridPane.setValignment(field, VPos.Bottom)
      field
    })
  margin = Insets(level * (50 + 20))
  padding = Insets(8)
}
