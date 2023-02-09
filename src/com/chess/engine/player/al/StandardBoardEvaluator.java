package com.chess.engine.player.al;
import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

import java.util.*;


public class StandardBoardEvaluator implements BoardEvaluator {
    private static final int UNSAFE_CHECK_BONUS = 100;
    private static final int CHECK_MATE_BONUS = 120000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 500;
    private static final int PIECE_WEIGHT = 3;
    private static final int MOBILITY_WEIGHT = 3;
    private static final int PAWN_STRUCTURE_WEIGHT = 3;
    private static final int ENDING_PRINCIPLE_WEIGHT = 2;

    @Override
    public int evaluate(final  Board board,
                        final int depth) {
        //        System.out.println("Evaluate: " + evaluate);
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
    }

    private int scorePlayer(final  Board board,
                            final Player player,
                            final int depth) {
        int score = pieceValue(player, board) * PIECE_WEIGHT + mobility(player, board) * MOBILITY_WEIGHT +
                check(player, board) + checkmate(player, depth)
                + castled(player) + positionValue(player, board) + openingPrincipleBonus(player) +
                pawnStructureBonus(player, board) * PAWN_STRUCTURE_WEIGHT + endingPrincipleBonus(player, board) *  ENDING_PRINCIPLE_WEIGHT
                + triviaBonus(player, board);
//        System.out.println("White player: ");
//        System.out.println("pieceValue: " + pieceValue(board.whitePlayer(), board) + ", mobility: " + mobility(board.whitePlayer(),board) * 3
//        + ", positionValue: " + positionValue(board.whitePlayer(), board) + ", OPV: " + openingPrincipleBonus(board.whitePlayer())
//        + ", PSV: " + pawnStructureBonus(board.whitePlayer(), board) + ", castle: " + castled(board.whitePlayer()));
//        System.out.println("Black player: ");
//        System.out.println("pieceValue: " + pieceValue(board.blackPlayer(), board) + ", mobility: " + mobility(board.blackPlayer(),board) * 3
//                + ", positionValue: " + positionValue(board.blackPlayer(), board) + ", OPV: " + openingPrincipleBonus(board.blackPlayer())
//                + ", PSV: " + pawnStructureBonus(board.blackPlayer(), board) + ", castle: " + castled(board.blackPlayer()));
//        System.out.println("Total: " + score);
//        System.out.println();
        return score;
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
    private static int check( final Player player, Board board) {
        int checkBonus = 0;
        if (player.getOpponent().isInCheck()) {
            for (Piece piece: player.getActivePieces()) {
                for (Move move: piece.calculateLegalMove(board)) {
                    if (move.getDestinationCoordinate() == player.getOpponent().getPlayerKing().getPiecePosition()) {
                        for (Move move1: player.getOpponent().getLegalMoves()) {
                            if (move1.getDestinationCoordinate() == piece.getPiecePosition()) { //Unsafe check
                                checkBonus += UNSAFE_CHECK_BONUS;
                                break;
                            }
                        }
                        //Safe check
                        if (piece.getPieceType() == Piece.PieceType.KNIGHT) checkBonus += 800;
                        else if (piece.getPieceType() == Piece.PieceType.BISHOP) checkBonus += 650;
                        else if (piece.getPieceType() == Piece.PieceType.ROOK) checkBonus += 1070;
                        else if (piece.getPieceType() == Piece.PieceType.QUEEN) checkBonus += 730;
                    }
                }
            }
        }
        return  checkBonus;
    }

    private static int mobility( final Player player, final Board board) {
        int mobilityBonus = 0;
        int numMoves;
        int[] knightMobilMG = {-20, -16, -5, -1, 1, 3, 7, 10, 12};
        int[] knightMobilEG = {-23, -17, -10, -5, 2, 3, 5, 7, 9};
        int[] bishopMobilMG = {-15, -5, 4, 8, 11, 15, 15, 17, 18, 20, 22, 23, 25, 27};
        int[] bishopMobilEG = {-20, -10, -5, 5, 7, 13, 18, 18, 20, 23, 25, 27, 30, 32};
        int[] rookMobilMG = {-20, -10, 0, 1, 1, 5, 7, 10, 13, 13, 13, 15, 20, 20, 23};
        int[] rookMobilEG = {-30, -10, 5, 15, 25, 33, 33, 40, 43, 45, 50, 53, 55, 57, 60};
        int[] queenMobilMG = {-10, -5, -3, -3, 8, 10, 10, 10, 12, 13, 26, 18, 20, 20, 20, 20, 20, 20, 20, 25, 25, 30, 33, 35, 35, 35, 35, 38};
        int[] queenMobilEG = {-17, -10, -5, 5, 13, 18, 20, 25, 25, 30, 30, 33, 40, 43, 45, 45, 45, 45, 47, 50, 50, 50, 55, 55, 57, 60, 60, 73};
        for (Piece piece: player.getActivePieces()) {
            numMoves = piece.calculateLegalMove(board).size();
            Piece.PieceType movePieceType = piece.getPieceType();
            if (isMidGame(board)) {
                if (movePieceType == Piece.PieceType.PAWN) {
                    mobilityBonus += numMoves;
                }
                else if (movePieceType == Piece.PieceType.KNIGHT) {
                    mobilityBonus += knightMobilMG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.BISHOP) {
                    mobilityBonus += bishopMobilMG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.ROOK) {
                    mobilityBonus += rookMobilMG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.QUEEN) {
                    mobilityBonus += queenMobilMG[numMoves];
                }
            }
            else {
                if (movePieceType == Piece.PieceType.PAWN) {
                    mobilityBonus += numMoves;
                }
                else if (movePieceType == Piece.PieceType.KNIGHT) {
                    mobilityBonus += knightMobilEG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.BISHOP) {
                    mobilityBonus += bishopMobilEG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.ROOK) {
                    mobilityBonus += rookMobilEG[numMoves];
                }
                else if (movePieceType == Piece.PieceType.QUEEN) {
                    mobilityBonus += queenMobilEG[numMoves];
                }
            }
        }
        return mobilityBonus;
    }

    private static int pieceValue(final Player player, final Board board){
        int pieceValueScore = 0;
        int numBishop = 0;
        for (final Piece piece: player.getActivePieces()) {

            if (piece.getPieceType() != Piece.PieceType.KING) {
                if (isMidGame(board)) {
                    pieceValueScore += piece.getPieceType().mgPieceValue;
                }
                else {
                    pieceValueScore += piece.getPieceType().egPieceValue;
                }
            }
            if (piece.getPieceType() == Piece.PieceType.BISHOP) {
                numBishop++;
            }
        }
//        if (isOpenGame()) {
//            pieceValueScore = pieceValueScore * 3 / 2;
//        }
        if (numBishop == 2) {
            if (isMidGame(board))   pieceValueScore += 150;
            else                    pieceValueScore += 300;
        }
        return pieceValueScore;
    }
    private static int positionValue(final Player player, final Board board) {
        int positionValueScore = 0;
        for (final Piece piece: player.getActivePieces()) {
            if (player.getAlliance() == Alliance.BLACK) {
                if (isMidGame(board)) {
                    positionValueScore += piece.getPieceType().mgValueTable[piece.getPiecePosition()];
                } else {
                    positionValueScore += piece.getPieceType().egValueTable[piece.getPiecePosition()];
                }
            } else if (player.getAlliance() == Alliance.WHITE) {
                if (isMidGame(board)) {
                    positionValueScore += piece.getPieceType().mgValueTable[piece.getPiecePosition() ^ 56];
                } else {
                    positionValueScore += piece.getPieceType().egValueTable[piece.getPiecePosition() ^ 56];
                }
            }
        }
        return positionValueScore;
    }
    private static boolean isOpenGame () {
        return (Counter.count <= 10);
    }
    public static boolean isMidGame(Board board) {
        int pieceValueScore = 0;
        for (final Piece piece: board.getBlackPieces()) {
            if (piece.getPieceType().equals(Piece.PieceType.BISHOP) || piece.getPieceType().equals(Piece.PieceType.KNIGHT))
                pieceValueScore += 1;
            else if (piece.getPieceType().equals(Piece.PieceType.ROOK))
                pieceValueScore += 2;
            else if (piece.getPieceType().equals(Piece.PieceType.QUEEN))
                pieceValueScore += 4;
        }
        for (final Piece piece: board.getWhitePieces()) {
            if (piece.getPieceType().equals(Piece.PieceType.BISHOP) || piece.getPieceType().equals(Piece.PieceType.KNIGHT))
                pieceValueScore += 1;
            else if (piece.getPieceType().equals(Piece.PieceType.ROOK))
                pieceValueScore += 2;
            else if (piece.getPieceType().equals(Piece.PieceType.QUEEN))
                pieceValueScore += 4;
        }
        return (pieceValueScore >= 9);
    }

    private static int openingPrincipleBonus(Player player) {
        if (isOpenGame()) {
            List<Integer> pawnCols = new ArrayList<>();
            int openingPrincipleBonus = 0;
            for (final Piece piece: player.getActivePieces()) {
//                for (final Move move: piece.calculateLegalMove(board)) {
//                    if (move.getDestinationCoordinate() == 28 ||
//                        move.getDestinationCoordinate() == 29 ||
//                        move.getDestinationCoordinate() == 36 ||
//                        move.getDestinationCoordinate() == 37) {
//                        openingPrincipleBonus += 2;
//                    }
//                }
                if ((piece.getPieceType() == Piece.PieceType.KING && !player.isInCheck()) && piece.isFirstMove()) {
                    if (piece.getPieceAlliance() == Alliance.BLACK) {
                        if (piece.getPiecePosition() != 4 && !player.isCastled())
                            openingPrincipleBonus -= 500;
                    } else if (piece.getPieceAlliance() == Alliance.WHITE) {
                        if (piece.getPiecePosition() != 60 && !player.isCastled())
                            openingPrincipleBonus -= 500;
                    }
                }
                else if (piece.getPieceType() == Piece.PieceType.ROOK && piece.isFirstMove()) {
                    if (piece.getPieceAlliance() == Alliance.BLACK) {
                        if ((piece.getPiecePosition() != 0 && piece.getPiecePosition() != 7) && !player.isCastled())
                            openingPrincipleBonus -= 250;
                    } else if (piece.getPieceAlliance() == Alliance.WHITE) {
                        if ((piece.getPiecePosition() != 56 && piece.getPiecePosition() != 63) && !player.isCastled())
                            openingPrincipleBonus -= 250;
                    }
                }
                else if (piece.getPieceType() == Piece.PieceType.QUEEN) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() != 59) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() != 3))
                        openingPrincipleBonus -= 200;
                }
                else if (piece.getPieceType() == Piece.PieceType.BISHOP) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 58) ||
                        (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 61) ||
                        (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 2) ||
                        (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 5))
                        openingPrincipleBonus -= 200;
                }
                else if (piece.getPieceType() == Piece.PieceType.KNIGHT) {
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 57) ||
                            (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 62) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 1) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 6))
                        openingPrincipleBonus -= 30;
                }
                else if (piece.getPieceType() == Piece.PieceType.PAWN) {
                    pawnCols.add(piece.getPiecePosition() % 8);
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 51) ||
                            (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 52)) {
//                        System.out.println("White Pawn penalty!");
                        openingPrincipleBonus -= 50;
                    }
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 19) ||
                            (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 20)) {
//                        System.out.println("White Pawn penalty!");
                        openingPrincipleBonus += 50;
                    }
                    if ((piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 27) ||
                            (piece.getPieceAlliance() == Alliance.WHITE && piece.getPiecePosition() == 28)) {
//                        System.out.println("White Pawn penalty!");
                        openingPrincipleBonus += 25;
                    }
                    else if ((piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 11) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 12)) {
//                        System.out.println("Black Pawn penalty!");
                        openingPrincipleBonus -= 50;
                    }
                    if ((piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 43) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 44)) {
//                        System.out.println("Black Pawn penalty!");
                        openingPrincipleBonus += 50;
                    }
                    if ((piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 35) ||
                            (piece.getPieceAlliance() == Alliance.BLACK && piece.getPiecePosition() == 36)) {
//                        System.out.println("Black Pawn penalty!");
                        openingPrincipleBonus += 25;
                    }
                }
            }
            if (!pawnCols.contains(3))   openingPrincipleBonus -= 100;
            if (!pawnCols.contains(4))   openingPrincipleBonus -= 100;
            return openingPrincipleBonus;
        }
        return 0;
    }
    private static int endingPrincipleBonus(Player player, Board board) {
        if (!isMidGame(board)) {
            int endingPrincipleBonus = 0;
            int bishopC = 0, knightC = 0, rookC = 0, queenC = 0, bishopO = 0, knightO = 0, rookO = 0, queenO = 0;
            for(Piece piece: player.getActivePieces()) {
                if (piece.getPieceType() == Piece.PieceType.BISHOP) bishopC++;
                if (piece.getPieceType() == Piece.PieceType.KNIGHT) knightC++;
                if (piece.getPieceType() == Piece.PieceType.ROOK) rookC++;
                if (piece.getPieceType() == Piece.PieceType.QUEEN) queenC++;
            }
            for(Piece piece: player.getOpponent().getActivePieces()) {
                if (piece.getPieceType() == Piece.PieceType.BISHOP) bishopO++;
                if (piece.getPieceType() == Piece.PieceType.KNIGHT) knightO++;
                if (piece.getPieceType() == Piece.PieceType.ROOK) rookO++;
                if (piece.getPieceType() == Piece.PieceType.QUEEN) queenO++;
            }

            if (bishopO + knightO + rookO + queenO == 0 && bishopC + knightC + rookC + queenC >= 1) { //KX vs lone K
                int rdO = Math.abs((player.getOpponent().getPlayerKing().getPiecePosition()+4)/8); //rank distance of Opponent (to edge)
                int fdO = Math.abs((player.getOpponent().getPlayerKing().getPiecePosition()+4)%8); // file distance of Opponent (to edge)
                endingPrincipleBonus += 90 - (7 * fdO * fdO / 2 + 7 * rdO * rdO / 2); //push opponent's K towards edge
                int rdC = Math.abs((player.getPlayerKing().getPiecePosition()+4)/8); //rank distance of Current player (to edge)
                int fdC = Math.abs((player.getPlayerKing().getPiecePosition()+4)%8); //file distance of Current player (to edge)
                endingPrincipleBonus += 140 - 20 * ((rdO - rdC) * (rdO - rdC) + (fdO - fdC) * (fdO - fdC)); // keeps 2 Ks close
            }
            return endingPrincipleBonus;
        }
        return 0;
    }
    private static int pawnStructureBonus(Player player, Board board) {
        if (!isOpenGame()) {
            Collection<Piece> ourPieces = player.getActivePieces();
            Collection<Piece> theirPieces = player.getOpponent().getActivePieces();
            int psBonus = 0, numPawn = 0;
            int[] pawnPosList = new int[8];
            int[] pawnPosCol = new int[8];
            int[] passedPawnMG = {5, 15, 25, 65, 165, 285};
            int[] passedPawnEG = {40, 55, 75, 115, Piece.PieceType.PAWN.egPieceValue, Piece.PieceType.PAWN.egPieceValue * 2};
            for (Piece piece : ourPieces) {
                if (piece.getPieceType() == Piece.PieceType.PAWN) {
                    //passedPawn
                    int rank, col, colOpp;
                    Vector<Integer> occupyCol = new Vector<>();
                    rank = piece.getPiecePosition() / 8;
                    col = piece.getPiecePosition() % 8;
                    for (Piece pieceOpp : theirPieces) {
                        if (pieceOpp.getPieceType() == Piece.PieceType.PAWN) {
                            colOpp = pieceOpp.getPiecePosition() % 8;
//                            if (colOpp == 0) {
//                                occupyCol.add(0);
//                                occupyCol.add(1);
//                            } else if (colOpp == 7) {
//                                occupyCol.add(7);
//                                occupyCol.add(6);
//                            } else {
                                occupyCol.add(colOpp);
                                occupyCol.add(colOpp + 1);
                                occupyCol.add(colOpp - 1);
//                            }
                        }
                    }
                    if (!occupyCol.contains(col)) {
                        if (isMidGame(board)) {
                            if (player.getAlliance() == Alliance.BLACK){
                                psBonus += passedPawnMG[rank - 1];
                            }
                            else {
                                psBonus += passedPawnMG[6-rank];
                            }
                        } else {
                            if (player.getAlliance() == Alliance.BLACK){
                                psBonus += passedPawnEG[rank - 1];
                            }
                            else {
                                psBonus += passedPawnEG[6-rank];
                            }
                        }
                    } else {
                        for (Piece piece1 : theirPieces) {
                            if (piece1.getPieceType() == Piece.PieceType.PAWN) {
                                if (piece1.getPiecePosition() % 8 <= col + 1 && piece1.getPiecePosition() % 8 >= col - 1) {
                                    if ((player.getAlliance() == Alliance.WHITE && rank <= piece1.getPiecePosition() / 8) ||
                                            (player.getAlliance() == Alliance.BLACK && rank >= piece1.getPiecePosition() / 8)) {
                                        if (isMidGame(board)) {
                                            if(player.getAlliance() == Alliance.BLACK) {
                                                psBonus += passedPawnMG[rank - 1];
                                            }
                                            else {
                                                psBonus += passedPawnMG[6-rank];
                                            }
                                        } else {
                                            if (player.getAlliance() == Alliance.BLACK){
                                                psBonus += passedPawnEG[rank - 1];
                                            }
                                            else {
                                                psBonus += passedPawnEG[6-rank];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //Add position to pawn pos list
                    pawnPosList[numPawn] = piece.getPiecePosition();
                    pawnPosCol[numPawn] = pawnPosList[numPawn] % 8;
                    numPawn++;
                }
            }

            Arrays.sort(pawnPosList);
            if (player.getAlliance() == Alliance.WHITE) {
                for(int i = 0; i < pawnPosList.length / 2; i++)
                {
                    int temp = pawnPosList[i];
                    pawnPosList[i] = pawnPosList[pawnPosList.length - i - 1];
                    pawnPosList[pawnPosList.length - i - 1] = temp;
                }
            }
            //Backward Pawn
            if (player.getAlliance() == Alliance.BLACK) {
                if ((pawnPosList[0] / 8 + 1) < pawnPosList[1] / 8 || (pawnPosList[0] / 8  == pawnPosList[1] / 8 && (pawnPosList[0] / 8 + 1) < pawnPosList[2] / 8)) {
                    if (isMidGame(board)) psBonus -= 20;
                    else psBonus -= 50;
                }
            } else {
                if ((pawnPosList[0] / 8 + 1) > pawnPosList[1] / 8 || (pawnPosList[0] / 8  == pawnPosList[1] / 8 && (pawnPosList[0] / 8 + 1) > pawnPosList[2] / 8)) {
                            if (isMidGame(board)) psBonus -= 20;
                            else psBonus -= 50;
                }
            }
            //Doubles Pawn & Connected Pawn
            if (player.getAlliance() == Alliance.BLACK) {
                if (numPawn > 1) {
                    for (int i = 1; i <= numPawn ; i++) {
                        for (int j = 2; j <= numPawn ; j++) {
                            if (pawnPosList[i - 1] == pawnPosList[j - 1] + 8 || pawnPosList[i - 1] == pawnPosList[j - 1] - 8) {
                                if (isMidGame(board)) psBonus -= 150;
                                else psBonus -= 200;
                            } else if (pawnPosList[i - 1] + 7 == pawnPosList[j - 1] || pawnPosList[i - 1] + 9 == pawnPosList[j - 1]) {
                                int connectedPawnRank = pawnPosList[j - 1] / 8;
                                if (connectedPawnRank == 2) psBonus += 5;
                                else if (connectedPawnRank == 3 || connectedPawnRank == 4) psBonus += 15;
                                else if (connectedPawnRank == 5) psBonus += 54;
                                else if (connectedPawnRank == 6) psBonus += 86;
                            }
                        }
                    }
                }
            } else {
                if (numPawn > 1) {
                    for (int i = 1; i <= numPawn; i++) {
                        for (int j = 2; j <= numPawn; j++) {
                            if (pawnPosList[i - 1] == pawnPosList[j - 1] + 8 || pawnPosList[i - 1] == pawnPosList[j - 1] - 8) {
                                if (isMidGame(board)) psBonus -= 50;
                                else psBonus -= 100;
                            } else if (pawnPosList[i - 1] - 7 == pawnPosList[j - 1] || pawnPosList[i - 1] - 9 == pawnPosList[j - 1]) {
                                int connectedPawnRank = pawnPosList[j - 1] / 8;
                                if (connectedPawnRank == 6) psBonus += 5;
                                else if (connectedPawnRank == 5 || connectedPawnRank == 4) psBonus += 15;
                                else if (connectedPawnRank == 3) psBonus += 54;
                                else if (connectedPawnRank == 2) psBonus += 86;
                            }
                        }
                    }
                }
            }
            //Shelter for castled King
            if (player.getPlayerKing().isCastled() && isMidGame(board)) {
                int kingPos = player.getPlayerKing().getPiecePosition();
                int kingPosCol = kingPos % 8;
                if (player.getAlliance() == Alliance.BLACK) {
                    if (Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos + 7) ||
                            Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos + 9) ||
                            Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos + 8)) {
                        psBonus += 50;
                    }

                } else {
                    if (Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos - 7) ||
                            Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos - 9) ||
                            Arrays.stream(pawnPosList).anyMatch(x -> x == kingPos - 8)) {
                        psBonus += 50;
                    }
                }
                if (kingPosCol >= 4) {
                    if (Arrays.stream(pawnPosCol).noneMatch(x -> x == 5) ||
                            Arrays.stream(pawnPosCol).noneMatch(x -> x == 6) ||
                            Arrays.stream(pawnPosCol).noneMatch(x -> x == 7) ) {
                        psBonus -= 200;
                    }
                }
                if (kingPosCol <=4) {
                    if (Arrays.stream(pawnPosCol).noneMatch(x -> x == 0) ||
                            Arrays.stream(pawnPosCol).noneMatch(x -> x == 1) ||
                            Arrays.stream(pawnPosCol).noneMatch(x -> x == 2) ) {
                        psBonus -= 200;
                    }
                }
            }
            //Double Attack Pawn
            List<Integer> oppPiecePos = new ArrayList<>();
            for (Piece piece: theirPieces)  oppPiecePos.add(piece.getPiecePosition());
            for (int i: pawnPosList) {
                if (player.getAlliance() == Alliance.BLACK) {
                    if (oppPiecePos.contains(i+7) && oppPiecePos.contains(i+9))     psBonus+=100;
                }
                else {
                    if (oppPiecePos.contains(i-7) && oppPiecePos.contains(i-9))     psBonus+=100;
                }
            }
//            System.out.print("Pawn list: ");
//            for (int i=0; i<numPawn; i++) {
//                System.out.print(pawnPosList[i] + " ");
//            }
//            System.out.println();
            return psBonus;
        }
        return 0;
    }
    private int triviaBonus(Player player, Board board) {
        int triviaBonus = 0;
        Collection<Piece> ourPieces = player.getActivePieces();
        Collection<Piece> theirPieces = player.getOpponent().getActivePieces();
        int rookFile = 0;
        //Rook on open/closed file
        for (Piece piece : ourPieces) {
            if (piece.getPieceType() == Piece.PieceType.ROOK) {
                rookFile = piece.getPiecePosition() % 8;
            }
        }
        List<Integer> pawnPos = new ArrayList<>();
        for (Piece piece : ourPieces) {
            if (piece.getPieceType() == Piece.PieceType.PAWN) {
                pawnPos.add(piece.getPiecePosition());
            }
        }
        if (!pawnPos.contains(rookFile)) {
            for (Piece piece: theirPieces) {
                if (piece.getPieceType() == Piece.PieceType.PAWN) {
                    pawnPos.add(piece.getPiecePosition());
                }
            }
            if (!pawnPos.contains(rookFile)) {
                if (isMidGame(board))   triviaBonus += 50;
                else                    triviaBonus += 30;
            }
            else {
                if (isMidGame(board))   triviaBonus += 20;
                else                    triviaBonus += 10;
            }
        }
        else {
            if (isMidGame(board))   triviaBonus += 10;
            else                    triviaBonus += 5;
        }
        if(7<=Counter.count && Counter.count <=20 && !player.isCastled()) {
            triviaBonus -= 200;
        }
        return triviaBonus;
    }
}

