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
case class ItemMatchAck(matched: Seq[Item]) extends ItemMatch
case class ItemMatchFailure(unmatched: Seq[Item]) extends ItemMatch

trait ItemHolder {

  protected var itemsHeld = Map.empty[String, Item]

  def items: Map[String, Item] = itemsHeld

  def addItems(items: Seq[Item]) =
    items foreach { item =>
      itemsHeld += item.name -> item  
    }

  def removeItems(items: Seq[Item]) =
    items foreach { item =>
      itemsHeld -= item.name
    }
  
  def itemByName(name: String): Option[Item] =
    items.find{case (key, _) => key.contains(name)}.map(el => el._2)
  
  def hasItem(name: String): Boolean =
    itemByName(name).nonEmpty

  def matchItemsTo(other: ItemHolder, action: GameAction, specificItems: Seq[Item] = Nil): ItemMatch = {

    def filterOtherItems: Seq[Item] = specificItems match {
      case Nil =>
        other.items.values.filter(item => item.matchAction == action).toSeq
      case _ =>
        other.items.values.filter(item => specificItems.contains(item) && item.matchAction == action).toSeq
    }
    
    val otherItems = filterOtherItems
    
    val unmatched = otherItems.foldLeft(Seq.empty[Item]) {(acc, item) =>
      if (item.matchItem.nonEmpty && !item.matchItem.map(i => itemsHeld.values.toSeq.contains(i)).get)
        acc :+ item
      else
        acc
    }
    if (unmatched.isEmpty) ItemMatchAck(otherItems)
    else ItemMatchFailure(unmatched)
  }
}
