package adventure

import adventure.game.Game.{OpenAction, MoveAction, PickUpAction}
import adventure.game._

object Items {

  object key extends MobileItem {
    override val matchItem: Option[Item] = None
    override val matchAction: GameAction = PickUpAction
    override val name: String = "shining key"
    override val description: String = s"A $name"
    override val onMatchMsg: String = s"You pick up a $name."
  }

  object keyDoor extends FixedItem {
    override val matchItem: Option[Item] = Some(key)
    override val matchAction: GameAction = MoveAction
    override val name: String = "iron door"
    override val description: String = s"A strong $name is blocking the way in that direction"
    override val onMatchMsg: String = s"You use a ${matchItem.get.name} to unlock a strong iron door."
  }

  object book extends MobileItem with ItemHolder {
    override val matchItem: Option[Item] = None
    override val matchAction: GameAction = OpenAction
    override val name: String = "old book"
    override val description: String = s"An $name on the table"
    override val onMatchMsg: String = s"You open the $name (Hint: Look around)"
  }

  object note extends MobileItem {
    override val matchItem: Option[Item] = None
    override val matchAction: GameAction = PickUpAction
    override val name: String = "note with numbers"
    override val description: String = s"A $name that fell from the ${book.name}"
    override val onMatchMsg: String = s"You pick up the $name"
  }

  object vault extends FixedItem with ItemHolder {
    override val matchItem: Option[Item] = Some(note)
    override val matchAction: GameAction = OpenAction
    override val name: String = "vault with numbered dial"
    override val description: String = s"A $name"
    override val onMatchMsg: String = s"You use the ${matchItem.get.name} to open the $name (Hint: Look around)"
  }

  object laser extends MobileItem {
    override val matchItem: Option[Item] = None
    override val matchAction: GameAction = PickUpAction
    override val name: String = "laser gun"
    override val description: String = s"A $name"
    override val onMatchMsg: String = s"In the open ${vault.name} you pick up a $name"
  }

  object laserDoor extends FixedItem {
    override val matchItem: Option[Item] = Some(laser)
    override val matchAction: GameAction = MoveAction
    override val name: String = "iron door"
    override val description: String = s"A strong $name is blocking the way in that direction"
    override val onMatchMsg: String = s"You use a ${matchItem.get.name} to melt down a strong iron door."
  }
}
