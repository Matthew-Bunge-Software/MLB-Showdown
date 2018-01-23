package gameData;

import java.util.ArrayList;
import java.util.List;

/**
 * General summary stats of where the game is at the current point, primary
 * score and inning. In the future is liable to include other important
 * information like innings charged to a current pitcher.
 * 
 * An empty GameStat should represent the typical conditions of a baseball game
 * at the top of the 1st inning before a batter has come to the plate.
 * 
 * @author Matthew Bunge
 */

public class GameStat {

	private int outs;
	private int homeRuns;
	private int awayRuns;
	private int homeHits;
	private int awayHits;
	private boolean top;
	private boolean inningEnd;
	private int inning;
	private ArrayList<Integer> homeSpread;
	private ArrayList<Integer> awaySpread;

	/**
	 * Creates a GameStat representing a brand new game instance.
	 */
	public GameStat() {
		outs = 0;
		homeRuns = awayRuns = homeHits = awayHits = 0;
		top = true;
		inning = 1;
		homeSpread = new ArrayList<>();
		awaySpread = new ArrayList<>();
		awaySpread.add(0);
	}

	/**
	 * Returns the number of outs in the current frame.
	 * 
	 * @return the number of outs in the current frame.
	 */
	public int getOuts() {
		return outs;
	}

	/**
	 * Increments the number of outs.
	 * Will not increment past 3.
	 */
	public void yerOut() {
		if (outs < 3) {
			outs++;
		}
	}

	/**
	 * Flips over the inning if a third out has been recorded.
	 */
	public void update() {
		if (outs == 3) {
			if (top) {
				top = false;
				homeSpread.add(0);
			} else {
				inning++;
				top = true;
				awaySpread.add(0);
			}
			inningEnd = true;
			outs = 0;
		}
	}

	/**
	 * Increments runs of the current team batting.
	 */
	public void score() {
		if (top) {
			awayRuns++;
			awaySpread.add(inning - 1, awaySpread.remove(inning - 1) + 1);
		} else {
			homeRuns++;
			homeSpread.add(inning - 1, homeSpread.remove(inning - 1) + 1);
		}
	}

	/**
	 * Increments the hits of the team currently up to bat.
	 */
	public void hit() {
		if (top) {
			awayHits++;
		} else {
			homeHits++;
		}
	}

	/**
	 * Indicates whether it's time to turn over the ending. Turns over the
	 * inning internally if it is time.
	 * 
	 * @return true if the inning is over, false otherwise.
	 */
	public boolean inningEnd() {
		if (inningEnd) {
			inningEnd = false;
			return true;
		}
		return false;
	}

	/**
	 * Returns the inning by inning list of the runs scored by the home team.
	 * If a frame has not been reaching the spread will have no value for it.
	 * 
	 * @return the list of runs scored by inning by the home team.
	 */
	public List<Integer> getHomeSpread() {
		return homeSpread;
	}

	/**
	 * Returns the inning by inning list of the runs scored by the away team.
	 * If a frame has not been reached the spread will have no value for it.
	 * 
	 * @return the list of runs scored by inning by the away team.
	 */
	public List<Integer> getAwaySpread() {
		return awaySpread;
	}
	
	/**
	 * Gets the total number of runs scored by the home team.
	 * 
	 * @return the runs scored by the home team.
	 */
	public int getHomeRuns() {
		return homeRuns;
	}
	
	/**
	 * Gets the total number of runs scored by the away team.
	 * 
	 * @return the runs scored by the away team.
	 */
	public int getAwayRuns() {
		return awayRuns;
	}
	
	/**
	 * Gets the total number of hits scored by the home team.
	 * 
	 * @return the hits scored by the home team.
	 */
	public int getHomeHits() {
		return homeHits;
	}
	
	/**
	 * Gets the total number of hits scored by the away team.
	 * 
	 * @return the hits scored by the away team.
	 */
	public int getAwayHits() {
		return awayHits;
	}
	
	/**
	 * Returns the inning of the current game. 
	 * For example, top of the first or bottom of the first will return a 1.
	 * 
	 * @return the number of the current inning.
	 */
	public int getInning() {
		return inning;
	}
	
	/**
	 * Returns the current half of the inning.
	 * 
	 * @return true if top half of the inning, false if the bottom half.
	 */
	public boolean getHalf() {
		return top;
	}
}