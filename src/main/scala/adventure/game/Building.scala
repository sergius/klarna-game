package adventure.game

import adventure.game.Building.{Neighbour, RoomInfo}

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

sealed trait Direction

sealed trait Building {

  val plan: Map[Int, Room]

  /**
   * Adds a room to a Building, returning a new Building.
   * @param newRoomInfo The details for the new room
   * @param toRoom The id of some existing room. The new room will be added next to it.
   *               Care should be taken with this parameter: if a room with this id
   *               is not found in the Building plan, the new room will not be added.
   * @param side The side of the existing room, where to add the new room
   * @return A new Building containing the added room. If the id of the existing room is
   *         not found, no changes are applied and the same Building is returned.
   */
  def addRoom(newRoomInfo: RoomInfo, toRoom: Int, side: Direction): Building
}

sealed trait Room extends ItemHolder {

  val id: Int

  val description: String

  val neighbours: Set[Neighbour]
}

object Building {

  case class RoomInfo(id: Int, description: String = "")
  case class Neighbour(room: Room, direction: Direction)

  def apply(roomInfo: RoomInfo): Building =
    new BuildingImpl(Map(roomInfo.id -> new RoomImpl(roomInfo, Set.empty[Neighbour])))

  private class BuildingImpl(val plan: Map[Int, Room]) extends Building {

    override def addRoom(newRoomInfo: RoomInfo, toRoom: Int, side: Direction): Building =
      plan.get(toRoom) match {

        case None => this

        case Some(room) =>
          val newRoom =
            new RoomImpl(newRoomInfo, Set(Neighbour(room, Direction.oppositeTo(side))))

          val updatedRoom =
            new RoomImpl(RoomInfo(room.id, room.description), room.neighbours + new Neighbour(newRoom, side))

          new BuildingImpl(plan + (room.id -> updatedRoom) + (newRoomInfo.id -> newRoom))
      }
  }

  private class RoomImpl(val roomInfo: RoomInfo, val neighbours: Set[Neighbour]) extends Room {
    lazy val id = roomInfo.id
    lazy val description = roomInfo.description
  }

}
