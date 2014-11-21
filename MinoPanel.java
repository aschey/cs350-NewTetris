/**
 * CS 350
 * Project #6
 * Austin Schey
 * MinoPanel.java: extends a JPanel to display a 
 * panel of CTetriMinos with networking capability
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;

public abstract class MinoPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private ArrayList<CTetriMino> originalCtms;
	private ArrayList<CTetriMino> newCtms;
	private CTetriMino selectedMino;
	private Image backBuffer;
	private Graphics gBackBuffer;
	private int xOffset;
	private int yOffset;
	private boolean isInitialized;
	private Color[] fillColors = {Color.YELLOW, Color.ORANGE, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.RED};

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket socket;
	
	private final int RADIUS = 20;
	private final int BOTTOM_SECTION_X = 400;
	
	public MinoPanel() {
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.originalCtms = new ArrayList<>();
		this.newCtms = new ArrayList<>();
		this.selectedMino = null;
		this.isInitialized = false;
	}
	
	private void init() {
		final int INIT_Y = 30;
		int x = 20;
		
		for (int i = 0; i < 7; i++) {
			this.originalCtms.add(new CTetriMino(x, INIT_Y, this.RADIUS, i+1, this.fillColors[i]));
			x += this.originalCtms.get(i).getDistanceToNext();
		}
		
		this.backBuffer = this.createImage(this.getWidth(), this.getHeight());
		this.gBackBuffer = this.backBuffer.getGraphics();
	}
	
	private void searchForClickedMino(ArrayList<CTetriMino> ctms, int x, int y) {
		for (int i = ctms.size()-1; i >= 0; i--) {
			CTetriMino ctm = ctms.get(i);
			if (ctm.containsPoint(x, y)) {
				if (ctms == this.originalCtms) {
					this.selectedMino = new CTetriMino(ctm);
				}
				else {
					this.selectedMino = ctm;
				}
				this.moveSelectedMinoToBack();
				// exit once the mino has been found
				return;
			}
		}
		// if no CTetriMino was found, the user clicked on whitespace
		this.selectedMino = null;
	}
	
	private void deleteSelectedMino() {
		this.newCtms.remove(this.selectedMino);
		this.selectedMino = null;
	}
	
	private void moveSelectedMinoToBack() {
		this.newCtms.remove(this.selectedMino);
		this.newCtms.add(this.selectedMino);
	}
	
	private void calculateOffsets(MouseEvent e) {
		this.xOffset = e.getX() - this.selectedMino.getX();
		this.yOffset = e.getY() - this.selectedMino.getY();
	}
	
	private void addMinoOffsets(MouseEvent e) {
		this.selectedMino.setX(e.getX() - this.xOffset);
		this.selectedMino.setY(e.getY() - this.yOffset);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		final int TOP_SECTION_X = 60;
		final int BOTTOM_SECTION_HEIGHT = 200;
		final int WIDTH = 600;
		super.paintComponent(g);
		if (!this.isInitialized) {
			this.init();
			this.isInitialized = true;
		}
		
		this.gBackBuffer.setColor(Color.WHITE);
		this.gBackBuffer.clearRect(0, 0, this.getSize().width, this.getSize().height);
		
		
		for (CTetriMino ctm : this.originalCtms) {
			ctm.draw(this.gBackBuffer);
		}
		
		for (CTetriMino ctm : this.newCtms) {
			ctm.draw(this.gBackBuffer);
		}
		
		this.gBackBuffer.setColor(Color.LIGHT_GRAY);
		this.gBackBuffer.fillRect(0, BOTTOM_SECTION_X, WIDTH, BOTTOM_SECTION_HEIGHT);
		this.gBackBuffer.setColor(Color.BLACK);
		this.gBackBuffer.drawLine(0, BOTTOM_SECTION_X, WIDTH, BOTTOM_SECTION_X);
		this.gBackBuffer.drawLine(0, TOP_SECTION_X, WIDTH, TOP_SECTION_X);
		
		g.drawImage(this.backBuffer, 0, 0, null);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// look for the clicked mino in the original set
		this.searchForClickedMino(this.newCtms, e.getX(), e.getY());
		// if not found, look in the new minos
		if (this.selectedMino == null) {
			this.searchForClickedMino(this.originalCtms, e.getX(), e.getY());
		}
		
		// don't do anything if the right mouse button was used
		if (this.selectedMino == null || SwingUtilities.isRightMouseButton(e)) {
			return;
		}
		
		this.calculateOffsets(e);
		this.sendData();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.selectedMino == null || SwingUtilities.isRightMouseButton(e)) {
			// don't do anything if the object is dragged with the right mouse button
			this.selectedMino = null;
			return;
		}
		
		this.addMinoOffsets(e);

		this.sendData();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.selectedMino != null && SwingUtilities.isRightMouseButton(e) && this.selectedMino.isMoved()) {
			this.selectedMino.rotate(e.getX(), e.getY());
			this.sendData();
		}
		
		// delete the mino if it is past the bottom line
		if (this.selectedMino != null && this.selectedMino.shouldDelete(this.BOTTOM_SECTION_X)) {
			this.deleteSelectedMino();
			this.sendData();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	protected void processConnection() {
		while (true) {
			try {
				this.newCtms = (ArrayList<CTetriMino>) this.input.readObject();
				this.repaint();
			}
			catch (IOException | ClassNotFoundException ex) {
				break;
			}
		}
	}

	protected void getStreams() {
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.output.flush();
			this.input = new ObjectInputStream(this.socket.getInputStream());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void sendData() {
		try {
			this.output.reset();
			this.output.writeObject(newCtms);
			this.output.flush();
			this.repaint();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void closeConnection() {
		try {
			this.input.close();
			this.output.close();
			this.socket.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void createSocket(Socket socket) {
		this.socket = socket;
	}
}
