package game;

import java.util.*;

public class PathFinder {
    // This class helps us find if there's a path of the same colored pieces
    // connecting different sides of the game board.

    // Represents a location (row and column) on the board.
    private static class Position {
        int row;
        int col;

        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        // This is how we can compare two 'Position' objects to see if they are at the same spot.
        @Override
        public boolean equals(Object obj) {
            // First we check if the other thing we are comparing to is also a 'Position'.
            if (!(obj instanceof Position)) {
                return false;
            }
            // If it is a Position, we can cast it to one.
            Position other = (Position) obj;
            // Two positions are considered the same if they have the same row and column numbers.
            return row == other.row && col == other.col;
        }

        // We also need to override hashCode() when we override equals() so that objects
        // that are equal have the same hash code.
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    // This method finds all the locations of a specific coloured piece in a given row.
    private static Collection<Position> getOnRow(Grid grid, int row, PieceColour piece) {
        // Well store the locations we find in this list.
        Collection<Position> positions = new ArrayList<>();
        // We go through each column in the specified row.
        for (int col = 0; col < grid.getSize(); col++) {
            // If the piece at the current row and column is the colour we are looking for
            if (grid.getPiece(row, col) == piece) {
                // we create a new 'Position' object for this location and add it to our list.
                positions.add(new Position(row, col));
            }
        }
        return positions;
    }

    // This method does the same as 'getOnRow', but for a specific column.
    private static Collection<Position> getOnCol(Grid grid, int col, PieceColour piece) {
        Collection<Position> positions = new ArrayList<>();
        for (int row = 0; row < grid.getSize(); row++) {
            if (grid.getPiece(row, col) == piece) {
                positions.add(new Position(row, col));
            }
        }
        return positions;
    }

    // This is the main method that actually tries to find a path between a set of starting positions
    // and a set of ending positions.
    private static boolean findPath(Grid grid, Collection<Position> starts, Collection<Position> ends) {
        // We use a queue to keep track of the positions we need to check. Think of it like a waiting list.
        Queue<Position> queue = new ArrayDeque<>();
        // This 2D boolean array helps us remember which positions we have already looked at,
        // so we dont get stuck in loops.
        boolean[][] visited = new boolean[grid.getSize()][grid.getSize()];

        // First we add all our starting positions to the queue and mark them as visited.
        for (Position start : starts) {
            queue.offer(start);
            visited[start.row][start.col] = true;
        }

        // These are the possible directions we can move from one position to an adjacent one:
        // up, down, left, right.
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // We keep cehcking as long as there are positions in our queue.
        while (!queue.isEmpty()) {
            // We take the first position from the queue.
            Position currentPos = queue.poll();

            // If the current position is one of our target ending positions, we've got a path
            if (ends.contains(currentPos)) {
                return true;
            }

            // Now, we look at all the neighbours of the current position.
            for (int[] dir : directions) {
                // Calculate the row and column of the neighbour.
                int nextRow = currentPos.row + dir[0];
                int nextCol = currentPos.col + dir[1];

                // First we need to make sure the neighbour is still within the game board.
                if (nextRow < 0 || nextRow >= grid.getSize() || nextCol < 0 || nextCol >= grid.getSize()) {
                    continue; // If it is out of bounds, we ignore this neighbour.
                }

                // We also need to make sure we havent visited this neighbour before.
                if (visited[nextRow][nextCol]) {
                    continue; // If we have, then we dont need to look at it again.
                }

                // Finally we need to check if the piece at the neighbour's location is the same colour
                // as the piece at our current location. We can only move along connected pieces of the same colour.
                if (grid.getPiece(nextRow, nextCol) == grid.getPiece(currentPos.row, currentPos.col)) {
                    // If its a valid neighbour (within bounds, not visited, and same color),
                    // we add it to the queue to explore its neighbours later, and we mark it as visited.
                    queue.offer(new Position(nextRow, nextCol));
                    visited[nextRow][nextCol] = true;
                }
            }
        }

        // If we have explored all reachable positions and havent found an end position,
        // it means there's no path between the starts and the ends.
        return false;
    }

    // This method checks if there is a path of the given player's pieces from the top row to the bottom row.
    public static boolean topToBottom(Grid grid, PieceColour player) {
        // The starting positions are all the pieces of the player's colour in the very first row.
        Collection<Position> starts = getOnRow(grid, 0, player);
        // The ending positions are all the pieces of the player's colour in the very last row.
        Collection<Position> ends = getOnRow(grid, grid.getSize() - 1, player);
        // Now we just need to see if theres a path connecting any of the 'starts' to any of the 'ends'.
        return findPath(grid, starts, ends);
    }

    // This method checks if there's a path of the given player's pieces from the leftmost column to the rightmost column.
    public static boolean leftToRight(Grid grid, PieceColour player) {
        // The starting positions are all the pieces of the player's color in the very first column.
        Collection<Position> starts = getOnCol(grid, 0, player);
        // The ending positions are all the pieces of the player's color in the very last column.
        Collection<Position> ends = getOnCol(grid, grid.getSize() - 1, player);
        // We then check if there's a path between these starting and ending positions.
        return findPath(grid, starts, ends);
    }
}