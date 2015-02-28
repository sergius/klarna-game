package adventure.game


trait ItemMatcher {
  this: Item =>

  val matchItem: Option[Item]
  val matchAction: GameAction
}

trait ItemInfo {
  this: Item =>

  val name: String
  val description: String
  val onMatchMsg: String
}

trait Item extends ItemInfo with ItemMatcher {
  val isMobile: Boolean
}

trait MobileItem extends Item {
  override val isMobile = true
}

trait FixedItem extends Item {
  override val isMobile = false
}

sealed trait ItemMatch
case class ItemMatchAck(messages: Seq[String]) extends ItemMatch
case class ItemMatchFailure(messages: Seq[String]) extends ItemMatch

trait ItemHolder {

  protected var itemsHeld = Map.empty[String, Item]

  def items: Map[String, Item] = itemsHeld

  def addItem(item: Item) =
    itemsHeld += item.name -> item

  def hasItem(name: String): Boolean = items.get(name).nonEmpty

  def matchItemsWith(other: ItemHolder, action: GameAction): ItemMatch = {
    val ownEventItems = items.values.filter(item => item.matchAction == action).toSeq
    val otherEventItems = other.items.values.filter(item => item.matchAction == action).toSeq
    val matches = otherEventItems.foldLeft((Seq.empty[String], Seq.empty[String])) {(acc, item) =>
      if (item.matchItem.isEmpty || item.matchItem.map(i => ownEventItems.contains(i)).get)
        (acc._1 :+ item.onMatchMsg, acc._2)
      else
        (acc._1, acc._2 :+ item.description)
    }
    if (matches._2.isEmpty) ItemMatchAck(matches._1)
    else ItemMatchFailure(matches._2)
  }
}
