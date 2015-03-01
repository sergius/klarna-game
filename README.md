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

As I mentioned, I tried to keep the solution as simple as possible but, at the same time, it had to make it easy create and play (with the same components and rules) games with `Building`s of different shapes and sizes and with different amounts of varied `Item`s actioned in multiple situations. Thus, I tried to code the *simplest generic* implementation and I believe this code can provide for programming many different games or many different episodes in the same game.

To best understand the reasoning behind each structure, have a look at its test suite.

### Building: game's world (map)

The `Building` is the name that I chose for what is the world of the game. It can also be defined as the map of the game or the collection of the 'rooms', as separate units of game's territory. This last similarity - collection of rooms - inspired the name of 'Building' for this element of domain.

Its main properties and responsibilities are the following:

   * A building can be created with exactly one room
   * The rooms are not created outside the building. A new room should be requested to the building, indicating where to add it (next to which existing room and to which of its sides).
   * The building is immutable: once you make a change, that's a new building. Therefore, the building should be built before starting the game, it doesn't change during the game.
   * If a wrong room `id` (e.g. nonexistent room) is used to modify the `Building`, the intent is ignored and the same `Building` is returned unmodified. *Note: this behaviour could be changed to return an error, if considered more convenient/correct*
   * The building has a plan: the collection of the created rooms. (I avoided the name "map" on purpose, as it could get mixed up with the usual `map` function. Besides, it's not exactly a map, it's rather something between a map and a graph.)
   * Each room has an `id`, a `description` (these two sometimes grouped as `RoomInfo` for convenience) and a set of `Neighbour`s. A `Neighbour` is a tuple consisting of room's `id` and `Direction` (North, South, East or West).
   * Each room is also an `ItemHolder` (described further).
   * The building knows only about itself and knows nothing about the `Game`, `Player` or anything else.

As you can deduce it, the `Building` is not only an object it's also a factory (in Scala there's no need to create a specific factory, using the companion object instead, with the same name as the class, for factory purposes).

### Items & ItemHolders

#### Items

`Item`s are special objects that can interact with the player and among themselves. This interaction should only occur in *specific conditions* and in response to specific `Event`s and `Action`s. This is probably the part of the design which most variations could have.

I chose the following definitions:

 * `Item`s have `name`s and `description`s (Actually, in this version the `name` is used as *id* in `ItemHolder`s, therefore the same `name` for different `Item`s should be avoided. For a bigger application this property could be conveniently and easily refactored.)
 * `Item`s may have (or have not) another `Item` to interact with and these are called `matchItem`s: e.g. in our game the *door* interacts with the *key*, the *key* interacts with nothing.
 * `Item`s must always have an `Action` to be associated with: `matchAction`. This means that an `Item` can interact with another `Item` or `ItemHolder` only when a certain `Action` in the `Game` is triggered. For a player this would mean that a door would open __only__ when the player tries to go through it and the key becomes player's possession __only__ when the player picks it up.
 * `Item`s can be `FixedItem` (like a door) or `MobileItem` (like a key). A `FixedItem` cannot be collected, as opposed to `MobileItem`.
 * `FixedItem`s are removed after their activation (i.e. successful match). For instance, it could "mean" that a door, when open with the proper key, just slides to a side and is not "visible" any more. It was the simplest solution for the exercise, however this behaviour could be refactored if necessary.
 * `Item` is a pure value holder with trivial behaviour and the best way to use it is to create an `object` (`Item`s are objects, aren't they), extending `FixedItem` or `MobileItem` trait and assigning the corresponding values, e.g.:

```

      object key extends MobileItem {
        //the player don't need to hold anything in order to pick it up
        override val matchItem: Option[Item] = None
        override val matchAction: GameAction = PickUpAction
        override val name: String = "shining key"
        override val description: String = s"A $name"
        override val onMatchMsg: String = s"You pick up a $name."
      }

```

#### ItemHolders

`ItemHolder` is a trait which can be mixed in to any component of the game.

 * An `ItemHolder` may hold zero or many `Item`s
 * `Item`s can be added to an `ItemHolder` and removed from it
 * `ItemHolder` knows how to *match* `Item`s with another `ItemHolder`.
 * To *match* `Item`s of one `ItemHolder` to another means that, for specific conditions in the game (`Event`s and `Action`s), the first `ItemHolder` tries to pair own `Item`s that might interact with the second `ItemHolder`'s `Item`s. The *match* can be applied to all `Item`s held by another `ItemHolder` or only to a specific subset. In the game it could mean, for instance, that having several `Item`s which the player could collect, the player may decide to only collect one or some of them, perform some other actions, and then collect the rest with possibly a different result (or effect). This adds flexibility to the game script design, making possible more sophisticated scenarios. __Note: To find matching `Item`s doesn't imply performing actions, i.e. if the player *can* collect a key, the pick-up *doesn't happen automatically*: the developer should specify explicitly the way (defining a `GameAction`) in which the player starts holding the key and the room stops holding it (via addItems() and removeItems()).__
 * Though not used in the sample game, an `ItemHolder` can contain another `ItemHolder` as `Item`, e.g. a `Room` might contain a *vault* (`Item` and `ItemHolder` at the same time), and the player could *match* the `Room`s door with one key and the *vault's* door with another key.


### Player
In this version of the application the `Player` is actually just a small container of mutable details. It is kept totally encapsulated in the `Game` and has trivial semantic and logic. Having only one player, the `Game` can perfectly undertake the responsibility of communicating in its name, if necessary.

However, this wouldn't work well for multi-player versions, requiring some refactoring. Nevertheless, the `Player` as a component is well decoupled and the refactoring could be done without affecting application's business logic.

 * `Player` knows its `position`, which is a `Room`
 * `Player` can change its `position` (when `Game` says so)
 * A `Player` is also an `ItemHolder` __Note: The `Player` starts as an empty `ItemHolder`. In case it has to start holding certain `Item`s, a minor refactoring could be necessary.__


### Game logic: Game & GameEngine

The `Game` holds the *what* of the application, the logic of what happens in its "world", the possible actions and the state. The `GameEngine` holds the *when* and *why*, i.e. it controls the logic of applications' events and the transitions from one `Game`'s state to another.

#### Game

The `Game` contains the following actions:

 * Current view: which returns the feedback of what surrounds the player
 * Move player: an intent to change `Player`'s position to another position. In order the intent to succeed, the new position should be *valid* and, if any `Item` is associated to the move, the `Player` should hold the matching `Item`. *Valid* position means that the `Room` which is `Player`'s current position has a `Neighbour` in the same `Direction` as the move that is being made.
 * Pick up item: an intent to remove an `Item` from it's current `ItemHolder` and add it to `Player`. The intent will succeed if the picked up `Item` has no other `Item` specified for match; in case it has one, the `Player` should hold it.

#### GameEngine

The `GameEngine` is basically an implementation of Finite State Machine behaviour, trying to follow the [Erlang design principles](http://www.erlang.org/documentation/doc-4.8.2/doc/design_principles/fsm.html), where:

```

    A FSM can be described as a set of relations of the form:

    State(S) x Event(E) -> Actions (A), State(S')
    ...

```

Meaning:

*If we are in state S and the event E occurs, we should perform the actions A and make a transition to the state S'.*

Following this definition, the state transition rules should be written as a number of functions which conform to the following convention:

```

    StateName(Event, StateData) ->
        .. code for actions here ...
        {next_state, StateName', StateData'}

```

There are two `State`s defined for this version of the game: `Stopped` and `Playing`. Nevertheless, the created structure makes it easy extending it to more `State`s if necessary.


## What could be improved

In a production ready application, I would take away from code all the `String`s, like messages, names, tags, etc., placing them in some specific configuration files (e.g. text-adventure.properties).

If more than one game were supposed to be developed, the code of the basic structures (e.g.`Game`, `Player`) could be refactored to more generic interfaces, as to be reused in any of the future applications.

## What isn't done

When the game is initiated or before that, when preparing its parameters, it would be good to have some verification of game's termination. This would check that exists a path which makes possible to reach from the initial position in the game to the ending one. This wasn't implemented here.

# How to run it

What is ready for play is the implementation of the sample game. In order to run it, in your IDE, choose and run `object GameApp` in `src/main/scala/adventure/GameApp.scala`.