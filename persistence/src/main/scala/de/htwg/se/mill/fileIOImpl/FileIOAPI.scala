package persistence

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import play.api.libs.json._

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity
import scala.concurrent.duration.DurationInt

import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

import databaseComponent.Slick.*
import scala.concurrent.Await

import com.google.inject.Inject
import com.google.inject.Guice

import databaseComponent.MongoDB.*

class FileIOAPI @Inject() (db: DBDAO) {

  private val routes: String =
    """
      Welcome to the REST Persistence API service!
      Available routes:

        persistence/load
        persistence/save


      Contributors:     Muhammed Ergül
                        Melanie Galip


    """.stripMargin
  implicit val system: ActorSystem[Any] =
    ActorSystem(Behaviors.empty, "my-system")

  val executionContext: ExecutionContextExecutor = system.executionContext
  given ExecutionContextExecutor = executionContext

  val route = concat(
    path("persistence") {
      get {
        complete(routes)
      }
    },
    path("persistence" / "load") {
      get {
        onSuccess(db.load()) { gameStateOpt =>
          gameStateOpt match {
            case Some(gameStateJson) =>
              complete(
                HttpEntity(
                  ContentTypes.`application/json`,
                  gameStateJson
                )
              )
            case None =>
              complete(StatusCodes.NotFound, "No game state found")
          }
        }
      }
    },
    path("persistence" / "save") {
      concat(
        post {
          entity(as[String]) { game =>
            Await.result(db.create(), 60.seconds)
            Await.result(db.save(game), 60.seconds)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      )
    }
  )

  val connectIP =
    sys.env.getOrElse("FILEIO_SERVICE_HOST", "0.0.0.0").toString
  val connectPort =
    sys.env.getOrElse("FILEIO_SERVICE_PORT", 8081).toString.toInt
  val bindingFuture = Http().newServerAt(connectIP, connectPort).bind(route)

  println(
    s"Server now online. Please navigate to http://$connectIP:$connectPort/persistence\nPress q to stop..."
  )

  while (StdIn.readLine() != "q") {}
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
  db.delete()
}
