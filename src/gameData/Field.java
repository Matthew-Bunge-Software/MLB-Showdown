package gameData;

import java.util.*;

import players.HitterData;

/**
 * The Field class stores the status of the base paths at any given time in a
 * game of MLB Showdown. Additionally, Field updates the status of the basepaths
 * given a card result and returns GameStat tracking information based on these
 * results.
 * 
 * Ideally in the future, it will also process strategy cards, and basically be
 * the main engine of strategy on the field.
 * 
 * Updates:
 * 
 * .2:
 * -Now contains a List of Tokens to be processed if a strategy card halts play on the field 
 * 
 * @author Matthew Bunge
 * @version .2
 */

public class Field {

	private BaseSlot first;
	private BaseSlot second;
	private BaseSlot third;
	private Random r;
	private List<String> problems;

	/**
	 * Constructs a field upon which to play a game of baseball
	 */
	public Field() {
		third = new BaseSlot(null);
		second = new BaseSlot(third);
		first = new BaseSlot(second);
		r = new Random();
		problems = new ArrayList<String>();
	}
	
	/**
	 * Returns a list of problems resulting from a strategy card interrupt
	 * 
	 * @return List of String tokens representing problems
	 */
	public List<String> getProblems() {
		return problems;
	}
	
	/**
	 * Informs on whether there are existing problems that halted execution of Field methods
	 * 
	 * @return True if there are any existing problems, False otherwise
	 */
	public boolean hasProblems() {
		return !problems.isEmpty();
	}

	/**
	 * Updates the field and GameStat based on a single result
	 * 
	 * @param onCard
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat single(HitterData onCard, GameStat track) {
		StrategyCard.emit("1B");
		if (!third.isEmpty()) {
			third.onBase = null;
			track.score();
		}
		if (!second.isEmpty()) {
			third.onBase = second.onBase;
			second.onBase = null;
		}
		if (!first.isEmpty()) {
			second.onBase = first.onBase;
		}
		first.onBase = onCard;
		return track;
	}

	/**
	 * Updates the field and GameStat based on a double result
	 * 
	 * @param onCard
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat twoBase(HitterData onCard, GameStat track) {
		StrategyCard.emit("2B");
		if (!third.isEmpty()) {
			third.onBase = null;
			track.score();
		}
		if (!second.isEmpty()) {
			second.onBase = null;
			track.score();
		}
		if (!first.isEmpty()) {
			third.onBase = first.onBase;
			first.onBase = null;
		}
		second.onBase = onCard;
		return track;
	}

	/**
	 * Updates the field and GameStat based on a triple result
	 * 
	 * @param onCard
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat triple(HitterData onCard, GameStat track) {
		StrategyCard.emit("3B");
		if (!third.isEmpty()) {
			third.onBase = null;
			track.score();
		}
		if (!second.isEmpty()) {
			second.onBase = null;
			track.score();
		}
		if (!first.isEmpty()) {
			first.onBase = null;
			track.score();
		}
		third.onBase = onCard;
		return track;
	}

	/**
	 * Updates the field and GameStat based on a homerun result
	 * 
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat homer(GameStat track) {
		StrategyCard.emit("HR");
		track.score();
		if (!third.isEmpty()) {
			third.onBase = null;
			track.score();
		}
		if (!second.isEmpty()) {
			second.onBase = null;
			track.score();
		}
		if (!first.isEmpty()) {
			first.onBase = null;
			track.score();
		}
		return track;
	}

	/**
	 * Updates the field and GameStat based on a walk result
	 * 
	 * @param onCard
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat walk(HitterData onCard, GameStat track) {
		StrategyCard.emit("BB");
		if (!first.isEmpty()) {
			if (!second.isEmpty()) {
				if (!third.isEmpty()) {
					track.score();
				}
				third.onBase = second.onBase;
			}
			second.onBase = first.onBase;
		}
		first.onBase = onCard;
		return track;
	}

	/**
	 * Updates the field and GameStat based on a single+ result
	 * 
	 * @param onCard
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat singlePlus(HitterData onCard, GameStat track) {
		track = single(onCard, track);
		StrategyCard.emit("1B+");
		if (second.isEmpty()) {
			second.onBase = first.onBase;
			first.onBase = null;
		}
		return track;
	}

	/**
	 * Updates the field and GameStat based on a groundout result Also does the
	 * required calculations and updates for a potential double play
	 * 
	 * @param fielder
	 *            The lineup of the team currently in the field
	 * @param atBat
	 *            The hitter who obtained this result
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat groundout(LineupManager fielder, HitterData atBat, GameStat track) {
		StrategyCard.emit("GO");
		track.yerOut();
		if (checkOuts(track)) {
			return track;
		}
		if (first.onBase != null) {
			StrategyCard.emit("BDP");
			boolean iC = infieldCheck(fielder, atBat);
			StrategyCard.emit("ADP" + iC);
			if (iC) {
				track.yerOut();
				if (checkOuts(track)) {
					return track;
				}
				first.onBase = null;
			} else {
				first.onBase = atBat;
			}
		}
		if (third.onBase != null) {
			track.score();
			third.onBase = null;
		}
		if (second.onBase != null) {
			third.onBase = second.onBase;
			second.onBase = null;
		}
		return track;
	}

	/**
	 * Updates the GameStat based on a strikeout result
	 * 
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat strikeout(GameStat track) {
		/*
		 * emit SO if card used return card used throw error?
		 */
		StrategyCard.emit("SO");
		track.yerOut();
		return track;
	}

	/**
	 * Updates the GameStat based on a flyout result
	 * 
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat flyout(GameStat track) {
		StrategyCard.emit("FO");
		track.yerOut();
		return track;
	}

	/**
	 * Updates the GameStat based on a popout result
	 * 
	 * @param track
	 *            GameStat of the current game
	 * @return The updated GameStat
	 */
	public GameStat popout(GameStat track) {
		StrategyCard.emit("PU");
		track.yerOut();
		return track;
	}
	
	/**
	 * Gets number of runners on the basepaths
	 * 
	 * @return Integer representing the number of runners on base
	 */
	public int getState() {
		int runners = 0;
		if (first.onBase != null) {
			runners++;
		}
		if (second.onBase != null) {
			runners++;
		}
		if (third.onBase != null) {
			runners++;
		}
		return runners;
	}

	/**
	 * Empties the bases. Example usage: people are on 2nd and 3rd base but
	 * according to a gamestat there are 3 outs
	 */
	public void clear() {
		first.onBase = null;
		second.onBase = null;
		third.onBase = null;
	}

	/**
	 * Private class for the purpose of representing a single base, may be
	 * modified in the future
	 * 
	 * At the moment nextBase is unused and may be completely redundent based on
	 * the current implementation
	 * 
	 * @author Matthew Bunge
	 * @version .1
	 */
	private class BaseSlot {

		public HitterData onBase;
		// private BaseSlot nextBase;

		private BaseSlot(BaseSlot nextBase) {
			onBase = null;
			// this.nextBase = nextBase;
		}

		private boolean isEmpty() {
			return onBase == null;
		}
	}

	// Implement in the case of stolen base functionality implementation
	/*
	 * private boolean catcherCheck(LineupManager teamOne, HitterData runner) {
	 * int total = r.nextInt(20) + 1; total += teamOne.getFielding(2); return
	 * total > runner.getSpeed(); }
	 */

	/**
	 * Executes an infield check, for example in the case of attempting a double
	 * play
	 * 
	 * @param teamOne
	 *            The lineup of the team currently in the field
	 * @param runner
	 *            The lead runner who the fielding team is attempting to throw
	 *            out
	 * @return True in the case that the infield check passed, false otherwise
	 */
	private boolean infieldCheck(LineupManager teamOne, HitterData runner) {
		int total = r.nextInt(20) + 1;
		for (int i = 3; i <= 6; i++) {
			total += teamOne.getFielding(i);
		}
		return total > runner.getSpeed();
	}

	/**
	 * Returns whether or not there are 3 outs in the inning or not. Useful for
	 * verifying whether a complex procedure like double play is even necessary
	 * 
	 * @param track
	 *            The GameStat of the current game
	 * @return True if there are exactly 3 outs, false otherwise
	 */
	private boolean checkOuts(GameStat track) {
		return track.getOuts() == 3;
	}
}