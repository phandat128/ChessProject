package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.opening.Node;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.al.MiniMax;
import com.chess.engine.player.al.MoveStrategy;
import com.chess.engine.player.al.NodeStorage;
import com.chess.engine.player.al.Semaphores;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.reverse;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(800,800);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(300,300);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static String defaultPieceImagesPath = "art/plain/";
    private final JMenuBar tableMenuBar;
    private final Color lightTileColor = Color.decode("#f0d9b5");
    private final Color darkTileColor = Color.decode("#b58863");
    private final Color lightTileColorHightlight = Color.decode("#cdd26a");
    private final Color darkTileColorHightlight = Color.decode("#aaa23a");
    private Node moveNode;

    private static final Table INSTANCE = new Table();
    private Table() {
        tableMenuBar=new JMenuBar();
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
        this.highlightLegalMoves = false;
        this.moveNode = NodeStorage.currentNode;
    }

    public static Table get() {
        return INSTANCE;
    }
    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPNG = new JMenuItem("Load PNG File");
        openPNG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up that png file");
            }
        });

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(openPNG);
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    private JMenu createOptionMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupMenuItem = new JMenuItem("Setup Game");
        setupMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckmate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStalemate()){
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            if (Table.get().getGameBoard().currentPlayer().isInCheckmate()){
                PrintWriter out=new PrintWriter(System.out);
                out.print("Game over," + Table.get().getGameBoard().currentPlayer() + "is in checkmate!");
            }
            if (Table.get().getGameBoard().currentPlayer().isInStalemate()){
                PrintWriter out=new PrintWriter(System.out);
                out.print("Game over," + Table.get().getGameBoard().currentPlayer() + "is in stalemate!");
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }
    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }
    private MoveLog getMoveLog() {
        return this.moveLog;
    }
    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }
    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }
    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }
    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }
    private static class AIThinkTank extends SwingWorker<Move, String> {
        private AIThinkTank() {

        }
        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax = new MiniMax(GameSetup.getSearchDepth());
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }
        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite(){
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                reverse(boardTiles);
                return boardTiles;
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }
        };
        abstract List<TilePanel> traverse (final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        private final ArrayList<TilePanel> boardTiles;

        BoardPanel () {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i=0;i< BoardUtils.NUM_TILES;i++){
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        public void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{
        private final List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves(){
            return this.moves;
        }

        public void addMove (final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }
    private class TilePanel extends JPanel {
        private final int titleId;

        TilePanel(final BoardPanel boardPanel, final int titleId) {
            super(new GridLayout());
            this.titleId = titleId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor(chessBoard);
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                	
                if (isRightMouseButton(e)){
                    sourceTile = null;
                    destinationTile = null;
                    humanMovedPiece = null;
                    }
                   else if (isLeftMouseButton(e)){
                       if (sourceTile == null){
                           sourceTile = chessBoard.getTile(titleId);
                           humanMovedPiece = sourceTile.getPiece();
                       if (humanMovedPiece == null) sourceTile = null;
                       }
                       else {
                           destinationTile = chessBoard.getTile(titleId);
                           final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                           final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                           if (transition.getMoveStatus().isDone()) {
                               chessBoard = transition.getTransitionBoard();
                               moveLog.addMove(move);
                               if (Semaphores.semaphore)
                               {
                                   if (moveNode.getChild().size() != 0) {
                                       for (Node node : moveNode.getChild()) {
                                           if (node.getMove() == move.toString()) {
                                               new NodeStorage(node);
                                               System.out.println(node.getMove());
                                           }
                                       }
                                   }
                               }
                           }
                           sourceTile = null;
                           destinationTile = null;
                           humanMovedPiece = null;
                       }
                       SwingUtilities.invokeLater(new Runnable() {
                           @Override
                           public void run() {
                               gameHistoryPanel.redo(chessBoard, moveLog);
                               takenPiecesPanel.redo(moveLog);
                               if (gameSetup.isAIPlayer(chessBoard.currentPlayer())){
                                   Table.get().moveMadeUpdate(PlayerType.HUMAN);
                               }
                               boardPanel.drawBoard(chessBoard);
                           }
                       });
                   }
                }

                @Override
                public void mousePressed( final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final Board board){
            assignTileColor(board);
            assignTilePieceIcon(board);
            validate();
            repaint();
        }
        //
        private void assignTilePieceIcon (final Board board){
            this.removeAll();
            if (board.getTile(this.titleId).isTileOccupied()){
                try{
                    highlightLegals(board);
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.titleId).getPiece().getPieceAlliance().toString().substring(0,1)+
                                    board.getTile(this.titleId).getPiece().toString()+".png"));
                    add (new JLabel(new ImageIcon(image)) );
                } catch (IOException e){
                    e.printStackTrace();}
            }
        }

        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move: pieceLegalMove(board)) {
                    if (move.getDestinationCoordinate() == this.titleId) {
                        try {
                            /*add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));*/
                                if(     BoardUtils.EIGHTH_RANK[this.titleId] ||
                                        BoardUtils.SIXTH_RANK[this.titleId] ||
                                        BoardUtils.FOURTH_RANK[this.titleId] ||
                                        BoardUtils.SECOND_RANK[this.titleId] ){
                                        setBackground(this.titleId % 2 ==0 ? lightTileColorHightlight : darkTileColorHightlight);
                                }
                            else{
                                if (    BoardUtils.SEVENTH_RANK[this.titleId] ||
                                        BoardUtils.FIFTH_RANK[this.titleId] ||
                                        BoardUtils.THIRD_RANK[this.titleId] ||
                                        BoardUtils.FIRST_RANK[this.titleId]
                                ){
                                        setBackground(Color.RED);
                                        setBackground(this.titleId % 2 !=0 ? lightTileColorHightlight : darkTileColorHightlight);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private Collection<Move> pieceLegalMove(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMove(board);
            }
            return Collections.emptyList();
        }
        private void assignTileColor(Board board) {
            if(     BoardUtils.EIGHTH_RANK[this.titleId] ||
                    BoardUtils.SIXTH_RANK[this.titleId] ||
                    BoardUtils.FOURTH_RANK[this.titleId] ||
                    BoardUtils.SECOND_RANK[this.titleId] ){

                    setBackground(this.titleId % 2 ==0 ? lightTileColor : darkTileColor);

                    highlightLegals(board);
            }
            else{
                if (    BoardUtils.SEVENTH_RANK[this.titleId] ||
                        BoardUtils.FIFTH_RANK[this.titleId] ||
                        BoardUtils.THIRD_RANK[this.titleId] ||
                        BoardUtils.FIRST_RANK[this.titleId]) {
                    setBackground(this.titleId % 2 !=0 ? lightTileColor : darkTileColor);

                    highlightLegals(board);
                }
            }
        }
    }
}
