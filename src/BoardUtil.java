import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author schuetz
 *
 */
public class BoardUtil {

	private final static int[] rowLengths = new int[] {5, 6, 7, 8, 9, 8, 7, 6, 5};
	private final static int[] rowSums = new int[10];
	
	static {
		rowSums[0] = 0;
		for (int i = 0; i < rowLengths.length; i++) {
			rowSums[i + 1] = rowSums[i] + rowLengths[i];
		}
	}
	
	//TODO this function hast to use getNeighbour
	static List<Integer> getNeighbours(int arrayPos) {
		
		List<Integer> neighbours = new ArrayList<Integer>();
		Point coords = getCoords(arrayPos);

		int incrUp = -1;
		int incrDown = 0;
		if (coords.y >= 4) {
			incrDown = -1;
			if (coords.y > 4) {
				incrUp = 0;
			}
		}
		int neighbourX = coords.x + incrUp;
		int neighbourY = coords.y - 1;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		neighbourX = coords.x + incrUp + 1;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		neighbourX = coords.x - 1;
		neighbourY = coords.y;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		neighbourX = coords.x + 1;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		neighbourX = coords.x + incrDown;
		neighbourY = coords.y + 1;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		neighbourX = coords.x + incrDown + 1;
		neighbours.add(getArrayPos(neighbourX, neighbourY));
		
		return neighbours;
	}
	
	/**
	 * @param arrayPos current position
	 * @param direction
	 * @return neighbour position in a given direction. When the board has no more positions in the direction, returns -1
	 */
	static int getNeighbour(int arrayPos, Direction direction) {

		Point coords = getCoords(arrayPos);
		
		int incrUp = -1;
		int incrDown = 0;
		if (coords.y >= 4) {
			incrDown = -1;
			if (coords.y > 4) {
				incrUp = 0;
			}
		}
		
		int neighbourX = -1;
		int neighbourY = -1;
		
		switch (direction) {
			case nw: 
				neighbourX = coords.x + incrUp;
				neighbourY = coords.y - 1;
				break;
			case ne:
				neighbourX = coords.x + incrUp + 1;
				neighbourY = coords.y - 1;
				break;
			case w:
				neighbourX = coords.x - 1;
				neighbourY = coords.y;
				break;
			case e:
				neighbourX = coords.x + 1;
				neighbourY = coords.y;
				break;
			case sw:
				neighbourX = coords.x + incrDown;
				neighbourY = coords.y + 1;
				break;
			case se:
				neighbourX = coords.x + incrDown + 1;
				neighbourY = coords.y + 1;
				break;
			}
		if (isValidPos(neighbourX, neighbourY)) { //FIXME!!! muss wieder auf "andere seite" auftauchen
			return getArrayPos(neighbourX, neighbourY);
		}
		return -1; //FIXME!!!
	}
	
	static Point getCoords(int arrayPos) {
		Point coords = new Point();
		if (arrayPos < rowSums[1]) {
			coords.y = 0;
		} else if (arrayPos < rowSums[2]) {
			coords.y = 1;
		} else if (arrayPos < rowSums[3]) {
			coords.y = 2;
		} else if (arrayPos < rowSums[4]) {
			coords.y = 3;
		} else if (arrayPos < rowSums[5]) {
			coords.y = 4;
		} else if (arrayPos < rowSums[6]) {
			coords.y = 5;
		} else if (arrayPos < rowSums[7]) {
			coords.y = 6;
		} else if (arrayPos < rowSums[8]) {
			coords.y = 7;
		} else { //<= 60
			coords.y = 8;
		}
		coords.x = arrayPos - rowSums[coords.y];
		return coords;
	}
	
	static int getArrayPos(int x, int y) {
		int rowsPart = rowSums[y];
		return rowsPart + x;
	}
	
	/**
	 * 
	 * @param arrayPos
	 * @param direction
	 * @return line with all positions in a direction until border of the board. Includes the selected position.
	 */
	static List<Integer> getStraightLine(int arrayPos, Direction direction) {
		List<Integer> line = new ArrayList<Integer>();
		line.add(arrayPos);
		int neighbour = getNeighbour(arrayPos, direction);
		while (neighbour != -1) {
			line.add(neighbour);
			neighbour = getNeighbour(neighbour, direction);
		}
		return line;
	}
	
	static boolean isValidPos(int x, int y) {
		if (x < 0 || y < 0 || y > 8 || x >= rowLengths[y]) {
			return false;
		}
		return true;
	}
}