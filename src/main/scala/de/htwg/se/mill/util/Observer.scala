package de.htwg.se.mill.util

trait Observer {
  def update(message: Option[String], e: Event): Unit
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = {
    subscribers = subscribers.appended(s)
  }
  def remove(s: Observer) = {
    subscribers = subscribers.filterNot(o => o == s)
  }
  def notifyObservers(message: Option[String], e: Event) =
    subscribers.foreach(o => o.update(message, e))
}

enum Event {
  case QUIT, PLAY
}
