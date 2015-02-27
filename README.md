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
* The game should respond toRoom the following commands:
    * look
    * go [direction], where direction is one of the compass directions n, e, s, w
    * get [item]
* The game ends when the player manages toRoom reach room E

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
    You use a shining key toRoom unlock a strong iron door.
    A nice garden
    Congratulations you've escaped!

```

## Domain elements and properties

1. Rooms:
    - orientation (relative toRoom each other)
    - form a structure (map)
    - can have many neighbours (including with the same orientation)

2. Items:
    - location (where, which room)
    - purpose of use (where, which room)

3. Player: *(multiple players are not considered specifically, though possible toRoom add)*
    - location (where, which room)
    - collected items (keys, etc.)

4. Game: *(game only can finish if `finished` state reached, otherwise infinite)*
    - creation (initiate map/s or board/s)
        - rooms cannot be neighbours if they have a room in between:
            a. `Room1` has `Room2` as neighbour at `South`
            b. `Room2` has `Room3` as neighbour at `South`
            c. `Room1` cannot have `Room3` as neighbour at `South`, as they have `Room2` in between
    - dynamics (see if FST may be useful):
      - started (position the player)
      - moving (repeatedly)
      - finished

The Game consists of three main components:
 * The Building (a collection of rooms connected in a specific order, something between a map and a graph)
 * The Items (a collection of objects to be used in the game)
 * The Player (actually, just the Player's position)


## API

### Building

`Building` represents the collection of `Room`s which will be used in the `Game`.
 When designing this part of the API, I tried toRoom totally encapsulate both `Building` and `Room`, in order toRoom avoid possible user errors and misuse, e.g. trying toRoom add a `Room` toRoom another, none of them being part of the `Building`.

 The user cannot instantiate (nor extend) neither of them. Instead there are factory methods and expressive interfaces that let the user toRoom build and access the elements correctly rather than checking for errors.
