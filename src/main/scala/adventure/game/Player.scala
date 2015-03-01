package adventure.game

class Player(initialPosition: Room) extends ItemHolder {

  private val LookAroundMsg = "You can also see the following:"

  private var currentPosition = initialPosition

  def position: Room = currentPosition

  def moveTo(room: Room) =
    currentPosition = room

  def lookAround(): Seq[String] = {
    val itemsDescription = currentPosition.items.map(item => item.description)
    if (itemsDescription.nonEmpty)
      currentPosition.description +: LookAroundMsg +: itemsDescription
    else
      Seq(currentPosition.description)
  }

}
