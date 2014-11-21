/**
 * CS 350
 * Project #6
 * Austin Schey
 * CMino.java: creates a CMino object
 */

import java.awt.Color;
import java.io.Serializable;

public class CMino implements Serializable {
	private static final long serialVersionUID = 1171637397933021813L;
	private int x;
	private int y;
	private int diameter;
	private Color fillColor;
	
	public CMino(int x, int y, int diameter, Color fillColor) {
		this.x = x;
		this.y = y;
		this.diameter = diameter;
		this.fillColor = fillColor;
	}
	
	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public int getDiameter() { return this.diameter; }
	public Color getColor() { return this.fillColor; }
	public void setX(int x) {this.x = x; }
	public void setY(int y) {this.y = y; }
	
	public boolean containsPoint(int newX, int newY) {
		int radius = this.diameter / 2;
		// add the radius to account for the fact that the x and y values
		// are in the corner instead of the middle of the circle
		int xLen = newX - (this.x + radius);
		int yLen = newY - (this.y + radius);
		double distance = Math.sqrt(Math.pow(xLen, 2) + Math.pow(yLen, 2));
		return distance <= radius;
	}
}
