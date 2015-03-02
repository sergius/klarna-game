package adventure

import org.scalatest.{Matchers, WordSpecLike}

class playing extends WordSpecLike with Matchers {

  import adventure.game.Building.{Neighbour, RoomInfo}
  import adventure.game.Direction.{East, North, South, West}
  import adventure.game.{Building, Room}

  case class Node(x: Int, y: Int, room: Room)

  def loop(rooms: Map[Int, Room], nodes: List[Node]): List[Node] = {

    def addNodeByDirection(neighbour: Neighbour, rooms: Map[Int, Room], nodes: List[Node]):
    (Map[Int, Room], List[Node]) = {

      def insertNode(x: Int, y: Int, room: Room, nodes: List[Node]): (Map[Int, Room], List[Node]) =
        (rooms - room.id, Node(x, y, room) :: nodes)

      val currNode = nodes.head

      (neighbour.direction, rooms.get(neighbour.roomId)) match {
        case (North, Some(room)) if currNode.y == 0 =>
          insertNode(currNode.x, 0, room, nodes.map(n => n.copy(y = n.y + 1)))
        case (North, Some(room)) =>
          insertNode(currNode.x, currNode.y - 1, room, nodes)
        case (West, Some(room)) if currNode.x == 0 =>
          insertNode(0, currNode.y, room, nodes.map(n => n.copy(x = n.x + 1)))
        case (West, Some(room)) =>
          insertNode(currNode.x - 1, currNode.y, room, nodes)
        case (South, Some(room)) =>
          insertNode(currNode.x, currNode.y + 1, room, nodes)
        case (East, Some(room)) =>
          insertNode(currNode.x + 1, currNode.y, room, nodes)
        case _ =>
          (rooms, nodes)
      }
    }


    def findNextIn(nodes: List[Node]): List[Neighbour] = {

      def unexploredNeighboursOf(node: Node): List[Neighbour] =
        node.room.neighbours.toList.filter(n => rooms.get(n.roomId).nonEmpty)

      nodes match {
        case Nil => Nil
        case n :: rest =>
          if (unexploredNeighboursOf(n).nonEmpty)
            unexploredNeighboursOf(n)
          else
            findNextIn(rest)
      }
    }

    if (rooms.isEmpty) {
      nodes
    } else {
      val next = findNextIn(nodes)
      if (next.nonEmpty) {
        val (rs, ns) = addNodeByDirection(next.head, rooms, nodes)
        loop(rs, ns)
      } else {
        // probably the graph is disconnected
        nodes
      }
    }
  }

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


  "When applying loop it" must {
    "return the right board" in {
      val expected =
        List(
          Node(0, 0, building.plan(room5.id)),
          Node(0, 1, building.plan(room1.id)),
          Node(1, 1, building.plan(room2.id)),
          Node(1, 2, building.plan(room3.id)),
          Node(0, 2, building.plan(room4.id))
        )

      val first = building.plan.head
      val node: Node = Node(0, 0, first._2)
      val result =
        loop(building.plan - first._1, List(node))

      result foreach (r => println(s"Node: ${r.room.description} - (${r.x},${r.y})"))

      expected should contain theSameElementsAs result
    }
  }

}
