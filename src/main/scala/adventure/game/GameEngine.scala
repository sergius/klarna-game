package adventure.game


/*  StateName(Event, StateData) ->
  .. code for actions here ...
  {next_state, StateName', StateData'}*/


sealed trait Event

object GameEngine {
  import adventure.game.Game.{MoveGameOver, MoveAck, MoveFailure, MoveImpossible}

  def InvalidMove(direction: Direction) = s"You cannot move to $direction"

  case class StartWith(building: Building, items: Map[String, Item], initPos: Room, gameOverPos: Room) extends Event
  case class Move(direction: Direction) extends Event
  case class PickUp(itemName: String) extends Event
  case object LookAround extends Event
  case object Quit extends Event

  sealed trait State {
    def transition(event: Event, game: Option[Game]): (State, Option[Game], Seq[String])
  }

  case object Playing extends State {
    override def transition(event: Event, gameOption: Option[Game]): (State, Option[Game], Seq[String]) = event match {
      case Move(direction) =>
        gameOption.get.movePlayer(direction) match {
          case MoveImpossible =>
            (Playing, gameOption, List(InvalidMove(direction)))
          case MoveFailure(problems) =>
            (Playing, gameOption, problems)
          case MoveAck(messages) =>
            (Playing, gameOption, messages)
          case MoveGameOver(messages) =>
            (Stopped, gameOption, messages)
        }


      //TODO send to gameOption the new location and get a new state
      //TODO Apply look()
      //TODO if not finished => (Playing, gameOption new state)
      //TODO else (Finished, gameOption new state)

      case LookAround => (Playing, None, Nil) //TODO Change this
        /*newData match {
          case None => Nil
          case Some(gameOption) =>
            gameOption.playerPosition.description :: gameOption.lookUpItems.toList.map(i => i.description)
        }*/
      //TODO Print the descriptions messages of the room and items

      case PickUp(itemName: String) => (Playing, None, Nil)//TODO Change this
      // TODO Pick up the item if found by name

      case _ => (Playing, gameOption, Nil)
    }
  }

  case object Stopped extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game], Seq[String]) = event match  {
      case StartWith(building, items, initPos, gameOverPos) =>
        (Playing, Some(Game(building, items, new Player(initPos), gameOverPos)), List()) //TODO Add feedback here
      case _ => (Stopped, None, Nil)
    }
  }
}

trait GameEngine {
  import GameEngine._

  private var state: State = Stopped
  private var data: Option[Game] = None
  private var messages = Seq.empty[String]

  protected def currentState = state
  protected def feedback = messages

  protected def applyEvent(event: Event) = {

    val (newState, newData, feedback) = state.transition(event, data)

    state = newState
    data = newData
    messages = feedback
  }
}
