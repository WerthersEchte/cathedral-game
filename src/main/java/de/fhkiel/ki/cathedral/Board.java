package de.fhkiel.ki.cathedral;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
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
      int numberOfConnections = 0;
      for(Position corner : placement.building().corners(placement.direction())){
        Position point = placement.position().plus(corner);
        if (!point.isViable() || field[point.y()][point.x()] == placement.building().getColor() ){
          numberOfConnections += 1;
          if(numberOfConnections > 1){
            buildRegions();
            // buildRegions(); //check for region in region bug
            break;
          }
        }
      }
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

  public List<Building> getPlacableBuildings(Color player) {
    return getBuildings().stream()
        .filter(building -> building.getColor() == player)
        .filter(building -> getNumberOfFreeBuildings(building) > 0)
        .toList();
  }

  public List<Building> getAllUnplacedBuildings() {
    return getBuildings().stream()
        .filter(building -> getNumberOfFreeBuildings(building) > 0)
        .toList();
  }

  private void buildRegions() {
    Arrays.stream(new Color[] {Color.Black, Color.White}).forEach(color -> {
      int[][] fieldWithoutColor = new int[10][10];
      for (int y = 0; y < 10; ++y) {
        for (int x = 0; x < 10; ++x) {
          if(field[y][x] != color){
            fieldWithoutColor[y][x] = 1;
          }
        }
      }
      int runner = 0;
      while (runner < 100) {
        if(fieldWithoutColor[runner/10][runner%10] == 1) {
          Deque<Position> freeFieldsToLookAt = new LinkedList<>();
          freeFieldsToLookAt.push(new Position(runner%10, runner/10));
          fieldWithoutColor[runner/10][runner%10] = 0;

          List<Position> region = new ArrayList<>();
          Set<Direction> borders = new HashSet<>();

          while (!freeFieldsToLookAt.isEmpty()) {
            Position currentField = freeFieldsToLookAt.pop();

            region.add(currentField);

            int xMin = -1;
            int xMax = 2;
            for (int y = -1; y < 2; ++y) {
              final int yToLookAt = currentField.y() + y;
              if (yToLookAt < 0) {
                borders.add(Direction._0);
              } else if (yToLookAt > 9) {
                borders.add(Direction._180);
              } else {
                for (int x = xMin; x < xMax; ++x) {
                  if (x != 0 || y != 0) {
                    final int xToLookAt = currentField.x() + x;
                    if (xToLookAt < 0) {
                      borders.add(Direction._270);
                      ++xMin;
                    } else if (xToLookAt > 9) {
                      borders.add(Direction._90);
                      --xMax;
                    } else if (fieldWithoutColor[yToLookAt][xToLookAt] == 1) {
                      freeFieldsToLookAt.push(new Position(xToLookAt, yToLookAt));
                      fieldWithoutColor[yToLookAt][xToLookAt] = 0;
                    }
                  }
                }
              }
            }
          }

          if (borders.size() < 3) {
            List<Placement> enemyBuildingsInRegion = getAllEnemyBuildingsInRegion(region, color);
            if (enemyBuildingsInRegion.size() < 2) {
              enemyBuildingsInRegion.forEach(this::removePlacement);
              placeColor(region, Color.getSubColor(color), 0, 0);
            }
          }
        }
        runner += 1;
      }
    });
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
    //placeColor(placement.form(), Color.None, placement.x(), placement.y());
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
