package ai;

import game.*;
import java.util.*;

public class PlayVsAI {
    public static void main(String[] args) {
        // Here we will set up the game board. We will start with a size of 5x5.
        Game game = new GameImpl(5);

        // Now we will create our AI. It uses the Minimax way to decide its moves.
        // The first number (5) tells the AI how many moves ahead to look.
        // A higher number makes the AI play better but its also makes it slower..
        AI ai = new Minimax(5, new MinPiecesHeuristic());

        // We need to decide which colour the AI will play as. we will assume it White.
        PieceColour aiColour = PieceColour.WHITE;

        // We'll use a Scanner to get the moves we make.
        Scanner scanner = new Scanner(System.in);

        // The game will keep going until someone wins or the board is full (that is a draw).
        do {
            // First we will see whose turn it is.
            System.out.println("Current player: " + game.currentPlayer());
            // Let's also print the current state of the game board so we will know what's happening.
            System.out.println("Game Board:");
            System.out.println(game.getGrid());

            // Now we check if it's the AI's turn.
            if (game.currentPlayer() == aiColour) {
                // If its the AI's turn, we ask the AI to make a move.
                Move aiMove = ai.getCurrentPlayerMove(game);
                System.out.println("AI's move: " + aiMove);
                game.makeMove(aiMove); // Then we make that move on the game board.
            } else {
                // If it's not the AI's turn, then it must be the ours turn.

                // We need to show the human player (who is us) the possible moves they can make.
                System.out.println("Your turn. Enter the number of the move you want to play.");
                Collection<Move> possibleMoves = game.getMoves();
                // Let's put the possible moves into a list so we can easily get them by number.
                Object[] movesList = possibleMoves.toArray();
                for (int i = 0; i < movesList.length; i++) {
                    System.out.print("Move " + (i + 1) + ": ");
                    System.out.println(movesList[i]);
                }

                // Now we wait for the human player(us) to enter their move.
                try {
                    int moveNumber = scanner.nextInt();
                    // The player enters a number, but our list starts at index 0, so we need to write -1.
                    Move selectedMove = (Move) movesList[moveNumber - 1];
                    game.makeMove(selectedMove); // We then make the move the player chosen.
                } catch (ArrayIndexOutOfBoundsException e) {
                    // If the player enters a number thats not in the list of moves, we tell them it's invalid.
                    System.out.println("That's not a valid move number. Please try again.");
                    continue; // We then go back to the start of the loop for the next turn.
                } catch (InputMismatchException e) {
                    // If the player enters something that's not a number, we also tell them it's invalid.
                    scanner.next(); // We need to clear the invalid input from the scanner.
                    System.out.println("Invalid input. Please enter a move number.");
                    continue; // And go to the next turn.
                }
            }
        } while (!game.isOver()); // The loop continues as long as the game is not over.

        // Once the game is over, we need to close the scanner to free up which is a good practice :).
        scanner.close();

        // Finally we announce that the game is over and who the winner is (if someone's won).
        System.out.println("Game Over!");
        System.out.println("Final Game Board:");
        System.out.println(game.getGrid());
        if (game.winner() == aiColour) {
            System.out.println("The AI wins!");
        } else if (game.winner() != PieceColour.NONE) {
            System.out.println("You win!");
        } else {
            System.out.println("It's a draw!");
        }
    }
}