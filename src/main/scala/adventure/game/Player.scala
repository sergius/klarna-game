package adventure.game

object Player {
  val LookAroundMsg = "I can also see the following"
}

class Player(initialPosition: Room) extends ItemHolder {
import Player._
  var currentPosition = initialPosition

  def position: Room = currentPosition

  def moveTo(room: Room) =
    currentPosition = room

  def lookAround(): Seq[String] = {
    val itemsDescription = currentPosition.items.values.toSeq.map(item => item.description)
    if (itemsDescription.nonEmpty) currentPosition.description +: LookAroundMsg +: itemsDescription
    else Seq(currentPosition.description)
  }

}
