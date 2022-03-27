package de.fhkiel.ki.cathedral;

import java.util.Objects;

public class Position {
  private static final int MIN_X = 0;
  private static final int MAX_X = 9;
  private static final int MIN_Y = 0;
  private static final int MAX_Y = 9;

  private final int x;
  private final int y;

  private final boolean viable;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;

    viable = x >= MIN_X && x <= MAX_X && y >= MIN_Y && y <= MAX_Y;
  }

  public int x() {
    return x;
  }

  public int y() {
    return y;
  }

  public boolean isViable() {
    return viable;
  }

  public Position plus(Position position) {
    return new Position(x + position.x, y + position.y);
  }

  public Position plus(int x, int y) {
    return new Position(this.x + x, this.y + y);
  }

  public Position minus(Position position) {
    return new Position(x - position.x, y - position.y);
  }

  public Position minus(int x, int y) {
    return new Position(this.x - x, this.y - y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Position position = (Position) o;
    return x == position.x &&
        y == position.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "(" + x + "/" + y + ")";
  }
}
