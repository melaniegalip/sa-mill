package aview

import util.Observer
import util.Event
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import javafx.scene.paint.Color
import scalafx.Includes.*
import scalafx.scene.layout.StackPane
import aview.gui.Ring
import scalafx.application.Platform
import scalafx.scene.layout.Background
import scalafx.scene.layout.BackgroundImage
import scalafx.scene.image.Image
import scalafx.scene.layout.BackgroundRepeat
import scalafx.scene.layout.BackgroundPosition
import scalafx.geometry.Side
import scalafx.scene.layout.BackgroundSize
import model.FieldInterface
import aview.gui.Board
import aview.gui.MessageBox
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.text.Font
import scalafx.scene.control.TextInputDialog
import util.Messages
import controller.ControllerInterface
import scalafx.scene.input.{KeyEvent, KeyCode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source, Flow}
import akka.stream.{ActorMaterializer, Materializer}

class GUI(val controller: ControllerInterface) extends JFXApp3 with Observer {
  controller.add(this)

  implicit val system: ActorSystem = ActorSystem("MillSystem")
  implicit val materializer: Materializer = Materializer(system)

  def runStream(): Unit = {
    val source = Source.actorRef[String](bufferSize = 100, overflowStrategy = akka.stream.OverflowStrategy.dropHead)
    
    val (actorRef, publisher) = source.toMat(Sink.asPublisher(fanout = false))(akka.stream.scaladsl.Keep.both).run()

    val flow = Flow[String].map { keyPress =>
      s"Key Pressed: $keyPress"
    }

    val sink = Sink.foreach[String] { data =>
      Platform.runLater {
        handleStreamData(data)
      }
    }

    Source.fromPublisher(publisher).via(flow).runWith(sink)

    stage.scene().onKeyPressed = handleKeyPress(actorRef) _
  }

  def handleStreamData(data: String): Unit = {
    new Alert(AlertType.Information) {
      initOwner(stage)
      headerText = data
    }.showAndWait()
  }

  def onAction: (field: FieldInterface) => Unit = (field: FieldInterface) => {
    if (controller.isSetting) {
      controller.setPiece(field)
    } else if (controller.isRemoving) {
      controller.removePiece(field)
    } else {
      if (controller.fromField.isDefined) {
        controller.movePiece(controller.fromField.get, field)
        controller.fromField = None
      } else {
        controller.fromField = Some(field)
      }
    }
  }

  override def update(message: Option[String], e: Event): Unit = {
    if (message.isDefined) {
      Platform.runLater {
        new Alert(AlertType.Warning) {
          initOwner(stage)
          headerText = message.get
        }.showAndWait()
      }
    }
    e match
      case Event.QUIT => Platform.exit()
      case Event.PLAY => start()
  }

  override def start(): Unit = {
    
    stage = new JFXApp3.PrimaryStage {
      title = "Mill"
      onCloseRequest = () => {
        system.terminate()
        Platform.exit();
      }
      scene = new Scene(800, 600) {
        resizable = false
        root = {
          if (controller.gameState.isEmpty) {
            val firstPlayerName = new TextInputDialog() {
              initOwner(stage)
              title = "Add first Player"
              headerText = Messages.introductionText
              contentText = "Please enter the name of the first player:"
            }.showAndWait()

            firstPlayerName match {
              case Some(name) => controller.addFirstPlayer(name)
              case None => {
                println("Add first Player Dialog was canceled.")
                sys.exit()
              }
            }
            val secondPlayerName = new TextInputDialog() {
              initOwner(stage)
              title = "Add second Player"
              headerText = Messages.addSecondPlayerText
              contentText = "Please enter the name of the second player:"
            }.showAndWait()

            secondPlayerName match {
              case Some(name) => controller.addSecondPlayer(name)
              case None => {
                println("Add second Player Dialog was canceled.")
                sys.exit()
              }
            }
            controller.newGame
          }
          Board(controller, onAction)
        }
      }
    }
    runStream()
  }

  def handleKeyPress(actorRef: akka.actor.ActorRef)(event: KeyEvent): Unit = {
    actorRef ! event.code.toString
    event.code match {
      case KeyCode.Q =>
        controller.quit
      case KeyCode.N =>
        controller.newGame
      case KeyCode.U =>
        controller.undo
      case KeyCode.R =>
        controller.redo
      case KeyCode.S =>
        controller.save
      case KeyCode.L =>
        controller.load
      case _ =>
        println("Invalid KeyCode")
    }
  }
}
