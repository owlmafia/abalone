

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class GameKeyListener implements KeyListener {
	
	private final Game game;
	
	private final int MULTIPLE_SELECTION_KEY = KeyEvent.VK_SHIFT;
	private final int SELECTION_KEY = KeyEvent.VK_S;
//	private final int CLEAR_KEY = KeyEvent.VK_Q;
	
	private Map<Integer, Direction> keyDirections = new HashMap<Integer, Direction>();
	
	private boolean inputEnabled = true;
	private boolean multipleSelectionKeyPressed = false;
	
	GameKeyListener(Game game) {
		this.game = game;
		
		keyDirections.put(KeyEvent.VK_W, Direction.nw);
		keyDirections.put(KeyEvent.VK_E, Direction.ne);
		keyDirections.put(KeyEvent.VK_D, Direction.e);
		keyDirections.put(KeyEvent.VK_X, Direction.se);
		keyDirections.put(KeyEvent.VK_Y, Direction.sw);
		keyDirections.put(KeyEvent.VK_A, Direction.w);
	}
	
	void processKeyEvent(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		if (inputEnabled) {
			int keyCode = e.getKeyCode();
			
			if (keyCode == MULTIPLE_SELECTION_KEY) {
				multipleSelectionKeyPressed = true;
			} else if (keyCode == SELECTION_KEY) {
				game.procesSingleSelection();
//				if (selected.isEmpty()) {
//					addSelection();
//				} else {
//					clearSelections();
//				}
			} else if (keyDirections.keySet().contains(keyCode)) {
				Direction direction = keyDirections.get(keyCode);
				if (multipleSelectionKeyPressed) {
					game.processMultipleSelection(direction);
//					addSelection(direction);
				} else {
					game.processMove(direction);
//					if (selected.isEmpty()) {
//						moveHover(direction);
//					} else {
//						//bewegen
//						boolean moved = game.tryMove(selected, direction);
//						if (moved) {
//							refresh();
//						}
//					}
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				System.exit(0);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == MULTIPLE_SELECTION_KEY) {
			multipleSelectionKeyPressed = false;
		}
	}

	public void setInputEnabled(boolean inputEnabled) {
		this.inputEnabled = inputEnabled;
	}
	
	public void keyTyped(KeyEvent arg0) {
	}
}
