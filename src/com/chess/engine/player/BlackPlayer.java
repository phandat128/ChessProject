package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Rook;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board,
                       final Collection<Move> blackStandardLegalMove,
                       final Collection<Move> whiteStandardLegalMove) {
        super(board, blackStandardLegalMove, whiteStandardLegalMove);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move>  calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        final List<Move> blackKingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove()) {
            if(!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    if(Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty() && 
                       Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(4, opponentsLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        blackKingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing, 6, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            if(!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied() && !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                && Player.calculateAttacksOnTile(2,opponentsLegals). isEmpty() &&
                Player.calculateAttacksOnTile(3,opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(4, opponentsLegals).isEmpty() &&
                rookTile.getPiece().getPieceType().isRook()) {
                    blackKingCastles.add(new Move.QueenSideCastleMove(this.board, this.playerKing, 2, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                }
            }
        }
        return blackKingCastles;
    }
}

