/**
 * CS 350
 * Project #6
 * Austin Schey
 * CTetriMino.java: creates a CTetrMino object
 * comprised of CMinos
 */

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

public class CTetriMino implements Serializable {
	private static final long serialVersionUID = 9011764794386176041L;
	private int x;
	private int y;
	private int type;
	private int diameter;
	private int distanceToNext;
	private Color fillColor;
	private CMino[] cMinos;
	private boolean moved;
	
	public CTetriMino(int x, int y, int diameter, int type, Color fillColor) {
		this.x = x;
		this.y = y;
		this.diameter = diameter;
		this.type = type;
		this.fillColor = fillColor;
		this.createMinos();
	}
	
	public CTetriMino(CTetriMino ctm) {
		// make a duplicate
		this.x = ctm.getX();
		this.y = ctm.getY();
		this.diameter = ctm.getDiameter();
		this.type = ctm.getType();
		this.fillColor = ctm.getColor();
		this.createMinos();
	}
	
	private CMino minoWithOffset(int xOffset, int yOffset) {
		return new CMino(this.x + this.diameter * xOffset, 
				this.y + this.diameter * yOffset, this.diameter, this.fillColor);
	}
	
	private void createMinos() {
		this.cMinos = new CMino[4];
		
		this.cMinos[0] = this.minoWithOffset(0, 0);
		this.cMinos[1] = this.minoWithOffset(1, 0);
		// create seven different shapes of minos
		switch(this.type) {
			case 1:
				this.cMinos[2] = this.minoWithOffset(1, -1);
				this.cMinos[3] = this.minoWithOffset(0, -1);
				this.distanceToNext = 60;
				break;
			
			case 2:
				this.cMinos[2] = this.minoWithOffset(2, 0);
				this.cMinos[3] =  this.minoWithOffset(3, 0);
				this.distanceToNext = 100;
				break;
				
			case 3:
				this.cMinos[2] = this.minoWithOffset(2, 0);
				this.cMinos[3] = this.minoWithOffset(1, -1);
				this.distanceToNext = 80;
				break;
			
			case 4:
				this.cMinos[2] = this.minoWithOffset(2, 0);
				this.cMinos[3] = this.minoWithOffset(2, -1);
				this.distanceToNext = 80;
				break;
			
			case 5:
				this.cMinos[2] = this.minoWithOffset(2, 0);
				this.cMinos[3] = this.minoWithOffset(0, -1);
				this.distanceToNext = 80;
				break;
			
			case 6:
				this.cMinos[2] = this.minoWithOffset(1, -1);
				this.cMinos[3] = this.minoWithOffset(2, -1);
				this.distanceToNext = 100;
				break;
			
			case 7:
				this.cMinos[2] = this.minoWithOffset(0, -1);
				this.cMinos[3] = this.minoWithOffset(-1, -1);
				this.distanceToNext = 0;
				break;
		}
	}
	
	public int getDistanceToNext() { 
		return this.distanceToNext; 
	}
	
	public void setX(int newX) {
		this.moved = true;
		int xOffset = newX - this.x;
		this.x = newX;
		for (CMino cm : this.cMinos) {
			cm.setX(cm.getX() + xOffset);
		}
	}
	
	public void setY(int newY) {
		this.moved = true;
		int yOffset = newY - this.y;
		this.y = newY;
		for (CMino cm : this.cMinos) {
			cm.setY(cm.getY() + yOffset);
		}
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getDiameter() {
		return this.diameter;
	}
	
	public Color getColor() {
		return this.fillColor;
	}
	
	public boolean isMoved() {
		return this.moved;
	}
	
	public void draw(Graphics g) {
		for (CMino cm : this.cMinos) {
			g.setColor(this.fillColor);
			g.fillOval(cm.getX(), cm.getY(), cm.getDiameter(), cm.getDiameter());
			g.setColor(Color.BLACK);
			g.drawOval(cm.getX(), cm.getY(), cm.getDiameter(), cm.getDiameter());
		}
	}
	
	public boolean containsPoint(int x, int y) {
		for (CMino cm : this.cMinos) {
			if (cm.containsPoint(x, y)) { 
				return true; 
			}
		}
		return false;
	}
	
	private CMino getClickedMino(int x, int y) {
		for (CMino cm : this.cMinos) {
			if (cm.containsPoint(x, y)) {
				return cm;
			}
		}
		return null;
	}
	
	public void rotate(int x, int y) {
		CMino baseMino = this.getClickedMino(x, y);
		int x0 = baseMino.getX();
		int y0 = baseMino.getY();
		for (CMino cm : this.cMinos) {
			int x1 = cm.getX();
			int y1 = cm.getY();
			cm.setX(x0 + (y1 - y0));
			cm.setY(y0 - (x1 - x0));
		}
	}
	
	public boolean shouldDelete(int threshold) {
		for (CMino cm : this.cMinos) {
			if (cm.getY() >= threshold - this.diameter) {
				return true;
			}
		}
		return false;
	}
}
