package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;


import java.util.*;
import java.util.stream.Stream;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> blackPieces;
    private final Collection<Piece> whitePieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.blackPieces = calculateActivePiece(this.gameBoard, Alliance.BLACK);
        this.whitePieces = calculateActivePiece(this.gameBoard, Alliance.WHITE);

        final Collection<Move> whiteStandardLegalMove = calculateLegalMove(this.whitePieces);
        final Collection<Move> blackStandardLegalMove = calculateLegalMove(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMove, blackStandardLegalMove);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMove, whiteStandardLegalMove);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for (int i=0; i<BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ((i+1) %  BoardUtils.NUM_TILES_PER_ROW == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player whitePlayer() { return this.whitePlayer; }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player currentPlayer() { return this.currentPlayer; }

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMove(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMove(this));
        }
        return legalMoves;
    }

    private static Collection<Piece> calculateActivePiece(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for(final Tile tile : gameBoard) {
            if (tile.isTileOccupied()){
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }
        return activePieces;
    }
    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    private static List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i=0; i<BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
     return Arrays.asList(tiles);
    }

    public static Board createStandardBoard(){ //thiết lập vị trí quân ban đầu
        final Builder builder = new Builder();
        //Bên đen
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));
        //Bên trắng
        builder.setPiece(new Rook(63, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Queen(60, Alliance.WHITE));
        builder.setPiece(new King(59, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        //Lượt trắng đi trước
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public Iterable<Move> getAllLegalMoves() {
        return Stream.concat(this.whitePlayer.getLegalMoves().stream(),
                            this.blackPlayer.getLegalMoves().stream()).toList();
    }
    public static class Builder {
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
