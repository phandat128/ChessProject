package com.chess.engine.player.al;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.opening.Node;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static com.chess.engine.player.al.StandardBoardEvaluator.isMidGame;

public class MiniMax implements MoveStrategy{
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    int counting;
    boolean quietBefore;

    public MiniMax(int searchDepth){
        this.boardEvaluator= new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
        this.counting = Counter.count;
        this.quietBefore  = false;
    }


    @Override
    public String toString(){
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        System.out.println(board.currentPlayer()+ " THINKING with depth = " + this.searchDepth);
//        int numMoves = board.currentPlayer().getLegalMoves().size();
        new Counter();
        Move bestMove = bestMove(board);
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println(board.currentPlayer()+ " MOVE with time = " + executionTime);
        Toolkit.getDefaultToolkit().beep();
        return bestMove;
    }
    private Move bestOpeningMoves(Board board) {
        Node chosenMoveNode;
        Node currentMoveNode;
        currentMoveNode = NodeStorage.currentNode;
        if (currentMoveNode.getChild().size() == 0) return null;
        chosenMoveNode = random(currentMoveNode.getChild());
        for (Move move : board.currentPlayer().getLegalMoves()) {
            if (move.toString().equals(chosenMoveNode.getMove())) {
                new NodeStorage(chosenMoveNode);
                return move;
            }
        }
        return null;
    }

    private Move bestMove(Board board) {
        Move oneBestMove;
        Collection<Move> bestMoves;
        while (counting <= 14 && Semaphores.semaphore) {
//            double ratio = Math.random() *10;
//            if (0 < ratio && ratio < 7) {
//                Move moveFromOpeningTree = bestOpeningMoves(board);
//                if (moveFromOpeningTree == null)    break;
//                else return moveFromOpeningTree;
//            }
            Move moveFromOpeningTree = bestOpeningMoves(board);

            if (moveFromOpeningTree == null) {
                new Semaphores();
            }
            else {
                System.out.println("moveFromOpeningTree: " + moveFromOpeningTree);
                return moveFromOpeningTree;
            }
        }
        bestMoves = abSearch(board);
        oneBestMove = random(bestMoves);
//        System.out.println("chosen move:" + oneBestMove.toString());
        return oneBestMove;
    }

    private Collection<Move> abSearch(Board board) {
        int currentValue;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        Collection<Move> bestMoves = new ArrayList<>();
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
//                System.out.println("Move: " + move.toString());
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE) :
                        max(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if ( board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMoves.clear();
                    bestMoves.add(move);
                }
                else if (board.currentPlayer().getAlliance().isWhite() && currentValue == highestSeenValue) {
                    bestMoves.add(move);
                }
                else if (board.currentPlayer().getAlliance().isBlack()&& currentValue < lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMoves.clear();
                    bestMoves.add(move);
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue == lowestSeenValue) {
                    bestMoves.add(move);
                }
//                System.out.println("Score: " + currentValue);
//                System.out.println();
            }
        }
        return bestMoves;
    }

    private boolean isQuietScene(Board board) {
        if (board.currentPlayer().isInCheck()) return false;
        for (Piece playerPieces: board.currentPlayer().getActivePieces()) {
            if (playerPieces.getPieceType() == Piece.PieceType.QUEEN || playerPieces.getPieceType() == Piece.PieceType.ROOK) {
                int des = playerPieces.getPiecePosition();
                for (Move move: board.currentPlayer().getOpponent().getLegalMoves()) {
                    if (move.getDestinationCoordinate() == des)     return false;
                }
            }
        }
        return true;
    }
    int Quiesce(Board board, int alpha, int beta ) {
        int standPat = this.boardEvaluator.evaluate(board, 1);
        if (standPat >= beta)   return beta;
        if (alpha < standPat)   alpha = standPat;
        for(final Move move : board.currentPlayer().getLegalMoves()){
            if (move.isAttack() && isMidGame(board) && (move.getMovedPiece().getPieceType().mgPieceValue > move.getAttackedPiece().getPieceType().mgPieceValue + 200)){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    final int score = -Quiesce(moveTransition.getTransitionBoard(), -beta, -alpha );
                    if( score >= beta )
                        return beta;
                    if( score > alpha )
                        alpha = score;
                }
            }
        }
        return standPat;
    }
    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckmate() ||
                board.currentPlayer().isInStalemate();
    }
    public int min(final Board board, final int depth, int alpha, int beta){
        int lowestSeenValue=Integer.MAX_VALUE;
        if(isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        if (depth==0) { //quiescenceSearch
            if (!isQuietScene(board) && !quietBefore) {
                quietBefore = true;
                return min(board, 3, alpha, beta);
            }
            else return this.boardEvaluator.evaluate(board, depth);
//            return Quiesce(board, alpha, beta);
        }

        if (depth > 4 && !board.currentPlayer().isInCheck()) { // null move heuristtic (depth > 3 && notZugzwangBoard()) //TODO
            final int nullValue = max(board, 3, alpha, beta);
            if (nullValue <= alpha)           return nullValue;
            if (beta >= nullValue)            beta = nullValue;
        }
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getTransitionBoard(),depth-1, alpha, beta);
                if(currentValue <= lowestSeenValue)     lowestSeenValue = currentValue;
                if (lowestSeenValue <= alpha)           return lowestSeenValue;
                if (beta >= lowestSeenValue)            beta = lowestSeenValue;
            }
        }
        return lowestSeenValue;
    }
    public int max(final Board board, final int depth, int alpha, int beta){
        int highestSeenValue=Integer.MIN_VALUE;
        if(isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        if (depth==0) {
            if (isQuietScene(board) && !quietBefore) {
                quietBefore = true;
                return max(board, 3, alpha, beta);
            }
            else return this.boardEvaluator.evaluate(board, depth);
//            return Quiesce(board, alpha, beta);
        }
        if (depth > 4 && !board.currentPlayer().isInCheck()) {
            final int nullValue = min(board, 3, alpha, beta);
            if (nullValue >= beta)           return nullValue;
            if (alpha <= nullValue)          alpha = nullValue;
        }
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getTransitionBoard(),depth-1, alpha, beta);
                if(currentValue >= highestSeenValue)    highestSeenValue = currentValue;
                if (highestSeenValue >= beta)           return highestSeenValue;
                if (alpha <= highestSeenValue)          alpha = highestSeenValue;
            }
        }
        return highestSeenValue;
    }
    public static <T> T random(Collection<T> coll) {
        int num = (int) (Math.random() * coll.size());
        for(T t: coll) if (--num < 0) return t;
        throw new AssertionError();
    }
}