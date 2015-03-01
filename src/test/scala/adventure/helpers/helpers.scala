package adventure.helpers

import adventure.game.Game.{PickUpAction, MoveAction}
import adventure.game.{MobileItem, Item, GameAction, FixedItem}

object helperActions {
    val FixedItemAction = MoveAction
    val MobileItemAction = PickUpAction
}

object fixedMatchMobile1 extends FixedItem {
  import helperActions._
  
  override val name: String = "fixedMatchMobile1"
  override val description: String = s"$name that can't be collected"
  override val matchAction: GameAction = FixedItemAction
  override val matchItem: Option[Item] = Some(mobile1)
  override val onMatchMsg: String = s"you activated $name with ${matchItem.get.name}"
}

object mobile1 extends MobileItem {
  import helperActions._
  
  override val name: String = "mobile1"
  override val description: String = s"$name that can be picked up"
  override val onMatchMsg: String = s"you picked up $name"
  override val matchAction: GameAction = MobileItemAction
  //the player don't need to hold anything in order to pick it up
  override val matchItem: Option[Item] = None
}

object mobile2 extends MobileItem {
  import helperActions._
  
  override val name: String = "mobile2"
  override val description: String = s"$name that can be picked up"
  override val onMatchMsg: String = s"you picked up $name"
  override val matchAction: GameAction = MobileItemAction
  //the player don't need to hold anything in order to pick it up
  override val matchItem: Option[Item] = None
}

object mobileMatchMobile1 extends MobileItem {
  import helperActions._
  
  override val name: String = "mobileMatchMobile1"
  override val description: String = s"$name that can be picked up"
  override val matchAction: GameAction = MobileItemAction
  override val matchItem: Option[Item] = Some(mobile1)
  override val onMatchMsg: String = s"you used ${matchItem.get.name} and picked up $name"
}