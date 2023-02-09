package com.chess.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PromoteFrame extends JFrame{
   public static int promoteChoice = 0;

   public PromoteFrame(Table.BoardPanel boardPanel) {
       JFrame choosePiece = new JFrame("Promotion");
       choosePiece.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       choosePiece.setSize(100, 100);
       JDialog dialog = new JDialog(choosePiece, "Promotion", true);

        JPanel mainGUI = new JPanel(new BorderLayout());
        mainGUI.setBorder(new EmptyBorder(80, 80, 80, 80));
        mainGUI.add(new JLabel("Choose piece type to promote:"), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        mainGUI.add(buttonPanel, BorderLayout.SOUTH);

        JButton setKnight = new JButton("Knight");
        setKnight.addActionListener(e-> {
        promoteChoice = 1;
        dialog.setVisible(false);
        });
        buttonPanel.add(setKnight);

        JButton setBishop = new JButton("Bishop");
        setBishop.addActionListener(e-> {
        promoteChoice = 2;
        dialog.setVisible(false);
        });
        buttonPanel.add(setBishop);

        JButton setRook = new JButton("Rook");
        setRook.addActionListener(e-> {
        promoteChoice = 3;
        dialog.setVisible(false);
         });
        buttonPanel.add(setRook);

        JButton setQueen = new JButton("Queen");
        setQueen.addActionListener(e-> {
        promoteChoice = 4;
        dialog.setVisible(false);
         });
        buttonPanel.add(setQueen);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(100, 150, 20, 20);
        cancel.addActionListener(e-> dialog.setVisible(false));
        buttonPanel.add(cancel);

        choosePiece.setVisible(false);

        dialog.setContentPane(mainGUI);
        dialog.pack();
        dialog.setLocationRelativeTo(boardPanel);
        dialog.setVisible(true);
   }
}
