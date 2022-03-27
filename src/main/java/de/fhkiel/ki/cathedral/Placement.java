package de.fhkiel.ki.cathedral;

import java.util.List;
import java.util.Objects;

public class Placement {

  private final Position position;
  private final Direction direction;
  private final Building building;

  public Placement(Position position, Direction direction, Building building) {
    this.position = position;
    this.direction = building.getTurnable().getRealDirection(direction);
    this.building = building;
  }

  public Placement(int x, int y, Direction direction, Building building) {
    this(new Position(x, y), direction, building);
  }

  public int x() {
    return position.x();
  }

  public int y() {
    return position.y();
  }

  public Position position() {
    return position;
  }

  public Direction direction() {
    return direction;
  }

  public Building building() {
    return building;
  }

  public List<Position> form() {
    return building.turn(direction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Placement placement = (Placement) o;
    return position.equals(placement.position) &&
        direction == placement.direction &&
        building.equals(placement.building);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, direction, building);
  }

  @Override
  public String toString() {
    return "Building: " + building + " Position: " + position + " Direction" + direction;
  }
}
