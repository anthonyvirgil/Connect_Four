package gui;

import gameExceptions.C4InvalidMessageException;
import gameExceptions.C4InvalidMoveException;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import client.C4Model;

/**
 * Serves as the view and controller for a Connect Four game application
 * 
 * @author Anthony-Virgil Bermejo, Kim Parise, George Lambadas
 * @version 1.4
 */
public class C4GUI extends JFrame implements Observer {

	// instance variables
	private static final long serialVersionUID = -6218323746697753249L;
	private JButton[] theButtons = null;
	private ImageIcon clearImage; // default image with no token inside
	private ImageIcon serverImage; // image that contains a black token
	private ImageIcon playerImage; // image that contains a red token
	private Border border;
	private int[] playedMove;
	private C4Model model;

	/**
	 * Creates a C4 GUI object attaching a model to be used with the controller
	 * 
	 * @param model
	 *            Model to be used with the controller
	 */
	public C4GUI(C4Model model) {
		super();

		this.model = model;

		// specific images for the buttons
		serverImage = new ImageIcon(this.getClass().getResource(
				"/images/connect4_server.jpg"));
		playerImage = new ImageIcon(this.getClass().getResource(
				"/images/connect4_player.jpg"));
		clearImage = new ImageIcon(this.getClass().getResource(
				"/images/connect4_clear.jpg"));

		border = BorderFactory.createEmptyBorder();

		// create the GUI with buttons and menu
		initialize();
		this.setTitle("Connect Four");
		pack();
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * Creates the GUI with buttons, event listeners and menus
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);

		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// event listener for when user clicks on the close button
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				model.showExitGameDialog();
			}
		});

		// creates and places buttons within layout
		createAndPlaceButtons();

		// creates the menu bar
		this.setJMenuBar(createMenuBar());

		// set the size of the frame
		this.setSize(gridBagLayout.preferredLayoutSize(this));

	}

	/**
	 * Creates all the buttons and places them within the GUI using specific
	 * GridBag constraints. Adds action listeners and mouse over listeners to
	 * each button.
	 */
	private void createAndPlaceButtons() {
		theButtons = new JButton[42]; // array of JButtons

		// create action listener to be set to each button
		MoveListener buttonListener = new MoveListener();

		// loops creating a button, setting its properties and even listeners
		for (int i = 0; i < theButtons.length; i++) {

			// create a new JButton using the default image
			theButtons[i] = new JButton(clearImage);

			// add an action command associating a String to its column position
			theButtons[i].setActionCommand(i % 7 + "");

			// add action listener
			theButtons[i].addActionListener(buttonListener);

			// set the preferred size and border for the buttons
			theButtons[i].setPreferredSize(new Dimension(75, 75));
			theButtons[i].setBorder(border);

			// add mouse over listener that changes images as mouse hovers over
			theButtons[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent evt) {
					((JButton) evt.getSource()).setIcon(playerImage);
				}

				public void mouseExited(MouseEvent evt) {
					((JButton) evt.getSource()).setIcon(clearImage);
				}
			});
		}

		// Sets the GridBag constraints for each button
		for (int i = 0; i < 7; i++) {
			add(theButtons[i], makeConstraints(i, 0, 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			add(theButtons[i + 7], makeConstraints(i, 1, 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			add(theButtons[i + 14], makeConstraints(i, 2, 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			add(theButtons[i + 21], makeConstraints(i, 3, 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			add(theButtons[i + 28], makeConstraints(i, 4, 1, 1));
		}

		for (int i = 0; i < 7; i++) {
			add(theButtons[i + 35], makeConstraints(i, 5, 1, 1));
		}

	}

	/**
	 * Creates the menu bar for the GUI and its menu items
	 * 
	 * @return The menu bar of the GUI
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		// create "File" menu and add it to the menu bar
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("File menu");
		menu.setToolTipText("File menu");
		menuBar.add(menu);

		// create New Game menuItem and its action listener
		menuItem = new JMenuItem("New Game", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Start a new game");
		menuItem.setToolTipText("Start a new game");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// display dialog asking if user wants to play a new game
				model.showNewGameDialog();
			}
		});
		menu.add(menuItem);

		// create About menu item and its action listener
		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Open About menu");
		menuItem.setToolTipText("Open About menu");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// display dialog with the developers' names and the game
				// version
				JOptionPane
						.showMessageDialog(null,
								"By: George Lamabadas, Kim Parise, Anthony-Virgil Bermejo\nVersion: 1.3");
			}
		});
		menu.add(menuItem);

		// add a separator line in the menu
		menu.addSeparator();

		// create Exit menuItem and its action listener
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"End the new game");
		menuItem.setToolTipText("End the game");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// displays dialog prompting user if they want to end game
				model.showExitGameDialog();
			}
		});
		menu.add(menuItem);

		return menuBar;
	}

	/**
	 * Creates and returns specific GridBag constraints for a component
	 * 
	 * @param gridx
	 *            X coordinate where the component will be placed in the GridBag
	 * @param gridy
	 *            Y coordinate where the component will be placed in the GridBag
	 * @param gridwidth
	 *            Width of the component
	 * @param gridheight
	 *            Height of the component
	 * @return Component's GridBag constraint
	 */
	private GridBagConstraints makeConstraints(int gridx, int gridy,
			int gridwidth, int gridheight) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridheight = gridheight;
		gbc.gridwidth = gridwidth;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;

		return gbc;
	}

	/**
	 * Sends a move through the model according to which column was clicked
	 */
	private class MoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {
				model.sendMove(Integer.parseInt(e.getActionCommand()));

			} catch (NumberFormatException | C4InvalidMoveException
					| C4InvalidMessageException e1) {
				System.out.println(e1.getMessage());
			}
		}
	}

	/**
	 * Changes the colour of a button according to the player that made the move
	 * and remove its event listeners
	 * 
	 * @param move
	 *            Array containing the id of the player making the move and the
	 *            column
	 */
	private void formatButtons(int[] move) {
		int colour = playedMove[0];
		int position = playedMove[1];

		// player has made a move, change image to red
		if (colour == 0)
			theButtons[position].setIcon(playerImage);
		// server has made a move, change image to black
		else if (colour == 1)
			theButtons[position].setIcon(serverImage);

		// remove mouse listener from the button for the mouse over events
		theButtons[position].removeMouseListener(theButtons[position]
				.getMouseListeners()[1]);

		// remove action listener from the button so player cannot make a move
		theButtons[position].removeActionListener(theButtons[position]
				.getActionListeners()[0]);

	}

	/**
	 * Resets the GUI, adding all event listeners back to the buttons and
	 * changing the icon to its default.
	 */
	private void resetGUI() {
		// create action listener to be set to each button
		MoveListener buttonListener = new MoveListener();

		// loops through all buttons
		for (int i = 0; i < theButtons.length; i++) {

			// if a mouse listener was added, remove it
			if (theButtons[i].getMouseListeners().length > 1)
				theButtons[i].removeMouseListener(theButtons[i]
						.getMouseListeners()[1]);

			// if an action listener was added, remove it
			if (theButtons[i].getActionListeners().length > 0)
				theButtons[i].removeActionListener(theButtons[i]
						.getActionListeners()[0]);

			// set icon to its default image
			theButtons[i].setIcon(clearImage);

			// add event listeners
			theButtons[i].addActionListener(buttonListener);
			theButtons[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent evt) {
					((JButton) evt.getSource()).setIcon(playerImage);
				}

				public void mouseExited(MouseEvent evt) {
					((JButton) evt.getSource()).setIcon(clearImage);
				}

			});

		}
	}

	/**
	 * Method that communicates with the model when the GUI needs to be updated
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof C4Model) {
			C4Model model = (C4Model) o;

			// if no arg, a move is being played. Update GUI accordingly
			if (arg == null) {
				playedMove = model.getPreviousMove();
				formatButtons(playedMove);
			}
			// if a "reset" is being sent, reset the GUI
			else if (((String) arg).equals("reset")) {
				resetGUI();
			}
			// if a "close" is being sent, close the window
			else if (((String) arg).equals("close")) {
				dispose();
				System.exit(0);
			}
		}
	}
}
