package adventure.game

import adventure.helpers._
import org.scalatest._

object testHolder1 extends ItemHolder

object testHolder2 extends ItemHolder

class ItemHolderSpec extends WordSpecLike with Matchers with BeforeAndAfter {

  after {
    testHolder1.removeItems(testHolder1.items)
    testHolder2.removeItems(testHolder2.items)
  }

  "When adding Items to an ItemHolder, it" must {
    "keep in its held items" in {
      testHolder1.items shouldBe empty

      testHolder1.addItems(Seq(fixedMatchMobile1, mobile1))

      testHolder1.items should have size 2

      testHolder1.items should contain only(fixedMatchMobile1, mobile1)
    }
  }

  "When adding Items to an ItemHolder, it" must {
    "loose them from its held items" in {
      testHolder1.addItems(Seq(fixedMatchMobile1, mobile1))
      testHolder1.items should have size 2
      testHolder1.items should contain only(fixedMatchMobile1, mobile1)

      testHolder1.removeItems(Seq(mobile1))

      testHolder1.items should contain only fixedMatchMobile1
    }
  }

  "When matching Items of ItemHolder1 to ItemHolder2, it" must {

    "when unmatched Items: return ItemMatchFailure with unmatched Items from ItemHolder2" in {
      testHolder1.addItems(Seq(fixedMatchMobile1)) // needs mobile1

      val matchResult =
        testHolder2.matchItemsTo(testHolder1, helperActions.FixedItemAction)

      matchResult shouldBe ItemMatchFailure(Seq(fixedMatchMobile1))
    }

    "when matched Items: return ItemMatchAck(matched)" in {
      testHolder1.addItems(Seq(fixedMatchMobile1)) //needs mobile1
      testHolder2.addItems(Seq(mobile1))

      val matchResult =
        testHolder2.matchItemsTo(testHolder1, helperActions.FixedItemAction)

      matchResult shouldBe ItemMatchAck(Seq(fixedMatchMobile1))
    }

    "when NO specific subset of Items is indicated: match all" in {
      testHolder1.addItems(Seq(mobile1, mobile2))

      testHolder1.items should have size 2

      val matchResult =
        testHolder2.matchItemsTo(testHolder1, helperActions.MobileItemAction)

      matchResult shouldBe ItemMatchAck(Seq(mobile1, mobile2))
    }

    "when specific subset of Items IS indicated: match only the specified Items" in {
      testHolder1.addItems(Seq(mobile1, mobile2))

      testHolder1.items should have size 2

      val matchResult =
        testHolder2.matchItemsTo(testHolder1,
          helperActions.MobileItemAction, Seq(mobile1))

      matchResult shouldBe ItemMatchAck(Seq(mobile1))
    }

    "when some Items have as *matchItem* any others: " +
      "if trying to match all, return those unmatched" in {
      testHolder1.addItems(Seq(mobile1, mobile2, mobileMatchMobile1))

      testHolder1.items should have size 3

      val matchResult =
        testHolder2.matchItemsTo(testHolder1,helperActions.MobileItemAction)

      matchResult shouldBe ItemMatchFailure(Seq(mobileMatchMobile1))
    }

    "when some Items have as *matchItem* any others: " +
    "permit collect the needed ones first and then collect the rest" in {
      testHolder1.addItems(Seq(mobile1, mobile2, mobileMatchMobile1))

      testHolder1.items should have size 3

      testHolder2.matchItemsTo(testHolder1,
        helperActions.MobileItemAction, Seq(mobile1, mobile2)) match {

        case ItemMatchAck(matched) =>
          // performing the pick-up action
          testHolder2.addItems(matched)
          testHolder1.removeItems(matched)
        case _ =>
      }

      testHolder1.items should contain only mobileMatchMobile1
      testHolder2.items should contain only(mobile1, mobile2)

      val matchResult =
        testHolder2.matchItemsTo(testHolder1, helperActions.MobileItemAction)

      matchResult shouldBe ItemMatchAck(Seq(mobileMatchMobile1))
    }
  }
}

