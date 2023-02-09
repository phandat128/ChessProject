package com.chess.engine.player.al;

public class Semaphores {
    public static boolean semaphore = true;
    Semaphores() {
        throw new RuntimeException("Cannot initiate");
    }

    public static void setSemaphore(boolean semaphore) {
        Semaphores.semaphore = semaphore;
    }
}
