package com.chess.engine.player.al;

public class Counter {
    static int count = 1;
    public Counter(){
        throw new RuntimeException("Cannot initiate");
    }
    public static void increase() {
        count++;
    }

}
