package com.smmaeda.swagmanai;

import java.awt.Point;
import java.time.Instant;

import connectK.BoardModel;

public class Minimax {
	public class GameNode
	{
		public BoardModel state;
		public int score = 0;
		
		public GameNode( BoardModel bm )
		{
			state = bm;
		}
	}
	
	public Minimax( byte player )
	{
		p = player == 1;
	}
	
	public void reset()
	{
		evaluations = 0;
	}
	
	// True if player is first player false otherwise
	private Boolean p;
	// Debugging variable that counts the number of evaluations to test for pruning
	private int evaluations = 0;
	// Used for deadline
	Instant deadlineCutoff;
	
	public Point getMove( BoardModel state, int depth, Instant cutoff ) throws DeadlinePassedException
	{
		evaluations = 0;
		deadlineCutoff = cutoff;
		//System.out.println( scoreBoard(state, p) );
		
		Point poi = recurseBestMove( new GameNode(state), depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE );
		//System.out.println( scoreBoard(state.placePiece(poi, player), p ) );
		System.out.println(evaluations + " states were evaluated.");
		return poi;
	}
	
	private Point recurseBestMove( GameNode node, int layer, boolean playing, int alpha, int beta ) throws DeadlinePassedException
	{
		Point none = new Point( -1, -1 );
		Point move = new Point();
		int bestScore;
		GameNode nodePrime;
		// check for deadline
		if( Instant.now().compareTo(deadlineCutoff) >= 0 )
		{
			throw new DeadlinePassedException();
		}
		
		// If the playing player can win
		if( !(move = CanWin( node.state, !(playing ^ p) )).equals(none)  )
		{
			node.score = ((playing)?(Integer.MAX_VALUE-1):Integer.MIN_VALUE+1);
		}
		// If the player that isn't playing can block a win
		else if( !(move = CanWin( node.state, (playing ^ p) )).equals(none ) )
		{
			// blocking a win is second only to winning
			node.score = ((playing)?(Integer.MAX_VALUE-2):Integer.MIN_VALUE+2);
		}
		// If we hit the end layer calculate the position current positions value
		else if( layer == 0 )
		{
			evaluations++;
			node.score = scoreBoard( node.state, p );
		}
		else if( playing )
		{
			bestScore = Integer.MIN_VALUE;
			outerMax: for( int i = 0; i < node.state.getWidth(); i++ )
			{
				// make sure that it only checks the top row if gravity is enabled
				for( int j = node.state.getHeight()-1; (!node.state.gravityEnabled() || j == node.state.getHeight()-1) && j >= 0; j-- )
				{
					if( node.state.getSpace(i, j) == 0 )
					{
						// Generate a move
						Byte token = (byte)(p?1:2);
						nodePrime = new GameNode( node.state.placePiece(new Point(i,j), token) );
						
						// Recurse from the new move
						recurseBestMove( nodePrime, layer-1, false, alpha, beta );
						
						if( nodePrime.score > bestScore )
						{
							move = new Point(i,j);
							bestScore = nodePrime.score;
							
							if( bestScore > alpha )
								alpha = bestScore;
						}
						
						if( beta <= alpha )
						{
							break outerMax;
						}
					}
				}
				
				node.score = bestScore;
			}
		}
		// else not playing
		else
		{
			bestScore = Integer.MAX_VALUE;
			outerMin: for( int i = 0; i < node.state.getWidth(); i++ )
			{
				// make sure that it only checks the top row if gravity is enabled
				for( int j = node.state.getHeight()-1; (!node.state.gravityEnabled() || j == node.state.getHeight()-1) && j >= 0; j-- )
				{
					if( node.state.getSpace(i, j) == 0 )
					{
						// Generate a move
						Byte token = (byte)(!p?1:2);
						nodePrime = new GameNode( node.state.placePiece(new Point(i,j), token) );
						
						// Recurse from the new move
						recurseBestMove( nodePrime, layer-1, true, alpha, beta );
						
						if( nodePrime.score < bestScore )
						{
							move = new Point(i,j);
							bestScore = nodePrime.score;
							
							if( bestScore < beta )
								beta = bestScore;
						}
						
						if( beta <= alpha )
						{
							break outerMin;
						}
					}
				}
				
				node.score = bestScore;
			}
		}
		
		return move;
	}
	
	private Point MinimaxAlgorithm()
	{
		int bestScore = Integer.MIN_VALUE;
		outerMax: for( int i = 0; i < node.state.getWidth(); i++ )
		{
			// make sure that it only checks the top row if gravity is enabled
			for( int j = node.state.getHeight()-1; (!node.state.gravityEnabled() || j == node.state.getHeight()-1) && j >= 0; j-- )
			{
				if( node.state.getSpace(i, j) == 0 )
				{
					// Generate a move
					Byte token = (byte)(p?1:2);
					nodePrime = new GameNode( node.state.placePiece(new Point(i,j), token) );
					
					// Recurse from the new move
					recurseBestMove( nodePrime, layer-1, false, alpha, beta );
					
					if( nodePrime.score > bestScore )
					{
						move = new Point(i,j);
						bestScore = nodePrime.score;
						
						if( bestScore > alpha )
							alpha = bestScore;
					}
					
					if( beta <= alpha )
					{
						break outerMax;
					}
				}
			}
			
			node.score = bestScore;
		}
	}
	
	
	// Checks to see if the current player can win 
	// if they can't, return -1, -1 for false
	// if they can return the move they can take to win
	private Point CanWin( BoardModel state, boolean currentPlayer )
	{
		BoardModel statePrime;
		for( int i = 0; i < state.getWidth(); i++ )
		{
			// make sure that it only checks the top row if gravity is enabled
			for( int j = state.getHeight()-1; (!state.gravityEnabled() || j == state.getHeight()-1) && j >= 0; j-- )
			{
				if( state.getSpace(i, j) == 0 )
				{
					statePrime = state.placePiece(new Point(i,j), (byte)(currentPlayer?1:2));
					if( statePrime.winner() == (byte)(currentPlayer?1:2))
					{
						return new Point(i,j);
					}
				}
			}
		}
		
		return new Point(-1,-1);
	}
	
	public static int scoreBoard(BoardModel state, Boolean player)
	{
		// Determine actual score
		int score = 0;
		
		int[][] limits = 
		{
			// i lower, i upper, j lower, j upper, k multiplier for i, k multiplier for j
			{ 0, state.getWidth(), 0, state.getHeight() - state.getkLength() + 1,  0, 1 }, // vertical
			{ 0, state.getWidth() - state.getkLength() + 1, 0, state.getHeight(), 1, 0 }, // horizontal
			{ 0, state.getWidth() - state.getkLength() + 1, 0, state.getHeight() - state.getkLength() + 1, 1, 1 }, // upright
			{ 0, state.getWidth() - state.getkLength() + 1, state.getkLength() - 1, state.getHeight(), 1, -1 } // downright
		};
		
		byte mytoken = player?(byte)1:(byte)2;
		byte value;
		byte token;
		
		int i, j, k, l;
		for( l = 0; l < 4; l++ )
		{
			for( i = limits[l][0]; i < limits[l][1]; i++ )
			{
				for( j = limits[l][2]; j < limits[l][3]; j++ )
				{
					token = (byte)0;
					for( k = 0; k < state.getkLength(); k++ )
					{
						if( (value = state.getSpace(i+k*limits[l][4],j+k*limits[l][5])) != token )
						{
							if( token == (byte) 0 )
							{
								token = value;
							}
							else if( value != 0 )
							{
								break;
							}
						}
					}
					if( k == state.getkLength() && token != (byte)0 )
					{
						score += (mytoken == token)?(1):(-1);
					}
				}
			}
		}
		
		return score;
	}
}
