package adventure

import adventure.Items.{key, keyDoor}
import adventure.game.Building
import adventure.game.Building.{RoomInfo, Neighbour}
import adventure.game.Direction._
import adventure.game.GameEngine._
import adventure.ui.Ui

object Conf {

  val room1 = RoomInfo(1, "A cold room")
  val room2 = RoomInfo(2, "A dusky room")
  val room3 = RoomInfo(3, "A hot room")
  val room4 = RoomInfo(4, "A bright room")
  val room5 = RoomInfo(5, "A nice garden")

  val building = Building(room1)
    .addRoom(room2, room1.id, East)
    .addRoom(room3, room2.id, South)
    .addRoom(room4, room3.id, West)
    .addRoom(room5, room1.id, North)
    .addNeighbour(room1.id, Neighbour(room4.id, South))

  building.plan(room5.id).addItems(List(keyDoor))
  building.plan(room3.id).addItems(List(key))

  val playerInitPos = building.plan(room1.id)

  val gameOverPos = building.plan(room5.id)
}


object GameApp extends App with Ui {
  import Conf._

  override def startWith() = applyEvent(StartWith(building, playerInitPos, gameOverPos))
}
