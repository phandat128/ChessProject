package com.chess.engine.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;

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
import java.util.ArrayList;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final BoardPanel boardPanel;
    private  final Board chessBoard;
    private final JFrame gameFrame;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static String defaultPieceImagesPath = "art/plain/";
    private final JMenuBar tableMenuBar;
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");


    public Table() {
        tableMenuBar=new JMenuBar();
        createTableMenuBar(tableMenuBar);
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.boardPanel=new BoardPanel();
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);

        this.gameFrame.setVisible(true);
    }


    private void createTableMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createFileMenu());
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
    }
    private class TilePanel extends JPanel {
        private final int titleId;

        TilePanel(final BoardPanel boardPanel, final int titleId) {
            super(new GridLayout());
            this.titleId = titleId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
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
                       if (humanMovedPiece == null)
                       sourceTile = null;
                       }else {
                           destinationTile = chessBoard.getTile(titleId);
                           final Move mone = null;
                       }
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
        private void assignTilePieceIcon (final Board board){
            this.removeAll();
            if (board.getTile(this.titleId).isTileOccupied()){
                try{
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.titleId).getPiece().getPieceAlliance().toString().substring(0,1)+
                                    board.getTile(this.titleId).getPiece().toString()+".gif"));
                    add (new JLabel(new ImageIcon(image)) );
                } catch (IOException e){
                    e.printStackTrace();}
            }
        }
        private void assignTileColor() {
            if(     BoardUtils.EIGHTH_RANK[this.titleId] ||
                    BoardUtils.SIXTH_RANK[this.titleId] ||
                    BoardUtils.FOURTH_RANK[this.titleId] ||
                    BoardUtils.SECOND_RANK[this.titleId] ){
                setBackground(this.titleId % 2 ==0 ? lightTileColor : darkTileColor);
            }
            else{
                if (BoardUtils.SEVENTH_RANK[this.titleId] ||
                        BoardUtils.FIFTH_RANK[this.titleId] ||
                        BoardUtils.THIRD_RANK[this.titleId] ||
                        BoardUtils.FIRST_RANK[this.titleId]
                )
                setBackground(this.titleId % 2 !=0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
