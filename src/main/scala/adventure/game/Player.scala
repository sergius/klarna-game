package adventure.game

class Player(initialPosition: Room) extends ItemHolder {

  var currentPosition = initialPosition

  def position: Room = currentPosition

  def moveTo(room: Room) =
    currentPosition = room

  def lookAround(): Seq[String] =
    currentPosition.description +: currentPosition.items.map(i => i.description)

}
