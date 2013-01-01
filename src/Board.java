
/**
 * 
 * @author schuetz
 *
 */
public class Board {
	
	private Player[] state = new Player[Game.POSITIONS];

	void setTeam(Player team, int position) {
		state[position] = team;
	}
	
	Player getPlayer(int position) {
		return state[position];
	}
	
	void setState(Player[] state) {
		this.state = state;
	}
	
//	void setStates(Player team, List<Integer> positions) {
//		for (Integer position : positions) {
//			setTeam(team, position);
//		}
//	}
	
	String serialize() {
		String serialized = "";
		String field;
		for (Player player : state) {
			field = Client.getProtocolRepresentation(player);
			serialized += field + " ";
		}
		return serialized;
	}
	
//	Player[] getState() {
//		return state;
//	}

//	public Iterator<Player> iterator() {
//		return new ArrayList<Player>(Arrays.asList(fields)).iterator();
//	}
//	
//	int getSize() {
//		return state.length;
//	}
}
