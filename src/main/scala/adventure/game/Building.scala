package adventure.game

import adventure.game.Building.Neighbour

sealed trait Direction

object Direction {

  case object North extends Direction

  case object East extends Direction

  case object South extends Direction

  case object West extends Direction

  def oppositeTo(dir: Direction): Direction = dir match {
    case North => South
    case South => North
    case East => West
    case West => East
  }
}

sealed trait Building {

  val plan: Map[Int, Room]

  /**
   * Adds a room to a Building, returning a new Building.
   * @param newRoomId The id for the new room.
   * @param toRoom The id of some existing room. The new room will be added next to it.
   *               Care should be taken with this parameter: if a room with this id
   *               is not found in the Building plan, the new room will not be added.
   * @param side The side of the existing room, where to add the new room
   * @return A new Building containing the added room. If the id of the existing room is
   *         not found, no changes are applied and the same Building is returned.
   */
  def addRoom(toRoom: Int, side: Direction)(newRoomId: Int, description: String = ""): Building
}

sealed trait Room {

  val id: Int

  val description: String

  val neighbours: Set[Neighbour]
}

object Building {

  type Neighbour = (Room, Direction)

  def apply(initRoomId: Int, description: String = ""): Building =
    new BuildingImpl(Map(initRoomId -> new RoomImpl(initRoomId, description, Set.empty[Neighbour])))

  private class BuildingImpl(val plan: Map[Int, Room]) extends Building {

    override def addRoom(toRoom: Int, side: Direction)
                        (newRoomId: Int, description: String = ""): Building = plan.get(toRoom) match {
      case None => this
      case Some(room) =>
        val newRoom = new RoomImpl(newRoomId, description, Set(new Neighbour(room, Direction.oppositeTo(side))))
        val updatedRoom = new RoomImpl(room.id, description, room.neighbours + new Neighbour(newRoom, side))
        val newPlan = plan + (room.id -> updatedRoom) + (newRoomId -> newRoom)
        new BuildingImpl(newPlan)
    }
  }

  private class RoomImpl(val id: Int, val description: String = "", val neighbours: Set[Neighbour]) extends Room

}
