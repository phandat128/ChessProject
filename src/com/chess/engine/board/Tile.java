package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final int tileCoordinate; //tọa độ ô: đánh số các ô bàn cờ (0-63) theo thứ tự trái sang phải, trên xuống dưới

    protected Tile(final int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    } //khởi tạo các ô

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTile(); //ánh xạ gồm 1 ô trống và tọa độ ô đó

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTile(){
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>(); //khởi tạo ánh xạ

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        } //đánh số ô trống từ 0-63
        return emptyTileMap;
    }

    public static Tile createTile(int tileCoordinate, Piece piece){
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    } //nếu có quân cờ thì khởi tạo tọa độ vào các ô bị chiếm, ngược lại đưa tọa độ vào map cache các ô trống

    public abstract boolean isTileOccupied(); //kiểm tra ô có chứa quân không

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile {
        private EmptyTile(final int coordinate) {
            super(coordinate);
        }
        @Override
        public boolean isTileOccupied() {
            return false;
        }
        @Override
        public Piece getPiece() {
            return null;
        }

        public String toString(){
            return "-";
        }
    }

    public static final class OccupiedTile extends Tile {
        private final Piece pieceOnTile;

        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }

        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }
    }
}
