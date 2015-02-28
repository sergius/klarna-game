package adventure

import adventure.game.Building.Neighbour
import adventure.game.Direction._
import adventure.game.GameEngine._
import adventure.game._

import scala.io.StdIn

object Conf {
  import adventure.game.Building.RoomInfo
  import adventure.game.Game.{MoveAction, PickUpAction}
  import adventure.game.{Building, Item}

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

  val room1 = RoomInfo(1, "A cold room")
  val room2 = RoomInfo(2, "A dusky room")
  val room3 = RoomInfo(3, "A hot room")
  val room4 = RoomInfo(4, "A bright room")
  val room5 = RoomInfo(5, "A nice garden")

  val building = Building(room1)
    .addRoom(room2, room1.id, East)
    .addRoom(room3, room2.id, South)
    .addRoom(room4, room3.id, West)
    .addRoom(room5, room1.id, North)
    .addNeighbour(room1.id, Neighbour(room4.id, South))

  building.plan(room5.id).addItems(List(ironDoor))
  building.plan(room3.id).addItems(List(key))

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

  //TODO Print gameOption map (building)
  printHelp()
  printFeedback()
  commandLoop()

  def printHelp(): Unit = {
    println("=== Please use the following commands: ===\n\n" +
      "1. 'look' - to see what's around\n" +
      "2. 'go n' - to go North (s = South, e = East, w = West\n" +
      "3. 'get key' - to pick up a key (or any other item)\n" +
      "4. 'exit' - to quit playing\n\n"
    )
  }

  private def printFeedback(): Unit =
    feedback foreach(message => {
      println()
      println(s"$message")
      println()
    })

  private def commandLoop(): Unit = {
    import Command._

    def applyRespondContinue(event: Event): Unit = {
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
      case Unknown(s, _) =>
        println(s"*** Unknown command: $s.")
        printHelp()
        commandLoop()
    }
  }

}
