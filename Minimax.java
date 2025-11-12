package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import game.*;

// This class implements the Minimax algorithm, by which the AI can decide the best move.
public class Minimax implements AI {
    private int maxDepth; // How many moves ahead the AI will look
    private Heuristic heuristic; // This will tell us How the AI evaluates a game state

    // Constructor: When we create a Minimax AI
    public Minimax(int maxDepth, Heuristic heuristic) {
        // Here we are using if to check whether the AI looks at least one move ahead
        if (maxDepth < 1) {
            throw new IllegalArgumentException("maxDepth must be at least 1"); // else throw this error
        }
        this.maxDepth = maxDepth;
        this.heuristic = heuristic;
    }

    // This function is used to get all possible moves in a random order 
    private ArrayList<Move> getMoves(Game game) {
        // This will get all possible moves from the current game state
        ArrayList<Move> moves = new ArrayList<>(game.getMoves());
        // This will get different possibilities.
        Collections.shuffle(moves, new Random()); // using shuffle to shuffle the moves randomly.
        return moves;
    }

    // The AI looks ahead at all possible future moves with the help of alpha and beta
    private long minimax(Game game, int depth, long alpha, long beta, PieceColour player) {
        // If the game is over or we haveve reached the maximum depth we want to look
        if (game.isOver() || depth == 0) {
            // If the current player has won, it will give a high score
            if (game.winner() == player) {
                return Integer.MAX_VALUE;
            }
            // If the opponent has won, it will give a  low score
            else if (game.winner() != PieceColour.NONE) {
                return Integer.MIN_VALUE;
            }
            // If the game is not over and we reached the depth limit, it will use the heuristic to score the board
            return heuristic.score(game);
        }

        // This will get all possible moves from the current game state
        ArrayList<Move> moves = getMoves(game);

        // If it's the AI's turn
        if (game.currentPlayer() == player) {
            long bestScore = Integer.MIN_VALUE; // Initialize with the low possible score
            // Go through each possible move
            for (Move move : moves) {
                // It will create a copy of the current game state
                Game newGame = game.copy();
                // It will make the move on the copied game
                newGame.makeMove(move);
                // It will recursively call minimax to evaluate the score after this move
                long score = minimax(newGame, depth - 1, alpha, beta, player);
                // At last we will update the best score if we found a better move
                bestScore = Math.max(bestScore, score);
                // if the current best score is good enough, we can stop exploring this branch
                if (bestScore >= beta) {
                    return bestScore;
                }
                // Update the alpha value
                alpha = Math.max(alpha, score);
            }
            return bestScore; // Finally it will return the best score found
        }
        // If it's the opponent's turn (we want to minimize their score)
        else {
            long bestScore = Integer.MAX_VALUE; // Start with the best possible score for the opponent
            // Same as AI it will go through each possible move
            for (Move move : moves) {
                // it will create a copy of the current game state
                Game newGame = game.copy();
                // it will make the move on the copied game
                newGame.makeMove(move);
                // It will recursively call minimax to evaluate the score after this move
                long score = minimax(newGame, depth - 1, alpha, beta, player);
                // It will update the best score if we found a move that makes a bad outcome for the opponent
                bestScore = Math.min(bestScore, score);
                // if the current best score is bad enough for us, we can stop exploring this branch
                if (bestScore <= alpha) {
                    return bestScore;
                }
                // Update beta
                beta = Math.min(beta, score);
            }
            return bestScore; // Return the best score found (which will return the opponent's best score)
        }
    }

    // This is the main function
    @Override
    public Move getCurrentPlayerMove(Game game) {
        // Here we will get all possible moves
        ArrayList<Move> moves = getMoves(game);
        // ok lets assume the first move is the best initially
        Move bestMove = moves.get(0);
        long bestScore = Integer.MIN_VALUE; // It will initialize with the worst possible score
        long alpha = Integer.MIN_VALUE;
        long beta = Integer.MAX_VALUE;
        PieceColour currentPlayer = game.currentPlayer(); // The AI is playing as this colour

        // This code will go through each possible move
        for (Move move : moves) {
            // It will create a copy of the game state
            Game newGame = game.copy();
            // It will make the current move on the copied game
            newGame.makeMove(move);
            // Evaluate the score of this move using the minimax way
            long score = minimax(newGame, maxDepth - 1, alpha, beta, currentPlayer);
            // If this move has a better score than the current best score
            if (score > bestScore) {
                bestScore = score; // We will update the best score
                bestMove = move;   // updating the move as the best one so far
            }
            // Update alpha for alpha-beta pruning in the next iterations
            alpha = Math.max(alpha, score);
        }
        return bestMove; // Return the move that resulted in the best score
    }
}