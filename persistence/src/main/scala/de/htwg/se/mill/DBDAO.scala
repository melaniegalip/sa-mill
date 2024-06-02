package persistence

import scala.concurrent.{Await, Future}

trait DBDAO {
  def delete(): Future[Unit]
  def create(): Future[Unit]
  def save(game: String): Future[Int]
  def load(): Future[Option[String]]
  def closeDatabase(): Unit
}
