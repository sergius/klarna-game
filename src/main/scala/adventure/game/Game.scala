package adventure.game

trait Game {

  val building: Building

  val items: Set[Item]

  def playerPosition: Room

  def moveTo(room: Room)
}


