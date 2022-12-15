package com.chess.engine.player.al;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

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
                + castled(player) + positionValue(player);
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
        for (final Piece piece: player.getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }
    private static int positionValue(final Player player) {
        int positionValueScore = 0;
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
        if (player.getAlliance() == Alliance.BLACK) {
            if (pieceValueScore >= 10) {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().mgValueTable[piece.getPiecePosition()];
                }
            } else {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().egValueTable[piece.getPiecePosition()];
                }
            }
//            else {
//                System.out.println("Undefined!");
//                float mg = ((float) pieceValueScore - 518)/5674;
//                float eg = 1 - mg;
//                for (final Piece piece: player.getActivePieces()) {
//                    positionValueScore += Math.round(piece.getPieceType().mgValueTable[piece.getPiecePosition()] * mg + piece.getPieceType().egValueTable[piece.getPiecePosition()] * eg);
//                }
//
//            }
        }
        else {
            if (pieceValueScore >= 10) {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().mgValueTable[flipPosition(piece.getPiecePosition())];
                }
            } else {
                for (final Piece piece: player.getActivePieces()) {
                    positionValueScore += piece.getPieceType().egValueTable[flipPosition(piece.getPiecePosition())];
                }
            }
//            else {
//                System.out.println("Undefined!");
//                float mg = ((float) pieceValueScore - 518)/5674;
//                float eg = 1 - mg;
//                for (final Piece piece: player.getActivePieces()) {
//                    positionValueScore += Math.round(piece.getPieceType().mgValueTable[flipPosition(piece.getPiecePosition())] * mg + piece.getPieceType().egValueTable[flipPosition(piece.getPiecePosition())]);
//                }
//
//            }
        }

        return positionValueScore;
    }
    private static int flipPosition(final int pos) {
        int flipPos = 0;
        if (pos % 8 != 0)   flipPos = pos^56;
        else if (pos == 8)  flipPos = 64;
        else if (pos == 16) flipPos = 56;
        else if (pos == 24) flipPos = 48;
        else if (pos == 32) flipPos = 40;
        else if (pos == 40) flipPos = 32;
        else if (pos == 48) flipPos = 24;
        else if (pos == 56) flipPos = 16;
        else if (pos == 64) flipPos = 8;
        return flipPos;
    }
    

}
