package adventure

import adventure.game.Direction._
import adventure.game._
import adventure.game.GameEngine._

import scala.io.StdIn

object Conf {
  import adventure.game.Building.RoomInfo
  import adventure.game.{Building, Item}
  import adventure.game.Game.{PickUpAction, MoveAction}

  object key extends MobileItem {
    override val matchItem: Option[Item] = None
    override val matchAction: GameAction = PickUpAction
    override val name: String = "shining key"
    override val description: String = s"A $name"
    override val onMatchMsg: String = s"You pick up a $name."
  }

  object ironDoor extends FixedItem {
    override val matchItem: Option[Item] = Some(key)
    override val matchAction: GameAction = MoveAction
    override val name: String = "iron door"
    override val description: String = s"A strong $name is blocking the way in that direction"
    override val onMatchMsg: String = s"You use a ${matchItem.get.name} to unlock a strong iron door."
  }

  val room1 = RoomInfo(1, "cold room")
  val room2 = RoomInfo(2, "dusky room")
  val room3 = RoomInfo(3, "hot room")
  val room4 = RoomInfo(4, "bright room")
  val room5 = RoomInfo(5, "nice garden")

  val building = Building(room1)
    .addRoom(room2, room1.id, East)
    .addRoom(room3, room2.id, South)
    .addRoom(room4, room3.id, West)
    .addRoom(room5, room1.id, North)

  building.plan(room5.id).addItem(ironDoor)
  building.plan(room3.id).addItem(key)

  val items = Map(key.name -> key, ironDoor.name -> ironDoor)

  val playerInitPos = building.plan(room1.id)

  val gameOverPos = building.plan(room5.id)
}



object GameApp extends App with GameEngine with Terminal {
  import Conf._

  override val commandParser: CommandParser.Parser[Command] =
    CommandParser.exit | CommandParser.look |
      CommandParser.go | CommandParser.get

  println("=== Starting Text Adventure game ... ===")
  applyEvent(StartWith(building, items, playerInitPos, gameOverPos))

  println("=== Game started ===")
  //TODO Print gameOption map (building)
  //TODO Print the expected commands
  commandLoop()

  private def commandLoop(): Unit = {
    import Command._

    def applyRespondContinue(event: Event): Unit = {
      def printFeedback(): Unit =
        feedback foreach(message => println(s"*** $message"))

      applyEvent(event)
      printFeedback()
      if (currentState != Stopped) commandLoop()
      else println("=== GameOver ===")
    }

    Command(StdIn.readLine()) match {
      case Quit =>
        println("=== Quiting Text Adventure game ... ===")
      case Look =>
        applyRespondContinue(LookAround)
      case Go(dir) =>
        dir match {
          case "n" => applyRespondContinue(Move(North))
          case "s" => applyRespondContinue(Move(South))
          case "e" => applyRespondContinue(Move(East))
          case "w" => applyRespondContinue(Move(West))
        }
      case Get(name) =>
        applyRespondContinue(PickUp(name))
    }
  }

}
