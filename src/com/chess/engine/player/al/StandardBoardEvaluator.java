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
    private static final int PAWN_STRUCTURE_WEIGHT = 2;

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
                pawnStructureBonus(player, board) * PAWN_STRUCTURE_WEIGHT + triviaBonus(player, board);
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
        for (Piece piece: player.getActivePieces()) {
            numMoves = piece.calculateLegalMove(board).size();
            Piece.PieceType movePieceType = piece.getPieceType();
            if (isMidGame(board)) {
                if (movePieceType == Piece.PieceType.PAWN) {
                    mobilityBonus += numMoves;
                }
                else if (movePieceType == Piece.PieceType.KNIGHT) {
                    if (numMoves == 0)  mobilityBonus -= 20;
                    else if (numMoves == 1)  mobilityBonus -= 16;
                    else if (numMoves == 2)  mobilityBonus -= 5;
                    else if (numMoves == 3)  mobilityBonus -= 1;
                    else if (numMoves == 4)  mobilityBonus += 1;
                    else if (numMoves == 5)  mobilityBonus += 3;
                    else if (numMoves == 6)  mobilityBonus += 7;
                    else if (numMoves == 7)  mobilityBonus += 10;
                    else if (numMoves == 8)  mobilityBonus += 12;
                }
                else if (movePieceType == Piece.PieceType.BISHOP) {
                    if (numMoves == 0)  mobilityBonus -= 15;
                    else if (numMoves == 1)  mobilityBonus -= 5;
                    else if (numMoves == 2)  mobilityBonus += 4;
                    else if (numMoves == 3)  mobilityBonus += 8;
                    else if (numMoves == 4)  mobilityBonus += 11;
                    else if (numMoves == 5)  mobilityBonus += 15;
                    else if (numMoves == 6)  mobilityBonus += 15;
                    else if (numMoves == 7)  mobilityBonus += 17;
                    else if (numMoves == 8)  mobilityBonus += 18;
                    else if (numMoves == 9)  mobilityBonus += 20;
                    else if (numMoves == 10)  mobilityBonus += 22;
                    else if (numMoves == 11)  mobilityBonus += 23;
                    else if (numMoves == 12)  mobilityBonus += 25;
                    else if (numMoves == 13)  mobilityBonus += 27;
                }
                else if (movePieceType == Piece.PieceType.ROOK) {
                    if (numMoves == 0)  mobilityBonus -= 20;
                    else if (numMoves == 1)  mobilityBonus -= 10;
                    else if (numMoves == 2)  mobilityBonus += 0;
                    else if (numMoves == 3)  mobilityBonus += 1;
                    else if (numMoves == 4)  mobilityBonus += 1;
                    else if (numMoves == 5)  mobilityBonus += 5;
                    else if (numMoves == 6)  mobilityBonus += 7;
                    else if (numMoves == 7)  mobilityBonus += 10;
                    else if (numMoves == 8)  mobilityBonus += 13;
                    else if (numMoves == 9)  mobilityBonus += 13;
                    else if (numMoves == 10)  mobilityBonus += 13;
                    else if (numMoves == 11)  mobilityBonus += 15;
                    else if (numMoves == 12)  mobilityBonus += 20;
                    else if (numMoves == 13)  mobilityBonus += 20;
                    else if (numMoves == 14)  mobilityBonus += 23;
                }
                else if (movePieceType == Piece.PieceType.QUEEN) {
                    if (numMoves == 0)  mobilityBonus -= 10;
                    else if (numMoves == 1)  mobilityBonus -= 5;
                    else if (numMoves == 2)  mobilityBonus -= 3;
                    else if (numMoves == 3)  mobilityBonus -= 3;
                    else if (numMoves == 4)  mobilityBonus += 8;
                    else if (numMoves == 5)  mobilityBonus += 10;
                    else if (numMoves == 6)  mobilityBonus += 10;
                    else if (numMoves == 7)  mobilityBonus += 10;
                    else if (numMoves == 8)  mobilityBonus += 12;
                    else if (numMoves == 9)  mobilityBonus += 13;
                    else if (numMoves == 10)  mobilityBonus += 15;
                    else if (numMoves == 11)  mobilityBonus += 18;
                    else if (numMoves == 12)  mobilityBonus += 20;
                    else if (numMoves == 13)  mobilityBonus += 20;
                    else if (numMoves == 14)  mobilityBonus += 20;
                    else if (numMoves == 15)  mobilityBonus += 20;
                    else if (numMoves == 16)  mobilityBonus += 20;
                    else if (numMoves == 17)  mobilityBonus += 20;
                    else if (numMoves == 18)  mobilityBonus += 20;
                    else if (numMoves == 19)  mobilityBonus += 25;
                    else if (numMoves == 20)  mobilityBonus += 25;
                    else if (numMoves == 21)  mobilityBonus += 30;
                    else if (numMoves == 22)  mobilityBonus += 33;
                    else if (numMoves == 23)  mobilityBonus += 35;
                    else if (numMoves == 24)  mobilityBonus += 35;
                    else if (numMoves == 25)  mobilityBonus += 35;
                    else if (numMoves == 26)  mobilityBonus += 35;
                    else if (numMoves == 27)  mobilityBonus += 38;
                }
            }
            else {
                if (movePieceType == Piece.PieceType.PAWN) {
                    mobilityBonus += numMoves;
                }
                else if (movePieceType == Piece.PieceType.KNIGHT) {
                    if (numMoves == 0)  mobilityBonus -= 23;
                    else if (numMoves == 1)  mobilityBonus -= 17;
                    else if (numMoves == 2)  mobilityBonus -= 10;
                    else if (numMoves == 3)  mobilityBonus -= 5;
                    else if (numMoves == 4)  mobilityBonus += 2;
                    else if (numMoves == 5)  mobilityBonus += 3;
                    else if (numMoves == 6)  mobilityBonus += 5;
                    else if (numMoves == 7)  mobilityBonus += 7;
                    else if (numMoves == 8)  mobilityBonus += 9;
                }
                else if (movePieceType == Piece.PieceType.BISHOP) {
                    if (numMoves == 0)  mobilityBonus -= 20;
                    else if (numMoves == 1)  mobilityBonus -= 10;
                    else if (numMoves == 2)  mobilityBonus -= 5;
                    else if (numMoves == 3)  mobilityBonus += 5;
                    else if (numMoves == 4)  mobilityBonus += 7;
                    else if (numMoves == 5)  mobilityBonus += 13;
                    else if (numMoves == 6)  mobilityBonus += 18;
                    else if (numMoves == 7)  mobilityBonus += 18;
                    else if (numMoves == 8)  mobilityBonus += 20;
                    else if (numMoves == 9)  mobilityBonus += 23;
                    else if (numMoves == 10)  mobilityBonus += 25;
                    else if (numMoves == 11)  mobilityBonus += 27;
                    else if (numMoves == 12)  mobilityBonus += 30;
                    else if (numMoves == 13)  mobilityBonus += 32;
                }
                else if (movePieceType == Piece.PieceType.ROOK) {
                    if (numMoves == 0)  mobilityBonus -= 30;
                    else if (numMoves == 1)  mobilityBonus -= 10;
                    else if (numMoves == 2)  mobilityBonus += 5;
                    else if (numMoves == 3)  mobilityBonus += 15;
                    else if (numMoves == 4)  mobilityBonus += 25;
                    else if (numMoves == 5)  mobilityBonus += 33;
                    else if (numMoves == 6)  mobilityBonus += 33;
                    else if (numMoves == 7)  mobilityBonus += 40;
                    else if (numMoves == 8)  mobilityBonus += 43;
                    else if (numMoves == 9)  mobilityBonus += 45;
                    else if (numMoves == 10)  mobilityBonus += 50;
                    else if (numMoves == 11)  mobilityBonus += 53;
                    else if (numMoves == 12)  mobilityBonus += 55;
                    else if (numMoves == 13)  mobilityBonus += 57;
                    else if (numMoves == 14)  mobilityBonus += 60;
                }
                else if (movePieceType == Piece.PieceType.QUEEN) {
                    if (numMoves == 0)  mobilityBonus -= 17;
                    else if (numMoves == 1)  mobilityBonus -= 10;
                    else if (numMoves == 2)  mobilityBonus -= 5;
                    else if (numMoves == 3)  mobilityBonus += 5;
                    else if (numMoves == 4)  mobilityBonus += 13;
                    else if (numMoves == 5)  mobilityBonus += 18;
                    else if (numMoves == 6)  mobilityBonus += 20;
                    else if (numMoves == 7)  mobilityBonus += 25;
                    else if (numMoves == 8)  mobilityBonus += 25;
                    else if (numMoves == 9)  mobilityBonus += 30;
                    else if (numMoves == 10)  mobilityBonus += 30;
                    else if (numMoves == 11)  mobilityBonus += 33;
                    else if (numMoves == 12)  mobilityBonus += 40;
                    else if (numMoves == 13)  mobilityBonus += 43;
                    else if (numMoves == 14)  mobilityBonus += 45;
                    else if (numMoves == 15)  mobilityBonus += 45;
                    else if (numMoves == 16)  mobilityBonus += 45;
                    else if (numMoves == 17)  mobilityBonus += 45;
                    else if (numMoves == 18)  mobilityBonus += 47;
                    else if (numMoves == 19)  mobilityBonus += 50;
                    else if (numMoves == 20)  mobilityBonus += 50;
                    else if (numMoves == 21)  mobilityBonus += 50;
                    else if (numMoves == 22)  mobilityBonus += 55;
                    else if (numMoves == 23)  mobilityBonus += 55;
                    else if (numMoves == 24)  mobilityBonus += 57;
                    else if (numMoves == 25)  mobilityBonus += 60;
                    else if (numMoves == 26)  mobilityBonus += 60;
                    else if (numMoves == 27)  mobilityBonus += 73;
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
//    private static int endingPrincipleBonus(Player player, Board board) {
//        if (!isMidGame(board)) {
//            int endingPrincipleBonus = 0;
//
//            return endingPrincipleBonus;
//        }
//        return 0;
//    }
    private static int pawnStructureBonus(Player player, Board board) {
        if (!isOpenGame()) {
            Collection<Piece> ourPieces = player.getActivePieces();
            Collection<Piece> theirPieces = player.getOpponent().getActivePieces();
            int psBonus = 0, numPawn = 0;
            int[] pawnPosList = new int[8];
            int[] pawnPosCol = new int[8];
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
                            if (colOpp == 0) {
                                occupyCol.add(0);
                                occupyCol.add(1);
                            } else if (colOpp == 7) {
                                occupyCol.add(7);
                                occupyCol.add(6);
                            } else {
                                occupyCol.add(colOpp);
                                occupyCol.add(colOpp + 1);
                                occupyCol.add(colOpp - 1);
                            }
                        }
                    }
                    if (!occupyCol.contains(col)) {
                        if (isMidGame(board)) {
                            if (player.getAlliance() == Alliance.BLACK){
                                if (rank == 1) psBonus += 5;
                                else if (rank == 2) psBonus += 15;
                                else if (rank == 3) psBonus += 20;
                                else if (rank == 4) psBonus += 65;
                                else if (rank == 5) psBonus += 165;
                                else if (rank == 6) psBonus += 285;
                            }
                            else {
                                if (rank == 6) psBonus += 5;
                                else if (rank == 5) psBonus += 15;
                                else if (rank == 4) psBonus += 20;
                                else if (rank == 3) psBonus += 65;
                                else if (rank == 2) psBonus += 165;
                                else if (rank == 1) psBonus += 285;
                            }
                        } else {
                            if (player.getAlliance() == Alliance.BLACK){
                                if (rank == 1) psBonus += 40;
                                else if (rank == 2) psBonus += 35;
                                else if (rank == 3) psBonus += 50;
                                else if (rank == 4) psBonus += 80;
                                else if (rank == 5) psBonus += 180;
                                else if (rank == 6) psBonus += 280;
                            }
                            else {
                                if (rank == 6) psBonus += 40;
                                else if (rank == 5) psBonus += 35;
                                else if (rank == 4) psBonus += 50;
                                else if (rank == 3) psBonus += 80;
                                else if (rank == 2) psBonus += 180;
                                else if (rank == 1) psBonus += 280;
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
                                                if (rank == 1) psBonus += 5;
                                                else if (rank == 2) psBonus += 15;
                                                else if (rank == 3) psBonus += 20;
                                                else if (rank == 4) psBonus += 65;
                                                else if (rank == 5) psBonus += 165;
                                                else if (rank == 6) psBonus += 285;
                                            }
                                            else {
                                                if (rank == 6) psBonus += 5;
                                                else if (rank == 5) psBonus += 15;
                                                else if (rank == 4) psBonus += 20;
                                                else if (rank == 3) psBonus += 65;
                                                else if (rank == 2) psBonus += 165;
                                                else if (rank == 1) psBonus += 285;
                                            }
                                        } else {
                                            if (player.getAlliance() == Alliance.BLACK){
                                                if (rank == 1) psBonus += 40;
                                                else if (rank == 2) psBonus += 35;
                                                else if (rank == 3) psBonus += 50;
                                                else if (rank == 4) psBonus += 80;
                                                else if (rank == 5) psBonus += 180;
                                                else if (rank == 6) psBonus += 280;
                                            }
                                            else {
                                                if (rank == 6) psBonus += 40;
                                                else if (rank == 5) psBonus += 35;
                                                else if (rank == 4) psBonus += 50;
                                                else if (rank == 3) psBonus += 80;
                                                else if (rank == 2) psBonus += 180;
                                                else if (rank == 1) psBonus += 280;
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

