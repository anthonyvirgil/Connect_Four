package client;

import gameExceptions.C4InvalidMessageException;
import gameExceptions.C4InvalidMoveException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Communicates between the server and client. Receives and inteprets messages
 * sent from the server. Also, sends messages from the client to the server.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.5
 */
public class C4ClientDataComm {
	private static final int MAXIMUM_MESSAGE_SIZE = 2;
	private Socket serverSocket;
	private C4Model model;

	/**
	 * Creates a C4ClientDataComm object that creates a socket to the server and
	 * handle to the model that will be sending specific messages and responding
	 * to the server's messages
	 * 
	 * @param serverIP
	 *            IP address of the server
	 * @param port
	 *            Port number of the server
	 * @param model
	 *            Model object that will be communicating through this object
	 */
	public C4ClientDataComm(String serverIP, int port, C4Model model) {
		try {
			// create a socket for the server
			this.serverSocket = new Socket(serverIP, port);
		} catch (IOException e) {
			System.out.println("Error connecting to server");
		}
		this.model = model;
	}

	/**
	 * Sends a byte array containing a message to the server.
	 * 
	 * @param message
	 *            Array of bytes containing the message that will be sent to the
	 *            server
	 * @throws C4InvalidMessageException
	 *             If the message client try to send is invalid
	 * @throws C4InvalidMoveException
	 *             If the move the client is attempting to make is invalid
	 */
	public void sendMessage(byte[] message) throws C4InvalidMessageException,
			C4InvalidMoveException {

		// throw an exception if the message does not respect the fixed length
		if (message.length != MAXIMUM_MESSAGE_SIZE)
			throw new C4InvalidMessageException(
					"Messages must be a length of 2 bytes.");

		try {
			// sends the byte array to the server
			OutputStream out = serverSocket.getOutputStream();
			out.write(message);

			// receive the message that the server will send in response
			receiveMessage();

		} catch (IOException e) {
			System.out.println("Error sending packet");
		} catch (C4InvalidMessageException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Interprets the message that the will be sent from the server.
	 * 
	 * @param message
	 *            Array of bytes containing the message that will be received
	 *            from the server
	 * @throws C4InvalidMessageException
	 *             If message client is trying to interpret is invalid
	 * @throws C4InvalidMoveException
	 *             If move server is attempting to make is invalid
	 */
	public void interpretMessage(byte[] message)
			throws C4InvalidMessageException, C4InvalidMoveException {

		// throw an exception if the message does not respect the fixed length
		if (message.length != MAXIMUM_MESSAGE_SIZE)
			throw new C4InvalidMessageException(
					"Messages must be of 2 byte length.");

		// get a handle to the first byte that determines the type of message
		// being sent
		byte firstByte = message[0];

		// interpretation of messages
		if (firstByte == 0x00)
			// server makes a move, send the 2nd byte to the model indicating
			// where the move was made
			model.receiveServerMove(message[1]);
		else if (firstByte == 0x02) {
			// displays a dialog indicating that the player has won, prompt
			// asking to play a new game
			model.showGameOverDialog("Congratulations! You have won!\nWould you like to play again?");
		} else if (firstByte == 0x03) {
			// server wins, send its final move and display a dialog
			model.serverMoveGameOver(message[1]);
		} else if (firstByte == 0x04) {
			// a draw between players has been made, send final move and display
			// dialog
			model.serverMoveDraw(message[1]);
		} else if (firstByte == 0x01) {
			if (message[1] == 0x01)
				// user wants to play a new game, create a new game
				model.createNewGame();
			else if (message[1] == 0x02)
				// user does not want to play anymore, close the application
				model.disposeViewWindow();
			else
				// if 2nd byte doesn't match a 0x01 or 0x02, throw an exception
				throw new C4InvalidMessageException("Invalid message: "
						+ message.toString());
		} else
			// first byte does not match the above criteria, throw an exception
			throw new C4InvalidMessageException(
					"Invalid first byte in message.");
	}

	/**
	 * Receives a message being sent from the server.
	 * 
	 * @throws C4InvalidMessageException
	 *             If message that is being received is invalid
	 * @throws C4InvalidMoveException
	 *             If move trying to attempt is invalid
	 */
	public void receiveMessage() throws C4InvalidMessageException,
			C4InvalidMoveException {
		byte[] byteBuffer = new byte[MAXIMUM_MESSAGE_SIZE];
		InputStream in;
		int receivedMessageSize, totalBytesReceived = 0;

		try {
			in = serverSocket.getInputStream();

			// receive message from server into a byte array
			while (totalBytesReceived < MAXIMUM_MESSAGE_SIZE) {
				if ((receivedMessageSize = in.read(byteBuffer,
						totalBytesReceived, MAXIMUM_MESSAGE_SIZE
								- totalBytesReceived)) == -1)
					throw new SocketException("Connection closed prematurely");
				totalBytesReceived += receivedMessageSize;
			}
			// send the message for interpretation
			interpretMessage(byteBuffer);

		} catch (IOException e) {
			System.out.println("Error receiving message in client.");
		}

	}

}
