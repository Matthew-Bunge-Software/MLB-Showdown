package com.cook.showdown.models.game;

import java.util.*;

import com.cook.showdown.models.players.HitterData;
import com.cook.showdown.models.strategy.StrategyCard;
import com.cook.showdown.services.LineupService;

/**
 * A representation of the basepaths, IE who is on which base. Contains all the
 * methods to do the final processing of the field after a card result is
 * finalized.
 * 
 * @author Matthew Bunge
 */

public class Field {

	private BaseSlot first;
	private BaseSlot second;
	private BaseSlot third;
	private List<String> problems;

	/**
	 * Constructs a field upon which to play a game of baseball
	 */
	public Field() {
		third = new BaseSlot(null);
		second = new BaseSlot(third);
		first = new BaseSlot(second);
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
	 * Gets the playerdata of the runner on some given base
	 * 
	 * @param base The base the runner is being requested for
	 * @return The runner on that base, null if no runner on base
	 */
	public HitterData getRunner(int base) {
		if (base == 1) {
			return first.getRunner();
		} else if (base == 2) {
			return second.getRunner();
		} else if (base == 3) {
			return third.getRunner();
		}
		throw new IllegalArgumentException("Valid base argument not passed");
	}

	/**
	 * Informs on whether there are existing problems that halted execution of
	 * Field methods
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
		track.hit();
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
		track.hit();
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
		track.hit();
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
		track.hit();
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
		track.hit();
		track = single(onCard, track);
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
	public GameStat groundout(LineupService fielder, HitterData atBat, GameStat track) {
		track.yerOut();
		if (checkOuts(track)) {
			return track;
		}
		if (first.onBase != null) {
			StrategyCard.emit("BDP");
			return track;
		}
		return finalizeGroundout(track);
	}
	
	public GameStat doublePlay(HitterData atBat, GameStat track, boolean iC) {
		StrategyCard.emit("DPC");
		if (iC) {
			track.yerOut();
			if (checkOuts(track)) {
				return track;
			}
			first.onBase = null;
		} else {
			first.onBase = atBat;
		}
		return finalizeGroundout(track);
	}
	
	private GameStat finalizeGroundout(GameStat track) {
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
	 * according to a GameStat there are 3 outs
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
	 * @author Matthew Bunge
	 */
	private class BaseSlot {

		public HitterData onBase;

		private BaseSlot(BaseSlot nextBase) {
			onBase = null;
		}

		private boolean isEmpty() {
			return onBase == null;
		}
		
		private HitterData getRunner() {
			return onBase;
		}
	}

	// Implement in the case of stolen base functionality implementation
	/*
	 * private boolean catcherCheck(LineupManager teamOne, HitterData runner) {
	 * int total = r.nextInt(20) + 1; total += teamOne.getFielding(2); return
	 * total > runner.getSpeed(); }
	 */

	/**
	 * Returns whether or not there are 3 outs in the inning. Useful for
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