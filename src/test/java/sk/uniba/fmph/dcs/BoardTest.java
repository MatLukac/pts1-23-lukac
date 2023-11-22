package sk.uniba.fmph.dcs;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.interfaces.PatternLineInterface;
import sk.uniba.fmph.dcs.interfaces.WallLineInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

class FakePatternLine implements PatternLineInterface {
    public int capacity;
    private List<Tile> tiles = new ArrayList<>();
    private int pointsToReturn = 0; // Default points to return in finishRound

    @Override
    public void put(List<Tile> tiles) {
        this.tiles.addAll(tiles);
    }

    @Override
    public Points finishRound() {
        Points points = new Points(pointsToReturn);
        tiles.clear(); // Assuming the tiles are cleared at the end of the round
        return points;
    }

    @Override
    public String state() {
        return tiles.toString();
    }

    // Method to set the points to be returned in finishRound
    public void setPointsToReturn(int points) {
        this.pointsToReturn = points;
    }
}

class FakeWallLine implements WallLineInterface {
    private List<Optional<Tile>> tiles = new ArrayList<>();

    @Override
    public void canPutTile(Tile tile) {
        // Implement logic if needed, or leave as a no-op
    }

    @Override
    public List<Optional<Tile>> getTiles() {
        return tiles;
    }

    @Override
    public Points putTile(Tile tile) {
        tiles.add(Optional.ofNullable(tile));
        return new Points(0); // Return 0 points as default, adjust as needed
    }

    @Override
    public String state() {
        return tiles.toString();
    }}


public class BoardTest {
    private Board board;
    private Floor fakeFloor;
    private Points fakePoints;
    private List<PatternLineInterface> fakePatternLines;
    private List<WallLineInterface> fakeWallLines;
    private FakeUsedTiles usedTiles;
    @Before
    public void setUp() {
        // Initialize the fake objects
        usedTiles = new FakeUsedTiles();
        ArrayList<Points> pointPattern = new ArrayList<Points>();
        pointPattern.add(new Points(1));
        pointPattern.add(new Points(2));
        pointPattern.add(new Points(2));

        fakeFloor = new Floor(usedTiles, pointPattern);
        fakePoints = new Points(5);
        fakePatternLines = Arrays.asList(new FakePatternLine(), new FakePatternLine());
        fakeWallLines = Arrays.asList(new FakeWallLine(), new FakeWallLine());

        board = new Board(fakeFloor, fakePoints, fakePatternLines, fakeWallLines);
    }

    @Test
    public void testPut() {
        List<Tile> tiles1 = Arrays.asList(Tile.BLUE, Tile.BLUE);
        board.put(-1, tiles1);
        assertEquals("tiles should go to floor", "BB", fakeFloor.state());
        List<Tile> tiles2 = Arrays.asList(Tile.STARTING_PLAYER);
        board.put(0, tiles2);
        assertEquals("should go to floor", "BBS", fakeFloor.state());
        List<Tile> tiles3 = Arrays.asList(Tile.BLACK, Tile.BLACK);
        board.put(0, tiles3);
        assertEquals("BB", fakePatternLines.get(0).state());
    }

    @Test
    public void testFinishRound() {
        List<Tile> tiles1 = Arrays.asList(Tile.BLACK);
        board.put(0, tiles1);
        List<Tile> tiles2 = Arrays.asList(Tile.BLACK, Tile.BLACK);
        board.put(1, tiles2);
        board.finishRound();
        assertEquals("Points should be 2", new Points(2), board.getPoints());
        List<Tile> tiles3 = Arrays.asList(Tile.GREEN);
        board.put(-1, tiles3);
        board.finishRound();
        assertEquals("After adding one tile to floor, points should go down minus 1", new Points(1), board.getPoints());
    }

    @Test
    public void testEndGame() {
    }

    @Test
    public void testState() {
        List<Tile> tiles1 = Arrays.asList(Tile.RED);
        List<Tile> tiles2 = Arrays.asList(Tile.YELLOW);
        board.put(0, tiles1);
        board.put(1, tiles2);
        String expectedState = """
                Pattern Lines:
                r
                Y.
                Wall Lines:
                ....
                ....
                Floor:
                
                """;
        assertEquals(expectedState, board.state());
    }

}
