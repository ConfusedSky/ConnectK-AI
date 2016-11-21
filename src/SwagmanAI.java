import java.awt.Point;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.smmaeda.swagmanai.DeadlinePassedException;
import com.smmaeda.swagmanai.Minimax;

import connectK.BoardModel;
import connectK.CKPlayer;
import connectK.ConnectKGUI;

public class SwagmanAI extends CKPlayer {

	Minimax minimax;
	
	public SwagmanAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "Swagman";
		minimax = new Minimax( player );
	}

	@Override
	public Point getMove(BoardModel state) 
	{
		return getMove( state, 5000 );
	}
	
	@Override
	public Point getMove(BoardModel state, int deadline) {
		minimax.reset();
		
		List<Point> previousBestMoves = new ArrayList<Point>();
		Point bestMove = new Point( 0, 0 );
		int depth = 1;
		Instant cutoff = Instant.now().plusMillis(deadline).minusMillis(100);
		
		while( true )
		{
			try
			{
				// Set cutoff to now + deadline - 100 to provide a safe buffer
				bestMove = minimax.getMove(state, depth, cutoff );
				System.out.println(bestMove);
				// Check the past 3 depths for the same value
				// if they are the same break
				int size = previousBestMoves.size();
				if( size >= 3 && 
					previousBestMoves.get(size-1).equals(bestMove) &&
					previousBestMoves.get(size-2).equals(bestMove) &&
					previousBestMoves.get(size-3).equals(bestMove)
				)
				{
					System.out.println("Too many repeats");
					break;		
				}
				previousBestMoves.add(bestMove);
			}
			catch( DeadlinePassedException c )
			{
				break;
			}
			depth += 1;
		}
		System.out.println("Maximum depth reached = " + depth);
		return bestMove;
	}
	
	public static void main( String[] args )
	{
		try {
			args = new String[]{"C:\\Users\\Masa\\Google Drive\\Fall 2016\\ICS 171\\Project\\ConnectKEclipse\\bin\\SwagmanAI.class"};
			ConnectKGUI.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
