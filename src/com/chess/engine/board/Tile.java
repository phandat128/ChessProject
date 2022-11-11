package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final int tileCoordinate; //the position of tile in the board (from 0 to 63)

    protected Tile(final int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTile();

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTile(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return emptyTileMap;
    }

    public static Tile createTile(int tileCoordinate, Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();
}
