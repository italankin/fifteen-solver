# `fifteen-solver`

`fifteen-solver` is a Java/Kotlin library for solving [15 puzzle][15-puzzle-wiki] games.

[15-puzzle-wiki]: https://en.wikipedia.org/wiki/15_puzzle

## Usage

### Download

1. Include JitPack repository (e.g. in `settings.gradle`):
   ```groovy
   dependencyResolutionManagement {
       repositories {
           maven {
               url 'https://jitpack.io'
               content { includeGroup("me.italankin.fifteen-solver") }
           }
       }
   }
   ```
2. Add the dependency:
   ```groovy
   dependencies {
       // solver dependency
       implementation 'me.italankin.fifteen-solver:solver:<version>'
       // optional: game dependency, if you only need game implementation
       implementation 'me.italankin.fifteen-solver:game:<version>'
   }
   ```
   See [releases](/releases/) for available versions.

### Basic example

```kotlin
fun main(): Unit = runBlocking {
    val session = Session(
        // generate 100 games of size 3x3 using random shuffle for scrambling
        generator = randomGames()
            .size(3 x 3)
            .scrambler(ShuffleScrambler())
            .generator()
            .bounded(100),
        // compare two A* solvers using Manhattan Distance and Linear Conflict heuristics
        solvers = listOf(
            Solver(
                heuristics = ManhattanDistance(),
                algorithm = AStar(),
            ),
            Solver(
                heuristics = LinearConflict(),
                algorithm = AStar(),
            ),
        ),
        // report results and compare solvers by average solve time
        reporter = SystemOutReporter(
            compareBy = listOf(SystemOutReporter.DefaultFields.AVG_TIME)
        ).withProgress(), // display live progress
    )
    session.dumpParameters() // dump session parameters to stdout
    session.execute() // start solving
}
```

<details>
<summary>Example output</summary>

```text
session parameters:
  concurrency: 11
  generator:
    Bounded(count=100) {
       Random(Classic, 3x3, Default, ShuffleScrambler)
    }
  games count: 100
  total games count: 200
  solvers:
    * Solver(heuristics=ManhattanDistance, algorithm=A*(Default))
    * Solver(heuristics=LinearConflict, algorithm=A*(Default))

┌────────────┬────────────────┐
│ Total      │ 200 (0 errors) │
│ Total time │         188 ms │
│ Max memory │          59 MB │
│ Avg memory │          32 MB │
│ GC count   │              2 │
│ GC time    │           3 ms │
└────────────┴────────────────┘
┌─────────────────────────────────────────────────────────────┬───────────────┬─────────────────┬─────────────────────┬────────────────┬────────────────┬────────────────┬─────────────┬─────────────┬─────────────┐
│ Solver                                                      │ CPU time (ms) │ Speed (games/s) │ Avg speed (ms/game) │ Time (min, ms) │ Time (max, ms) │ Time (avg, ms) │ Moves (min) │ Moves (max) │ Moves (avg) │
├─────────────────────────────────────────────────────────────┼───────────────┼─────────────────┼─────────────────────┼────────────────┼────────────────┼────────────────┼─────────────┼─────────────┼─────────────┤
│ Solver(heuristics=LinearConflict, algorithm=A*(Default))    │           778 │         128.560 │               7.778 │          0.033 │        113.835 │          7.778 │          11 │          28 │      22.580 │
│ Solver(heuristics=ManhattanDistance, algorithm=A*(Default)) │           993 │         100.745 │               9.926 │          0.021 │        116.841 │          9.926 │          11 │          28 │      22.580 │
├─────────────────────────────────────────────────────────────┼───────────────┼─────────────────┼─────────────────────┼────────────────┼────────────────┼────────────────┼─────────────┼─────────────┼─────────────┤
│ Total                                                       │          1770 │        1063.830 │               0.940 │          0.021 │        116.841 │          8.852 │          11 │          28 │      22.580 │
└─────────────────────────────────────────────────────────────┴───────────────┴─────────────────┴─────────────────────┴────────────────┴────────────────┴────────────────┴─────────────┴─────────────┴─────────────┘
Compare by AVG_TIME:
┌─────────────────────────────────────────────────────────────┬───────┬────────┬──────────┐
│ Solver                                                      │  Base │   Diff │ Diff (%) │
├─────────────────────────────────────────────────────────────┼───────┼────────┼──────────┤
│ Solver(heuristics=LinearConflict, algorithm=A*(Default))    │ 7.778 │  0.000 │   0.000% │
│ Solver(heuristics=ManhattanDistance, algorithm=A*(Default)) │ 9.926 │ +2.148 │ +27.610% │
└─────────────────────────────────────────────────────────────┴───────┴────────┴──────────┘
```

</details>

### Sessions

[`Session`][session] is the main entry point. It encapsulates solving, reporting and threading logic for games.
Each session can be `execute()`d only once.

[session]: solver/src/main/java/me/italankin/fifteen/solver/Session.kt

#### Parameters

* `generator` - [generator](#generators) for games
* `solvers` - list of [solvers](#solvers)
* `reporter` - [reports](#reporters) progress and results of solves
* `concurrency` - limit number of parallel solves (defaults to available processors count)

For example:

```kotlin
val session = Session(
    generator = randomGames().generator(),
    solvers = listOf(Solver(ManhattanDistance(), AStar())),
    reporter = SystemOutReporter().withProgress(),
    concurrency = Session.Concurrency.Fixed(numThreads = 4)
)
```

### Algorithms

> [`me.italankin.fifteen.solver.algorithm`](solver/src/main/java/me/italankin/fifteen/solver/algorithm)

15 puzzle can be solved using different algorithms, but currently implemented are:

* [`A*`][a-star-impl] - consumes large amount of memory, but solves relatively quick.

  **Warning**: solving anything above 4x3 or 3x4 can be slow and result in OOMs for most machines.

  If you just need *any* solution, the process can be sped up with different strategies
  for picking most promising node:
    * Default (optimal solutions)
    * Bounded relaxation (suboptimal solutions):
        * `StaticWeighting`
        * `DynamicWeighting`
* [`IDA*`][ida-star-impl] - slow, but has low memory usage

[a-star-impl]: solver/src/main/java/me/italankin/fifteen/solver/algorithm/astar/AStar.java

[ida-star-impl]: solver/src/main/java/me/italankin/fifteen/solver/algorithm/idastar/IDAStar.java

You can implement your own algorithm using [`Algorithm`][algorithm].

[algorithm]: solver/src/main/java/me/italankin/fifteen/solver/algorithm/Algorithm.kt

### Heuristics

> [`me.italankin.fifteen.solver.heuristics`](solver/src/main/java/me/italankin/fifteen/solver/heuristics)

Currently available heuristics are:

* `ManhattanDistance`
* `LinearConflict`
* `RelaxedAdjacency`
* `HammingDistance`
* `Inversions`
* `EuclideanDistance`

Custom heuristics can be created by implementing [`Heuristics`][heuristics-interface] interface.

[heuristics-interface]: solver/src/main/java/me/italankin/fifteen/solver/heuristics/Heuristics.kt

### Solvers

[`Solver`][solver]s allow testing and comparing different combinations `Heuristics` and `Algorithm`s. For example:

[solver]: solver/src/main/java/me/italankin/fifteen/solver/Solver.kt

```kotlin
val session = Session(
    generator = /* ... */,
    solvers = listOf(
        ManhattanDistance(),
        LinearConflict(),
        HammingDistance()
    ).map { heuristics -> Solver(heuristics, AStar()) },
    reporter = /* ... */
)
```

### Generators

> [`me.italankin.fifteen.solver.generator`](solver/src/main/java/me/italankin/fifteen/solver/generator)

To solve a game, it must be created, which can be done with a help of [generators][game-generator]:

[game-generator]: solver/src/main/java/me/italankin/fifteen/solver/generator/GameGenerator.kt

* `RandomGameGenerator` - create random games with specified parameters
  ```kotlin
  val randomGameGenerator = randomGames()
      .size(4 x 4)
      .scrambler(ShuffleScrambler(random = Random(0)))
      .factory(GameFactory.Classic)
      .missingTile(MissingTile.Default)
      .skipSolved(true)
      .generator()
  ```
* `StaticGameGenerator` - yields the same games on every request
  ```kotlin
  val staticGameGenerator = staticGames {
      +ClassicGame(3, 3, listOf(0, 4, 8, 5, 7, 3, 6, 1, 2))
      +listOf(
          ClassicGame(3, 3, listOf(7, 8, 4, 1, 6, 2, 3, 0, 5)),
          ClassicGame(3, 3, listOf(5, 1, 4, 0, 2, 7, 8, 3, 6)),
      )
  }
  ```
* `BoundedGameGenerator` - `GameGenerator` with fixed number of produced games
  ```kotlin
  val boundedGameGenerator = randomGames()
      .generator()
      .bounded(100)
  ```
* `FilterGameGenerator` - filter games
  ```kotlin
  val filterGameGenerator = randomGames()
      .bounded(100)
      .filterGames { it.state[0] != 0 }
  ```
* `ConcatGameGenerator` - concat multiple `BoundedGameGenerator`s
  ```kotlin
  val concatGameGenerator = concatGames {
      +randomGames().size(3 x 3).bounded(100)
      +randomGames().size(4 x 4).bounded(100)
  }
  ```
* `ShufflingGameGenerator` - shuffle results from `BoundedGameGenerator` on each request
  ```kotlin
  val shufflingGameGenerator = randomGames()
      .bounded(100)
      .shuffle(random = Random(0))
  ```
* `RepeatingGameGenerator` - repeat results from `BoundedGameGenerator` specified number of times
  ```kotlin
  val repeatingGameGenerator = randomGames()
      .bounded(100)
      .repeat(5)
  ```
* `FrozenGameGenerator` - freeze state of `BoundedGameGenerator` to produce same results on each request
  ```kotlin
  val frozenGameGenerator = randomGames()
      .bounded(100)
      .freeze()
  ```

#### Scramblers

> [`me.italankin.fifteen.game.scrambler`](game/src/main/java/me/italankin/fifteen/game/scrambler)

[`Game.Scrambler`][scrambler] is an interface for creating game scrambles. Currently available `Scrambler`s are:

[scrambler]: game/src/main/java/me/italankin/fifteen/game/Game.kt

* `ShuffleScrambler` - random shuffle
* `ShuffleHarderScrambler` - random shuffle, but requires more moves on average than `ShuffleScrambler`
* `SolvedScrambler` - solved game
* `RandomClickScrambler` - randomly clicks on a tile in the same row or column as empty space
* `RandomMovesScrambler` - scramble puzzle by doing random moves
* `StrideScrambler` - randomly select a cell and move `0` to that cell

<details>
<summary>Example comparison of average moves for 3x3</summary>

```text
┌────────────────────────────────────────────────────────┬────────┬─────────┬──────────┐
│ Solver                                                 │   Base │    Diff │ Diff (%) │
├────────────────────────────────────────────────────────┼────────┼─────────┼──────────┤
│ ShuffleHarderScrambler                                 │ 25.346 │   0.000 │    0.000 │
│ ShuffleScrambler                                       │ 21.987 │  -3.359 │  -13.254 │
│ StrideScrambler(iterations=50, allowEmptyMoves=false)  │ 21.466 │  -3.880 │  -15.307 │
│ RandomClickScrambler(rounds=50)                        │ 15.610 │  -9.736 │  -38.412 │
│ RandomMovesScrambler(moves=50, allowPrevMoveUndo=true) │ 12.570 │ -12.776 │  -50.406 │
└────────────────────────────────────────────────────────┴────────┴─────────┴──────────┘
```

</details>

### Reporters

> [`me.italankin.fifteen.solver.reporter`](solver/src/main/java/me/italankin/fifteen/solver/reporter)

Available `Reporter`s:

* `SystemOutReporter` - reports summary stats to `stdout`
* `ProgressReporter` - reports current progress to `stdout`, e.g.:
  ```text
  queue: 86/11/3/100 (3%), 1.476 games/s, memory: 3679 MB, elapsed: 2s, remaining: 66s
  ```
* `JsonReporter` - dumps results to JSON
* `CsvReporter` - dumps results to CSV
* `CompositeReporter` - concatenates multiple reporters
  ```kotlin
  val compositeReporter = SystemOutReporter() + 
      ProgressReporter() + 
      JsonReporter(JsonReporter.Output.ToFile("results.json"))
  ```

### Statistics

[Statistics][stats] can be calculated by simply calling `stats()` extension function on the results:

[stats]: solver/src/main/java/me/italankin/fifteen/solver/Stats.kt

```kotlin
val session = Session(...)
val results = session.execute()
val stats = results.stats()
// do something with stats
```

You can also utilize [`Table`][table] class, example usage can be found in [`SystemOutReporter`][system-out-reporter].

[system-out-reporter]: solver/src/main/java/me/italankin/fifteen/solver/reporter/SystemOutReporter.kt

[table]: solver/src/main/java/me/italankin/fifteen/solver/util/Table.kt

## License

See [`LICENSE`](./LICENSE).
