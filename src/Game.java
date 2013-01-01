import java.applet.AudioClip;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author schuetz
 *
 */
public class Game {

	public static final int POSITIONS = 61;
	
	private int currentHovered;
	private final List<Integer> selectedPositions = new ArrayList<Integer>();
	private List<Integer> movedPositions = new ArrayList<Integer>();
	
//	private boolean selectMany;
	
	private final Board board;
	
	private final Gui gui;
	
	private final Player me;
	private final Player enemy;
	
	private int meKicked;
	private int enemyKicked;
	
	private final GameKeyListener gameKeyListener;
	
	private Client client;
	
	//TODO sound stuff in own class
	private static final String WIN_SOUND = "applause.wav";
	private static final String LOSE_SOUND = "boo.wav";
	private static final String MOVE_SOUND = "moved.wav";
	private static final String KICK_SOUND = "kicked.wav";
	private AudioClip winClip;
	private AudioClip loseClip;
	private AudioClip moveClip;
	private AudioClip kickClip;
	
	Game(Client client, Player player) {
		client.setGame(this);
		new Thread(client).start();
		this.client = client;
		
		initSounds();
		
		gameKeyListener = new GameKeyListener(this);
		gui = new Gui(gameKeyListener, player, this);
		board = new Board();
		
		initPositions();
		
		me = player;
		enemy = me == Player.white? Player.black: Player.white;
		
		initHover();
	}

	private void initSounds() {
		URL url = getClass().getResource("sounds/" + WIN_SOUND);
		winClip = java.applet.Applet.newAudioClip(url);
		url = getClass().getResource("sounds/" + LOSE_SOUND);
		loseClip = java.applet.Applet.newAudioClip(url);
		url = getClass().getResource("sounds/" + MOVE_SOUND);
		moveClip = java.applet.Applet.newAudioClip(url);
		url = getClass().getResource("sounds/" + KICK_SOUND);
		kickClip = java.applet.Applet.newAudioClip(url);
	}
	
	private void initPositions() {
		Player[] initState = new Player[POSITIONS];
		for (int i = 0; i < 11; i++) {
			initState[i] = Player.white;
		}
		for (int i = 13; i < 16; i++) {
			initState[i] = Player.white;
		}
		for (int i = 45; i < 48; i++) {
			initState[i] = Player.black;
		}
		for (int i = 50; i < 61; i++) {
			initState[i] = Player.black;
		}
		board.setState(initState);
		renderCurrentBoardState();
	}
	
	private void initHover() {
		currentHovered = me == Player.white? 0 : 60;
	}
	
	boolean validateMultipleSelection(int arrayPos) {
		//todo alle selected und arraypos auf der selben (horizontal/diagonal) linie?
		return true;
	}
	
	void processMove(Direction direction) {
		stopShowingMoved();
		if (selectedPositions.isEmpty()) {
			moveHover(direction);
		} else {
			MoveResult moveResult = move(selectedPositions, direction);
			if (moveResult.success) {
				setInputEnabled(false);
				moveClip.play();
				selectedPositions.clear();
				sendGameState(moveResult.kick, moveResult.moved);
				renderCurrentBoardState();
			}
		}
	}
	
	private MoveResult move(List<Integer> selected, Direction direction) {
		boolean success = false;
		List<Integer> moved = new ArrayList<Integer>();
		boolean kick = false;
		
		Player team = board.getPlayer(selected.get(0));
		if (team == me) {
		
			if (selected.size() == 1) {
				int selection = selected.get(0);
				List<Integer> line = BoardUtil.getStraightLine(selection, direction); //get line in direction until the border of the board
	//			System.out.println("linesize: " + line.size());
				if (line.size() > 1) { //current position not on the borders of the board
					Player nextMarbleTeam = board.getPlayer(line.get(1));
					
					if (nextMarbleTeam == null) { //target position is free, just move to it
						board.setTeam(me, line.get(1));
						board.setTeam(null, selection);
						success = true;
						
					} else if (nextMarbleTeam == me) { //same team next on the line -> try to push
						int ownCount = 0;
						while (board.getPlayer(line.get(ownCount)) == me) { //how many consecutive of my team (including myself)?
							ownCount++;
							if (line.size() == ownCount) {
								return new MoveResult(false, false); //line filled only with my own team, no move possible
							}
						}
						boolean enemies = board.getPlayer(line.get(ownCount)) == enemy; //is one enemy directly next to my team?
						int totalCount = ownCount;
						if (enemies) {
							while (totalCount < line.size() && board.getPlayer(line.get(totalCount)) == enemy) { //how much own and enemy consecutive pieces? 
								totalCount++;
							}
						}
						int enemyCount = totalCount - ownCount; //how much enemies?
	//					System.out.println("ownCount: " + ownCount);
	//					System.out.println("enemyCount: " + enemyCount);
						
						//move my pieces if (there are no enemies) or ((if enemies are lesser) and (there is not a piece of me directly after them))
						if (!enemies || ((ownCount > enemyCount && ownCount < 4)
								&& (totalCount < line.size()? !(board.getPlayer(line.get(totalCount)) == me) : true))) {
//							System.out.println("###########################" + (totalCount < line.size()? !(board.getPlayer(line.get(totalCount)) == me) : true));
//							System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!" + !(board.getPlayer(line.get(totalCount)) == me));
							success = true;
							
							board.setTeam(null, selection);
							int position;
							for (int i = 0; i < ownCount; i++) { //push all my pieces 1 position (last one overwrites enemy if there is one)
								position = line.get(i + 1);
								board.setTeam(me, position);
								moved.add(position);
							}
							if (enemies) {
								for (int i = ownCount; i < totalCount - 1; i++) { //push all oponent pieces (but last one) 1 position
									position = line.get(i + 1);
									board.setTeam(enemy, position);
									moved.add(position);
								}
								if (totalCount == line.size()) { //if the line was full (consecutive), one enemy piece has to be kicked
									kickOut(enemy);
									kick = true;
								}  else {
									board.setTeam(enemy, line.get(totalCount)); //push last oponent piece 1 position
								}
//								else {
//									if(board.getPlayer(line.get(totalCount)) == me) { //if there is a piece of me directly after the enemies, no movement possible
//										return new MoveResult(false, false);
//									} 
//									else {
//										board.setTeam(enemy, line.get(totalCount));  //if there is place free after the enemies push them (put one new enemy at the end, looks the same as pushing all)
//									}
//								}
							}
						} 
					}
				}
			} else {
			//TODO multiple selection - nur ne, nw, se, sw - eher nur e und w???
			//fuer jede selektion muss nachbar in gewaehlte richtung leer sein
			//dann alle selections dahin bewegen
			}
		}
		return new MoveResult(success, kick, moved);
	}
	
	private void sendGameState(boolean kick, List<Integer> moved) {
		client.sendGameState(moved, board, kick);
	}
	
	void receiveGameState(Player[] gameState, List<Integer> moved, boolean kick) {
		setNewBoardState(gameState);
		moveClip.play();
		showMovedPositions(moved);
		if (kick) {
			kickClip.play();
			kickOut(me);
		}
		setInputEnabled(true);
	}
	
	private void showMovedPositions(List<Integer> moved) {
		movedPositions = moved;
		for (int i: moved) {
			gui.drawPositionState(i, board.getPlayer(i), false, false, true);
		}
		gui.flushBoard();
	}

	public void stopShowingMoved() {
		if (!movedPositions.isEmpty()) {
			for (int i: movedPositions) {
				gui.drawPositionState(i, board.getPlayer(i), false, false, false);
			}
			movedPositions.clear();
			gui.flushBoard();
		}
	}

	private void kickOut(Player player) {
		kickClip.play();
		if (player == me) {
			meKicked++;
		} else {
			enemyKicked++;
		}
		gui.showKickOut(player);
		if (meKicked == 6) {
			gui.showWinner(enemy.toString());
			winClip.play();
			setInputEnabled(false);
		} else if (enemyKicked == 6) {
			gui.showWinner(me.toString());
			loseClip.play();
			setInputEnabled(false);
		}
	}
	
	void setInputEnabled(boolean inputEnabled) {
		gameKeyListener.setInputEnabled(inputEnabled);
	}
	
	void setNewBoardState(Player[] boardState) {
		board.setState(boardState);
		renderCurrentBoardState();
	}

	void procesSingleSelection() {
		stopShowingMoved();
		if (selectedPositions.isEmpty()) {
			addSelection();
		} else {
			clearSelections();
		}
	}

	private void addSelection() {
		if (board.getPlayer(currentHovered) == me) {
			selectedPositions.add(currentHovered);
			gui.drawPositionState(currentHovered, me, true, false, false);
		}
	}
	
	private void addSelection(Direction direction) {
		System.out.println("Game.addSelection() TODO");
	}
	
	private void clearSelections() {
		for (int i = 0; i < selectedPositions.size(); i++) {
			gui.drawPositionState(selectedPositions.get(i), me, false, false, false);
		}
		gui.flushBoard();
		selectedPositions.clear();
	}

	void processMultipleSelection(Direction direction) {
		stopShowingMoved();
		addSelection(direction);
	}

	private void moveHover(Direction direction) {
		int newHovered = BoardUtil.getNeighbour(currentHovered, direction);
		if (newHovered != -1) { //FIXME
			gui.drawPositionState(currentHovered, board.getPlayer(currentHovered), false, false, false);
			gui.drawPositionState(newHovered, board.getPlayer(newHovered), false, true, false);
			gui.flushBoard();
			currentHovered = newHovered;
		}
	}
	
	private void renderCurrentBoardState() {
		gui.clearBoard();
		for (int i = 0; i < POSITIONS; i++) {
			gui.drawPositionState(i, board.getPlayer(i), false, false, false);
		}
		gui.drawPositionState(currentHovered, board.getPlayer(currentHovered), false, true, false);
		gui.flushBoard();
	}

	void quit() {
		client.sendQuit();
		System.exit(0);
	}

	void requestRestart() {
		setInputEnabled(false);
		client.sendRestartRequest();
		gui.setWaitingDialogVisibility(true);
	}
	
	void receiveQuit() {
		setInputEnabled(false);
		String text = "Oponent quitted the game.";
//		Object[] options = {"New connection", "Quit game"}; //TODO
		Object[] options = {"Quit game"};
		int selectedOption = gui.showOptionPane("Notification", text, options);
//		if (selectedOption == 0) {
//			TODO - restart connection
//		} else {
//			System.exit(0);
//		}
		if (selectedOption == 0) {
			System.exit(0);
		}
	}

	void receiveRestartRequest() {
		setInputEnabled(false);
		String text = "Oponent asks to restart match.";
		Object[] options = {"Yes", "No"};
		int selectedOption = gui.showOptionPane("Notification", text, options);
		if (selectedOption == 0) {
			client.sendRestartYes();
			restart();
		} else {
			client.sendRestartNo();
		}
		setInputEnabled(true);
	}

	void receiveRestartYes() {
		restart();
		gui.setWaitingDialogVisibility(false);
		setInputEnabled(true);
	}

	void receiveRestartNo() {
		setInputEnabled(true);
		gui.showMessagePane("Oponent don't want to restart the game.");
	}
	
	private void restart() {
		initPositions();
		initHover();
	}
}