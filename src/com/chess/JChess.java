package com.chess;

import com.chess.engine.board.Board;
import com.chess.engine.opening.Node;
import com.chess.engine.opening.OpeningMovesTree;
import com.chess.gui.Table;

import java.util.ArrayList;

public class JChess {
    public static void main(String [] args){
        new OpeningMovesTree();
        Board board =Board.createStandardBoard();
        System.out.println(board);
        Table.get().show();
    }
}
