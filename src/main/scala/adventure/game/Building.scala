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

  def addNeighbour(roomId: Int, neighbour: Neighbour): Building
}

sealed trait Room extends ItemHolder {

  val id: Int

  val description: String

  val neighbours: Set[Neighbour]
}

object Building {

  def apply(roomInfo: RoomInfo): Building =
    new BuildingImpl(Map(roomInfo.id -> new RoomImpl(roomInfo.id, roomInfo.description, Set.empty[Neighbour])))

  case class RoomInfo(id: Int, description: String = "")

  case class Neighbour(roomId: Int, direction: Direction)

  private class RoomImpl(val id: Int, val description: String, val neighbours: Set[Neighbour]) extends Room

  private class BuildingImpl(val plan: Map[Int, Room]) extends Building {

    override def addRoom(newRoomInfo: RoomInfo, toRoom: Int, side: Direction): Building =
      plan.get(toRoom) match {
        case Some(room) =>
          val updatedPlan =
            plan.updated(room.id,
              new RoomImpl(room.id, room.description, room.neighbours + Neighbour(newRoomInfo.id, side)))
              .updated(newRoomInfo.id,
                new RoomImpl(newRoomInfo.id, newRoomInfo.description,
                  Set(Neighbour(room.id, Direction.oppositeTo(side)))))


          new BuildingImpl(updatedPlan)


        case _ => this
      }

    override def addNeighbour(roomId: Int, neighbour: Neighbour): Building =
      (plan.get(roomId), plan.get(neighbour.roomId)) match {
        case (Some(room), Some(neigh)) =>
          val updatedPlan =
            plan.updated(room.id,
              new RoomImpl(room.id, room.description, room.neighbours + neighbour))
              .updated(neigh.id,
                new RoomImpl(neigh.id, neigh.description, neigh.neighbours +
                  Neighbour(room.id, Direction.oppositeTo(neighbour.direction))))

          new BuildingImpl(updatedPlan)

        case _ => this
      }
  }

}
