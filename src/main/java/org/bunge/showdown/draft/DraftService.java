package org.bunge.showdown.draft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.bunge.showdown.lineup.LineupService;
import org.bunge.showdown.player.HitterData;
import org.bunge.showdown.player.PitcherData;
import org.bunge.showdown.player.PlayerData;


/**
 * DraftManager is primarily used to initialize all players used in a game
 * session. While mainly for use in drafting teams, the structures developed in
 * this class can also be used as general reference by other classes.
 * 
 * @author Matthew Bunge
 *
 */

public class DraftService {

	private Map<String, PlayerData> pool;
	private Map<String, PlayerData> drafted;

	/**
	 * Creates a DraftManager from a Map of player names to players
	 * 
	 * @param pool
	 *            Map<String, PlayerData> containing playernames connected to
	 *            players
	 */
	public DraftService(Map<String, PlayerData> pool) {
		this.pool = pool;
		this.drafted = new TreeMap<String, PlayerData>();
	}

	/**
	 * Factory method to created a DraftManager from one or two input files
	 * containing pitchers or hitters
	 * 
	 * @param pitchers
	 *            The file containing pitchers to be used
	 * @param hitters
	 *            The file containing hitters to be used
	 * @return The DraftManager containing all players in the passed input
	 *         file(s)
	 * @throws FileNotFoundException
	 *             if no files are passed
	 * @throws IllegalArgumentException
	 *             if a bad file name is passed or if there is an formatting
	 *             error in an input file
	 */
	public static DraftService initializePool(File pitchers, File hitters) throws FileNotFoundException {
		Map<String, PlayerData> pool = new TreeMap<String, PlayerData>();
		Scanner processor = null;
		PlayerData player;
		if (pitchers != null) {
			processor = new Scanner(pitchers);
			processor.useDelimiter("\\n|\\t");
			while (processor.hasNext()) {
				try {
					player = new PitcherData(processor);
					pool.put(player.toString(), player);
				} catch (Exception e) {
					throw new IllegalArgumentException("Input error in the Pitcher file");
				}
			}
		}
		if (hitters != null) {
			processor = new Scanner(hitters);
			processor.useDelimiter("\\n|\\t");
			while (processor.hasNext()) {
				try {
					player = new HitterData(processor);
					pool.put(player.toString(), player);
				} catch (Exception e) {
					throw new IllegalArgumentException("Input error in the hitter file");
				}
			}
		}
		if (processor == null) {
			throw new IllegalArgumentException("You need at least 1 file in input");
		}
		processor.close();
		return new DraftService(pool);
	}

	/**
	 * Drafts a player into the passed LineupManager.
	 * 
	 * @param soFar
	 *            The LineupManager the player is being drafted into
	 * @param playerName
	 *            The name of the player being drafted
	 * @return The modified LineupManager including the newly drafted player
	 */
	public LineupService draftPlayer(LineupService soFar, String playerName) {
		if (!pool.containsKey(playerName)) {
			throw new IllegalArgumentException("Player not in pool");
		}
		PlayerData draftee = pool.remove(playerName);
		soFar.addPlayer(draftee);
		drafted.put(playerName, draftee);
		return soFar;
	}

	/**
	 * Gets a copy of the pool of available players
	 * 
	 * @return Copy of the map of all players in the draft pool
	 */
	public Map<String, PlayerData> getPool() {
		Map<String, PlayerData> copy = new HashMap<String, PlayerData>();
		copy.putAll(pool);
		return copy;
	}

	/**
	 * Empties all the drafted players back into the draft pool
	 */
	public void reset() {
		pool.putAll(drafted);
		drafted.clear();
	}
}
