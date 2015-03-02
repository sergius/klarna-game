package adventure.ui

import scala.util.parsing.combinator.RegexParsers

trait Terminal {
  import Command._

  sealed trait Command

  object Command {

    def apply(command: String): Command =
      CommandParser.parseAsCommand(command)

    case class Unknown(command: String, message: String) extends Command
    case object Look extends Command
    case object Quit extends Command
    case class Go(direction: String) extends Command
    case class Get(itemName: String) extends Command
    case class Open(itemName: String) extends Command
  }

  val commandParser: CommandParser.Parser[Command]

  object CommandParser extends RegexParsers {

    def parseAsCommand(s: String): Command = {
      parseAll(commandParser, s) match {
        case Success(command, _) => command
        case NoSuccess(message, _) => Unknown(s, message)
      }
    }

    def exit: Parser[Command] = "exit".r ^^ (_ => Quit)

    def look: Parser[Command] = "look".r ^^ (_ => Look)

    def go: Parser[Command] = ("go".r ~ direction) ^^ {
      case _ ~ dir => Go(dir)
    }

    def get: Parser[Command] = ("get".r ~ name) ^^ {
      case _ ~ itemName => Get(itemName)
    }

    def open: Parser[Command] = ("open".r ~ name) ^^ {
      case _ ~ itemName => Open(itemName)
    }

    def direction: Parser[String] = """(n|s|w|e)""".r ^^ (_.trim)

    def name: Parser[String] = """\w+""".r ^^ (_.trim)

  }
}
