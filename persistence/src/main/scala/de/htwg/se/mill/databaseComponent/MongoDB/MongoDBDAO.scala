package databaseComponent.MongoDB

import org.mongodb.scala._
import org.mongodb.scala.model._
import org.mongodb.scala.result._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import persistence.DBDAO
import scala.util.{Try, Success, Failure}
import play.api.libs.json.Json
import org.bson.json.JsonObject
import org.mongodb.scala.bson.collection.immutable.Document

class MongoDBDAO extends DBDAO {

  private val databaseDB: String = sys.env.getOrElse("MONGO_DB", "tbl")
  private val databaseUser: String = sys.env.getOrElse("MONGO_USERNAME", "root")
  private val databasePassword: String = sys.env.getOrElse("MONGO_PASSWORD", "mongo")
  private val databasePort: String = sys.env.getOrElse("MONGO_PORT", "27017")
  private val databaseHost: String = sys.env.getOrElse("MONGO_HOST", "mongo")
  
  private val databaseURI: String = 
    s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin&maxPoolSize=50&minPoolSize=5&maxIdleTimeMS=30000&connectTimeoutMS=10000"

  private val client: MongoClient = MongoClient(databaseURI)
  private val db: MongoDatabase = client.getDatabase(databaseDB)
  private val gameCollection: MongoCollection[JsonObject] = db.getCollection("game")

  override def delete(): Future[Unit] = {
    gameCollection.drop().toFuture().map(_ => ())
  }

  override def create(): Future[Unit] = {
    for {
      _ <- db.createCollection("game").toFuture()
      _ <- gameCollection.createIndex(Indexes.ascending("_id")).toFuture()
    } yield ()
  }

  override def save(game: String): Future[Int] = {
    gameCollection
      .replaceOne(
        Filters.eq("_id", 1),
        new JsonObject(game),
        ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => 1)
      .recover {
        case exception =>
          println(s"$exception, $game")
          throw new IllegalArgumentException(exception)
      }
  }

  override def load(): Future[Option[String]] = {
    gameCollection
      .find()
      .first()
      .toFuture()
      .map(doc => Some(doc.getJson()))
      .recover {
        case exception =>
          println(s"$exception")
          None
      }
  }

  override def closeDatabase(): Unit = {
    client.close()
  }
}
