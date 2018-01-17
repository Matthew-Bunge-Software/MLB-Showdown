package gameData;

import java.util.ArrayList;

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
	public int homeRuns;
	public int awayRuns;
	public int homeHits;
	public int awayHits;
	public boolean top;
	private boolean inningEnd;
	public int inning;
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
	 * Gets the number of outs in the current frame.
	 * 
	 * @return The number of outs in the current frame.
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
	 * Gets the runs scored by the home team.
	 * 
	 * @return ArrayList<Integer> of runs scored by the home team.
	 */
	public ArrayList<Integer> getHomeSpread() {
		return homeSpread;
	}

	/**
	 * Gets the runs scored by the away team.
	 * 
	 * @return ArrayList<Integer> of runs scored by the away team.
	 */
	public ArrayList<Integer> getAwaySpread() {
		return awaySpread;
	}
}