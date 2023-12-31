package sk.uniba.fmph.dcs;

import interfaces.BagInterface;
import interfaces.TableCenterAddInterface;
import interfaces.TileSource;

import java.util.ArrayList;

public final class Factory implements TileSource {
    private static final int MAX_NUMBER_OF_TILES = 4;
    private final BagInterface bag;
    private final TableCenterAddInterface tableCenter;
    private ArrayList<Tile> tiles;

    public Factory(final BagInterface bag, final TableCenterAddInterface tableCenter) {
        this.bag = bag;
        this.tableCenter = tableCenter;
        tiles = new ArrayList<>();
    }

    @Override
    public ArrayList<Tile> take(final int idx) {
        ArrayList<Tile> toReturn = new ArrayList();
        ArrayList<Tile> toTableCenter = new ArrayList<>();
        if (idx < 0 || idx >= tiles.size()) {
            throw new IllegalArgumentException("index not in tiles[]");
        }
        Tile chosenTile = tiles.get(idx);
        for (Tile tile : tiles) {
            if (!tile.equals(chosenTile)) {
                toTableCenter.add(tile);
            } else {
                toReturn.add(tile);
            }
        }
        tiles.removeAll(tiles);
        tableCenter.add(toTableCenter);
        return toReturn;
    }

    @Override
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    @Override
    public void startNewRound() {
        tiles.addAll(bag.take(MAX_NUMBER_OF_TILES));
    }

    @Override
    public String state() {
        String toReturn = "";
        for (final Tile tile : tiles) {
            toReturn += tile.toString();
        }
        return toReturn;
    }
}
