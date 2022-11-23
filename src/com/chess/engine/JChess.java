package com.chess.engine;

import com.chess.engine.board.Board;
import com.chess.engine.gui.Table;

public class JChess {
    public static void main(String [] args){
        Board board =Board.createStandardBoard();
        System.out.println(board);
        Table table=new Table();
    }
}
