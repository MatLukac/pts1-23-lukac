package sk.uniba.fmph.dcs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.util.*;

import interfaces.FinalPointsCalculationInterface;
import interfaces.PatternLineInterface;
import interfaces.UsedTilesGiveInterface;
import interfaces.UsedTilesTakeInterface;
import org.junit.Before;
import org.junit.Test;


class FakeUsedTilesGive implements UsedTilesGiveInterface {
    public ArrayList<Tile> tiles;

    public FakeUsedTilesGive() {
        this.tiles = new ArrayList();
    }

    @Override
    public void give(Collection<Tile> tiles) {
        this.tiles.addAll(tiles);
    }
}

public class BoardIntegrationTest {
    private Board board;
    private FinalPointsCalculation finalPointsCalculation;
    private GameFinished gameFinished;
    private ArrayList<WallLine> wallLines;
    private ArrayList<PatternLine> patternLines;
    private Floor floor;
    private FakeUsedTilesGive usedTiles;

    @Before
    public void setUp() {
        finalPointsCalculation = new FinalPointsCalculation();
        gameFinished = new GameFinished();
        usedTiles = new FakeUsedTilesGive();

        ArrayList<Points> pointPattern = new ArrayList<>(List.of(new Points(-1), new Points(-1), new Points(-2), new Points(-2), new Points(-2), new Points(-3), new Points(-3)));
        floor = new Floor(usedTiles, pointPattern);

        wallLines = new ArrayList(5);
        wallLines.add(new WallLine(List.of(Tile.BLUE, Tile.YELLOW, Tile.RED, Tile.BLACK, Tile.GREEN), null, null));
        wallLines.add(new WallLine(List.of(Tile.GREEN, Tile.BLUE, Tile.YELLOW, Tile.RED, Tile.BLACK), null, null));
        wallLines.add(new WallLine(List.of(Tile.BLACK, Tile.GREEN, Tile.BLUE, Tile.YELLOW, Tile.RED), null, null));
        wallLines.add(new WallLine(List.of(Tile.RED, Tile.BLACK, Tile.GREEN, Tile.BLUE, Tile.YELLOW), null, null));
        wallLines.add(new WallLine(List.of(Tile.YELLOW, Tile.RED, Tile.BLACK, Tile.GREEN, Tile.BLUE), null, null));

        wallLines.get(0).setLineDown(wallLines.get(1));
        wallLines.get(1).setLineUp(wallLines.get(0));
        wallLines.get(1).setLineDown(wallLines.get(2));
        wallLines.get(2).setLineUp(wallLines.get(1));
        wallLines.get(2).setLineDown(wallLines.get(3));
        wallLines.get(3).setLineUp(wallLines.get(2));
        wallLines.get(3).setLineDown(wallLines.get(4));
        wallLines.get(4).setLineUp(wallLines.get(3));

        patternLines = new ArrayList(5);
        patternLines.add(new PatternLine(1, wallLines.get(0), floor, usedTiles));
        patternLines.add(new PatternLine(2, wallLines.get(1), floor, usedTiles));
        patternLines.add(new PatternLine(3, wallLines.get(2), floor, usedTiles));
        patternLines.add(new PatternLine(4, wallLines.get(3), floor, usedTiles));
        patternLines.add(new PatternLine(5, wallLines.get(4), floor, usedTiles));

        board = new Board(floor, new ArrayList(List.of(new Points(1))), new ArrayList(patternLines), new ArrayList(wallLines), finalPointsCalculation, gameFinished);
    }

    @Test
    public void boardIntegrationTest() {
        for (int i = 0; i < 0; i++) {
            assertEquals("All WallLines should be empty when created.", "_".repeat(i), wallLines.get(i).state());
        }
        for (PatternLine patternLine : patternLines) {
            assertEquals("All PatternLines should be empty when created.", "", patternLine.state());
        }

        assertEquals("Floor should be empty when created.", "", floor.state());

        board.put(0, new ArrayList(List.of(Tile.RED)));
        board.put(1, new ArrayList(List.of(Tile.RED, Tile.RED)));
        board.put(2, new ArrayList(List.of(Tile.RED, Tile.RED, Tile.RED)));
        board.put(3, new ArrayList(List.of(Tile.RED, Tile.RED, Tile.RED, Tile.RED, Tile.RED)));
        board.put(4, new ArrayList(List.of(Tile.RED, Tile.RED, Tile.RED)));

        assertEquals("First PatternLine should contain 'R'.", "R", patternLines.get(0).state());
        assertEquals("Second PatternLine should contain 'RR'.", "RR", patternLines.get(1).state());
        assertEquals("Third PatternLine should contain 'RRR'.", "RRR", patternLines.get(2).state());
        assertEquals("Fourth PatternLine should contain 'RRRR'.", "RRRR", patternLines.get(3).state());
        assertEquals("Fifth PatternLine should contain 'RRR'.", "RRR", patternLines.get(4).state());
        assertEquals("Floor now should contain 'RR'.", "R", floor.state());

        assertEquals("There is no finish row, so finishRound should yield FinishRoundResult.NORMAL.", FinishRoundResult.NORMAL, board.finishRound());
        //System.out.println(wallLines.get(4).state());
        assertEquals("After board.finishRound(), first wall should be '__R__'.", "__R__", wallLines.get(0).state());
        assertEquals("After board.finishRound(), second wall should be '___R_'.", "___R_", wallLines.get(1).state());
        assertEquals("After board.finishRound(), third wall should be '____R'.", "____R", wallLines.get(2).state());
        assertEquals("After board.finishRound(), fourth wall should be 'R____'.", "R____", wallLines.get(3).state());
        assertEquals("After board.finishRound(), fifth wall should be '_____'.", "_____", wallLines.get(4).state());

    }
}
