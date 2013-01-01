import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
/**
 * 
 * @author schuetz
 *
 */
public class Gui extends JFrame {

	private BoardPanel boardPanel;
	private InfoPanel infoPanel;
	private WaitingDialog waitingDialog;
	
	Gui(KeyListener keyListener, Player player, final Game game) { //FIXME replace reference to game with some listener for the menu
		setTitle("Abalone");
		
		waitingDialog = new WaitingDialog(this);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu gameMenu = new JMenu();
		menuBar.add(gameMenu);
		gameMenu.setText("Game");
		
		JMenuItem newMenuItem = new JMenuItem();
		gameMenu.add(newMenuItem);
		newMenuItem.setText("New");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				game.requestRestart();
			}
		});
		
		JMenuItem quitMenuItem = new JMenuItem();
		gameMenu.add(quitMenuItem);
		quitMenuItem.setText("Quit");
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				game.quit();
			}
		});
		
//		JMenu soundMenu = new JMenu();
//		menuBar.add(soundMenu);
//		soundMenu.setText("Sound");
//		
//		final JMenuItem toggleSoundMenuItem = new JMenuItem();
//		soundMenu.add(toggleSoundMenuItem);
//		toggleSoundMenuItem.setText("Mute");
//		toggleSoundMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				if (toggleSoundMenuItem.getText().equals("Mute")) {
//					game.mute();
//					toggleSoundMenuItem.setText("Activate");
//				} else {
//					game.activateSound();
//					toggleSoundMenuItem.setText("Mute");
//				}
//			}
//		});
		
		JMenu helpMenu = new JMenu();
		helpMenu.setText("?");
		menuBar.add(helpMenu);
		
		final JTextPane aboutTextPane = new JTextPane();
        aboutTextPane.setContentType("text/html");
        aboutTextPane.setText("<div align= center><b>Abalone</b></div><br>Programming and graphics (except intro graphic) by Ivan Schuetz<br><br>");

		JMenuItem aboutMenuItem = new JMenuItem();
		helpMenu.add(aboutMenuItem);
		aboutMenuItem.setText("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JOptionPane.showMessageDialog(Gui.this, aboutTextPane, "About", JOptionPane.PLAIN_MESSAGE, null);
			}
		});
		
		boardPanel = new BoardPanel(keyListener);
		
		infoPanel = new InfoPanel(player);
		
		add(boardPanel);
		add(infoPanel, BorderLayout.EAST);
		
		addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				game.quit();
		  }
		});
		pack();
		setVisible(true);
	}
	
	void showKickOut(Player player) {
		infoPanel.showKickOut(player);
	}

	void showWinner(String winner) {
		infoPanel.showWinner(winner);
	}
	
	void drawPositionState(int boardPosition, Player team, boolean selected, boolean hover, boolean oponentMoveHover) {
		boardPanel.drawPositionState(boardPosition, team, selected, hover, oponentMoveHover);
	}

	void clearBoard() {
		boardPanel.clearBoard();
	}

	void flushBoard() {
		boardPanel.flush();
	}

	int showOptionPane(String title, String text, Object[] options) {
		return JOptionPane.showOptionDialog(this,
				text,
				title,
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
	}
	
	void showMessagePane(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
	
	public void setWaitingDialogVisibility(boolean visible) {
		waitingDialog.setLocationRelativeTo(this);
		waitingDialog.setVisible(visible);
	}
}