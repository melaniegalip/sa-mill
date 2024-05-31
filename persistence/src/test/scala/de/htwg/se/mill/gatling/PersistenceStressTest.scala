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

class PersistenceStressTest extends SimulationSkeleton {

  override val operations = List(
    buildOperation("persistence save", "POST", "/persistence/save", ElFileBody("C:\\Users\\User\\sa-mill\\persistence\\src\\test\\scala\\de\\htwg\\se\\mill\\gatling\\example_bodies\\game.json")),
    buildOperation("persistence load", "GET", "/persistence/load", StringBody("")),
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

    setUp(
      // stress test with increasing the user number from 0 to 3000 in 1 minute
      scn.inject(
        stressPeakUsers(3000) during (20.second)
      )
    ).protocols(httpProtocol)
  }

  executeOperations()
}