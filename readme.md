# A cathedral implementation in Java
[![License: Unlicense](https://img.shields.io/badge/License-Unlicense-blue.svg)](http://unlicense.org/)
![Build](https://github.com/WerthersEchte/cathedral-game/actions/workflows/build.yml/badge.svg)
[![Test coverage](.github/badges/jacoco.svg)](https://github.com/WerthersEchte/cathedral-game/actions/workflows/build.yml)
[![Code Quality](https://github.com/WerthersEchte/cathedral-game/actions/workflows/codequality.yml/badge.svg)](https://github.com/WerthersEchte/cathedral-game/actions/workflows/codequality.yml)

## About
This is a Java implementation of the board game cathedral (https://en.wikipedia.org/wiki/Cathedral_(board_game)). It indended use is for developing basic ki and ki adjacent programs. It contains only the needed gamelogic.

## How to
### get it

### use it
Example creating a new game and placing the Cathedral

    // create a game
    Game game = new Game();

    // Select a building
    // Get possible buildings with game.getPlacableBuildings()
    Building building = Building.Blue_Cathedral;
    // Select a position for the building
    Position position = new Position(3, 5);
    // Select a direction for the building to face
    // Get possible directions the building can turn with building.getTurnable().getPossibleDirections()
    Direction direction = Direction._90;

    // Create a placement from the building, position and rotation
    Placement placement = new Placement(position, direction, building);

    // take the turn with the placement
    game.takeTurn(placement);

    // repeat

    // when the game is finished
    // game.isFinished() returns true if no more buildings can be placed
    if(game.isFinished()){
      System.out.println("Game is finished");
    }

For more information and examples [https://werthersechte.github.io/cathedral-game/](https://werthersechte.github.io/cathedral-game/)
