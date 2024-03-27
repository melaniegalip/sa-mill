import de.htwg.se.mill.model.Player
import de.htwg.se.mill.model.Board
import scala.collection.immutable.ListMap

val nineMensMorris = Board.withSize().get
val test1 = nineMensMorris.toString
  == """
⚫――――――――――――⚫――――――――――――⚫
│   ⚫――――――――⚫――――――――⚫   │
│   │   ⚫――――⚫――――⚫   │   │
│   │   │            │   │   │
⚫――⚫――⚫         ⚫――⚫――⚫
│   │   │            │   │   │
│   │   ⚫――――⚫――――⚫   │   │
│   ⚫――――――――⚫――――――――⚫   │
⚫――――――――――――⚫――――――――――――⚫"""
val fields = nineMensMorris.fields
val largestRow = nineMensMorris.fields.maxBy(_.y).y
val upperSection = nineMensMorris.fields
  .filter(field => field.y == 0)
val lowerSection = nineMensMorris.fields.filter(field => field.y == largestRow)
val section = nineMensMorris.fields
  .filter(field => field.y > 0 && field.y < (nineMensMorris.size - 1))
  .sortBy(_.x)
val middleSections = section.splitAt(section.length / 2)
middleSections(0).map(f => s"${f.x},${f.y},${f.ring}")
middleSections(1).sortWith(_.ring > _.ring).map(f => s"${f.x},${f.y},${f.ring}")
val test = ListMap(
  lowerSection
    .groupBy(_.ring)
    .toSeq
    .sortWith(_._1 > _._1): _*
)
  .map(fields =>
    fields(1)
      .map(field => s"(${field.x}, ${field.y}, ${field.ring})")
      .mkString(",")
  )
  .mkString(",")
val upperSectionFormatted = upperSection
  .map(field => s"(${field.x}, ${field.y}, ${field.ring})")
  .mkString(",")
nineMensMorris.fieldsDump
val Melanie = Player("Melanie", "🔴")
val Reyhan = Player("Reyhan", "🔵")
val bigBoard = Board.withSize(5).get
bigBoard.fieldsDump
