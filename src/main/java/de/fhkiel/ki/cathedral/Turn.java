package de.fhkiel.ki.cathedral;

import java.util.Map;

public class Turn {
  private final int turnNumber;
  private final Board board;

  private final Placement action;

  public Turn(int turnNumber, Board board, Placement action) {
    this.turnNumber = turnNumber;
    this.board = board;
    this.action = action;
  }

  public Turn(int turnNumber, Board board) {
    this.turnNumber = turnNumber;
    this.board = board;
    this.action = null;
  }

  public Turn copy() {
    return new Turn(turnNumber, board.copy(), action);
  }

  public Map<Color, Integer> score() {
    return board.score();
  }

  public int getTurnNumber() {
    return turnNumber;
  }

  public Board getBoard() {
    return board;
  }

  public boolean hasAction() {
    return action != null;
  }

  public Placement getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "Turn: " + turnNumber + " Action: " + action + "\nScore: B " + score().get(Color.Black) + " | " + score().get(Color.White) + " W\n" +
        board;
  }
}
