package client;

import gameExceptions.C4InvalidMessageException;
import gameExceptions.C4InvalidMoveException;
import gui.C4GUI;

import java.util.Observable;

import javax.swing.JOptionPane;

/**
 * Connect Four model that notifies the view when there has been a visual change
 * in the game. The controller communicates with this class to send messages to
 * the server for various actions.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.7
 */
public class C4Model extends Observable {

	// data comm object connected to the model
	private C4ClientDataComm dataComm;

	// byte array containing information of what the last played move was
	private int[] previousMove;

	// array containing the number of markers in each column of the game board
	private int[] colCtrs;

	/**
	 * Creates a C4Model object, creating the data comm object that will send
	 * and receive its messages.
	 * 
	 * @param serverIP
	 *            IP address of the server
	 * @param port
	 *            Port number of the server
	 */
	public C4Model(String serverIP, int port) {
		dataComm = new C4ClientDataComm(serverIP, port, this);
		colCtrs = new int[7];
		previousMove = new int[2];

		// display dialog prompting user for the first game
		showFirstGameDialog();
	}

	/**
	 * Returns the last played move of either server or player.
	 * 
	 * @return Last played move of the game
	 */
	public int[] getPreviousMove() {
		return this.previousMove;
	}

	/**
	 * Sends the user's specified move to the game, updates view accordingly
	 * 
	 * @param col
	 *            Column at which the user's move was played
	 * @throws C4InvalidMoveException
	 *             If the move user is trying to attempt is invalid
	 * @throws C4InvalidMessageException
	 *             If the message client is sending is invalid
	 */
	public void sendMove(int col) throws C4InvalidMoveException,
			C4InvalidMessageException {

		// the column where the move is being played is full, throw an exception
		if (colCtrs[col] == 6)
			throw new C4InvalidMoveException("Invalid move made by client.");

		// throw exception if server makes a move out of game board's indices
		if (col < 0 || col > 6)
			throw new C4InvalidMoveException("Client has made an invalid move.");

		previousMove[0] = 0; // specifies the color of the button will be red

		// specifies the exact button whose display will be changed
		previousMove[1] = 41 - 7 * colCtrs[col] - (6 - col);

		// increase number of tokens in the column
		colCtrs[col]++;

		// send message for view to be updated
		setChanged();
		notifyObservers();

		// send message to game where the user made their move
		dataComm.sendMessage(new byte[] { 0x00, ((byte) col) });

	}

	/**
	 * Receives a move from the server, updates view accordingly
	 * 
	 * @param b
	 *            Byte containing the column where the server made its move
	 * @throws C4InvalidMoveException
	 *             If the move being received is invalid
	 */
	public void receiveServerMove(byte b) throws C4InvalidMoveException {

		// the column where the move is being played is full, throw an exception
		if (colCtrs[b] == 6)
			throw new C4InvalidMoveException("Invalid move made by server.");

		// throw exception if server makes a move out of game board's indices
		if (b < 0 || b > 6)
			throw new C4InvalidMoveException("Server has made an invalid move.");

		previousMove[0] = 1; // specifies the color of the button will be black

		// specifies the exact button whose display will be changed
		previousMove[1] = 41 - 7 * colCtrs[b] - (6 - b);

		// increase number of tokens in the column
		colCtrs[b]++;

		// send message for view to be updated
		setChanged();
		notifyObservers();
	}

	/**
	 * Method called when the board is filled and nobody has won.
	 * 
	 * @param b
	 *            Server's final move
	 */
	public void serverMoveDraw(byte b) {
		try {
			// receive server's last move
			receiveServerMove(b);
		} catch (C4InvalidMoveException e) {
			System.out.println(e.getMessage());
		}
		// display dialog that game is a draw, prompt for new game
		showGameOverDialog("This game is a draw, board is full\nWould you like to try again?");
	}

	/**
	 * Method called when server has won the game.
	 * 
	 * @param b
	 *            Server's final move
	 */
	public void serverMoveGameOver(byte b) {
		try {
			// receive server's last move
			receiveServerMove(b);
		} catch (C4InvalidMoveException e) {
			System.out.println(e.getMessage());
		}
		// display dialog that server has won, prompt for new game
		showGameOverDialog("Server is victorious!\nWould you like to make another attempt?");
	}

	/**
	 * Displays a confirm dialog asking if user wants to exit. Yes, game is
	 * ended and exited. No, game continues for playing.
	 */
	public void showExitGameDialog() {
		int result = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to exit?", "End Game",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			// sends message to server to end the game
			sendForEndGame();
		} else {
			// continue playing
		}

	}

	/**
	 * Displays a confirm dialog indicating a game has ended with a specific
	 * text. Prompts the user if they want to play a new game or not.
	 * 
	 * @param text
	 *            Message to be displayed in the dialog window
	 */
	public void showGameOverDialog(String text) {
		int result = JOptionPane.showConfirmDialog(null, text, "Game Over",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			// send message to server to start a new game
			sendForNewGame();
		} else {
			// send message to server to end the game
			sendForEndGame();
		}
	}

	/**
	 * Displays a confirm dialog prompting the user if they want to play a new
	 * game.
	 */
	public void showNewGameDialog() {
		int result = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to play a new game?", "New Game",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			// sends message to server to start a new game
			sendForNewGame();
		} else {
			// continue playing
		}
	}

	/**
	 * Displays a confirm dialog the first time the game application is run.
	 * Prompts the user if they want to play the game.
	 */
	private void showFirstGameDialog() {
		int result = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to play Connect Four?", "Play Game?",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			// create the GUI and display it
			C4GUI gui = new C4GUI(this);

			// add the model as an observer to the view
			this.addObserver(gui);

			// send message to server to start a new game
			sendForNewGame();
		} else {
			// send message to server to end the game
			sendForEndGame();
		}

	}

	/**
	 * Sends a specific message to the server communicating it wants to begin a
	 * new game
	 */
	private void sendForNewGame() {
		try {
			// send server a message to start a new game
			dataComm.sendMessage(new byte[] { 0x02, 0x00 });
		} catch (C4InvalidMessageException | C4InvalidMoveException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Sends a specific message to the server communicating it wants to end the
	 * game
	 */
	private void sendForEndGame() {

		try {
			// send server a message to end the game
			dataComm.sendMessage(new byte[] { 0x02, 0x01 });
		} catch (C4InvalidMessageException | C4InvalidMoveException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Creates a new Connect Four game, resetting the view and all essential
	 * class variables
	 */
	public void createNewGame() {
		previousMove = new int[2];
		colCtrs = new int[7];
		setChanged();
		notifyObservers("reset");// reset tells gui to reset all buttons
	}

	/**
	 * Closes the view connected to the model
	 */
	public void disposeViewWindow() {
		setChanged();
		notifyObservers("close"); // closes the game window
	}
}
