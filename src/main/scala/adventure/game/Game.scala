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

  sealed trait TryPickUp
  case object ItemNotFound extends TryPickUp
  case object ItemPossiblyCollected extends TryPickUp
  case object ItemNotMobile extends TryPickUp
  case class PickUpFailure(feedback: Seq[String]) extends TryPickUp
  case class PickUpAck(feedback: Seq[String]) extends TryPickUp

  def apply(building: Building, initPos: Room, gameOverPos: Room) =
    new Game(building, initPos, gameOverPos)
}

class Game(val building: Building, initPos: Room, gameOverPos: Room) {
  import Game._

  private val player = new Player(initPos)

  def currentView: Seq[String] = player.lookAround()

  def movePlayer(direction: Direction): TryMove = {
    val foundNeighbours = player.position.neighbours
    foundNeighbours.find(n => n.direction == direction) match {
      case None => 
        MoveImpossible

      case Some(neighbour) =>
        player.matchItemsTo(building.plan(neighbour.roomId), MoveAction) match {
          case ItemMatchAck(matched) =>
            player.moveTo(building.plan(neighbour.roomId))
            player.position.removeItems(matched)
            val feedback: Seq[String] =
              matched.map(i => i.onMatchMsg) ++ player.lookAround()

            if (player.position == gameOverPos) MoveGameOver(feedback)
            else MoveAck(feedback)

          case ItemMatchFailure(unmatched) =>
            MoveFailure(unmatched.map(i => i.description))
        }
    }
  }

  def pickUpItem(name: String): TryPickUp = {
    player.position.itemByName(name) match {
      case None if player.hasItem(name) =>
        ItemPossiblyCollected
      case None =>
        ItemNotFound
      case Some(item) if !item.isMobile =>
        ItemNotMobile
      case Some(item) =>
        player.matchItemsTo(player.position, PickUpAction, Seq(item)) match {
          case ItemMatchAck(matched) =>
            player.addItems(matched)
            player.position.removeItems(matched)
            PickUpAck(matched.map(i => i.onMatchMsg))
          case ItemMatchFailure(unmatched) =>
            PickUpFailure(unmatched.map(i => i.description))
        }
    }
  }
}


