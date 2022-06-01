package de.fhkiel.ki.cathedral;

public class Test {
  public static void main(String[] args) {
    // Instantiate the game
    Game game = new Game();
    // print the empty game to the console
    System.out.println(game);

    // Place the cathedral
    // create the placement
    Placement cathedral = new Placement(3, 3, Direction._0, Building.Blue_Cathedral);
    // make the placement
    game.takeTurn(cathedral);
    // print the game to the console
    System.out.println(game);

    // Place the black academy
    // create the placement
    Placement blackAcademy = new Placement(8, 3, Direction._270, Building.Black_Academy);
    // make the placement
    game.takeTurn(blackAcademy);
    // print the game to the console
    System.out.println(game);

    Board base = new Board();
    base.placeBuilding(new Placement(3, 3, Direction._0, Building.Blue_Cathedral));
    base.placeBuilding(new Placement(8, 3, Direction._270, Building.Black_Academy));
    base.placeBuilding(new Placement(7, 1, Direction._0, Building.White_Castle));
    base.placeBuilding(new Placement(7, 5, Direction._90, Building.Black_Manor));
    base.placeBuilding(new Placement(6, 7, Direction._0, Building.White_Infirmary));

    base.placeBuilding(new Placement(8, 7, Direction._270, Building.Black_Castle));
    base.placeBuilding(new Placement(4, 1, Direction._180, Building.White_Academy));
    base.placeBuilding(new Placement(1, 1, Direction._0, Building.Black_Infirmary));
    base.placeBuilding(new Placement(4, 8, Direction._90, Building.White_Manor));
    base.placeBuilding(new Placement(3, 7, Direction._270, Building.Black_Tower));

    base.placeBuilding(new Placement(1, 6, Direction._0, Building.White_Abbey));
    base.placeBuilding(new Placement(1, 4, Direction._0, Building.Black_Abbey));
    base.placeBuilding(new Placement(5, 4, Direction._270, Building.White_Tower));
    base.placeBuilding(new Placement(0, 8, Direction._0, Building.Black_Square));
    base.placeBuilding(new Placement(9, 1, Direction._0, Building.White_Bridge));

    System.out.println(base);

    base = new Board();

    base.placeBuilding(new Placement(1, 0, Direction._90, Building.White_Inn));
    System.out.println(base);
    base.placeBuilding(new Placement(3, 1, Direction._0, Building.Black_Bridge));
    System.out.println(base);
    base.placeBuilding(new Placement(1, 3, Direction._90, Building.Black_Tower));
    System.out.println(base);
  }
}
