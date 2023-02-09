package com.chess.engine.player.al;

import com.chess.engine.opening.Node;
import com.chess.engine.opening.OpeningMovesTree;

public class NodeStorage {
    public static Node currentNode = OpeningMovesTree.root;
    public NodeStorage(Node node) {
        currentNode = node;
    }
    public static void store(Node node){
        currentNode = node;
    }
}
