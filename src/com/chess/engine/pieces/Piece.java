package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final int piecePosition; //vị trí quân cờ hiện tại
    protected final Alliance pieceAlliance; //màu của quân cờ
    protected final boolean isFirstMove;
    protected final PieceType pieceType;

    protected final int cachedHashCode;

    Piece (final PieceType pieceType,  final int piecePosition, final Alliance pieceAlliance){
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = false;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31*result + pieceAlliance.hashCode();
        result = 31*result + piecePosition;
        result = 31*result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if (this == other) {
            return true;
        }
        if(!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
                pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMove(final Board board);

    public abstract Piece movePiece(Move move);

    public int getPiecePosition() { return this.piecePosition;}

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }



    public enum PieceType {

        PAWN("P"){
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT("K") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }
        };
        private String pieceName;
        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
    }
}
