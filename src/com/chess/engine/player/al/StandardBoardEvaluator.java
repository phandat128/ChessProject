package com.chess.engine.player.al;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

import java.util.Arrays;
import java.util.List;

public class StandardBoardEvaluator implements BoardEvaluator {
     private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 12000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final  Board board,
                        final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(),depth) ;
    }

    private int scorePlayer(final  Board board,
                            final Player player,
                            final int depth) {
        return pieceValue(player) + mobility(player) + 
                check(player)+ checkmate(player, depth) 
                + castled(player) + positionValue(player) + openingPrincipleBonus(player, board);
    }

    private static int castled (Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckmate() ? CHECK_MATE_BONUS * depthBonus(depth):0;
    }

    private static int depthBonus(int depth) {
        return depth ==  0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check( final Player player) {
        return player.getOpponent().isInCheck()? CHECK_BONUS : 0;
    }

    private static int mobility( final Player player) {
        return  player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player){
        int pieceValueScore = 0;
        if (isMidGame(player)) {
            for (final Piece piece: player.getActivePieces()){
                pieceValueScore += piece.getPieceType().mgPieceValue;
            }
        }
        else {
            for (final Piece piece: player.getActivePieces()) {
                pieceValueScore += piece.getPieceType().egPieceValue;
            }
        }
        return pieceValueScore;
    }
    private static int positionValue(final Player player) {
        int positionValueScore = 0;

        if (player.getAlliance() == Alliance.BLACK) {
            if (isMidGame(player)) {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().mgValueTable[piece.getPiecePosition()];
                }
            } else {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().egValueTable[piece.getPiecePosition()];
                }
            }
        }
        else if (player.getAlliance() == Alliance.WHITE){
            if (isMidGame(player)) {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().mgValueTable[(piece.getPiecePosition())^56];
                }
            } else {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().egValueTable[(piece.getPiecePosition())^56];
                }
            }
        }

        return positionValueScore;
    }

    private static int openingPrincipleBonus(Player player, Board board) {
        int openingPrincipleBonus = 0;
        if (Counter.count <= 15) {
            for (final Piece piece: player.getActivePieces()) {
                for (final Move move: piece.calculateLegalMove(board)) {
                    if (move.getDestinationCoordinate() == 28 ||
                        move.getDestinationCoordinate() == 29 ||
                        move.getDestinationCoordinate() == 36 ||
                        move.getDestinationCoordinate() == 37) {
                        openingPrincipleBonus += 3;
                    }
                }
                if (piece.getPieceType() == Piece.PieceType.KING) {
                    if (piece.getPieceAlliance() == Alliance.BLACK) {
                        if (piece.getPiecePosition() != 4 || piece.getPiecePosition() != 6 || piece.getPiecePosition() != 2)
                            openingPrincipleBonus -= 50;
                    } else if (piece.getPieceAlliance() == Alliance.WHITE) {
                        if (piece.getPiecePosition() != 60 || piece.getPiecePosition() != 62 || piece.getPiecePosition() != 58)
                            openingPrincipleBonus -= 50;
                    }
                }
                if (piece.getPieceType() == Piece.PieceType.QUEEN) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() != 59) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() != 3))
                        openingPrincipleBonus -= 30;
                }
                if (piece.getPieceType() == Piece.PieceType.BISHOP) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 58) ||
                        (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 61) ||
                        (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 2) ||
                        (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 5))
                        openingPrincipleBonus -= 10;
                }
                if (piece.getPieceType() == Piece.PieceType.KNIGHT) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 57) ||
                            (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 62) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 1) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 6))
                        openingPrincipleBonus -= 10;
                }
            }
        }
        return openingPrincipleBonus;
    }

    private static boolean isMidGame (Player player) {
        int pieceValueScore = 0;
        for (final Piece piece: player.getActivePieces()) {
            if (piece.getPieceType().equals(Piece.PieceType.PAWN) || piece.getPieceType().equals(Piece.PieceType.KING)) {
                continue;
            }
            if (piece.getPieceType().equals(Piece.PieceType.BISHOP) || piece.getPieceType().equals(Piece.PieceType.KNIGHT))
                pieceValueScore += 1;
            if (piece.getPieceType().equals(Piece.PieceType.ROOK))
                pieceValueScore += 2;
            if (piece.getPieceType().equals(Piece.PieceType.QUEEN))
                pieceValueScore += 4;
        }
        for (final Piece piece: player.getOpponent().getActivePieces()) {
            if (piece.getPieceType().equals(Piece.PieceType.PAWN) || piece.getPieceType().equals(Piece.PieceType.KING)) {
                continue;
            }
            if (piece.getPieceType().equals(Piece.PieceType.BISHOP) || piece.getPieceType().equals(Piece.PieceType.KNIGHT))
                pieceValueScore += 1;
            if (piece.getPieceType().equals(Piece.PieceType.ROOK))
                pieceValueScore += 2;
            if (piece.getPieceType().equals(Piece.PieceType.QUEEN))
                pieceValueScore += 4;
        }
        return (pieceValueScore >= 10);

    }
}

