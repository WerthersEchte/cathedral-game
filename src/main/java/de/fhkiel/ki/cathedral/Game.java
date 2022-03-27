package de.fhkiel.ki.cathedral;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {
  private final List<Turn> turns = new ArrayList<>();
  private boolean ignoreRules = false;

  public Game() {
    this(new Board());
  }

  public Game( Board starter ){
    turns.add(new Turn(0, starter, null));
  }

  public Game copy() {
    Game gameCopy = new Game();
    gameCopy.turns.clear();
    gameCopy.ignoreRules = ignoreRules;

    turns.forEach(turn -> gameCopy.turns.add(turn.copy()));

    return gameCopy;
  }

  public boolean takeTurn(Placement placement) {
    return takeTurn(placement, false);
  }

  public boolean takeTurn(Placement placement, boolean fast) {
    if (placement == null || placement.building() == null || placement.position() == null || !placement.position().isViable()) {
      return false;
    }

    if (!ignoreRules && placement.building().getColor() != getCurrentPlayer()) {
      return false;
    }
    Board nextBoardState = lastTurn().getBoard().copy();

    if (!nextBoardState.placeBuilding(placement, fast)) {
      return false;
    }

    turns.add(new Turn(turns.size(), nextBoardState, placement));

    return true;
  }

  public Turn lastTurn() {
    return turns.get(turns.size() - 1);
  }

  public void undoLastTurn() {
    if (turns.size() > 1) {
      turns.remove(turns.size() - 1);
    }
  }

  public void forfeitTurn() {
    turns.add(new Turn(turns.size(), lastTurn().getBoard().copy()));
  }

  public Color getCurrentPlayer() {
    if (turns.size() == 1) {
      return Color.Blue;
    } else {
      if (turns.size() % 2 == 0) {
        return Color.Black;
      }
      return Color.White;
    }
  }

  public List<Building> getPlacableBuildings() {
    if (ignoreRules) {
      return lastTurn().getBoard().getBuildings().stream()
          .filter(building -> lastTurn().getBoard().getNumberOfFreeBuildings(building) > 0)
          .collect(Collectors.toList());
    }
    return getPlacableBuildings(getCurrentPlayer());
  }

  public List<Building> getPlacableBuildings(Color player) {
    return lastTurn().getBoard().getBuildings().stream()
        .filter(building -> building.getColor() == player)
        .filter(building -> lastTurn().getBoard().getNumberOfFreeBuildings(building) > 0)
        .collect(Collectors.toList());
  }

  public List<Building> getAllBuildings() {
    return lastTurn().getBoard().getBuildings().stream()
        .filter(building -> lastTurn().getBoard().getNumberOfFreeBuildings(building) > 0)
        .collect(Collectors.toList());
  }

  public Board getBoard() {
    return lastTurn().getBoard();
  }

  public Map<Color, Integer> score() {
    return lastTurn().score();
  }

  public void ignoreRules(boolean ignoreRules) {
    this.ignoreRules = ignoreRules;
  }

  @Override
  public String toString() {
    return "Turn: " + turns.size() + (ignoreRules ? " (Rules ignored)\n" : "\n") + lastTurn();
  }
}
