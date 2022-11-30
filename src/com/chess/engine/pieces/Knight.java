package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Knight extends Piece{
	
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17}; //tất cả nước đi của quân mã theo tọa độ
	
	public Knight(final int piecePosition, final Alliance pieceAlliance) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
	}

	public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
		super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
	}
	
	@Override
	public Collection<Move> calculateLegalMove(final Board board) {//tìm nước đi hợp lệ
		
		final List<Move> legalMoves = new ArrayList<>();//danh sách các nước có thể đi
		
		for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) { //duyệt tất cả nước đi
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset; //tọa độ mới tất cả các trường hợp đi quân
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {					//nếu tọa độ đó nằm trong bàn cờ
				if(IsFirstColumnExclusion(this.piecePosition, currentCandidateOffset) || 			//nếu tọa độ ở cột
						isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) || 		//hoặc nếu tọa độ ở cột 2
						isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) || 	//hoặc nếu tọa độ ở cột 7
						isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {		//hoặc nếu tọa độ ở cột 8
					continue;																		//không xét 1 số nước nữa (do ra khỏi bàn cờ)
				}
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);//tile chứa tọa độ đích
				if(!candidateDestinationTile.isTileOccupied()) {												//nếu tile không bị chiếm
					legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));	//nước đi đến tiel đó là hợp lệ
				} else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();						//lấy quân chiếm ô đó
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();						//lấy thuộc tính Màu quân
					if(this.pieceAlliance != pieceAlliance) {													//nếu màu quân chiếm khác màu quân đi nước đó
						legalMoves.add(new Move.AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));//nước đi đến ô đó là hợp lệ
					}
				}
			}
			
		}
		
		// return ImmutableList.copyOf(legalMoves); // **** replaced by a List because i cannot import it in my local
		return legalMoves;
	}

	@Override
	public Knight movePiece(Move move) {
		return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
	}

	public String toString() {
		return PieceType.KNIGHT.toString();
	}
	private static boolean IsFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
	} //trả về 1 nếu vị trí hiện tại thuộc cột 1 và nước đi dự tính {-17, -10, 6, 15}

	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
	} //trả về 1 nếu vị trí hiện tại thuộc cột 2 và nước đi dự tính {-10, 6}

	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
	} //trả về 1 nếu vị trí hiện tại thuộc cột 7 và nước đi dự tính {10, -6}

	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
	} //trả về 1 nếu vị trí hiện tại thuộc cột 8 và nước đi dự tính {17, 10, -6, -15}

}
