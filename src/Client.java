import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author schuetz
 * 
 */

//TODO!! better protocol processing
public class Client implements Runnable {

	private Game game;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	//protocol constants
	static final String NONE = "0";
	static final String BLACK = "1";
	static final String WHITE = "2";
	static final String PLAYER_PROPOSAL = "pr";
	static final String SET_PLAYER = "pl";
	static final String KICK = "k";
	static final String GAME_STATE = "g";
	static final String QUIT = "q";
	static final String RESTART_REQUEST = "r";
	static final String RESTART_YES = "ry";
	static final String RESTART_NO = "rn";
//	static final String RESTART_REQUEST_CANCEL = "rc";

	Client(String serverIp) {
		try {
			socket = new Socket(serverIp, 4445);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setGame(Game game) {
		this.game = game;
	}

	public void run() {
		try {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String fromServer;

			while ((fromServer = in.readLine()) != null) {
				if (fromServer.substring(0, 1).equals(GAME_STATE)) {
					receiveGameState(fromServer);
				} else if (fromServer.substring(0, 1).equals(QUIT)) {
					//TODO can start game again - with restart/reconnect in menu or automatically show initial dialog??
					//restart different from reconnect - restart with same player (request restart), reconnect quits current game and shows initial dialog
					receiveQuit();
					break;
				} else if (fromServer.length() > 1 && fromServer.substring(0, 2).equals(RESTART_YES)) { //FIXME order matters here
					receiveRestartYes();
				} else if (fromServer.length() > 1 && fromServer.substring(0, 2).equals(RESTART_NO)) {
					receiveRestartNo();
				} else if (fromServer.substring(0, 1).equals(RESTART_REQUEST)) {
					receiveRestartRequest();
				} else {
					System.out.println("Client.run() unhandled message: " + fromServer);
				}
			}
			System.out.println("closing client...");
			out.close();
			in.close();
			stdIn.close();
			socket.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: localhost.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: localhost.");
			System.exit(1);
		}
	}

	private void send(String message) {
		out.println(message);
	}

	private Player[] deserializeBoard(String serializedBoard) {
		String[] parts = serializedBoard.split(" ");
		if (parts.length != 61) {
			throw new RuntimeException("Wrong size of received board: " + parts.length);
		}
		Player[] board = new Player[61];
		Player team;
		for (int i = 0; i < 61; i++) {
			if (parts[i].equals(NONE)) {
				team = null;
			} else if (parts[i].equals(BLACK)) {
				team = Player.black;
			} else {
				team = Player.white;
			}
			board[i] = team;
		}
		return board;
	}

	/**
	 * Waits until both clients connected
	 */
	Player getPlayer(Player playerProposal) {
		String playerProposalString = getProtocolRepresentation(playerProposal);
		send(PLAYER_PROPOSAL + " " + playerProposalString);
		
		Player player = null;
		String fromServer;
//		System.out.println("CLIENT client is sending playerProposal: " + playerProposal);
		try {
			while ((fromServer = in.readLine()) != null) { //FIXME delete while? just ask one time?
//				System.out.println("client received in Client.getplayer " + fromServer);
				if (fromServer.length() > 1 && fromServer.substring(0, 2).equals(SET_PLAYER)) {
					String playerString = fromServer.substring(3, 4);
					player = getPlayer(playerString);
					break;
				 } else {
	//	    		 System.out.println("Client.getPlayer() wrong server answer: " + fromServer);
				 }
		   } 
	  } catch (IOException e) {
		e.printStackTrace();
	  }
	  if (player == null) { //FIXME necessary?
		  throw new RuntimeException("Client.getPlayer() player is null");
	  }
	  return player;
	}
	
	/**
	 * Serialized game state: g movedpositionscount (movedposition )*(k )boardstate
	 * g identifies game state line
	 * movedpositioncount is how much pieces where moved by the oponent (of both players)
	 * after that the positions which where moved
	 * k is a notification if the oponent kicked one piece. It's thus optional
	 * boardstate - serialized board
	 * 
	 * Examples:
	 * 3 moved pieces in positions 8,9,10, one kicked
	 * g 3 8 9 10 k <boardstate>
	 * 
	 * No moved pieces, no kick
	 * g 0 <boardstate>
	 */
	void sendGameState(List<Integer> moved, Board board, boolean kick) {
		String serialized = GAME_STATE + " " + moved.size() + " ";
		for (int i : moved) {
			serialized += i + " ";
		}
		if (kick) {
			serialized += KICK + " ";
		}
		send(serialized + board.serialize());
	}

	private void receiveGameState(String message) {
//		System.out.println("Client receives: " + message);
		List<Integer> movedPositions = new ArrayList<Integer>();
		
		int movedPositionsCount = Integer.parseInt(message.substring(2, 3));
//		System.out.println("movedPositionCount: " + movedPositionsCount);
//		System.out.println("substring: "  + message.substring(4));
		StringTokenizer st = new StringTokenizer(message.substring(4));
		int offset = 4;
		String token;
		for (int i = 0; i < movedPositionsCount; i++) {
			token = st.nextToken();
//			System.out.println("adding: " + Integer.parseInt(token));
			movedPositions.add(Integer.parseInt(token));
			offset += token.length() + 1;
		}
		
//		System.out.println("after offset: " + message.substring(offset));
		
		Player[] gameState;
		boolean kick = false;
		if (message.substring(offset, offset + 1).equals(KICK)) {
			kick = true;
			gameState = deserializeBoard(message.substring(offset + 2));
		} else {
			gameState = deserializeBoard(message.substring(offset));
		}
		game.receiveGameState(gameState, movedPositions, kick);
	}

	static String getProtocolRepresentation(Player player) {
		if (player == null) return NONE;
		switch(player) {
			case white: return WHITE;
			case black: return BLACK;
			default: return null;
		}
	}
	
	static Player getPlayer(String protocolRepresentation) {
		if (protocolRepresentation.equals(BLACK)) {
			return Player.black;
		} else if (protocolRepresentation.equals(WHITE)) {
			return Player.white;
		}
		return null;
	}

	void sendQuit() {
		send(QUIT);
	}
	
	private void receiveQuit() {
		game.receiveQuit();
	}

	void sendRestartRequest() {
		send(RESTART_REQUEST);
	}
	
	private void receiveRestartRequest() {
		game.receiveRestartRequest();
	}
	
	void sendRestartYes() {
		send(RESTART_YES);
	}
	
	private void receiveRestartYes() {
		game.receiveRestartYes();
	}

	public void sendRestartNo() {
		send(RESTART_NO);
	}
	
	private void receiveRestartNo() {
		game.receiveRestartNo();
	}
}