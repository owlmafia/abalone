import java.util.List;
/**
 * 
 * @author schuetz
 *
 */
//FIXME private und final, konstruktor ruft den anderen auf
public class MoveResult {
	boolean success;
	List<Integer> moved;
	boolean kick;
	
	MoveResult(boolean success) {
		this.success = success;
	}
	
	MoveResult(boolean success, boolean kick) {
		this.success = success;
		this.kick = kick;
	}
	
	MoveResult(boolean success, boolean kick, List<Integer> moved) {
		this.success = success;
		this.moved = moved;
		this.kick = kick;
	}
}
