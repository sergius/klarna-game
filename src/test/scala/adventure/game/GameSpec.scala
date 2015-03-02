package adventure.game

import adventure.game.Building.RoomInfo
import adventure.game.Direction.North
import adventure.game.Game._
import adventure.helpers.{mobileMatchMobile1, mobile1, fixedMatchMobile1}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpecLike}

class GameSpec extends WordSpecLike with Matchers with BeforeAndAfter {
  val roomInit = RoomInfo(1, "test room 1")
  val roomInBetween = RoomInfo(2, "test room 2")
  val roomGameOver = RoomInfo(3, "test room 3")
  val direction = North

  val building =  Building(roomInit).addRoom(roomInBetween, roomInit.id, direction).addRoom(roomGameOver, roomInBetween.id, direction)

  after {
    building.plan foreach{ case (_, room) =>
      room.removeItems(room.items)
    }
  }

  "When trying to move the Player, Game" must {
    "if Player's position doesn't have neighbours in the requested direction: return MoveImpossible" in {
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))
      val wrongDirection = Direction.oppositeTo(direction)

      val moveResult = game.movePlayer(wrongDirection)

      moveResult shouldBe MoveImpossible
    }

    "if the Player doesn't hold the needed Item(s) to move: return MoveFailure(itemsThatNeedMatch)" in {
      building.plan(roomInBetween.id).addItems(Seq(fixedMatchMobile1))

      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      val moveResult = game.movePlayer(direction)

      moveResult.getClass shouldBe classOf[MoveFailure]
    }

    "if required move is valid and the Player holds the needed Item(s): return MoveAck(feedback)" in {
      building.plan(roomInBetween.id).addItems(Seq(fixedMatchMobile1))
      building.plan(roomInit.id).addItems(Seq(mobile1))

      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))
      game.pickUpItem(mobile1.name)

      val moveResult = game.movePlayer(direction)

      moveResult.getClass shouldBe classOf[MoveAck]
    }

    "if the move results in reaching the GameOver position: return MoveGameOver(feedback)" in {
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      game.movePlayer(direction) // moves to intermediary position
      val moveResult = game.movePlayer(direction)

      moveResult.getClass shouldBe classOf[MoveGameOver]
    }
  }

  "When trying to perform an Item pick-up, Game" must {
    "if the Item is not found (in the ItemHolder, e.g. room) and the Player holds one with similar name: " +
      "return ItemPossiblyCollected" in {
      building.plan(roomInit.id).addItems(Seq(mobile1))
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      game.pickUpItem(mobile1.name) // successful pick-up
      val pickUpResult = game.pickUpItem(mobile1.name)

      pickUpResult shouldBe ItemPossiblyCollected
    }

    "if the Item is not found and the Player holds none with similar name: return ItemNotFound" in {
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      val pickUpResult = game.pickUpItem(mobile1.name)

      pickUpResult shouldBe ItemNotFound()
    }

    "if the Item is not mobile: return ItemNotMobile" in {
      building.plan(roomInit.id).addItems(Seq(fixedMatchMobile1))
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      val pickUpResult = game.pickUpItem(fixedMatchMobile1.name)

      pickUpResult shouldBe ItemNotMobile
    }

    "if the Item needs a match which the Player DOESN'T hold: " +
      "return PickUpFailure(itemsThatNeedMatchDescription)" in {
      building.plan(roomInit.id).addItems(Seq(mobileMatchMobile1))
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      val pickUpResult = game.pickUpItem(mobileMatchMobile1.name)

      pickUpResult shouldBe PickUpFailure(Seq(mobileMatchMobile1.description))
    }

    "if the Item doesn't need a match or needs a match which the Player DOES hold: " +
      "return PickUpAck(onMatchMessage)" in {
      building.plan(roomInit.id).addItems(Seq(mobile1))
      building.plan(roomInBetween.id).addItems(Seq(mobileMatchMobile1))
      val game = Game(building, building.plan(roomInit.id), building.plan(roomGameOver.id))

      val noMatchNeededResult = game.pickUpItem(mobile1.name)

      noMatchNeededResult shouldBe PickUpAck(Seq(mobile1.onMatchMsg))

      game.movePlayer(direction) // going to where sits the Item needing match

      val matchNeededResult = game.pickUpItem(mobileMatchMobile1.name)

      matchNeededResult shouldBe PickUpAck(Seq(mobileMatchMobile1.onMatchMsg))
    }
  }

}

