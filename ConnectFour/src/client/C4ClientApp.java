package client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Runs a Connect Four game on the client side.
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.4
 */
public class C4ClientApp {

	// constant containing the regular expression for a valid IP address
	private static final String IP_ADDRESS_REG_EXP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	/**
	 * Main method that runs the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String serverIP;

				// keep prompting user until they input a valid IP or cancels
				do {
					// display dialog for user input
					serverIP = (String) JOptionPane.showInputDialog(null,
							"Please enter a valid IP address (e.g. 127.0.0.1)",
							"Server Address", JOptionPane.PLAIN_MESSAGE, null,
							null, null);

					if (serverIP != null) {
						// display message that user did not input anything
						if (serverIP.equals("")) {
							JOptionPane.showMessageDialog(null,
									"You did not enter an IP address.");
						}
						// create model if the IP address is valid
						else if (serverIP.matches(IP_ADDRESS_REG_EXP)) {
							new C4Model(serverIP, 50000);
						}
						// if inputed string is not a valid IP
						else {
							JOptionPane.showMessageDialog(null,
									"You did not enter a valid IP address.");
						}
					}
				} while (serverIP != null
						&& !serverIP.matches(IP_ADDRESS_REG_EXP));
			}
		});
	}

}
