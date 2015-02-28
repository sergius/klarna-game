package adventure.game

sealed trait GameAction

object Game {

  object MoveAction extends GameAction
  object PickUpAction extends GameAction

  sealed trait TryMove
  case class MoveAck(feedback: Seq[String]) extends TryMove
  case class MoveGameOver(feedback: Seq[String]) extends TryMove
  case class MoveFailure(feedback: Seq[String]) extends TryMove
  case object MoveImpossible extends TryMove

  def apply(building: Building, items: Map[String, Item], player: Player, gameOverPos: Room) =
    new Game(building, items, player, gameOverPos)

  def impossibleMove(direction: Direction): String =
    s"You cannot move to $direction"

  val GameOver = "Congratulations, you've escaped!"
}

class Game(building: Building, items: Map[String, Item], player: Player, gameOverPos: Room) {
  import Game._

  def movePlayer(direction: Direction): TryMove = {
    player.position.neighbours.find(n => n.direction == direction) match {
      case None => 
        MoveImpossible

      case Some(neighbour) =>
        player.matchItemsWith(neighbour.room, MoveAction) match {
          case ItemMatchAck(success) =>
            player.moveTo(neighbour.room)
            if (player.position == gameOverPos)
              MoveGameOver(success ++ player.lookAround() :+ GameOver)
            else
              MoveAck(success ++ player.lookAround)

          case ItemMatchFailure(problems) =>
            MoveFailure(problems)
        }
    }
  }

  def lookUpItems: Map[String, Item] = player.position.items

  def playerPosition: Room = player.position

  def pickUpItem(name: String): String = {
    player.position.items.get(name) match {
      case None if player.hasItem(name) =>
        s"There's no $name in the room. Perhaps you already picked it."

      case None => s"There's no $name in the room."

      case Some(item) => s"You pick up a $name"
    }
  }
}


