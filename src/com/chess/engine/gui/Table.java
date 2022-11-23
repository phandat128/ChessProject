package com.chess.engine.gui;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Table {
    private final BoardPanel boardPanel;
    private final JFrame gameFrame;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
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

        TilePanel(final BoardPanel boardPanel, final int titleId){
            super(new GridLayout());
            this.titleId=titleId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }

        private void assignTileColor() {
            if(     BoardUtils.FIRST_ROW[this.titleId] ||
                    BoardUtils.THIRD_ROW[this.titleId] ||
                    BoardUtils.FIFTH_ROW[this.titleId] ||
                    BoardUtils.SEVENTH_ROW[this.titleId] ){
                setBackground(this.titleId % 2 ==0 ? lightTileColor : darkTileColor);
            }
            else{
                setBackground(this.titleId % 2 ==0 ? darkTileColor : lightTileColor);
            }
        }
    }
}
