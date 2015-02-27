package adventure.game


/*  StateName(Event, StateData) ->
  .. code for actions here ...
  {next_state, StateName', StateData'}*/

object GameEngine {

  sealed trait Event
  case class Start(game: Game) extends Event
  case class Move(room: Room) extends Event
  case object Quit extends Event

  sealed trait State {
    def transition(event: Event, game: Option[Game]): (State, Option[Game])
  }

  /**
   *  Special object for the case that an event is received which doesn't produce
   *  transition from current state
   */
  case object Ignoring extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game]) = {
      (Ignoring, None)
    }
  }

  case object Playing extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game]) = event match {
      case Move(room) => (Playing, None)
      //TODO send to game the new location and get a new state
      //TODO Apply look()
      //TODO if not finished => (Playing, game new state)
      //TODO else (Finished, game new state)

      case Quit =>
        (Stopped, None)

      case _ => (Ignoring, None)
    }
  }

  case object Finished extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game]) = {
      //TODO Finish the game
    (Stopped, None)
    }
  }

  case object Stopped extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game]) = event match  {
      case Start(newGame) =>
        (Playing, Some(newGame))
      case _ => (Ignoring, None)
    }
  }
}

trait GameEngine {
  import GameEngine._

  private var state: State = Stopped
  private var data: Option[Game] = None

  def applyEvent(event: Event) = {

    val (newState, newData) = state.transition(event, data)

    newState match {
      case Ignoring => // do nothing if the event not expected for state

      case s: State =>
        state = newState
        data = newData
    }
  }
}
