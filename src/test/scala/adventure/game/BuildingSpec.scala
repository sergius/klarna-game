package adventure.game

import adventure.game.Building.{Neighbour, RoomInfo}
import adventure.game.Direction.North
import org.scalatest.{Matchers, WordSpecLike}

class BuildingSpec extends WordSpecLike with Matchers {

  "When the Building is created it" must {
    "have an initial one (and only one) room" in {
      val initRoomId = 12
      val building = Building(RoomInfo(initRoomId))

      building.plan should have size 1
      building.plan.head._1 should equal(initRoomId)
    }
  }

  "The Building is immutable, therefore calling addRoom() on it" must {

    "create a new Building with the new room in its plan" in {
      val initRoomId = 12
      val building = Building(RoomInfo(initRoomId))

      building.plan should have size 1

      val newRoomId = 42
      val updatedBuilding = building.addRoom(RoomInfo(newRoomId), initRoomId, North)

      updatedBuilding.plan should have size 2
      updatedBuilding.plan.get(initRoomId) shouldNot be(None)
      updatedBuilding.plan.get(newRoomId) shouldNot be(None)
    }

    "make neighbours to each other the new room and the existing one" in {
      val room1Id = 1
      val room2Id = 42
      val testDir = North
      val building = Building(RoomInfo(room1Id)).addRoom(RoomInfo(room2Id), room1Id, testDir)

      val neighboursOfRoom1: Set[Neighbour] = building.plan(room1Id).neighbours
      neighboursOfRoom1 should have size 1
      val neighbour1: Neighbour = neighboursOfRoom1.head
      neighbour1.room.id should equal(room2Id)
      neighbour1.direction should equal(testDir)

      val neighboursOfRoom2: Set[Neighbour] = building.plan(room2Id).neighbours
      neighboursOfRoom2 should have size 1
      val neighbour2: Neighbour = neighboursOfRoom2.head
      neighbour2.room.id should equal(room1Id)
      neighbour2.direction should equal(Direction.oppositeTo(testDir))
    }

    "return the same Building if the id of the existing room is not found in the Building plan" in {
      val room1Id = 1
      val room2Id = 42
      val wrongId = 0
      val testDir = North
      val initialBuilding = Building(RoomInfo(room1Id))
      val updatedBuilding = initialBuilding.addRoom(RoomInfo(room2Id), wrongId, testDir)

      updatedBuilding should equal(initialBuilding)
    }

  }

}
