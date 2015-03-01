package adventure.game

import adventure.game.Building.{Neighbour, RoomInfo}
import adventure.game.Direction.{South, East, North}
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

    "make neighbours to each other: the new room and the existing one" in {
      val room1Id = 1
      val room2Id = 42
      val testDir = North
      val building = Building(RoomInfo(room1Id)).addRoom(RoomInfo(room2Id), room1Id, testDir)

      val neighboursOfRoom1: Set[Neighbour] = building.plan(room1Id).neighbours
      neighboursOfRoom1 should have size 1
      val neighbour1: Neighbour = neighboursOfRoom1.head
      neighbour1.roomId should equal(room2Id)
      neighbour1.direction should equal(testDir)

      val neighboursOfRoom2: Set[Neighbour] = building.plan(room2Id).neighbours
      neighboursOfRoom2 should have size 1
      val neighbour2: Neighbour = neighboursOfRoom2.head
      neighbour2.roomId should equal(room1Id)
      neighbour2.direction should equal(Direction.oppositeTo(testDir))
    }

    "return the same Building if the id of the existing room is not found in the Building plan" in {
      val room1Id = 1
      val room2Id = 42
      val wrongId = 0
      val direction = North
      val initialBuilding = Building(RoomInfo(room1Id))
      val updatedBuilding = initialBuilding.addRoom(RoomInfo(room2Id), wrongId, direction)

      updatedBuilding should equal(initialBuilding)
    }
  }

  "When adding a neighbour to a room (both have to be existing Rooms) it" must {
    "return a new Building with the updated plan, with two rooms as mutual neighbours" in {

      //scheme of proposed structure

      // = as graph =            = as map =
      //
      //          - room2         --- ------
      //         |               | r | room2|
      // room1 --                | o |      |
      //         |               | o |------
      //          - room3        | m |      |
      //                         | 1 | room3|
      //                          --- ------

      val room1 = RoomInfo(1, "test room 1")
      val room2 = RoomInfo(2, "test room 2")
      val room3 = RoomInfo(3, "test room 3")
      val direction1 = East
      val direction2 = South
      val initialBuilding = Building(room1).addRoom(room2, room1.id, direction1).addRoom(room3, room1.id, direction1)
      val updatedBuilding = initialBuilding.addNeighbour(room2.id, Neighbour(room3.id, direction2))

      updatedBuilding should not equal initialBuilding

      updatedBuilding.plan(room2.id).neighbours should have size 2
      updatedBuilding.plan(room2.id).neighbours should contain
        only(Neighbour(room1.id, Direction.oppositeTo(direction1)), Neighbour(room3.id, direction2))
      updatedBuilding.plan(room3.id).neighbours should contain
        only(Neighbour(room1.id, Direction.oppositeTo(direction1)), Neighbour(room2.id, Direction.oppositeTo(direction2)))
    }

    "if any of the Room's id-s, passed as parameters, don't correspond to existing rooms," +
      "no changes are applied and the same Building is returned" in {
      val room1 = RoomInfo(1, "test room 1")
      val room2 = RoomInfo(2, "test room 2")
      val nonexistent = RoomInfo(3, "test room 3")
      val direction1 = East
      val direction2 = South
      val initialBuilding = Building(room1).addRoom(room2, room1.id, direction1)

      val updatedBuilding1 = initialBuilding.addNeighbour(room2.id, Neighbour(nonexistent.id, direction2))
      updatedBuilding1 shouldBe initialBuilding

      val updatedBuilding2 = initialBuilding.addNeighbour(nonexistent.id, Neighbour(room2.id, direction2))
      updatedBuilding2 shouldBe initialBuilding
    }
  }

}
