package gameData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import players.*;

public class DraftManager {
	
	private Map<String, PlayerData> pool;
	private Map<String, PlayerData> drafted;
	
	public DraftManager(Map<String, PlayerData> pool) {
		this.pool = pool;
		this.drafted = new TreeMap<String, PlayerData>();
	}

	public static DraftManager initializePool(File pitchers, File hitters) throws FileNotFoundException {
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
				} catch(Exception e) {
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
		return new DraftManager(pool);
	}
	
	public LineupManager draftPlayer(LineupManager soFar, String playerName) {
		if (!pool.containsKey(playerName)) {
			throw new IllegalArgumentException("Player not in pool");
		}
		PlayerData draftee = pool.remove(playerName);
		soFar.addPlayer(draftee);
		drafted.put(playerName, draftee);
		return soFar;
	}
	
	public Map<String, PlayerData> getPool() {
		Map<String, PlayerData> copy = new HashMap<String, PlayerData>();
		copy.putAll(pool);
		return copy;
	}
	
	public void reset() {
		pool.putAll(drafted);
		drafted.clear();
	}
}
