package gameLogic;

import gameExceptions.C4InvalidMoveException;
import gameExceptions.C4InvalidCheckWinException;

/**
 * Game logic for the Connect Four board game
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.6
 */
public class C4Game {
	// two dimensional array representing the Connect Four game board
	private int[][] gameBoard;

	// constants for the limits of the actual 6x7 game board
	private static final int BOTTOM_ROW = 8, FARTHEST_LEFT = 3;

	// constants indicating who is playing which move
	public static final int PLAYER_ID = 1, SERVER_ID = 2;

	private int[] colCtrs; // counters for the amount of tokens in each column

	// counter for how many moves are being played during the game
	private int moveCounter;

	/**
	 * Creates a C4 game object, initializing instance variables
	 */
	public C4Game() {
		gameBoard = new int[12][13];
		moveCounter = 0;
		colCtrs = new int[7];
	}

	/**
	 * Makes move by a player on the game board.
	 * 
	 * @param col
	 *            Column to be played in
	 * @param player
	 *            Player making move
	 * @return True or false depending on if the move was valid or not.
	 * @throws C4InvalidMoveException
	 *             If move player is trying to attempt is invalid
	 */
	public boolean makeMove(int col, int player) throws C4InvalidMoveException {

		// if an invalid player, throw an exception
		if (player != PLAYER_ID && player != SERVER_ID)
			throw new C4InvalidMoveException("Invalid player ID.");

		// check if column out of range
		if (col > 6 || col < 0)
			throw new C4InvalidMoveException(
					"Move must be between columns 0 and 6.");

		// check if there is still space in the column to play
		if (colCtrs[col] == 6)
			return false;

		// row that the player will make a move on
		int playerRow = BOTTOM_ROW - colCtrs[col];

		// column that the player will make a move on
		int playerColumn = col + FARTHEST_LEFT;

		// make the move on the game board with the player's ID
		gameBoard[playerRow][playerColumn] = player;

		// increment counters for the column played on and the total number of
		// moves
		colCtrs[col]++;
		moveCounter++;

		// move is successful, return true
		return true;
	}

	/**
	 * Removes a move played by a player on the game board.
	 * 
	 * @param col
	 *            Column to be played in
	 * @return True, if the move has been removed. False, if there is no move to
	 *         be removed.
	 * @throws C4InvalidMoveException
	 *             If an attempted remove is invalid
	 */
	public boolean removeMove(int col) throws C4InvalidMoveException {

		// throw exception if specified column is out of range
		if (col > 6 || col < 0)
			throw new C4InvalidMoveException(
					"Move must be between columns 0 and 6.");

		// return false if there are no tokens in the specified column
		if (colCtrs[col] == 0)
			return false;

		// row of the token to be removed
		int row = BOTTOM_ROW - (--colCtrs[col]);

		// column of the token to be removed
		int column = col + FARTHEST_LEFT;

		// remove move, setting it to 0
		gameBoard[row][column] = 0;

		// decrement number of total moves
		moveCounter--;

		// remove was successful, return true
		return true;
	}

	/**
	 * Check for 4 pieces in a row diagonally, horizontally and vertically.
	 * 
	 * @param row
	 *            Previous row played on
	 * @param col
	 *            Previous column played on
	 * @return boolean Representing whether or not there are 4 in a row
	 * @throws C4InvalidCheckWinException
	 *             If an attempt there is an attempt to check at an invalid spot
	 */
	public boolean checkFourConnected(int row, int col)
			throws C4InvalidCheckWinException {

		int tokenCtr = 0; // counter of continuous token

		// which id of which kind of player will be checked
		int player = gameBoard[BOTTOM_ROW - row][FARTHEST_LEFT + col];

		// if a move is checked on an empty spot, throw exception
		if (player == 0)
			throw new C4InvalidCheckWinException(
					"Cannot check for four in a row on an empty move.");

		// check downward-right diagonal
		for (int i = 0; i < 7; i++) {
			if (gameBoard[BOTTOM_ROW - (colCtrs[col] - 1) + i - 3][col
					+ FARTHEST_LEFT + i - 3] == player) {
				if (++tokenCtr == 4)
					return true;
			} else
				tokenCtr = 0;
		}

		// check upward-right diagonal
		for (int i = 0; i < 7; i++) {
			if (gameBoard[BOTTOM_ROW - (colCtrs[col] - 1) - i + 3][col
					+ FARTHEST_LEFT + i - 3] == player) {
				if (++tokenCtr == 4)
					return true;
			} else
				tokenCtr = 0;
		}

		// check row left to right
		for (int i = 0; i < 7; i++) {
			if (gameBoard[BOTTOM_ROW - row][col + FARTHEST_LEFT - 3 + i] == player) {
				if (++tokenCtr == 4)
					return true;
			} else
				tokenCtr = 0;
		}

		// check whole column vertically
		for (int i = 0; i < 7; i++) {
			if (gameBoard[BOTTOM_ROW - row - 3 + i][col + FARTHEST_LEFT] == player) {
				if (++tokenCtr == 4)
					return true;
			} else
				tokenCtr = 0;
		}

		return false;
	}

	/**
	 * AI method that uses brute force logic to make a reasonable move.
	 * 
	 * @return True if game is over, false otherwise.
	 * @throws C4InvalidMoveException
	 *             If move AI is attempting to make is invalid
	 */
	public byte[] artificialIntelligenceMakeMove()
			throws C4InvalidMoveException {
		byte move = 0x00;

		/*
		 * If a win for the server is possible, make a move at that place. Loops
		 * through all columns, places a move and checks if a win is possible.
		 * If a win is possible, make the move and end the game. If not, remove
		 * that move.
		 */
		for (int i = 0; i < 7; i++) {
			if (makeMove(i, SERVER_ID))
				try {
					// if server can win
					if (checkFourConnected(colCtrs[i] - 1, i)) {
						// return a game over message with the column index
						return new byte[] { 0x03, (byte) i };
					} else
						// remove the move
						removeMove(i);
				} catch (C4InvalidCheckWinException e) {
					System.out.println(e.getMessage());
				}
		}

		/*
		 * If a win for the user is possible, make a move at that place. Loops
		 * through all columns, places a move and checks if a user's win is
		 * possible. If a user can win, block the user there. If not, remove
		 * that move.
		 */
		for (int i = 0; i < 7; i++) {
			if (makeMove(i, PLAYER_ID))
				try {
					// if user can win
					if (checkFourConnected(colCtrs[i] - 1, i)) {
						removeMove(i); // remove user's move
						makeMove(i, SERVER_ID); // make the server winning move

						// if total number of moves is 42, send draw game
						// message
						if (moveCounter == 42)
							move = 0x04;

						// send message where the server's move is played
						return new byte[] { move, (byte) i };
					} else
						// remove the move
						removeMove(i);
				} catch (C4InvalidCheckWinException e) {
					System.out.println(e.getMessage());
				}
		}

		// if user nor server can win, make a random move

		// number of columns for a possible random move
		int colPossibilities = 0;
		int whereToMove; // column where the random move can be made

		// checks how many columns are not full
		for (int c : colCtrs)
			if (c != 6) {
				colPossibilities++;
			}

		// random column where the move will be made
		whereToMove = (int) Math.abs((Math.random() * colPossibilities)) + 1;

		// increments whereToMove for every row that is already full
		for (int i = 0; i < whereToMove; i++) {
			if (colCtrs[i] == 6)
				whereToMove++;
		}
		whereToMove--;

		// make a move at the random column
		makeMove(whereToMove, SERVER_ID);

		// if total number of moves is 42, send draw game message
		if (moveCounter == 42)
			move = 0x04;

		// return message where the random move was made
		return new byte[] { move, (byte) whereToMove };
	}

	/**
	 * Returns the number of tokens in a specific column
	 * 
	 * @param col
	 *            Column to return the number of tokens
	 * @return Number of tokens
	 */
	public int getRowInColumn(int col) {
		return colCtrs[col];
	}

	/**
	 * String representation of the values of the game board
	 */
	public void getGameBoard() {
		for (int i = 0; i < gameBoard.length; i++) {
			for (int j = 0; j < gameBoard[0].length; j++)
				System.out.print(gameBoard[i][j] + "\t");
			System.out.println();
		}

	}
}
