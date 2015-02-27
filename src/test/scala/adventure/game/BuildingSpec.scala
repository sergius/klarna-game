package adventure.game

import adventure.game.Building.Neighbour
import adventure.game.Direction.North
import org.scalatest.{Matchers, WordSpecLike}

class BuildingSpec extends WordSpecLike with Matchers {

  "When the Building is created it" must {
    "have an initial one (and only one) room" in {
      val initRoomId = 12
      val building = Building(initRoomId)

      building.plan should have size 1
      building.plan.head._1 should equal(initRoomId)
    }
  }

  "The Building is immutable, therefore calling addRoom() on it" must {

    "create a new Building with the new room in its plan" in {
      val initRoomId = 12
      val building = Building(initRoomId)

      building.plan should have size 1

      val newRoomId = 42
      val updatedBuilding = building.addRoom(initRoomId, North)(newRoomId)

      updatedBuilding.plan should have size 2
      updatedBuilding.plan.get(initRoomId) shouldNot be(None)
      updatedBuilding.plan.get(newRoomId) shouldNot be(None)
    }

    "make neighbours to each other the new room and the existing one" in {
      val room1Id = 1
      val room2Id = 42
      val testDir = North
      val building = Building(room1Id).addRoom(room1Id, testDir)(room2Id)

      val neighboursOfRoom1: Set[(Room, Direction)] = building.plan(room1Id).neighbours
      neighboursOfRoom1 should have size 1
      val neighbour1: Neighbour = neighboursOfRoom1.head
      neighbour1._1.id should equal(room2Id)
      neighbour1._2 should equal(testDir)

      val neighboursOfRoom2: Set[(Room, Direction)] = building.plan(room2Id).neighbours
      neighboursOfRoom2 should have size 1
      val neighbour2: Neighbour = neighboursOfRoom2.head
      neighbour2._1.id should equal(room1Id)
      neighbour2._2 should equal(Direction.oppositeTo(testDir))
    }

    "return the same Building if the id of the existing room is not found in the Building plan" in {
      val room1Id = 1
      val room2Id = 42
      val wrongId = 0
      val testDir = North
      val initialBuilding = Building(room1Id)
      val updatedBuilding = initialBuilding.addRoom(wrongId, testDir)(room2Id)

      updatedBuilding should equal(initialBuilding)
    }

  }

}
