package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public final class EmptyTile extends Tile{

    EmptyTile(int tileCoordinate){
        super(tileCoordinate);
    }

    @Override
    public boolean isTileOccupied(){
        return false;
    }

    @Override
    public Piece getPiece(){
        return null;
    }
}
