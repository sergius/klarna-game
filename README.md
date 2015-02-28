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

#### Sample game log

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

## Domain elements, properties and responsibilities

Defining the concepts and delimiting the responsibilities is probably the most challenging of this exercise, as there's more than one (valid) way to do it. Deciding on these matters, my priorities where:

   * Keep it as simple as possible (but not simpler than that :) )
   * Make it as generic as possible, i.e. easy to reuse and/or refactor
   * Build the robustness into the design
        - giving it a clear interface
        - avoiding confusions in use
        - avoiding the use of exceptions in error treatment

I also tried to keep the code as functional as possible, pushing the mutable parts to specific thin structures.

#### Overthinking?

Perhaps, reviewing the code you will consider some choices being result of overthinking, but it has an explanation.

As I mentioned, I tried to keep the solution as simple as possible but, at the same time, it had to make it easy create and play (with the same components and rules) games with `Building`s of different shapes and sizes and with different amounts of varied `Item`s actioned in multiple situations. Thus, I tried to code the *simplest generic* implementation.


### Building: game's map

The `Building` is the name that I chose for what is the map of the game. It can also be defined as the 'board' of the game or the collection of the 'rooms', as separate units of game's territory. This last similarity - collection of rooms - inspired the name of 'Building' for this element of domain.

Its main properties and responsibilities are the following:

   * A building can be created with a minimum of one room
   * The rooms are not created outside the building. A new room should be requested to the building, indicating where to add it (next to which existing room and to which of its sides).
   * The building is immutable: once you make a change, that's a new building. Therefore, the building should be built before starting the game, it doesn't change during the game
   * The building has a plan: the collection of the created rooms. (I avoided the name "map" on purpose, as it could get mixed up with the usual `map` function. Besides, it's not exactly a map, it's rather something between a map and a graph.)
   * Each room has an `id`, a `description` (these two sometimes grouped as `RoomInfo` for convenience) and a set of `Neighbour`s. A `Neighbour` is a pair of room's `id` and the `Direction` (North, South, East or West).
   * Each room is also an `ItemHolder` (described further).
   * The building knows only about itself and knows nothing about the `Game`, `Player` or anything else.

As you can deduce it, the `Building` is not only an object it's also a factory (in Scala there's no need to give specific name "factory", using the companion object with the same name as the class for factory purposes).



### Items & ItemHolders

#### Items

`Item`s are special objects that can interact with the player and among themselves. This interaction should only occur in *specific conditions* and in response to specific `Event`s and `Action`s. This is probably the part of design which most variations could have.

I chose the following definitions:

 * `Item`s have `name`s and `description`s (Actually, in this version the `name` is used as key in `ItemHolder`s, therefore the same `name` for different `Item`s should be avoided. For a bigger application this property could be conveniently and easily refactored.)
 * `Item`s may have (or have not) another `Item` to interact with and these are called `matchItem`s: e.g. in our game the *door* interacts with the *key*, the *key* interacts with nothing.
 * `Item`s must always have an `Action` to be associated with: `matchAction`. This means that an `Item` can interact with another `Item` or `ItemHolder` only when a certain `Action` in the `Game` is triggered. For a player this would mean that a door would open __only__ when the player tries to go through it and the key becomes player's possession __only__ when the player picks it up.
 * `Item`s can be `FixedItem` (like a door) or `MobileItem` (like a key). A `FixedItem` cannot be collected, as opposed to `MobileItem`.

#### ItemHolders

`ItemHolder` is a trait which can be mixed in to any component of the game.

 * An `ItemHolder` may hold zero or many `Item`s
 * `Item`s can be added to an `ItemHolder` and removed from it
 * `ItemHolder` knows how to *match* `Item`s with another `ItemHolder`.
 * To *match* `Item`s means that in specific conditions of the game (`Event`s and `Action`s) and holding specific `Item`s, an `ItemHolder` can acquire `Item`s from another `ItemHolder` or make `Item`s interact.
 * Though not used in the sample game, an `ItemHolder` can contain another `ItemHolder` as `Item`, e.g. a `Room` might contain a *vault* (`Item` and `ItemHolder` at the same time), and the player could *match* the `Room`s door with one key and the *vault's* door with another key.


### Player

`Player` is actually just a small container of changing details during the game.

 * `Player` knows its `position`, which is a `Room`
 * `Player` can change `position`
 * A `Player` is also an `ItemHolder`


### Game logic: GameEngine and Game


## What it doesn't

  * No termination verification

# How to run it