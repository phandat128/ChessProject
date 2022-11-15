package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{
    private final static int[] CANDIDATE_MOVE_C0ORDINATE ={8};
    Pawn(int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidate0ffset : CANDIDATE_MOVE_C0ORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidate0ffset);
            // kiem tra xem co hop le hay khong
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            if (currentCandidate0ffset == 8 && board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
            } else if (currentCandidate0ffset == 16 && this.isFirstMove() &&
                    (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidate0ffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece piece0nCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != piece0nCandidate.getPieceAlliance()) {
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }
            } else if (currentCandidate0ffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                            (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece piece0nCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != piece0nCandidate.getPieceAlliance()) {
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }
            }
        }
        return null;
    }}
