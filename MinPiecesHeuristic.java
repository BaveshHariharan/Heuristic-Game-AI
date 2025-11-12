package ai;

import game.*;
import java.util.*;

// This class is used to calculate a score for the game state.
// It will try to say us how close a player is to winning by finding the minimum number of empty cells
// For that we needed to create a winning path, assuming the opponent wont interfere.
public class MinPiecesHeuristic implements Heuristic {

    // It will give a position on the grid along with the distance from a starting point.
    private class Position {
        int row, col, distance;

        Position(int row, int col, int distance) {
            this.row = row;
            this.col = col;
            this.distance = distance;
        }
    }

    // This method uses Dijkstra's algorithm to find the shortest path (for empty cells)
    // for a given player's colour to connect from one side of the board to the other.
    private int dijkstra(Grid grid, PieceColour piece, int startRow, int startCol, int endRow, int endCol) {
        // A queue is used to keep track of positions to explore, it is ordered by their distance.
        PriorityQueue<Position> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a.distance));
        // Here this will keep track of visited cells to avoid loops.
        boolean[][] visited = new boolean[grid.getSize()][grid.getSize()];

        // This will keep on add starting positions to the queue.
        // If we are checking from top-to-bottom (that is startRow is 0), add all cells in the first row.
        // If we are checking from left-to-right (which is startCol is 0), add all cells in the first column.
        for (int i = 0; i < grid.getSize(); ++i) {
            if (startRow == 0) {
                // let the initial distance be 0 if the cell is already with us and let it be 1 if it is empty.
                int initialDistance = (grid.getPiece(startRow, i) == piece) ? 0 : 1;
                queue.offer(new Position(startRow, i, initialDistance));
            }
            if (startCol == 0) {
                int initialDistance = (grid.getPiece(i, startCol) == piece) ? 0 : 1;
                queue.offer(new Position(i, startCol, initialDistance));
            }
        }

        // possible directions to move ( that is up, down, left, right).
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // While the queue is not empty, we have to check
        while (!queue.isEmpty()) {
            // This line will get the position with the smallest distance from the queue.
            Position currentPos = queue.poll();

            // If we have already visited this position, skip it.
            if (visited[currentPos.row][currentPos.col]) {
                continue;
            }
            // This will mark the current position as visited.
            visited[currentPos.row][currentPos.col] = true;

            // If we've reached an end of a row or column, we have got a path.
            // For the top-to-bottom, the end row is the last row.
            // For the left-to-right, the end column is the last column.
            if (currentPos.row == endRow || currentPos.col == endCol) {
                return currentPos.distance; // This will return the number of empty cells in the shortest path.
            }

            // check the nearby rpws and cols of the current position.
            for (int[] dir : directions) {
                int nextRow = currentPos.row + dir[0];
                int nextCol = currentPos.col + dir[1];

                // Check if the nearby rows and cols is within the grid boundaries.
                if (nextRow >= 0 && nextRow < grid.getSize() && nextCol >= 0 && nextCol < grid.getSize()) {
                    // If we haven't visited those rows and cols yet
                    if (!visited[nextRow][nextCol]) {
                        PieceColour neighbourPiece = grid.getPiece(nextRow, nextCol);
                        int newDistance = currentPos.distance;

                        // If those rows and cols are empty, we need one more piece to potentially connect.
                        if (neighbourPiece == PieceColour.NONE) {
                            newDistance++;
                        }
                        // If those belongs to the opponent, we can't move there for this path.
                        else if (neighbourPiece != piece) {
                            continue;
                        }
                        // Add the neighbour to the queue with its new distance.
                        queue.offer(new Position(nextRow, nextCol, newDistance));
                    }
                }
            }
        }
        // If we can't find a path, it will return a very large number.
        return grid.getSize() * grid.getSize();
    }

    @Override
    public int score(Game game) {
        Grid grid = game.getGrid();
        PieceColour player = game.currentPlayer();

        // This line will calculate the minimum pieces needed for the current player to win
        // either by connecting top to bottom or left to right.
        int topToBottomPieces = dijkstra(grid, player, 0, -1, grid.getSize() - 1, -1);
        int leftToRightPieces = dijkstra(grid, player, -1, 0, -1, grid.getSize() - 1);

        // We want to minimize the number of pieces needed to win, so we take the minimum of the two directions.
        // We return the negative of this value because the Minimax way tries to maximize the score,
        // and a lower number of pieces needed to win is good for us.
        return -Math.min(topToBottomPieces, leftToRightPieces);
    }
}