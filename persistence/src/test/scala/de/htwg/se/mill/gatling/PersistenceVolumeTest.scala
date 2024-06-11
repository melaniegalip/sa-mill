package gatling

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import akka.http.javadsl.Http
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.structure.PopulationBuilder
import io.gatling.core.body.Body
import akka.http.javadsl.model.HttpMethod
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source, Flow}
import akka.stream.{ActorMaterializer, Materializer}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import scala.concurrent.Await
import play.api.libs.json.Writes.mapWrites

object TestDataStream {
  implicit val system: ActorSystem = ActorSystem("TestDataStreamSystem")
  implicit val materializer: Materializer = ActorMaterializer()

  var game =
    Json.parse(
      scala.io.Source
        .fromURL(getClass.getResource("/game_volume.json"))
        .mkString
    )

  val testDataSource: Source[String, _] =
    Source.fromIterator(() => Iterator.continually(generateTestData))

  def generateTestData: String = {
    val field = Map.apply(
      "x" -> scala.util.Random.nextInt(3).toString(),
      "y" -> scala.util.Random.nextInt(3).toString(),
      "ring" -> scala.util.Random.nextInt(3).toString(),
      "color" -> (scala.util.Random.nextInt(3) match
        case 0 => "⚫"
        case 1 => "🔴"
        case 2 => "🔵"
      )
    )
    val fields = (game \ "gameState" \ "game" \ "board" \ "fields").get
      .as[List[Map[String, String]]]

    def equalsField(f: Map[String, String]) = f.get("x") == field.get("x")
      && f.get("y") == field.get("y")
      && f.get("ring") == field.get("ring")

    game = game
      .transform(
        (__ \ "gameState" \ "game" \ "board" \ "fields").json.update(
          __.read[List[Map[String, String]]]
            .map(_ =>
              Json.toJson(fields.map {
                case f if equalsField(f) => field; case x => x
              })
            )
        )
      )
      .asOpt
      .get

    game.toString
  }

  def getTestData(count: Int): Future[String] = {
    testDataSource.take(count).runWith(Sink.head)
  }
}

class PersistenceVolumeTest extends SimulationSkeleton {

  val testDataCount = 100
  val testDataFuture = TestDataStream.getTestData(testDataCount)
  val testData = Await.result(testDataFuture, 10.seconds)

  override val operations = List(
    buildOperation(
      "persistence save",
      "POST",
      "/persistence/save",
      StringBody(testData)
    ),
    buildOperation(
      "persistence load",
      "GET",
      "/persistence/load",
      StringBody("")
    )
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

    setUp(
      scn
        .inject(
          // ramp up users to 100 in 10 seconds
          rampUsersPerSec(10) to 100 during (10.second)
        )
        .andThen(
          scn2.inject(
            // hold 100 users for 10 seconds
            constantUsersPerSec(100) during (10.second)
          )
        )
        .andThen(
          scn3.inject(
            // ramp down users to 0 in 10 seconds
            rampUsersPerSec(100) to 0 during (10.second)
          )
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}

object Test {
  @main def main: Unit = {
    val testDataCount = 100
    val testDataFuture = TestDataStream.getTestData(testDataCount)
    val testData = Await.result(testDataFuture, 10.seconds)
  }
}
