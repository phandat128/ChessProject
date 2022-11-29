package com.chess.engine.board;

public class BoardUtils {
    public static final boolean[] FIRST_COLUMN = initColumn(0); //cột 1, tọa độ ô đầu là 0
    public static final boolean[] SECOND_COLUMN = initColumn(1); //cột 2, tọa độ ô đầu là 1
    public static final boolean[] SEVENTH_COLUMN = initColumn(6); //cột 7, tọa độ ô đầu là 6
    public static final boolean[] EIGHTH_COLUMN = initColumn(7); //cột 8, tọa độ ô đầu là 7

    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] FIRST_RANK= initRow(56);

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private BoardUtils() {
        throw new RuntimeException("You can not instantiate me!");
    }

    private static boolean[] initColumn(int ColumnNumber) {
        final boolean[] column = new boolean[NUM_TILES]; // xâu boolean 64 phần tử toàn 0
        do {
            column[ColumnNumber] = true;
            ColumnNumber += NUM_TILES_PER_ROW;
        } while (ColumnNumber < NUM_TILES); //đánh số 1 cách đều 8 đơn vị tính từ tọa độ ô đầu khi tọa độ < 64
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        do {
            row[rowNumber] = true;
            rowNumber ++;
        } while (rowNumber % NUM_TILES_PER_ROW !=0);
        return row;
    }

    public static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >=0 && coordinate < NUM_TILES;
	}
}
