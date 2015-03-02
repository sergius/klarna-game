package adventure.game


object GameEngine {
  import adventure.game.Game._

  val GameOver = "Congratulations, you've escaped!"

  def moveImpossibleMsg(direction: Direction) = s"You cannot move to $direction"
  def itemNotFoundMsg(name: String) = s"The $name wasn't found"
  def itemNotMobileMsg(name: String) = s"Is impossible to pick up a $name"
  def itemPossiblyCollectedMsg(name: String) = s"The $name wasn't found, perhaps you've already collected it."
  def itemsNotPickedUpMsg(items: Seq[String]) = s"You couldn't pick up: ${items.mkString(", ")}"
  def itemNotHolderMsg(name: String) = s"Is impossible to open a $name"
  def itemsNotOpenMsg(items: Seq[String]) = s"You couldn't open: ${items.mkString(", ")}"

  sealed trait Event
  case class StartWith(building: Building,
                       initPos: Room, gameOverPos: Room) extends Event
  case class Move(direction: Direction) extends Event
  case class PickUp(itemName: String) extends Event
  case class OpenHolder(itemName: String) extends Event
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
            (Playing, gameOption, List(moveImpossibleMsg(direction)))
          case MoveFailure(problems) =>
            (Playing, gameOption, problems)
          case MoveAck(messages) =>
            (Playing, gameOption, messages)
          case MoveGameOver(messages) =>
            (Stopped, gameOption, messages :+ GameOver)
        }

      case LookAround =>
        (Playing, gameOption, gameOption.get.currentView)

      case PickUp(itemName: String) =>
        gameOption.get.pickUpItem(itemName) match {
          case i:ItemNotFound =>
            (Playing, gameOption, List(itemNotFoundMsg(itemName)))
          case ItemNotMobile =>
            (Playing, gameOption, List(itemNotMobileMsg(itemName)))
          case ItemPossiblyCollected =>
            (Playing, gameOption, List(itemPossiblyCollectedMsg(itemName)))
          case PickUpFailure(problems) =>
            (Playing, gameOption, List(itemsNotPickedUpMsg(problems)))
          case PickUpAck(messages) =>
            (Playing, gameOption, messages)
        }

      case OpenHolder(itemName: String) =>
        gameOption.get.openItem(itemName) match {
          case i: ItemNotFound =>
            (Playing, gameOption, List(itemNotFoundMsg(itemName)))
          case ItemNotHolder =>
            (Playing, gameOption, List(itemNotHolderMsg(itemName)))
          case OpenFailure(problems) =>
            (Playing, gameOption, List(itemsNotOpenMsg(problems)))
          case OpenAck(messages) =>
            (Playing, gameOption, messages)
        }

      case _ => //ignore anything else 
        (Playing, gameOption, Nil)
    }
  }

  case object Stopped extends State {
    override def transition(event: Event, game: Option[Game]): (State, Option[Game], Seq[String]) = event match  {
      case StartWith(building, initPos, gameOverPos) =>
        val newGame = Game(building, initPos, gameOverPos)
        (Playing, Some(newGame), newGame.currentView)
      case _ =>
        (Stopped, None, Nil)
    }
  }
}

trait GameEngine {
  import GameEngine._

  private var state: State = Stopped
  private var data: Option[Game] = None
  private var messages = Seq.empty[String]

  protected def currentState = state
  protected def currentData = data
  protected def feedback = messages

  protected def applyEvent(event: Event) = {

    val (newState, newData, feedback) = state.transition(event, data)

    state = newState
    data = newData
    messages = feedback
  }
}
