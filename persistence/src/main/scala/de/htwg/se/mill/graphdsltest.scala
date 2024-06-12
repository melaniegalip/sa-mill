package persistence

import akka.stream.scaladsl.Merge
import scala.concurrent.Await
import akka.stream.scaladsl.RunnableGraph
import akka.stream.ClosedShape
import akka.NotUsed
import akka.stream.scaladsl.Zip
import akka.stream.scaladsl.Broadcast
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.GraphDSL
import akka.actor.ActorSystem
import akka.stream.Materializer

object graphdsltest {

  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: Materializer = Materializer(system)

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val source = Source(1 to 10)
    val sink = Sink.foreach(println)

    val broadcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    val f1 = Flow[Int].map(_ + 10)
    val f2 = Flow[Int].map(_ + 10)
    val f3 = Flow[Int].map(_ + 10)
    val f4 = Flow[Int].map(_ + 10)

    source ~> f1 ~> broadcast ~> f2 ~> merge ~> f3 ~> sink
    broadcast ~> f4 ~> merge

    ClosedShape
  })

  @main def main: Unit = {
    g.run()
  }
}
