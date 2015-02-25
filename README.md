# klarna-game

* The game world consists of 5 rooms with the following paths between them:
```

        E

        |

        A­B

        | |

        D­C
```

* The player starts in room A
* The path between A and E is blocked by a locked door
* In room C lies the key the enables the player to pass the locked door
* The game should respond to the following commands:
    * look
    * go [direction], where direction is one of the compass directions n, e, s, w
    * get [item]
* The game ends when the player manages to reach room E

## Sample game log

```

    A cold room

    >> go n
    a strong iron door is blocking the way in that direction.

    >> go e
    A dusky room

    >> go s
    A hot room
    I can also see the following:
    a shining key

    >> get key
    You pick up a shining key.

    >> go w
    A bright room

    >> go n
    A cold room

    >> go n
    You use a shining key to unlock a strong iron door.
    A nice garden
    Congratulations you've escaped!

```

## Domain elements and properties

1. Rooms:
    - orientation (relative to each other)
    - form a structure (building; makes it generic and gives the possibility to hypothetically write a game with several buildings)

2. Items:
    - location (where, which room)
    - purpose of use (where, which room)

3. Player: *(multiple players are not considered specifically, though possible to add)*
    - location (where, which room)
    - collected items (keys, etc.)

4. Game: *(game only can finish if `finished` state reached, otherwise infinite)*
    - creation (initiate building/s or board/s)
    - dynamics (see if FST may be useful):
      - started (position the player)
      - moving (repeatedly)
      - finished