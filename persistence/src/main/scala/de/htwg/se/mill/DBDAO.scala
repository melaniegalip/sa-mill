package persistence

import scala.concurrent.{Await, Future}

trait DBDAO {
  def dropTables(): Future[Unit]
  def createTables(): Future[Unit]
  def save(game: String): Future[Int]
  def load(): Future[Option[String]]
  def closeDatabase(): Unit
}
