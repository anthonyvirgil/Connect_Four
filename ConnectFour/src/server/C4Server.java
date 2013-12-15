package server;

import gameExceptions.C4InvalidMoveException;

import java.net.*;
import java.io.*;

/**
 * Iterative server that will run forever, servicing one client at a time
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.5
 */
public class C4Server {

	private byte[] byteBuffer; // buffer that will receive bytes
	private int receivedMessageSize; // size of the received message
	private int serverPort; // port number
	private ServerSocket serverSocket; // socket of the server

	/**
	 * Creates a C4Server object with a specified port number from which it will
	 * accept clients.
	 * 
	 * @param port
	 *            Port number that the server will be accepting clients from
	 */
	public C4Server(int port) {
		this.serverPort = port;
		byteBuffer = new byte[2];
		receivedMessageSize = 0;
	}

	/**
	 * Starts the server with an infinite loop that handles clients one at a
	 * time
	 */
	public void start() {
		try {
			// create a socket with a specified port number
			serverSocket = new ServerSocket(serverPort);

			// display server's IP address for user to know where to connect
			System.out.println("Server started, listening at "
					+ InetAddress.getLocalHost().getHostAddress() + " on port "
					+ serverPort + "\n");

			// run forever handling clients one at a time
			for (;;) {
				System.out.println("Waiting for connection...");

				// block until a single client connects to server
				Socket clientSocket = serverSocket.accept();

				// display client information that the server is handling
				System.out.println("Connected! Handling client at "
						+ clientSocket.getInetAddress().getHostAddress()
						+ " on port " + serverPort + "\n");

				// create a new server session when client is handled
				C4ServerSession serverSession = new C4ServerSession(
						clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Error connecting to client.");
		} catch (C4InvalidMoveException e) {
			System.out.println(e.getMessage());
		}
	}
}
