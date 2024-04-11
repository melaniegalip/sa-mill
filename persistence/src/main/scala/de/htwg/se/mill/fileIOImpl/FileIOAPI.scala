package persistence

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import play.api.libs.json._
import model.GameState

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.server.Directives._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object FileIOAPI {
  private val routes: String =
    """
      Welcome to the REST Persistence API service!
      Available routes:
        
        persistence/load
        persistence/save


      Contributors:     Muhammed Ergül
                        Melanie Galip
                              
      
    """.stripMargin
  implicit val system:ActorSystem[Any] = ActorSystem(Behaviors.empty, "my-system")

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
        complete(HttpEntity(ContentTypes.`application/json`, FileIOJson.load.toString))
      }
    },
    path("persistence" / "save") {
      concat(
        post {
          entity(as[String]) { game =>
            FileIOJson.save(GameState.fromJson((Json.parse(game) \ "gameState").get))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      )
    }
  )
   

   val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/persistence\nPress RETURN to stop...")
    //StdIn.readLine() // let it run until user presses return
    //bindingFuture
    //  .flatMap(_.unbind()) // trigger unbinding from the port
    //  .onComplete(_ => system.terminate()) // and shutdown when done
}