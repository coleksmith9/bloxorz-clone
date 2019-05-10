/**
 * This class is responsible for holding the layout of the map at a given time.
 * X = normal square............................................................
 * H = hole, the goal area the player is trying to enter........................
 * P = player's start position, from which they can be vertical or horizontal...
 * ' ' = an empty spot on the board. If the user is vertically on a space or....
 * half of them is on it when laying down, they fall down and die...............
 */
package bloxorz;

import java.io.Serializable;

/**
 *
 * @author smith_859800
 */
class Map implements Serializable {

	public int getPosX2() {
		return posX2;
	}

	public void setPosX2(int posX2) {
		this.posX2 = posX2;
	}

	public int getPosY2() {
		return posY2;
	}

	public void setPosY2(int posY2) {
		this.posY2 = posY2;
	}
	// instance variables
	// the starting layout of the map
	String[][] layout;
	
	// index of the player position in the 2D array
	int posX, posY;
	
	// index of the other part of the player
	int posX2, posY2;
	
	// direction of the player (V = vertical, L = left, R = right, B = back, F = front)
	/**
	 * B   R
	 *  \ / 
	 *   P
	 *	/ \
	 * L   F
	 */
	String direction;

	public Map(String[][] layout, int posX, int posY) {
		this.layout = layout;
		this.posX = posX;
		this.posY = posY;
		posX2 = posX;
		posY2 = posY;
		direction = "V";
	}

	public String[][] getLayout() {
		return layout;
	}

	public void setLayout(String[][] layout) {
		this.layout = layout;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	
}
