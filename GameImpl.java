package game;

import java.util.ArrayList;
import java.util.Collection;

public class GameImpl implements Game {
    // This is the game board where the pieces are placed.
    private final Grid grid;
    // It keeps track of whose turn it is.
    private PieceColour currentPlayer;
    // This line counts how many moves have been played.
    private int movesMade;

    // This is how we can create a new game. We need to know the size of the board.
    public GameImpl(int size) {
        // The size of the board has to be a positive number.
        if (size <= 0) {
            throw new IllegalArgumentException("size of the game must be positive.");
        }
        // Here it will create the actual game board with the given size.
        this.grid = new GridImpl(size);
        // White always starts the game.
        this.currentPlayer = PieceColour.WHITE;
        // At the beginning, no moves were made.
        this.movesMade = 0; // So setting it to 0
    }

    // This will tell us if the game has ended.
    @Override
    public boolean isOver() {
        // The game is over if someone has won, or if the entire board is full.
        return winner() != PieceColour.NONE || movesMade == grid.getSize() * grid.getSize();
    }

    // This code will check and returns who the winner of the game is.
    @Override
    public PieceColour winner() {
        // First lets see if White has won. White wins if they have a path of their pieces
        // from the top to the bottom OR from the left to the right of the board.
        if (PathFinder.topToBottom(grid, PieceColour.WHITE) || PathFinder.leftToRight(grid, PieceColour.WHITE)) {
            return PieceColour.WHITE; // returns if the winner is white
        }
        // If White hasn't won, then it means black won. Black also wins in the same way:
        // a path from top to bottom OR left to right made of their pieces.
        if (PathFinder.topToBottom(grid, PieceColour.BLACK) || PathFinder.leftToRight(grid, PieceColour.BLACK)) {
            return PieceColour.BLACK; // Yes, Black is the winner!
        }
        // If both White and Black has no winning path, then there's no winner yet.
        return PieceColour.NONE;
    }

    // Returns the color of the player whose turn it is right now.
    @Override
    public PieceColour currentPlayer() {
        return currentPlayer;
    }

    // Returns all the possible moves that can be made in the current game state.
    @Override
    public Collection<Move> getMoves() {
        // Well create a list to store all the possible moves.
        Collection<Move> possibleMoves = new ArrayList<>();
        // We need to go through every spot on the game board.
        for (int row = 0; row < grid.getSize(); row++) {
            for (int col = 0; col < grid.getSize(); col++) {
                // If a spot on the board is empty (no piece has been placed there yet)
                if (grid.getPiece(row, col) == PieceColour.NONE) {
                    // then placing a piece in that spot is a valid move.
                    // So, we create a new 'Move' object for this spot and add it to our list of possible moves.
                    possibleMoves.add(new MoveImpl(row, col));
                }
            }
        }
        // After checking every spot, we return the list of all possible moves.
        return possibleMoves;
    }

    // Allows the current player to make a move.
    @Override
    public void makeMove(Move move) {
        // First we get the row and column where the player wants to place their piece.
        int row = move.getRow();
        int col = move.getCol();

        // We need to make sure that the move is actually on the game board.
        if (row < 0 || row >= grid.getSize() || col < 0 || col >= grid.getSize()) {
            throw new IllegalArgumentException("Move is outside the board.");
        }
        // We also need to make sure that the spot where they want to place the piece is empty.
        if (grid.getPiece(row, col) != PieceColour.NONE) {
            throw new IllegalArgumentException("That spot is already taken.");
        }

        // If the move is valid, we place the current player's piece on the board.
        grid.setPiece(row, col, currentPlayer);
        // After a player makes a move, it becomes the other player's turn.
        currentPlayer = (currentPlayer == PieceColour.WHITE) ? PieceColour.BLACK : PieceColour.WHITE;
        // We also record that a move has been made.
        movesMade++;
    }

    // Returns the current state of the game board.
    @Override
    public Grid getGrid() {
        // To prevent someone from directly changing our game board from outside,
        // we return a copy of the grid, not the original one.
        return grid.copy();
    }

    // Creates a completely independent copy of the current game.
    @Override
    public Game copy() {
        // First we create a new GameImpl object with the same size as the current game.
        GameImpl copy = new GameImpl(grid.getSize());
        // Then we set the current player of the copy to be the same as in the original game.
        copy.currentPlayer = this.currentPlayer;
        // We also copy the number of moves made so far.
        copy.movesMade = this.movesMade;
        // Finally we need to copy the state of the board. We go through each cell
        // of the original board and set the same piece in the corresponding cell of the copied board.
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                copy.grid.setPiece(i, j, this.grid.getPiece(i, j));
            }
        }
        // Now, copy is a completely independent copy of the current game state.
        return copy;
    }
}