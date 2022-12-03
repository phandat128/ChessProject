package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.PawnAttackMove;
import com.chess.engine.board.Move.PawnEnPassantAttackMove;
import com.chess.engine.board.Move.PawnMove;
import com.chess.engine.board.Move.PawnPromotion;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Pawn extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {7, 9, 8, 16}; //tất cả nước đi của quân tốt theo tọa độ

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {//tìm nước đi hợp lệ

        final List<Move> legalMoves = new ArrayList<>();//danh sách các nước có thể đi

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) { //duyệt tất cả nước đi
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset); //tọa độ mới tất cả các trường hợp đi quân
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                continue;
            }
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
            	if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
            		legalMoves.add(new PawnPromotion(new PawnMove(board, this , candidateDestinationCoordinate)));
            	} else {
            		legalMoves.add(new Move.PawnMove(board, this, candidateDestinationCoordinate));
            	}
            } else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                      (BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new Move.PawnJump(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7 &&
                    !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                    BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                    	if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    		legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this , candidateDestinationCoordinate, pieceOnCandidate)));
                    	} else {
                    		legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    	}
                    }
                } else if(board.getEnPassantPawn() != null) {
                	if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                		final Piece pieceOnCandidate = board.getEnPassantPawn();
                		if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                			legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                		}
                	}
                }
            } else if (currentCandidateOffset == 9 &&
                    !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack() ||
                            BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())) {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied()){
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                    	if(this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    		legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this , candidateDestinationCoordinate, pieceOnCandidate)));
                    	} else {
                    		legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    	}
                    }
                }
            }
        }
        // return ImmutableList.copyOf(legalMoves); // **** replaced by a List because i cannot import it in my local
        return legalMoves;
    }
    
    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece() {
    	return new Queen(this.piecePosition, this.pieceAlliance, false);
    }

}
