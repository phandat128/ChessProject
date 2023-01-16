package com.chess.engine.opening;

import java.util.ArrayList;
import java.util.List;

public class Node {
    String move;
    ArrayList<Node> nextMoves = new ArrayList<>();

    public Node(String key) {
        move = key;
    }

    public Node addChild(String key) {
        Node newChild = new Node(key);
        nextMoves.add(newChild);
        return newChild;
    }

    public ArrayList<Node> getChild() {
        return nextMoves;
    }

    public String getMove() {
        return move;
    }

}
