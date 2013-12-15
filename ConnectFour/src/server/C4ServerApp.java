package server;

/**
 * Runs a server for a Connect Four game.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.3
 */
public class C4ServerApp {

	/**
	 * Main method to start the server
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// default port is set to 50000
		int port = 50000;

		// if specifying a specific port through argument list, set the port
		if (args.length != 0)
			port = Integer.parseInt(args[0]);

		// create an instance of a C4Server
		C4Server server = new C4Server(port);

		// start the server
		server.start();
	}
}