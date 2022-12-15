package com.chess.engine.player.al;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

import java.util.ArrayList;
import java.util.Collection;

public class MiniMax implements MoveStrategy{
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    public MiniMax(int searchDepth){
        this.boardEvaluator= new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
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
        Move bestMove;
        bestMove = random(bestMove(board));
//        final long executionTime = System.currentTimeMillis() - startTime;
        return bestMove;
    }


    private Collection<Move> bestMove(Board board) {
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        Collection<Move> bestMove = new ArrayList<>();
        int nullValue = board.currentPlayer().getAlliance().isWhite() ?
                min(board, this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE) :
                max(board, this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE) :
                        max(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if ( board.currentPlayer().getAlliance().isWhite() && currentValue > nullValue){
                    nullValue = currentValue;
                    bestMove.clear();
                    bestMove.add(move);
                }
                else if (board.currentPlayer().getAlliance().isWhite() && currentValue == nullValue) {
                    bestMove.add(move);
                }
                else if (board.currentPlayer().getAlliance().isBlack()&& currentValue < nullValue){
                    nullValue = currentValue;
                    bestMove.clear();
                    bestMove.add(move);
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue == nullValue) {
                    bestMove.add(move);
                }
            }
        }
        if (bestMove.isEmpty()) {
            for(final Move move : board.currentPlayer().getLegalMoves()){
                final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
                if(moveTransition.getMoveStatus().isDone()){
                    currentValue = board.currentPlayer().getAlliance().isWhite() ?
                            min(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE) :
                            max(moveTransition.getTransitionBoard(),this.searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if ( board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue){
                        highestSeenValue = currentValue;
                        bestMove.clear();
                        bestMove.add(move);
                    }
                    else if (board.currentPlayer().getAlliance().isWhite() && currentValue == highestSeenValue) {
                        bestMove.add(move);
                    }
                    else if (board.currentPlayer().getAlliance().isBlack()&& currentValue < lowestSeenValue){
                        lowestSeenValue = currentValue;
                        bestMove.clear();
                        bestMove.add(move);
                    } else if (board.currentPlayer().getAlliance().isBlack() && currentValue == lowestSeenValue) {
                        bestMove.add(move);
                    }
                }
            }
        }
        return bestMove;
    }

    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckmate() ||
                board.currentPlayer().isInStalemate();
    }
    public int min(final Board board, final int depth, int alpha, int beta){
        if(depth==0 || isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue=Integer.MAX_VALUE;
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getTransitionBoard(),depth-1, alpha, beta);
                if(currentValue <= lowestSeenValue){
                    lowestSeenValue=currentValue;
                }
                if (lowestSeenValue <= alpha) return lowestSeenValue;
                if (beta >= lowestSeenValue) beta = lowestSeenValue;
            }
        }
        return lowestSeenValue;
    }
    public int max(final Board board, final int depth, int alpha, int beta){
        if(depth==0 || isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue=Integer.MIN_VALUE;
        for(final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getTransitionBoard(),depth-1, alpha, beta);
                if(currentValue >= highestSeenValue){
                    highestSeenValue=currentValue;
                }
                if (highestSeenValue >= beta) return highestSeenValue;
                if (alpha <= highestSeenValue) alpha = highestSeenValue;
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
