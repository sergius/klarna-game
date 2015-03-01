package adventure.game

import adventure.game.Building.RoomInfo
import adventure.game.Direction.North
import adventure.game.GameEngine._
import org.scalatest.{Matchers, WordSpecLike}

class GameEngineSample extends GameEngine {

  def testState = currentState
  def testData = currentData
  def testFeedback = feedback
  def testApplyEvent(event: Event) = applyEvent(event)
}

class GameEngineSpec extends WordSpecLike with Matchers {

  val roomInit = RoomInfo(1, "test room 1")
  val roomGameOver = RoomInfo(2, "test room 2")
  val direction = North
  val building = Building(roomInit).addRoom(roomGameOver, roomInit.id, direction)
  

  "When created, GameEngine" must {
    "be in Stopped state" in {
      val gameEngineSample = new GameEngineSample()

      gameEngineSample.testState shouldBe Stopped

      gameEngineSample.testData shouldBe None
    }
  }
  
  "On StartWith(parameters) in Stopped state, GameEngine" must {
    "initiate a Game with parameters and move to Playing state" in {
      val gameEngineSample = new GameEngineSample()

      gameEngineSample.testApplyEvent(StartWith(building, building.plan(roomInit.id), building.plan(roomGameOver.id)))

      gameEngineSample.testState shouldBe Playing

      gameEngineSample.testData should not equal None
      gameEngineSample.testData.get.building shouldBe building
    }
  }

  "On reaching the GameOverPosition in Playing state, GameEngine" must {
    "move to Stopped state" in {
      val gameEngineSample = new GameEngineSample()

      gameEngineSample.testApplyEvent(StartWith(building, building.plan(roomInit.id), building.plan(roomGameOver.id)))

      gameEngineSample.testState shouldBe Playing

      gameEngineSample.testApplyEvent(Move(direction)) // moving to roomGameOver

      gameEngineSample.testState shouldBe Stopped
      gameEngineSample.testData should not equal None
      gameEngineSample.testData.get.building shouldBe building
      gameEngineSample.testFeedback should contain(GameOver)
    }
  }
}

