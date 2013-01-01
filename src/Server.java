import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
/**
 * 
 * @author schuetz
 * 
 */
public class Server extends Thread {

	private Socket[] clients = new Socket[2];
	ServerSocket serverSocket;
	
	//First client which connects sets this variable to selected player
	volatile private String alreadySelectedPlayer = null;

	public void run() {
		try {
			serverSocket = new ServerSocket(4445);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4445.");
			System.exit(1);
		}
		Socket clientSocket = null;
		for (int i = 0; i < 2; i++) {
			try {
				clientSocket = serverSocket.accept();
				clients[i] = clientSocket;
				new ServerThread(i, clientSocket).start();
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		}
		// serverSocket.close(); TODO wenn der server explizit runtergefahren wird!
	}
	
	void shutDown() {
		try {
			//TODO say both clients bye
			serverSocket.close();
			System.out.println("Serversocket closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized String getPlayer(String proposalPlayer, int myId) {
//		System.out.println("SERVER thread joined getPlayer id: " + myId);
		String player = null;
		if (alreadySelectedPlayer == null) { //client arrived first
//			System.out.println("client arrived first");
			if (proposalPlayer.equals(Client.NONE)) { //first did not select - wait for second client
				alreadySelectedPlayer = Client.NONE;
				try {
//					System.out.println("waiting...");
					wait();
//					System.out.println("awake!");
					return getOponent(alreadySelectedPlayer); //second already selected, or got random player - first gets the oponent
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else { //first selected a player
				alreadySelectedPlayer = proposalPlayer; //set selection - this one wins if the second client selects the same
				player = proposalPlayer;
			}
		} else { //client arrived second
//			System.out.println("client arrived second");
			if (alreadySelectedPlayer.equals(Client.NONE)) { //first did not select a player
//				System.out.println("first did not select a player");
				if (proposalPlayer.equals(Client.NONE)) { //second did not either - second gets random player and notifies first
					player = getRandomPlayer();
					alreadySelectedPlayer = player;
					notify();
					return player;
				} else { //second selected a player, notifies first
					alreadySelectedPlayer = proposalPlayer;
					notify();
					return proposalPlayer;
				}
			} else { //first selected a player
//				System.out.println("first selected a player");
				if (proposalPlayer.equals(Client.NONE)) { //if second client did not select anything, return oponent of the first one
					return getOponent(alreadySelectedPlayer);
				} else { //both first and second selected a player
//					System.out.println("both first and second selected a player");
					if (isOponent(alreadySelectedPlayer, proposalPlayer)) { //different players, everything fine
						return proposalPlayer;
					} else {
						return getOponent(alreadySelectedPlayer); //second client selected same player as first - second has to change
					}
				}
			}
			
		}
		if (player == null) {
			System.out.println("Server.getPlayer - player is null");
		}
		return player;
	}
	
	private boolean isOponent(String player1, String player2) {
		return !player1.equals(player2);
	}
	
	private String getOponent(String player) {
		if (player.equals(Client.BLACK)) {
			return Client.WHITE;
		} else return Client.BLACK;
	}
	
	private String getRandomPlayer() {
		int random = new Random().nextInt(1);
		switch(random) {
			case 0: return Client.BLACK;
			case 1: return Client.WHITE;
			default: throw new RuntimeException("SERVER getRandomPlayer - not handled case");
		}
	}
	class ServerThread extends Thread {

		private int myId;
		private int oponentId;

		ServerThread(int myId, Socket myClient) {
			this.myId = myId;
			oponentId = myId == 0 ? 1 : 0;
		}

		public void run() {
//			System.out.println("SERVER serverthread running: " + myId);
			PrintWriter oponentWriter;
			PrintWriter myWriter;
			try {
				while (clients[0] == null || clients[1] == null) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
				
				Socket myClient = clients[myId];
				Socket oponentClient = clients[oponentId];
				myWriter = new PrintWriter(myClient.getOutputStream(), true);
				oponentWriter = new PrintWriter(oponentClient.getOutputStream(), true);
				BufferedReader myReader = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
			
				String inputLine;
				while ((inputLine = myReader.readLine()) != null) {
					
//					System.out.println("SERVER server read input: " + inputLine);
//					System.out.println("SERVER line length: " + inputLine.length());
					
					if (inputLine.substring(0, 1).equals(Client.GAME_STATE)
							|| inputLine.substring(0, 1).equals(Client.RESTART_REQUEST)
							|| inputLine.substring(0, 1).equals(Client.QUIT)
							|| inputLine.length() > 1 && inputLine.substring(0, 2).equals(Client.RESTART_YES)
							|| inputLine.length() > 1 && inputLine.substring(0, 2).equals(Client.RESTART_NO)
					) {
						oponentWriter.println(inputLine);
						if (inputLine.substring(0, 1).equals(Client.QUIT)) { 
							shutDown();
						}
					} else if (inputLine.length() > 1 && inputLine.substring(0, 2).equals(Client.PLAYER_PROPOSAL)) {
							String proposalPlayer = inputLine.substring(3, 4);
//							System.out.println("SERVER the proposal player for client " + myId + "is: " + proposalPlayer);
							String player = getPlayer(proposalPlayer, myId);
//							System.out.println("SERVER this is the result player for client " + myId + ": " + player);
							myWriter.println(Client.SET_PLAYER + " " + player);
					} else {
						System.out.println("Server.ServerThread.run() - unhandled message: " + inputLine);
					}
				}
				
				myReader.close();
				myClient.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}