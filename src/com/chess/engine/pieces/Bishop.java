package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Bishop extends Piece{

    // Cac buoc di chuyen cheo
    private  final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES={-9,-7,7,9};

    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMove(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();    //danh sach nuoc di hop le

        for(final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES){

            int candidateDestinationCoordinate = this.piecePosition; //khởi tạo tọa độ dự kiến = tọa độ hiện tại

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) { //khi tọa độ dự kiến còn trong bàn cờ

                // Khi di den cac diem gioi han thi dung lai
                if(isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) ||
                        isEightColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset)){
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;        //cập nhật tọa độ dự kiến theo vector

                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) { //nếu tọa độ dự kiến trong bàn cờ
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate); //xét ô có tọa độ dự kiến
                    if (!candidateDestinationTile.isTileOccupied()) {                                    //nếu ô không bị chiếm
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate)); //thêm nước đi vào danh sách nước hợp lệ
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();//lấy màu quân cờ đang chiếm ô dự kiến đến
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        } //nếu khác màu quân cờ, thêm vào danh sách nước hợp lệ
                        break;
                    }
                }
            }
        }
        return legalMoves;
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    public String toString() {
        return PieceType.BISHOP.toString();
    }

    // Neu la cot 1 va buoc di chuyen la ( di cheo len tren ve phia ben trai || di chuyen xuong duoi ve phia ben trai)
    private static boolean isFirstColumnExclusion(final int currentPosition , final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }
    // Neu la cot 8 va buoc di chuyen la (di chuyen len tren ve phia ben phai || di chuyen xuong duoi ve phia ben phai)
    private static boolean isEightColumnExclusion(final int currentPosition , final int candidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }


}
