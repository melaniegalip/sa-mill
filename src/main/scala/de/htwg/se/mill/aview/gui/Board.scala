package de.htwg.se.mill.aview.gui

import scalafx.scene.layout.StackPane
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundImage
import scalafx.scene.image.Image
import scalafx.scene.layout.BackgroundRepeat
import scalafx.scene.layout.BackgroundSize
import scalafx.geometry.Side
import scalafx.scene.layout.BackgroundPosition
import de.htwg.se.mill.model.FieldInterface
import scalafx.geometry.Insets
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.scene.control.Label
import scalafx.scene.control.ContentDisplay
import scalafx.geometry.Pos
import de.htwg.se.mill.controller.ControllerInterface

final case class Board(
    val controller: ControllerInterface,
    val onAction: (field: FieldInterface) => Unit
) extends StackPane {
  background = new Background(
    Array(
      new BackgroundImage(
        new Image("bg.jpg"),
        BackgroundRepeat.NoRepeat,
        BackgroundRepeat.NoRepeat,
        new BackgroundPosition(
          Side.Left,
          0,
          true,
          Side.Bottom,
          0,
          true
        ),
        new BackgroundSize(
          BackgroundSize.Auto,
          BackgroundSize.Auto,
          true,
          true,
          false,
          true
        )
      )
    )
  )
  children = Seq(
    MessageBox(
      s"${controller.gameState.get.game.currentPlayer}'s turn(${controller.currentGameState})"
    )
  ).appendedAll(
    (0 until controller.gameState.get.game.board.size)
      .map(n =>
        new Ring(
          controller.gameState.get.game.board.fields.filter(f => f.ring == n),
          controller.gameState.get.game.board.size,
          onAction,
          n
        )
      )
  )

}
