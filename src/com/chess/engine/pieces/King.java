package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.WhitePlayer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class King extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9}; //tất cả nước đi thường của quân vua theo tọa độ
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    public King(final int piecePosition, final Alliance pieceAlliance,
                final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove, final boolean isCastled,
                final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    public boolean isCastled() {
        return this.isCastled;
    }
    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }
    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {//tìm nước đi hợp lệ

        final List<Move> legalMoves = new ArrayList<>();//danh sách các nước có thể đi

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) { //duyệt tất cả nước đi thường
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset; //tọa độ mới tất cả các trường hợp đi quân
            if(IsFirstColumnExclusion(this.piecePosition, currentCandidateOffset) || 			//nếu tọa độ ở cột 1
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {		//hoặc nếu tọa độ ở cột 8
                continue;																		//không xét 1 số nước nữa (do ra khỏi bàn cờ)
            }
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {					//nếu tọa độ đó nằm trong bàn cờ
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);            //tile chứa tọa độ dự kiến
                if(!candidateDestinationTile.isTileOccupied()) {												//nếu tile không bị chiếm
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));	//nước đi đến tile đó là hợp lệ
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();						//lấy quân chiếm ô đó
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();						//lấy thuộc tính Màu quân chiếm ô đó
                    if(this.pieceAlliance != pieceAlliance) {													//nếu màu quân chiếm khác màu quân đi nước đó
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));//nước đi đến ô đó là hợp lệ
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(),
                move.getMovedPiece().getPieceAlliance(),
                false, move.isCastlingMove(), false, false);
    }

    public String toString() {
        return PieceType.KING.toString();
    }
    private static boolean IsFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7 || candidateOffset == -1);
    } //trả về 1 nếu vị trí hiện tại thuộc cột 1 và nước đi dự tính {-9, -1, 7}

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
    } //trả về 1 nếu vị trí hiện tại thuộc cột 8 và nước đi dự tính {-7, 1, 9}

}
