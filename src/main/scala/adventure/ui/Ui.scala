package adventure.ui

import adventure.game.Direction.{East, North, South, West}
import adventure.game.GameEngine
import adventure.game.GameEngine._

import scala.io.StdIn

trait Ui extends GameEngine with Terminal {
  this: App =>

  override val commandParser: CommandParser.Parser[Command] =
    CommandParser.exit | CommandParser.look |
      CommandParser.go | CommandParser.get | CommandParser.open

  def startWith(): Unit

  println("=== Starting Text Adventure game ... ===")
  startWith()
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
      case Open(name) =>
        applyRespondContinue(OpenHolder(name))
      case Unknown(s, _) =>
        println(s"*** Unknown command: $s.")
        printHelp()
        commandLoop()
    }
  }

}