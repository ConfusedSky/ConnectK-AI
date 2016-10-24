import java.awt.Point;
import java.util.Random;

import connectK.BoardModel;
import connectK.CKPlayer;

public class SwagmanAI extends CKPlayer {
	
	Random r = new Random();

	public SwagmanAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "Swagman";
	}

	@Override
	public Point getMove(BoardModel state) {
		
		Point choice = new Point(0,0);
		
		do
		{
			choice.x = r.nextInt(state.getWidth());
			choice.y = r.nextInt(state.getHeight());
		} while( state.getSpace(choice) != 0 );
		
		return choice;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
}
