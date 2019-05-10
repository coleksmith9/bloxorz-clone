/**
 * Responsible for drawing the game field and the player in their current
 * position. The screen will be drawn in an approximated version of an isometric
 * drawing. Each square is technically SQUARE_DIMENSION x SQUARE_DIMENSION, but
 * to get to the next point to draw to, you take the sine and cosine of the
 * angle * SQUARE_DIMENSION and draw a line between them.
 */
package bloxorz;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends javax.swing.JPanel implements KeyListener {

	// stores the layout of the current map and player
	Map currentGame;
	String[][] mapArray;
	int exitRow, exitCol;
	int startRow, startCol;

	// angle and side length for calculations
	final int ANGLE = 30;
	final int SQUARE_DIMENSION = 40;

	// holds data about the vertices of the player and the inner edges
	Polygon player;
	private Color playerColor;
	int xTopLeft, xMiddle, xTopRight, xBottom, yTopLeft, yMiddle, yTopRight, yBottom;

	// timers used to delay the appearance of the "you lose" and "you win" screens
	int counter;
	Timer lostTimer;
	Timer wonTimer;

	// test map
	final Map TEST_MAP = new Map(
			new String[][]{
				{"X", "P", "X", "X", "X"},
				{"X", "X", " ", "X", "X"},
				{"X", "H", "X", "X", "X"},
				{"X", "X", "X", "X", "X"}}, 1, 0);

	boolean gameOver;

	public GamePanel() {
		initComponents();

		// Timer used to delay the game over screen
		this.lostTimer = new Timer(10, (ActionEvent ev) -> {
			counter++;
			if (counter > 100) {
				JOptionPane.showMessageDialog(GamePanel.this, "You fell off the map!", "Uh oh!", JOptionPane.ERROR_MESSAGE);
				String choice = (String) JOptionPane.showInputDialog(GamePanel.this, "Play again or exit?", "You lose!", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Play again", "Exit", "Different level"}, "Play again");
				switch (choice) {
					case "Play again":
						startGame();
						break;
					case "Different level":
						openLevelSelector();
						break;
					default:
						System.exit(0);
				}
				lostTimer.stop();
			}
		});

		// Timer used to delay the "you won" screen
		this.wonTimer = new Timer(10, (ActionEvent ev) -> {
			counter++;
			if (counter > 100) {
				JOptionPane.showMessageDialog(GamePanel.this, "You won!", "Awesome!", JOptionPane.PLAIN_MESSAGE);
				String choice = (String) JOptionPane.showInputDialog(GamePanel.this, "Play again or exit?", "You lose!", JOptionPane.PLAIN_MESSAGE, null, new String[]{"Play again", "Exit", "Different level"}, "Play again");
				switch (choice) {
					case "Play again":
						startGame();
						break;
					case "Different level":
						openLevelSelector();
						break;
					default:
						System.exit(0);
				}
				wonTimer.stop();
			}
		});

		counter = 0;

		currentGame = TEST_MAP;
		mapArray = currentGame.getLayout();
		getExitCoordinates();
		
		

		startGame();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// draws the map
		for (int r = 0; r < mapArray.length; r++) {
			for (int c = 0; c < mapArray[0].length; c++) {
				getColor(r, c, g);
				Polygon p = getTilePolygon(r, c);
				// draws filled polygon of the right color
				g.fillPolygon(p);

				// adds black border around it
				getBorderColor(r, c, g);
				g.drawPolygon(p);
			}
		}

		// adds x and y labels for debug
//		g.drawString("X", 80, 80);
//		g.drawString("Y", 80, 240);
		
		// draws the player
		getPlayerPolygon();
		g.setColor(playerColor);
		g.fillPolygon(player);

		// draws black outline around the player
		g.setColor(Color.BLACK);
		g.drawPolygon(player);

		// draws edges of the player
		getInnerLinesOfPlayer(currentGame.posX, currentGame.posY, currentGame.direction);
		g.drawLine(xTopLeft, yTopLeft, xMiddle, yMiddle);
		g.drawLine(xTopRight, yTopRight, xMiddle, yMiddle);
		g.drawLine(xBottom, yBottom, xMiddle, yMiddle);
	}

	/**
	 * This method returns a Polygon object that contains the required points to
	 * draw the tile based on the row and column within the 2D array.
	 */
	private Polygon getTilePolygon(int r, int c) {
		return new Polygon(getXPoints(r, c), getYPoints(r, c), 4);
	}

	/**
	 * @param r row within the 2D array
	 * @param c col within the 2D array
	 * @return int[] with x values
	 */
	private int[] getXPoints(int r, int c) {
		double cosValue = Math.cos(Math.toRadians(ANGLE));
		int xStartPos = 50 + (int) ((r + c) * SQUARE_DIMENSION * cosValue);
		return new int[]{xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + (int) (SQUARE_DIMENSION * cosValue)};
	}

	/**
	 * @param r row within 2D array
	 * @param c col within 2D array
	 * @return int[] with y values
	 */
	private int[] getYPoints(int r, int c) {
		int yStartPos;
		double sinValue = Math.sin(Math.toRadians(ANGLE));
		if (r + c == 0) {
			yStartPos = this.getHeight() / 2 - (int) ((c - r) * SQUARE_DIMENSION * sinValue);
		} else {
			yStartPos = this.getHeight() / 2 - (int) ((c - r) * SQUARE_DIMENSION * sinValue) - (r - c);
		}

		return new int[]{yStartPos, yStartPos + (int) (SQUARE_DIMENSION * sinValue), yStartPos, yStartPos - (int) (SQUARE_DIMENSION * sinValue)};
	}

	/**
	 * This method sets the color of g based on the type of tile it is within
	 * the map.
	 *
	 * @param r row within 2D array
	 * @param c col within 2D array
	 * @param g Graphics component, needed to set color
	 */
	private void getColor(int r, int c, Graphics g) {
		switch (mapArray[r][c].toUpperCase()) {
			case "H":
				g.setColor(Color.BLACK);
				break;
			case " ":
				g.setColor(this.getBackground());
				break;
			case "P":
				g.setColor(Color.RED);
				break;
			default:
				g.setColor(Color.GRAY);
		}
	}

	/**
	 * This method sets the color of g based on the type of tile it is within
	 * the map. If it is an empty tile, it sets it to the background color.
	 * Else, it is black.
	 *
	 * @param r row within 2D array
	 * @param c col within 2D array
	 * @param g Graphics component, needed to set color
	 */
	private void getBorderColor(int r, int c, Graphics g) {
		switch (mapArray[r][c]) {
			case " ":
				g.setColor(this.getBackground());
				break;
			default:
				g.setColor(Color.BLACK);
				break;
		}
	}

	/**
	 * Sets player to contain the correct x and y points to draw the irregular
	 * hexagon based on row, col, and direction.
	 */
	private void getPlayerPolygon() {
		int x = currentGame.getPosX();
		int y = currentGame.getPosY();

		int[] xPoints = getPlayerXPoints(x, y, currentGame.getDirection());
		int[] yPoints = getPlayerYPoints(x, y, currentGame.getDirection());
		player = new Polygon(xPoints, yPoints, 6);
	}

	/**
	 * @param x x pos in 2D array
	 * @param y y pos in 2D array
	 * @param direction which direction the player is facing
	 * @return int[] of x points of the player's polygon
	 */
	private int[] getPlayerXPoints(int x, int y, String direction) {
		double cosValue = Math.cos(Math.toRadians(ANGLE));
		int xStartPos = 50 + (int) ((x + y) * SQUARE_DIMENSION * cosValue);
		switch (direction) {
			case "V":
				return new int[]{xStartPos, xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + (int) (SQUARE_DIMENSION * cosValue)};
			case "F":
				return new int[]{xStartPos, xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue)};
			case "B":
				// has to recalculate xStartPos since it usually starts on the bottom left corner
				xStartPos += (int) (SQUARE_DIMENSION * cosValue);
				return new int[]{xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos - (int) (SQUARE_DIMENSION * cosValue), xStartPos - 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos - 2 * (int) (SQUARE_DIMENSION * cosValue)};
			case "R":
				return new int[]{xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos, xStartPos - (int) (SQUARE_DIMENSION * cosValue)};
			case "L":
				xStartPos -= (int) (SQUARE_DIMENSION * cosValue);
				return new int[]{xStartPos, xStartPos + (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue), xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue), xStartPos, xStartPos - (int) (SQUARE_DIMENSION * cosValue)};
			default:
				return null;
		}
	}

	/**
	 * @param x x pos in 2D array
	 * @param y y pos in 2D array
	 * @param direction which direction the player is facing
	 * @return int[] of y points of the player's polygon
	 */
	private int[] getPlayerYPoints(int x, int y, String direction) {
		double sinValue = Math.sin(Math.toRadians(ANGLE));
		int yStartPos;
		if (x + y == 0) {
			yStartPos = this.getHeight() / 2 - (int) ((x - y) * SQUARE_DIMENSION * sinValue);
		} else {
			yStartPos = this.getHeight() / 2 - (int) ((x - y) * SQUARE_DIMENSION * sinValue) - (y - x);
		}

		switch (direction) {
			case "V":
				return new int[]{yStartPos, yStartPos - 2 * SQUARE_DIMENSION, yStartPos - 2 * SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue), yStartPos - 2 * SQUARE_DIMENSION, yStartPos, yStartPos + (int) (SQUARE_DIMENSION * sinValue)};
			case "F":
				return new int[]{yStartPos, yStartPos - SQUARE_DIMENSION, yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION + (int) (SQUARE_DIMENSION * sinValue), yStartPos + (int) (SQUARE_DIMENSION * sinValue), yStartPos + 2 * (int) (SQUARE_DIMENSION * sinValue)};
			case "B":
				// has to recalculate yStartPos since it usually is the bottom left corner 
				yStartPos += (int) (SQUARE_DIMENSION * sinValue);
				return new int[]{yStartPos, yStartPos - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - 3 * (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - 2 * (int) (SQUARE_DIMENSION * sinValue), yStartPos - 2 * (int) (SQUARE_DIMENSION * sinValue)};
			case "R":
				return new int[]{yStartPos, yStartPos + (int) (SQUARE_DIMENSION * sinValue), yStartPos - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - 2 * (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION};
			case "L":
				yStartPos += (int) (SQUARE_DIMENSION * sinValue);
				return new int[]{yStartPos, yStartPos + (int) (SQUARE_DIMENSION * sinValue), yStartPos - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION - 2 * (int) (SQUARE_DIMENSION * sinValue), yStartPos - SQUARE_DIMENSION};
			default:
				return null;
		}
	}

	/**
	 * This method calculates the coordinates used to draw the inner edges of
	 * the player based off direction, x, and y.
	 *
	 * @param x x pos in 2D array
	 * @param y y pos in 2D array
	 * @param direction which direction the player is facing
	 */
	private void getInnerLinesOfPlayer(int x, int y, String direction) {
		// position of the bottom left corner
		double cosValue = Math.cos(Math.toRadians(ANGLE));
		double sinValue = Math.sin(Math.toRadians(ANGLE));
		int xStartPos = 50 + (int) ((x + y) * SQUARE_DIMENSION * cosValue);
		int yStartPos;
		if (x + y == 0) {
			yStartPos = this.getHeight() / 2 - (int) ((x - y) * SQUARE_DIMENSION * sinValue);
		} else {
			yStartPos = this.getHeight() / 2 - (int) ((x - y) * SQUARE_DIMENSION * sinValue) - (y - x);
		}

		switch (direction) {
			case "V":
				xTopLeft = xStartPos;
				yTopLeft = yStartPos - 2 * SQUARE_DIMENSION;

				xMiddle = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yMiddle = yStartPos - (int) (SQUARE_DIMENSION * sinValue) - SQUARE_DIMENSION - 3;

				xTopRight = xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue);
				yTopRight = yStartPos - 2 * SQUARE_DIMENSION;

				xBottom = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yBottom = yStartPos + (int) (SQUARE_DIMENSION * sinValue);
				break;
			case "F":
				xTopLeft = xStartPos;
				yTopLeft = yStartPos - SQUARE_DIMENSION;

				xMiddle = xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue);
				yMiddle = yStartPos - 2;

				xTopRight = xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue);
				yTopRight = yStartPos - (int) (SQUARE_DIMENSION * sinValue) - 2;

				xBottom = xStartPos + 2 * (int) (SQUARE_DIMENSION * cosValue);
				yBottom = yStartPos + SQUARE_DIMENSION - 3;
				break;
			case "B":
				xStartPos += (int) (SQUARE_DIMENSION * cosValue);
				yStartPos += (int) (SQUARE_DIMENSION * sinValue);

				xTopLeft = xStartPos - 2 * (int) (SQUARE_DIMENSION * cosValue);
				yTopLeft = yStartPos - SQUARE_DIMENSION - 2 * (int) (SQUARE_DIMENSION * sinValue);

				xMiddle = xStartPos;
				yMiddle = yStartPos - SQUARE_DIMENSION;

				xBottom = xStartPos;
				yBottom = yStartPos;

				xTopRight = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yTopRight = yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue);
				break;
			case "R":
				xTopLeft = xStartPos;
				yTopLeft = yStartPos - SQUARE_DIMENSION;

				xMiddle = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yMiddle = yStartPos - SQUARE_DIMENSION + (int) (SQUARE_DIMENSION * sinValue);

				xBottom = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yBottom = yStartPos + (int) (SQUARE_DIMENSION * sinValue);

				xTopRight = xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue);
				yTopRight = yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue);
				break;
			case "L":
				xStartPos -= (int) (SQUARE_DIMENSION * cosValue);
				yStartPos += (int) (SQUARE_DIMENSION * sinValue);

				xTopLeft = xStartPos;
				yTopLeft = yStartPos - SQUARE_DIMENSION;

				xMiddle = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yMiddle = yStartPos - SQUARE_DIMENSION + (int) (SQUARE_DIMENSION * sinValue);

				xBottom = xStartPos + (int) (SQUARE_DIMENSION * cosValue);
				yBottom = yStartPos + (int) (SQUARE_DIMENSION * sinValue);

				xTopRight = xStartPos + 3 * (int) (SQUARE_DIMENSION * cosValue);
				yTopRight = yStartPos - SQUARE_DIMENSION - (int) (SQUARE_DIMENSION * sinValue);
				break;
		}
	}

	/**
	 * This method sets the start position of the player, makes them face
	 * vertically, and repaints.
	 */
	public final void startGame() {
		gameOver = false;
		
		// randomly generates a color for the player
		playerColor = new Color((int)(255 * Math.random()), (int)(255 * Math.random()), (int)(255 * Math.random()));

		getStartCoordinates();

		currentGame.setPosX(startCol);
		currentGame.setPosY(startRow);
		currentGame.setDirection("V");

		calculateSecondPoint();

		repaint();
	}

	/**
	 * This method tests for key presses. On key press, it does the following.
	 * 1. Moves the player. 2. Sets their direction. 3. Repaints. 4. Calculates
	 * the point for the second section of the player. 5. Tests if they are on a
	 * hole or out of bounds. 6. Tests if they have won.
	 *
	 * @param e key that is pressed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (gameOver) {
			return;
		}

		int key = e.getKeyCode();

		// calculates move based off key pressed and current direction
		switch (key) {
			case (KeyEvent.VK_DOWN):
				setNextPos(KeyEvent.VK_DOWN);
				currentGame.setDirection(getNextDirection(currentGame.direction, KeyEvent.VK_DOWN));
				break;
			case (KeyEvent.VK_UP):
				setNextPos(KeyEvent.VK_UP);
				currentGame.setDirection(getNextDirection(currentGame.direction, KeyEvent.VK_UP));
				break;
			case (KeyEvent.VK_LEFT):
				setNextPos(KeyEvent.VK_LEFT);
				currentGame.setDirection(getNextDirection(currentGame.direction, KeyEvent.VK_LEFT));
				break;
			case (KeyEvent.VK_RIGHT):
				setNextPos(KeyEvent.VK_RIGHT);
				currentGame.setDirection(getNextDirection(currentGame.direction, KeyEvent.VK_RIGHT));
				break;
		}
		repaint();
		calculateSecondPoint();

		counter = 0;
		if (!isInBounds()) {
			gameOver = true;

			lostTimer.start();
		} else if (isOnHole()) {
			gameOver = true;

			lostTimer.start();
		} else if (hasWon()) {
			gameOver = true;

			wonTimer.start();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * This method contains the logic to get the new direction the player will
	 * be facing based off how they were facing and which direction they are
	 * moving.
	 *
	 * @param facing current direction they are facing ("V", "B", "F", "L", "R")
	 * @param movementDirection direction the player is moving based on keypress
	 * @return String version of the new direction ("V", "B", "F", "L", "R")
	 */
	private String getNextDirection(String facing, int movementDirection) {
		switch (facing) {
			case "L":
				if (movementDirection == KeyEvent.VK_DOWN || movementDirection == KeyEvent.VK_UP) {
					return "V";
				} else {
					return "L";
				}
			case "R":
				if (movementDirection == KeyEvent.VK_DOWN || movementDirection == KeyEvent.VK_UP) {
					return "V";
				} else {
					return "R";
				}
			case "V":
				if (movementDirection == KeyEvent.VK_DOWN) {
					return "L";
				} else if (movementDirection == KeyEvent.VK_UP) {
					return "R";
				} else if (movementDirection == KeyEvent.VK_RIGHT) {
					return "F";
				} else if (movementDirection == KeyEvent.VK_LEFT) {
					return "B";
				}
			case "F":
				if (movementDirection == KeyEvent.VK_DOWN || movementDirection == KeyEvent.VK_UP) {
					return "F";
				} else {
					return "V";
				}
			case "B":
				if (movementDirection == KeyEvent.VK_DOWN || movementDirection == KeyEvent.VK_UP) {
					return "B";
				} else {
					return "V";
				}
		}
		return currentGame.getDirection();
	}

	/**
	 * This method contains the logic for properly incrementing the position of
	 * the player based on the key they press to move.
	 *
	 * @param keyPressed KeyEvent.VK_"direction"
	 */
	private void setNextPos(int keyPressed) {
		switch (keyPressed) {
			case KeyEvent.VK_DOWN:
				if (currentGame.getDirection().equals("L")) {
					currentGame.setPosX(currentGame.posX - 2);
				} else {
					currentGame.setPosX(currentGame.posX - 1);
				}
				break;
			case KeyEvent.VK_UP:
				if (currentGame.getDirection().equals("R")) {
					currentGame.setPosX(currentGame.posX + 2);
				} else {
					currentGame.setPosX(currentGame.posX + 1);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (currentGame.getDirection().equals("B")) {
					currentGame.setPosY(currentGame.posY - 2);
				} else {
					currentGame.setPosY(currentGame.posY - 1);
					currentGame.setPosY2(currentGame.posY2 - 1);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (currentGame.getDirection().equals("F")) {
					currentGame.setPosY(currentGame.posY + 2);
				} else {
					currentGame.setPosY(currentGame.posY + 1);
				}
				break;
		}
	}

	/**
	 * Determines whether or not a player is within the bounds of the map. If
	 * they are out of them, the "GAME OVER" screen will be displayed, where the
	 * player has the option to quit or restart the level.
	 */
	private boolean isInBounds() {
		if (currentGame.posX < 0 || currentGame.posY < 0 || currentGame.posX >= mapArray[0].length || currentGame.posY >= mapArray.length) {
			return false;
		} else if (currentGame.posX2 < 0 || currentGame.posY2 < 0 || currentGame.posX2 >= mapArray[0].length || currentGame.posY2 >= mapArray.length) {
			return false;
		}
		return true;
	}

	/**
	 * This method determines whether the player has won by comparing their
	 * coordinates to the exit coordinates and checking if they are vertical.
	 *
	 * @return if the game is over, ie has the player won
	 */
	private boolean hasWon() {
		if (currentGame.posY == exitRow && currentGame.posX == exitCol && currentGame.direction.equals("V")) {
			gameOver = true;
//			System.out.println("You win!");
		}
		return gameOver;
	}

	/**
	 * This method locates the coordinates of the exit within the 2D array by
	 * looping through and looking for "H".
	 */
	private void getExitCoordinates() {
		for (int r = 0; r < mapArray.length; r++) {
			for (int c = 0; c < mapArray[0].length; c++) {
				if ("H".equals(mapArray[r][c])) {
					exitRow = r;
					exitCol = c;
					return;
				}
			}
		}
	}

	/**
	 * This method takes the current coordinates of the player and their
	 * direction and calculates the coordinates of the second half of the player
	 * in order to calculate bounds and holes.
	 */
	private void calculateSecondPoint() {

		switch (currentGame.direction) {
			case ("V"):
				currentGame.setPosX2(currentGame.posX);
				currentGame.setPosY2(currentGame.posY);
				break;
			case ("L"):
				currentGame.setPosX2(currentGame.posX - 1);
				currentGame.setPosY2(currentGame.posY);
				break;
			case ("R"):
				currentGame.setPosX2(currentGame.posX + 1);
				currentGame.setPosY2(currentGame.posY);
				break;
			case ("F"):
				currentGame.setPosX2(currentGame.posX);
				currentGame.setPosY2(currentGame.posY + 1);
				break;
			case ("B"):
				currentGame.setPosX2(currentGame.posX);
				currentGame.setPosY2(currentGame.posY - 1);
				break;
		}
//		System.out.println("posX: " + currentGame.posX);
//		System.out.println("posX2: " + currentGame.posX2);
//		System.out.println("posY: " + currentGame.posY);
//		System.out.println("posY2: " + currentGame.posY2);
	}

	/**
	 * Determines whether the player is on a hole by checking if each section is
	 * currently on a hole.
	 *
	 * @return true or false
	 */
	private boolean isOnHole() {
		try {
			return (mapArray[currentGame.posY][currentGame.posX].equals(" ") || mapArray[currentGame.posY2][currentGame.posX2].equals(" "));
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}

	/**
	 * This method sets the current game to the new map, including setting the
	 * layout, putting the player in position, and finding the exit coordinates.
	 *
	 * @param newGame Map that contains a 2D array and start coordinates.
	 */
	public void setCurrentGame(Map newGame) {
		this.currentGame = newGame;
		mapArray = currentGame.getLayout();

		getStartCoordinates();
		currentGame.setPosX(startCol);
		currentGame.setPosY(startRow);

		getExitCoordinates();
	}

	/**
	 * Locates the start position of the player in order to place them in the
	 * right position at the start of the game. Loops through the 2D array and
	 * looks for "P". Stops looking after it finds one.
	 */
	private void getStartCoordinates() {
		for (int r = 0; r < mapArray.length; r++) {
			for (int c = 0; c < mapArray[0].length; c++) {
				if ("P".equals(mapArray[r][c].toUpperCase())) {
					startRow = r;
					startCol = c;
					return;
				}
			}
		}
	}

	/**
	 * Opens the level selector screen in the Window class.
	 */
	private void openLevelSelector() {
		((Window) SwingUtilities.getWindowAncestor(this)).openLevelSelector();
	}
}
