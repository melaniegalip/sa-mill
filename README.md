[![Build Status](https://app.travis-ci.com/melaniegalip/Mill.svg?branch=main)](https://app.travis-ci.com/melaniegalip/Mill)
[![Coverage Status](https://coveralls.io/repos/github/melaniegalip/Mill/badge.svg?branch=main)](https://coveralls.io/github/melaniegalip/Mill?branch=main)

# The Mill - Classic Board Game


Mill is one of the oldest board games and known in Europe by different names: Nine Men Morris, Mühle, Molenspel and Merrelles.

<img src="https://i.etsystatic.com/10392661/r/il/3bd19c/3379188715/il_1588xN.3379188715_gife.jpg" height="750" width="750" >

## Rules

There are 2 players in the game. Each player has 9 pieces of a given color. Each player tries to form a “mill”, which is 3 of their pieces in one row connected by the line. To form a mill, the pieces can be placed either horizontally or vertically, but not at at a 90 degree angle, and they must be connected by lines. The game begins when all pieces are off the board. Players decide who goes first either by agreement or by lot, such as toss of a coin.
There are 3 phases to the game:

### Phase 1: Placing the pieces onto the board


The players take turns placing one piece at a time onto the board, onto the empty dots.
During this phase each player tries to prevent the opponent from forming a mill. However, if a mill was formed anyway, then the player who formed a mill gets to remove one of the opponent’s pieces, of their choice, from the board. The piece removed has to be not in a mill itself. If all of the opponent’s pieces form a mill, then none of the opponent’s pieces can be removed.
Players can form more than one mill in a single move by strategically placing their pieces. If they form more than one mill, then they get to remove as many of the opponent’s pieces, as the number of mills that they formed.

### Phase 2: Moving the pieces on the board

Once all of the pieces have been placed onto the board, the players begin moving their pieces, in alternating moves, one dot at a time to try to form mills in the same fashion as in the first phase.
In the second phase pieces can only move to an adjacent dot, and pieces cannot jump over each other or skip dots if more than one are available in a row.
A player is allowed to move a piece out of a mill and then moving it back on the next move to form the mill again. If this happens it is considered as if they formed a new mill and they get to remove one of the opponent’s pieces.

### Phase 3: Flying the pieces across the board

When one of the players has been reduced down to 3 last pieces, phase 2 ends and phase 3 begins.
In phase 3, the limitation of moving only to an adjacent dot is removed, and both players can move their pieces to any available dot, even if that requires skipping dots or jumping over other pieces.

## Goal


The game ends when one of the players is reduced to two pieces, and cannot any longer form mills, or if a player has no legal moves to make, making their opponent the winner in either situation.

## Features

- 99% Code Coverage
- Custom Emoji Font
- GUI + TUI running concurrently
- Strategy, State, Command, Memento, Factory, Singleton Design Patterns
- Saving + Loading via XML or JSON
- ScalaFX GUI with custom Background Image
- Functional and Clean Code
- Google Guice DI with Clean Architecture using Interfaces
- MVC with Observer Pattern
- Undo/Redo Game Turns
- Docker Image with GUI
- Logger with Console and Rolling File Appenders
- Scala 3

## Docker Image

### Configure Display Output

On the command line execute:

```bash
ip=$(ifconfig en0 | grep inet | awk '$1=="inet" {print $2}')
xhost + $ip
```

### Build Docker Image

```bash
docker build -t mill:v1 .
```

### Run Docker Image

```bash
docker run -e DISPLAY=$ip:0 -v /tmp/.X11-unix:/tmp/.X11-unix -it mill:v1
```
