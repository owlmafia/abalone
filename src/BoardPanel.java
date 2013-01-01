
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 * 
 * @author schuetz
 *
 */
public class BoardPanel extends JPanel {

	private static final int SPACE_BETWEEN_POSITIONS = 5;
	static final int SIDE = 350;
	private int diameter;
	
	//Buffer to calculate mapping boardposition -> screenCoords just 1 time
	private Point[] screenCoords = new Point[Game.POSITIONS];
	
	private static final Color BACKGROUND_COLOR = new Color(200, 200, 250);
	private static final Color POSITION_BORDER_COLOR = Color.black;
	private static final Color EMPTY_COLOR = new Color(0.7f, 0.7f, 0.7f);
	private static final Color SELECTED_COLOR = new Color(1f, 0.4f, 0.4f, 0.7f);
	private static final Color HOVER_COLOR = new Color(0.7f, 0.7f, 1f, 0.7f);
	private static final Color OPONENT_MOVE_COLOR = new Color(1f, 0.7f, 0.7f, 0.7f);

	private Image emptyBoardImage;
	private Image gameBoardImage;
	
	private Image blackMarbleImage = new ImageIcon(getClass().getClassLoader().getResource("img/blackmarble.png")).getImage();
	private Image whiteMarbleImage = new ImageIcon(getClass().getClassLoader().getResource("img/whitemarble.png")).getImage();
	
	BoardPanel(KeyListener keyListener) {
		
		initEmptyBoardImage();
		initGameBoardImage();
		
		setFocusable(true);
		requestFocus();
		
		Dimension dimension = new Dimension(SIDE, SIDE);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setSize(dimension);
		
		addKeyListener(keyListener);
	}
	
	private void initGameBoardImage() {
		gameBoardImage = createBufferedImage(SIDE, SIDE);
		clearBoard();
	}

	private void initEmptyBoardImage() {

		//total space amount for longest row
		int nineSpace = SPACE_BETWEEN_POSITIONS * 10;
		
		//calculate diameter of circles
		diameter = (Math.round((float)(SIDE - nineSpace) / 9));
		
		emptyBoardImage = createBufferedImage(SIDE, SIDE);
		Graphics boardGraphics = emptyBoardImage.getGraphics();
		boardGraphics.setColor(BACKGROUND_COLOR);
		boardGraphics.fillRect(0, 0, SIDE, SIDE);
		boardGraphics.setColor(POSITION_BORDER_COLOR);
		
		//x, y coords from each circle to draw
		int x = SPACE_BETWEEN_POSITIONS;
		int y = SPACE_BETWEEN_POSITIONS;
		
		//offset for odd rows
		int offset = diameter / 2;
		
		//start row length
		int circlesInRow = 5;
		
		//index in circle buffer
		int circleCount = 0;
		
		//helper vars
		byte incr = -1;
		byte incrCircles = 1;
		int ecount = 2;
		int ocount = 1;
		
		//rows
		for (int j = 0; j < 9; j++) {
			
			if (j % 2 == 0) {
				x = ecount * diameter + (ecount + 1) * SPACE_BETWEEN_POSITIONS;
				ecount = ecount + incr;
			} else {
				x = ocount * diameter + (ocount + 1) * SPACE_BETWEEN_POSITIONS + offset;
				ocount = ocount + incr;
			}
			
			for (int i = 0; i < circlesInRow; i++) {
				boardGraphics.drawOval(x, y, diameter, diameter);
				screenCoords[circleCount] = new Point(x, y);
				circleCount++;
				x = x + diameter + SPACE_BETWEEN_POSITIONS;
			}
			
			//decrement circle amount from 4th row
			if (j == 4) {
				incrCircles = -1;
				ecount += 2;
				ocount++;
				incr = 1;
			}
			circlesInRow += incrCircles;
			
			y = y + diameter + SPACE_BETWEEN_POSITIONS;
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(gameBoardImage, 0, 0, null);
	}

	private Image createBufferedImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		GraphicsDevice gs = ge.getDefaultScreenDevice(); 
		GraphicsConfiguration gc = gs.getDefaultConfiguration(); 
		return gc.createCompatibleImage(width, height, Transparency.OPAQUE); 
	}
	
	private Point getScreenCoords(int boardPosition) {
		return screenCoords[boardPosition];
	}
	
	void drawPositionState(int boardPosition, Player player, boolean selected, boolean hover, boolean oponentMoveHover) {
		if (selected && hover) {
			System.out.println("BoardPanel.drawPositionState() warning - selected and hover true");
		}
		Point screenCoords = getScreenCoords(boardPosition);
		Graphics g = gameBoardImage.getGraphics();

		drawPosition(g, screenCoords, player);
		
		if (selected) {
			g.setColor(SELECTED_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
		if (hover) {
			g.setColor(HOVER_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
		if (oponentMoveHover) {
			g.setColor(OPONENT_MOVE_COLOR);
			g.fillOval(screenCoords.x, screenCoords.y, diameter, diameter);
		}
	}
	
	private void drawPosition(Graphics g, Point coords, Player player) {
		if (player == null) {
			g.setColor(EMPTY_COLOR);
			g.fillOval(coords.x, coords.y, diameter, diameter);
			g.setColor(POSITION_BORDER_COLOR);
			g.drawOval(coords.x, coords.y, diameter, diameter);
		} else {
			Image marbleImage;
			if (player == Player.black) {
				marbleImage = blackMarbleImage;
			} else {
				marbleImage = whiteMarbleImage;
			}
			g.drawImage(marbleImage, coords.x, coords.y, null);
		}
	}
	
	void flush() {
		repaint();
		revalidate();
	}
	
	void clearBoard() {
		Graphics g = gameBoardImage.getGraphics();
		g.drawImage(emptyBoardImage, 0, 0, null);
	}
}
