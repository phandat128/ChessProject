package com.chess.engine.player.al;

import com.chess.engine.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
