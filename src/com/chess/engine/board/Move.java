package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;

public abstract class Move {

    final Board board;
    final Piece movedPiece;//quân cờ được di chuyển
    final int destinationCoordinate;//tọa độ đích

    public static final Move NULL_MOVE = new NullMove();
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31*result + this.movedPiece.getPiecePosition();
        result = 31*result + this.movedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals( final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getmovedPiece().equals(otherMove.getmovedPiece());
    }

    private int getCurrentCoordinate() {
        return this.getmovedPiece().getPiecePosition();
    }
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getmovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack() {
        return false;
    }
    public boolean isCastlingMove() {
        return false;
    }
    public Piece getAttackedPiece() {
        return null;
    }

    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        for(final Piece piece : this.board.currentPlayer().getActivePiece()) {
            if(!this.movedPiece.equals(piece)){
                builder.setPiece(piece);
            }
        }
        for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePiece()) {
            builder.setPiece(piece);//xay dung tat ca quan co khac tren ban co moi
        }
        builder.setPiece(this.movedPiece.movePiece(this)); //xay dung quan co duoc di chuyen
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance()); //chuyen quyen di quan cho doi phuong
        return builder.build();
    }
    public static final class MajorMove extends Move { //nước đi thông thường
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class AttackMove extends Move { //nước đi tấn công
        final Piece attackedPiece;//quần cờ bị tấn công
        public AttackMove(final Board board, final Piece movedPiece,final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return getDestinationCoordinate() == otherAttackMove.getDestinationCoordinate() &&
                    getmovedPiece().equals(otherAttackMove.getmovedPiece());
        }
        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }


    public static final class PawnMove extends Move { //nước đi thông thường của tốt
        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static class PawnAttackMove extends AttackMove { //nước đi tấn công của tốt
        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }
    public static final class PawnEnPassantAttackMove extends PawnAttackMove { //nước đi en passant của tốt
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }
    public static final class PawnJump extends Move { //nước đi 2 bước của tốt
        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePiece()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePiece()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }


    static abstract class CastleMove extends Move { //nước đi nhập thành
        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static final class KingSideCastleMove extends CastleMove { //nước đi 2 bước của tốt
        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    public static final class QueenSideCastleMove extends CastleMove { //nước đi 2 bước của tốt
        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }


    public static final class NullMove extends Move { //nước đi 2 bước của tốt
        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Không thể thực hiện nước rỗng!");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Không thể khởi tạo!");
        }
        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
            for(final Move move: board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.destinationCoordinate == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }




}

