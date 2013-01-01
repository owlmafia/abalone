/**
 * 
 * @author schuetz
 *
 */
public enum Direction {
	nw, ne, e, se, sw, w;
	
	Direction opposite() {
		switch(this) {
			case nw : return Direction.se;
			case ne : return Direction.sw;
			case w : return Direction.e;
			case e: return Direction.w;
			case sw: return Direction.ne;
			case se: return Direction.nw;
		}
	return null;
	}
}
