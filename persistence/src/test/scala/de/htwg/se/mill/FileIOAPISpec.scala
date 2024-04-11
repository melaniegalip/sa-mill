import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import persistence.FileIOAPI

class FileIOAPISpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val testKit: ActorTestKit = ActorTestKit()
  implicit val system: ActorSystem[_] = testKit.system
  implicit val ec = system.executionContext

  "The FileIOAPI" should {
    "return a welcome message for the root route" in {
      Get("/persistence") ~> FileIOAPI.route ~> check {
        responseAs[String] should include("Welcome to the REST Persistence API service!")
      }
    }

    "return saved game state for load route" in {
      Get("/persistence/load") ~> FileIOAPI.route ~> check {
        contentType should ===(ContentTypes.`application/json`)
      }
    }

    "save the game state for save route" in {
      val jsonEntity =
        s"""
           |{
           |  "gameState": {
           |    "playerName": "Alice",
           |    "score": 100
           |  }
           |}
           |""".stripMargin

      Post("/persistence/save", HttpEntity(MediaTypes.`application/json`, jsonEntity)) ~> FileIOAPI.route ~> check {
        responseAs[String] should include("Game saved")
      }
    }
  }

  override def afterAll(): Unit = {
    testKit.shutdownTestKit()
  }
}
