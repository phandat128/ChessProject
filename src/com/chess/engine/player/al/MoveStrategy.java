package com.chess.engine.player.al;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public interface MoveStrategy {
    Move execute(Board board);
}
