package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;

    private final boolean isInCheck;

    Player(final Board board,final Collection<Move> myLegalMoves, final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = establishKing();
        // this.legalMoves = Iterables.concat(myLegalMoves,calculateKingCastles(myLegalMoves, opponentMoves));
        this.legalMoves = myLegalMoves;
        this.legalMoves.addAll(calculateKingCastles(myLegalMoves, opponentMoves));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> opponentMoves) {
    //returns a collection of opponent's moves which attack the piece at {piecePosition}
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move: opponentMoves){
            if (piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }

        return attackMoves;
    }

    private King establishKing() {
        for (final Piece piece: getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board!");
    }

    public King getPlayerKing() {
        return playerKing;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckmate(){
        return this.isInCheck && !hasEscapeMove();
    }

    public boolean isInStalemate(){
        return !this.isInCheck && !hasEscapeMove();
    }
    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }
    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }
    private boolean hasEscapeMove() {
        for (final Move move: this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    //TODO: implement these below methods


    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        // if this move is illegal, this transition has an illegal_move status
        if (!isMoveLegal(move)){
            return new MoveTransition(this.board, move,MoveStatus.ILLEGAL_MOVE);
        }

        //else declare a transition board which is a new board after move
        final Board transitionBoard = move.execute();
        //list the moves lead current player's king to be attacked
        final Collection<Move> kingAttacks
                = calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());
        //if there is any move, return status LEAVES_PLAYER_IN_CHECK
        if (!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        //nothing wrong happened then makeMove
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }



    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> OpponentLegals);
}
