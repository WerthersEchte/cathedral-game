package de.fhkiel.ki.cathedral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Board {
  private final Map<Building, Integer> freeBuildings = new EnumMap<>(Building.class);
  private final List<Placement> placedBuildings = new ArrayList<>();

  private final Color[][] field = new Color[10][10];

  public Board() {
  this(Building.values());
  }

  public Board(Building... buildings) {
    for (Building building : buildings) {
      freeBuildings.put(building, building.getNumberInGame());
    }

    for (int y = 0; y < 10; ++y) {
      for (int x = 0; x < 10; ++x) {
        field[y][x] = Color.None;
      }
    }
  }

  public Board copy() {
    Board newBoard = new Board( new Building[]{} );

    newBoard.freeBuildings.putAll(freeBuildings);
    newBoard.placedBuildings.addAll(placedBuildings);

    for (int y = 0; y < 10; ++y) {
      System.arraycopy(field[y], 0, newBoard.field[y], 0, 10);
    }
    return newBoard;
  }

  private boolean regionsBuild = false;

  public boolean placeBuilding(Placement placement) {
    return placeBuilding(placement, false);
  }

  public boolean placeBuilding(Placement placement, boolean fast) {

    if (freeBuildings.getOrDefault(placement.building(), 0) <= 0) {
      return false;
    }

    if (isNotPlaceable(placement)) {
      return false;
    }

    placeColor(placement.form(), placement.building().getColor(), placement.x(),
        placement.y());
    freeBuildings.put(placement.building(),
        freeBuildings.getOrDefault(placement.building(), 1) -
            1); //do not want any negative building number
    placedBuildings.add(placement);

    regionsBuild = false;
    if (!fast) {
      buildRegions();
      buildRegions();
    }

    return true;
  }

  private Placement lastPlacement = null;
  private Map<Color, Integer> currentScore = null;

  public Map<Color, Integer> score() {
    if (currentScore == null ||
        !placedBuildings.isEmpty() && lastPlacement != placedBuildings.get(placedBuildings.size() - 1)) {
      if (!placedBuildings.isEmpty()) {
        lastPlacement = placedBuildings.get(placedBuildings.size() - 1);
      }

      Map<Color, Integer> score = new EnumMap<>(Color.class);
      score.put(Color.Black, 0);
      score.put(Color.White, 0);

      freeBuildings.keySet()
          .stream()
          .filter(building -> score.containsKey(building.getColor()))
          .forEach(building -> score.put(building.getColor(),
              score.get(building.getColor()) + building.score() * freeBuildings.get(building)));
      currentScore = score;
    }

    return currentScore;
  }

  public int getNumberOfFreeBuildings(Building building) {
    return freeBuildings.getOrDefault(building, 0);
  }

  public Set<Building> getBuildings() {
    return freeBuildings.keySet();
  }

  private void buildRegions() {
    Arrays.stream(new Color[] {Color.Black, Color.White}).forEach(color -> {
      Stack<Position> freeFields = new Stack<>();
      for (int y = 0; y < 10; ++y) {
        for (int x = 0; x < 10; ++x) {
          if (this.field[y][x] != color) {
            freeFields.add(new Position(x, y));
          }
        }
      }
      while (!freeFields.isEmpty()) {
        Stack<Position> freeFieldsToLookAt = new Stack<>();
        freeFieldsToLookAt.push(freeFields.pop());

        List<Position> region = new ArrayList<>();
        Set<Direction> borders = new HashSet<>();

        while (!freeFieldsToLookAt.isEmpty()) {
          Position currentField = freeFieldsToLookAt.pop();

          region.add(currentField);

          for (int y = -1; y < 2; ++y) {
            final int realY = currentField.y() + y;
            if (realY < 0) {
              borders.add(Direction._0);
              continue;
            }
            if (realY > 9) {
              borders.add(Direction._180);
              continue;
            }
            for (int x = -1; x < 2; ++x) {
              final int realX = currentField.x() + x;
              if (realX < 0) {
                borders.add(Direction._270);
                continue;
              }
              if (realX > 9) {
                borders.add(Direction._90);
                continue;
              }
              Optional<Position> fieldToLookAt = freeFields.stream()
                  .filter(position -> position.equals(new Position(realX, realY))).findAny();
              if (fieldToLookAt.isPresent()) {
                freeFieldsToLookAt.push(fieldToLookAt.get());
                freeFields.remove(fieldToLookAt.get());
              }
            }
          }
        }

        if (borders.size() < 3 && isNotAlreadyOwned(region, color)) {
          List<Placement> enemyBuildingsInRegion = getAllEnemyBuildingsInRegion(region, color);
          if (enemyBuildingsInRegion.size() < 2) {
            enemyBuildingsInRegion.forEach(this::removePlacement);
            placeColor(region, Color.getSubColor(color), 0, 0);
          }
        }
      }
    });
  }

  private boolean isNotAlreadyOwned(List<Position> region, Color color) {
    return region.stream()
        .anyMatch(position -> field[position.y()][position.x()] != Color.getSubColor(color));
  }

  private List<Placement> getAllEnemyBuildingsInRegion(List<Position> region, Color color) {
    return placedBuildings.stream()
        .filter(placement -> region.contains(placement.position()))
        .filter(placement -> placement.building().getColor() != color)
        .collect(Collectors.toList());
  }

  private void removePlacement(Placement placement) {
    placedBuildings.remove(placement);
    freeBuildings.put(placement.building(), freeBuildings.getOrDefault(placement.building(), 0) + 1);
    placeColor(placement.form(), Color.None, placement.x(), placement.y());
  }

  private void placeColor(List<Position> form, Color color, int x, int y) {
    form.forEach(position -> field[position.y() + y][position.x() + x] = color);
  }

  private boolean isNotPlaceable(Placement placement) {
    return placement.form().stream().anyMatch(position -> {
      Position realPosition = position.plus(placement.position());

      return
          !realPosition.isViable() ||
              !colorIsCompatible(field[realPosition.y()][realPosition.x()], placement.building().getColor());
    });
  }

  private boolean colorIsCompatible(Color onPosition, Color toPlace) {
    return onPosition == Color.None ||
        onPosition == Color.Black_Owned && toPlace == Color.Black ||
        onPosition == Color.White_Owned && toPlace == Color.White;
  }

  public Map<Building, Integer> getFreeBuildings() {
    return new EnumMap<>(freeBuildings);
  }

  public List<Placement> getPlacedBuildings() {
    return new ArrayList<>(placedBuildings);
  }

  public Color[][] getField() {
    return field;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Board board1 = (Board) o;
    return Objects.equals(freeBuildings, board1.freeBuildings) &&
        Objects.equals(placedBuildings, board1.placedBuildings) &&
        Arrays.deepEquals(field, board1.field);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(freeBuildings, placedBuildings);
    result = 31 * result + Arrays.deepHashCode(field);
    return result;
  }

  @Override
  public String toString() {
    StringJoiner boardAsString = new StringJoiner(", ", Board.class.getSimpleName() + "[", "]")
        .add("\nfreeBuildings=" + freeBuildings)
        .add("\nplacedBuildings=" + placedBuildings)
        .add("\nboard=\n");
    for (int y = 0; y < 10; ++y) {
      boardAsString.add(Arrays.toString(field[y]) + "\n");
    }
    return boardAsString.toString();
  }

}
