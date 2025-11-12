package game;

import java.util.Arrays;

public class GridImpl implements Grid {
    // The size of our game board. Since its a square, this is both the number of rows and columns.
    private final int size;
    // This is the 2D array that represents the grid itself.
    // Each spot on the grid will hold a 'PieceColour' (either WHITE, BLACK, or NONE).
    private final PieceColour[][] grid;

    // This is like the constructor of our Grid. Its called when we create a new GridImpl.
    // We need to know how big to make the grid.
    public GridImpl(int size) {
        // First lets make sure the size we were given makes sense.
        // A grid cant have a size of zero or less.
        if (size <= 0) {
            throw new IllegalArgumentException("Grid size must be positive.");
        }
        // If the size is all good, we store it.
        this.size = size;
        // Now we actually create our 2D array. It will have 'size' rows and 'size' columns.
        this.grid = new PieceColour[size][size];
        // When we first create the grid, its empty. So, we need to go through every spot
        // and set it to 'NONE', which means no piece is there.
        for (int i = 0; i < size; i++) {
            // For each row, we fill all the columns in that row with 'PieceColour.NONE'.
            Arrays.fill(this.grid[i], PieceColour.NONE);
        }
    }

    // This method will lets us find out how big the grid is.
    @Override
    public int getSize() {
        return size;
    }

    // If we know the row and column of a spot on the grid, this method will tell us
    // like know what piece is there.
    @Override
    public PieceColour getPiece(int row, int col) {
        // First we should check if the row and column numbers we were given are valid.
        // They shouldnt be less than 0 or greater than or equal to the size of the grid.
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("Row or column is outside the grid.");
        }
        // If the row and column are valid, we can just return the PieceColour at that spot in our 2D array.
        return grid[row][col];
    }

    // This method lets us place a piece (of a certain colour) at a specific row and column on the grid.
    @Override
    public void setPiece(int row, int col, PieceColour piece) {
        // Again we first check if the given row and column are within the grid.
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("Row or column is outside the grid.");
        }
        // We should also make sure that we are actually trying to set a piece colour (it cant be null).
        if (piece == null) {
            throw new IllegalArgumentException("The piece to set cannot be nothing (null).");
        }
        // If everything is good, we set the PieceColour at the given row and column in our grid.
        grid[row][col] = piece;
    }

    // Sometimes we need to make a copy of the grid, so we can work with it without changing the original.
    @Override
    public Grid copy() {
        // We create a new GridImpl with the same size as the current one.
        GridImpl copy = new GridImpl(size);
        // Then we need to copy the contents of our current grid to the new one.
        // We can do this row by row.
        for (int i = 0; i < size; i++) {
            // For each row in our grid, we copy all the PieceColours in that row
            // to the same row in the 'copy' grid.
            System.arraycopy(this.grid[i], 0, copy.grid[i], 0, size);
        }
        // Finally, we return the new, copied grid.
        return copy;
    }

    // This method is used when we want to print the grid to the console.
    // It creates a String that represents the current state of the board.
    @Override
    public String toString() {
        // Well use a StringBuilder to efficiently build the string.
        StringBuilder sb = new StringBuilder();
        // We go through each row of the grid.
        for (int i = 0; i < size; i++) {
            // For each row we go through each column.
            for (int j = 0; j < size; j++) {
                // We look at the PieceColour at the current spot and append a character to our string.
                switch (grid[i][j]) {
                    case WHITE:
                        sb.append('W');
                        break;
                    case BLACK:
                        sb.append('B');
                        break;
                    case NONE:
                        sb.append('.');
                        break;
                }
            }
            // After we gone through all the columns in a row, we add a newline character
            // so that the next row starts on a new line when we print it.
            sb.append('\n');
        }
        // In the end, we convert our StringBuilder to a regular String and return it.
        return sb.toString();
    }
}