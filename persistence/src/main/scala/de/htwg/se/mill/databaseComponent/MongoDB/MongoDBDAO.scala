package de.htwg.se.mill.databaseComponent.MongoDB
import org.mongodb.scala.{
  Document,
  MongoClient,
  MongoCollection,
  MongoDatabase,
  SingleObservableFuture,
  result
}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

import persistence.DBDAO
import scala.util.Try
import play.api.libs.json.Json
import scala.util.Failure
import scala.util.Success
import org.bson.json.JsonObject
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.UpdateOptions
import com.mongodb.client.model.ReplaceOptions

class MongoDBDAO extends DBDAO:
  private val databaseDB: String = sys.env.getOrElse("MONGO_DB", "tbl")
  private val databaseUser: String =
    sys.env.getOrElse("MONGO_USERNAME", "root")
  private val databasePassword: String =
    sys.env.getOrElse("MONGO_PASSWORD", "mongo")
  private val databasePort: String = sys.env.getOrElse("MONGO_PORT", "27017")
  private val databaseHost: String =
    sys.env.getOrElse("MONGO_HOST", "mongo")

  private val databaseURI: String =
    s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin"
  private val client: MongoClient = MongoClient(databaseURI)
  private val db: MongoDatabase = client.getDatabase(databaseDB)
  private val gameCollection: MongoCollection[JsonObject] =
    db.getCollection("game")

  def dropTables(): Future[Unit] = {
    Try {
      Await.result(gameCollection.drop().head, 10.seconds)
    } match
      case Failure(exception) =>
        println(exception)
        Future.failed(exception)
      case Success(value) =>
        println(value)
        Future.successful(())
  }
  def createTables(): Future[Unit] = {
    Try {
      Await.result(db.createCollection("game").head, 10.seconds)
      Await.result(db.listCollectionNames().head, 10.seconds)
    } match
      case Failure(exception) =>
        println(exception)
        Future.failed(exception)
      case Success(value) =>
        println(value)
        Future.successful(())
  }
  def save(game: String): Future[Int] = {
    Try(
      Await.result(
        gameCollection
          .replaceOne(
            Filters.eq("_id", 1),
            new JsonObject(game),
            ReplaceOptions().upsert(true)
          )
          .head(),
        30.seconds
      )
    ) match {
      case Failure(exception) =>
        println(s"$exception, $game")
        Future.failed(new IllegalArgumentException(exception))
      case Success(value) =>
        println(value)
        Future.successful(1)
    }
  }
  def load(): Future[Option[String]] = {
    Try(
      Await.result(
        gameCollection.find().first().head(),
        30.seconds
      )
    ) match {
      case Failure(exception) =>
        println(s"$exception")
        Future.failed(exception)
      case Success(value) =>
        println(value)
        Future.successful(Some(value.getJson()))
    }
  }
  def closeDatabase(): Unit = {
    client.close()
  }
