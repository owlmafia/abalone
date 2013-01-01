import java.awt.Color;
/**
 * 
 * @author schuetz
 *
 */
public enum Player {
	white(Color.white),
	black(Color.black);
	
	Color color;
	
	Player(Color color) {
		this.color = color;
	}
}