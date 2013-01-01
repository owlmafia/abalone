import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 * 
 * @author schuetz
 *
 */
public class KickedPanel extends JPanel {
	
	private static final Color BACKGROUND_COLOR = Color.lightGray;
	
	private final Image whiteKickedImage = new ImageIcon(getClass().getClassLoader().getResource("img/whitemarble2.png")).getImage();
	private final Image blackKickedImage = new ImageIcon(getClass().getClassLoader().getResource("img/blackmarble2.png")).getImage();
	private int whiteKicked;
	private int blackKicked;
	
	private int diameter = 15;
	private int spaceX = 5;
	private int spaceY = 7;
	
	KickedPanel() {
		setBackground(BACKGROUND_COLOR);
		
		Dimension dimension = new Dimension(250, 50);
		setSize(dimension);
		setPreferredSize(dimension);
		setMaximumSize(dimension);
		setMinimumSize(dimension);
	}
	
	public void showKickOut(Player player) {
		if (player == Player.white) {
			whiteKicked++;
		} else {
			blackKicked++;
		}
		repaint();
		revalidate();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int x = spaceX;
		int y = spaceY;
		
		g.setColor(Player.white.color);
		for (int i = 0; i < whiteKicked; i++) {
			g.drawImage(whiteKickedImage, x, y, null);
			x += diameter + spaceX;
		}
		x = spaceX;
		y += diameter + spaceY;
			
		g.setColor(Player.black.color);
		for (int i = 0; i < blackKicked; i++) {
			g.drawImage(blackKickedImage, x, y, null);
			x += diameter + spaceX;
		}
	}

	void refresh() {
		repaint();
		revalidate();
	}
}