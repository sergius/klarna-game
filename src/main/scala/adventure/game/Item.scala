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

  def items: Seq[Item] = itemsHeld.values.toSeq

  def addItems(items: Seq[Item]) =
    items foreach { item =>
      itemsHeld += item.name -> item  
    }

  def removeItems(items: Seq[Item]) =
    items foreach { item =>
      itemsHeld -= item.name
    }
  
  def itemByName(name: String): Option[Item] =
    itemsHeld.find{case (key, _) => key.contains(name)}.map(el => el._2)
  
  def hasItem(name: String): Boolean =
    itemByName(name).nonEmpty

  /**
   * Matches `Item`s held by this `ItemHolder` with the `Item`s
   * of *other* `ItemHolder` in the context of a specific `GameAction`.
   * This function can be applied to a reduced collection of specific
   * `Item`s (of *other*) or, if `specificItems` empty, this will apply
   * to all *other's* `Item`s
   * held.
   * @param other Other `ItemHolder` to compare the `Item`s with
   * @param action The `GameAction` for which the `Item`s will be matched
   * @param specificItems A `Seq` of specific `Item`s (contained by *other*)
   *                      to be matched
   * @return An instance of `ItemMatch`, with the *matched* or *unmatched* `Item`s.
   */
  def matchItemsTo(other: ItemHolder, action: GameAction,
                   specificItems: Seq[Item] = Nil): ItemMatch = {

    def filterOtherItems: Seq[Item] = specificItems match {
      case Nil =>
        other.items.filter(item => item.matchAction == action)
      case _ =>
        other.items.filter(item =>
          specificItems.contains(item) && item.matchAction == action)
    }
    
    val otherItems = filterOtherItems
    
    val unmatched = otherItems.foldLeft(Seq.empty[Item]) {(acc, item) =>
      if (item.matchItem.nonEmpty &&
          !item.matchItem.map(i => itemsHeld.values.toSeq.contains(i)).get)
        acc :+ item
      else
        acc
    }
    if (unmatched.isEmpty) ItemMatchAck(otherItems)
    else ItemMatchFailure(unmatched)
  }
}
