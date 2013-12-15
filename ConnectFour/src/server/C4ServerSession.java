package server;

import gameExceptions.C4InvalidMessageException;
import gameExceptions.C4InvalidMoveException;
import gameExceptions.C4InvalidCheckWinException;
import gameLogic.C4Game;

import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream

/**
 * Defines a server session for a single C4 game, only created when a client
 * connects to a server
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.5
 */
public class C4ServerSession {

	private static final int MAXIMUM_MESSAGE_SIZE = 2;
	Socket clientSocket; // socket for the client
	// boolean indicating that the server will be ending
	private boolean sessionEnded;
	private byte[] serverMessage; // message that server will send
	private C4Game game;
	private InputStream in;
	private OutputStream out;

	/**
	 * Creates a C4ServerSession object
	 * 
	 * @param socket
	 *            Socket of the client
	 * @throws C4InvalidMoveException
	 */
	public C4ServerSession(Socket socket) throws C4InvalidMoveException {
		this.clientSocket = socket;
		this.sessionEnded = false;

		// once object is created, run the session
		runSession();

	}

	/**
	 * Run the server session which sends, receives and interprets messages from
	 * the client.
	 */
	private void runSession() {
		try {
			in = clientSocket.getInputStream();
			out = clientSocket.getOutputStream();
		} catch (IOException ioe) {
			System.out.println("Error creating Input/Output streams.");
		}

		// keep running until the session will end
		do {
			try {
				// receive message from client
				receiveMessage();

				// send message to client
				sendMessage(serverMessage);

			} catch (C4InvalidMessageException | C4InvalidMoveException e) {
				System.out.println(e.getMessage());
			}
		} while (!sessionEnded);

		// session has ended, close the socket
		closeSocket();
	}

	/**
	 * Receives a message from the client.
	 * 
	 * @throws C4InvalidMessageException
	 *             If message sent from client is invalid
	 * @throws C4InvalidMoveException
	 *             If move client is attempting is invalid
	 */
	private void receiveMessage() throws C4InvalidMessageException,
			C4InvalidMoveException {
		byte[] byteBuffer = new byte[MAXIMUM_MESSAGE_SIZE];

		int receivedMessageSize, totalBytesReceived = 0;
		try {
			in = clientSocket.getInputStream();

			while (totalBytesReceived < MAXIMUM_MESSAGE_SIZE) {
				if ((receivedMessageSize = in.read(byteBuffer,
						totalBytesReceived, MAXIMUM_MESSAGE_SIZE
								- totalBytesReceived)) == -1)
					throw new SocketException("Connection closed prematurely.");
				totalBytesReceived += receivedMessageSize;
			}

			// interpret the message sent from client
			interpretMessage(byteBuffer);

		} catch (IOException e) {
			System.out.println("Error receiving messages.");
		}

	}

	/**
	 * Interprets the message sent from the client.
	 * 
	 * @param message
	 *            Message to be interpreted
	 * @throws C4InvalidMessageException
	 *             If message sent from client is invalid
	 * @throws C4InvalidMoveException
	 *             If move client is attempting is invalid
	 */
	private void interpretMessage(byte[] message)
			throws C4InvalidMessageException, C4InvalidMoveException {

		// if message length is not the fixed length, throw an exception
		if (message.length != MAXIMUM_MESSAGE_SIZE)
			throw new C4InvalidMessageException(
					"Messages must be of 2 byte length.");

		byte firstByte = message[0]; // first byte in the array
		byte secondByte; // second byte in the array

		if (firstByte == 0x00) {
			// user makes a move at a specified column
			game.makeMove(message[1], C4Game.PLAYER_ID);
			try {
				if (game.checkFourConnected(
						game.getRowInColumn(message[1]) - 1, message[1]))
					// send message to client indicating user has won
					serverMessage = new byte[] { 0x02, 0x00 };
				else {
					// server makes a move in response to user's play
					serverMessage = game.artificialIntelligenceMakeMove();
				}
			} catch (C4InvalidCheckWinException e) {
				System.out.println(e.getMessage());
			}
		} else if (firstByte == 0x02) {
			secondByte = message[1];
			if (secondByte == 0x00) {
				// client wants to play a new game
				game = new C4Game(); // create new game
				// send message to client to reset GUI
				serverMessage = new byte[] { 0x01, 0x01 };
			} else if (secondByte == 0x01) {
				// user does not want to play again

				// send message to close application
				serverMessage = new byte[] { 0x01, 0x02 };
				sessionEnded = true; // end the session

			} else
				throw new C4InvalidMessageException("Invalid message.");
		} else
			throw new C4InvalidMessageException(
					"Invalid first byte in message.");

	}

	/**
	 * Sends a message to the client
	 * 
	 * @param message
	 *            Message to be sent to the client
	 */
	private void sendMessage(byte[] message) {
		try {
			out.write(message);

		} catch (IOException e) {
			System.out.println("Failed to send message to client");
		}

	}

	/**
	 * Closes the socket and its input/output streams
	 */
	private void closeSocket() {
		try {
			in.close();
			out.close();
			clientSocket.close();

		} catch (IOException e) {
			System.out.println("Error closing client socket.");
		}
	}

}
