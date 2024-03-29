package com.chess.engine.board;

import com.chess.engine.pieces.*;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.player.al.StandardBoardEvaluator;
import com.chess.gui.PromoteFrame;

public abstract class Move {

    protected final Board board;
    protected final Piece movedPiece;//quân cờ được di chuyển
    protected final int destinationCoordinate;//tọa độ đích
    protected final boolean isFirstMove;
    protected int priorityOrder;

    private static final int ATTACK_MOVE_PRIORITY = 1000;
    private static final int DEPTH_PRE_CALCULATE = 2;

    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
        this.priorityOrder = 0;
//        this.priorityOrder = new StandardBoardEvaluator().evaluate(board, DEPTH_PRE_CALCULATE);
    }

    private Move(final Board board, final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
        this.priorityOrder = -1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.movedPiece.getPiecePosition();
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.destinationCoordinate;
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof final Move otherMove)) {
            return false;
        }
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
               getMovedPiece().equals(otherMove.getMovedPiece());
    }
    
    public Board getBoard() {
    	return this.board;
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
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

    public int getPriorityOrder() {
        return priorityOrder;
    }

    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
            if(!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static class MajorAttackMove extends AttackMove {
    	public MajorAttackMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
                               final Piece pieceAttacked) {
    		super(board, pieceMoved, destinationCoordinate, pieceAttacked);
    	}
    	
    	@Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString () {
            return movedPiece.getPieceType() + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    
    public static final class MajorMove extends Move { //nước đi thông thường
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString () {
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static class AttackMove extends Move { //nước đi tấn công
        final Piece attackedPiece;//quần cờ bị tấn công
        public AttackMove(final Board board, final Piece movedPiece,final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
            this.priorityOrder += ATTACK_MOVE_PRIORITY;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof final AttackMove otherAttackMove)) {
                return false;
            }
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

//        @Override
//        public Board execute() {
//            return null;
//        }
        
        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnMove extends Move {
        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        
        @Override
        public boolean equals(final Object other) {
        	return this == other || other instanceof PawnMove && super.equals(other);
        }
        
        @Override
        public String toString() {
        	return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static  class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        
        @Override
        public boolean equals(final Object other) {
        	return this == other || other instanceof PawnAttackMove && super.equals(other);
        }
        
        @Override
        public String toString() {
        	return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0) + "x" +
        			BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttackMove extends AttackMove {
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                       final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }
        
        @Override
        public Board execute() {
        	final Builder builder = new Builder();
        	for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
        		if(!this.movedPiece.equals(piece)) {
        			builder.setPiece(piece);
        		}
        	}
        	for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
        		if(!piece.equals(this.getAttackedPiece())) {
        			builder.setPiece(piece);
        		}
        	}
        	builder.setPiece(this.movedPiece.movePiece(this));
        	builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        	return builder.build();
        }
        
        @Override
        public String toString () {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    
    public static class PawnPromotion extends Move {
    	
    	final Move decoratedMove;
    	final Pawn promotedPawn;
        final Piece promotionPiece;
    	
    	public PawnPromotion(final Move decoratedMove, final Piece promotionPiece) {
    		super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
    		this.decoratedMove = decoratedMove;
    		this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
            this.promotionPiece = promotionPiece;
    	}
    	
    	@Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }
    	
    	@Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }
    	
    	@Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
            	if(!this.promotedPawn.equals(piece)) {
            		builder.setPiece(piece);
            	}
            }
            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
            	builder.setPiece(piece);
            }
            //////TODO MORE
            builder.setPiece(promotionPiece);
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

    	
    	@Override
    	public boolean isAttack()
    	{
    		return this.decoratedMove.isAttack();
    	}
    	
    	@Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }
    	
    	@Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate) + "=" + promotionPiece.toString();
        }
        public Move getDecoratedMove(){
            return this.decoratedMove;
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook =castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }
        public Rook getCastleRook() {
            return this.castleRook;
        }
        @Override
        public boolean isCastlingMove() {
            return true;
        }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        @Override
        public int hashCode() {
        	final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.destinationCoordinate;
            return result;
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof final CastleMove otherCastleMove)) {
                return false;
            }
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
        @Override
        public String toString () {
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        
        @Override
        public boolean equals(final Object other) {
        	return this == other || other instanceof KingSideCastleMove & super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                   final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        
        @Override
        public boolean equals(final Object other) {
        	return this == other || other instanceof QueenSideCastleMove & super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, -1);
        }
        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        @Override
        public int getDestinationCoordinate() {
            return -1;
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute the null move!");
        }
    }

    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
        	for(final Move move : board.getAllLegalMoves()) {
                if(move.getCurrentCoordinate() == currentCoordinate && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return new NullMove();
        }

    }
}

    
    