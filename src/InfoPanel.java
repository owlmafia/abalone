import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;


public class InfoPanel extends JPanel {

	private KickedPanel kickedPanel;
	private static final int WIDTH = 125;
	private static final Color BACKGROUND_COLOR = Color.black; 
	private JLabel winnerLabel;
	
	InfoPanel(Player player) {
		setBackground(BACKGROUND_COLOR);
		Dimension infoPanelDimension = new Dimension(WIDTH, BoardPanel.SIDE);
		setPreferredSize(infoPanelDimension);
		BoxLayout boxLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		
		Border line = BorderFactory.createMatteBorder(0, 2, 0, 0, new java.awt.Color(100, 100, 100));
		setBorder(line);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new FlowLayout());
		playerPanel.setBackground(Color.gray);
		Dimension playerPanelDimension = new Dimension(125, 35);
		playerPanel.setSize(playerPanelDimension);
		playerPanel.setPreferredSize(playerPanelDimension);
		playerPanel.setMaximumSize(playerPanelDimension);
		playerPanel.setMinimumSize(playerPanelDimension);
		
		ImageIcon playerImage;
		if (player == Player.white) {
			 playerImage = new ImageIcon(getClass().getClassLoader().getResource("img/whitemarble1.png"));
		} else {
			 playerImage = new ImageIcon(getClass().getClassLoader().getResource("img/blackmarble1.png"));
		}
		playerPanel.add(new JLabel("Player: "));
		playerPanel.add(new JLabel(playerImage));
		
		kickedPanel = new KickedPanel();
		
		winnerLabel = new JLabel();
		winnerLabel.setForeground(Color.white);
		winnerLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		add(playerPanel);
		add(kickedPanel);
		add(winnerLabel);
	}
	
	
	void showKickOut(Player player) {
		kickedPanel.showKickOut(player);
	}

	void showWinner(String team) {
		winnerLabel.setText(team + " wins!");
		repaint();
		revalidate();
	}
}
