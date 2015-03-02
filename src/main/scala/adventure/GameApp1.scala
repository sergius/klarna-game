package adventure

import adventure.Items._
import adventure.game.Building.{Neighbour, RoomInfo}
import adventure.game.Direction.{East, South}
import adventure.game.GameEngine.StartWith
import adventure.game._
import adventure.ui.Ui

object Conf1 {

  val room1 = RoomInfo(1, "A dark room")
  val room2 = RoomInfo(2, "A metal workshop")
  val room3 = RoomInfo(3, "A library")
  val room4 = RoomInfo(4, "An empty armory")
  val room5 = RoomInfo(5, "A round hall")
  val room6 = RoomInfo(6, "A terrace")

  val building = Building(room1)
    .addRoom(room2, room1.id, East)
    .addRoom(room3, room2.id, East)
    .addRoom(room4, room1.id, South)
    .addRoom(room5, room2.id, South)
    .addRoom(room6, room5.id, East)
    .addNeighbour(room4.id, Neighbour(room5.id, East))


  book.addItems(List(note))
  vault.addItems(List(laser))
  building.plan(room2.id).addItems(List(key))
  building.plan(room3.id).addItems(List(book))
  building.plan(room4.id).addItems(List(keyDoor, vault))
  building.plan(room6.id).addItems(List(laserDoor))

  val playerInitPos = building.plan(room1.id)

  val gameOverPos = building.plan(room6.id)
}

object GameApp1 extends App with Ui {
  import Conf1._

  override def startWith() = applyEvent(StartWith(building, playerInitPos, gameOverPos))
}
