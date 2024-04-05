package aview.gui

import scalafx.scene.layout.StackPane
import scalafx.geometry.Insets
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color
import scalafx.scene.control.Label
import scalafx.scene.control.ContentDisplay
import scalafx.scene.text.Font

final case class MessageBox(message: String = "") extends StackPane {
  val currentPath = new java.io.File(".").getCanonicalPath
  padding = Insets(20)
  children = List(
    new Rectangle {
      width = 350
      height = 100
      fill = Color.Bisque
      stroke = Color.Burlywood
    },
    new Label {
      text = message
      font = Font.loadFont(
        "file:" + currentPath + "\\aview\\src\\resources\\OpenSansEmoji.ttf",
        20
      )
      contentDisplay = ContentDisplay.Center
    }
  )
}
