package gameData;

import java.io.*;
import java.io.IOException;
import java.util.*;

import players.*;

public class LineupManager {

	private Map<String, PlayerData> team;
	private PlayerData[] field;
	private HitterData[] lineup;
	private PitcherData currentPitcher;
	private Set<PlayerData> discarded;
	private int currentSpot;
	private List<StrategyCard> sCards;

	/**
	 * Creates new empty LineupManager.
	 */
	public LineupManager() {
		team = new HashMap<String, PlayerData>();
		field = new PlayerData[10];
		lineup = new HitterData[9];
		currentPitcher = null;
		discarded = new HashSet<PlayerData>();
		sCards = new ArrayList<StrategyCard>();
	}

	/**
	 * Creates LineupManager with initial collection of players.
	 * 
	 * @param team
	 *            Map<String, PlayerData> of initial players in LineupManager.
	 */
	public LineupManager(Map<String, PlayerData> team) {
		this();
		this.team = team;
	}

	/**
	 * Gets players on the team.
	 * 
	 * @return Map<String, PlayerData> of players on the team.
	 */
	public Map<String, PlayerData> getTeam() {
		return team;
	}

	public int playerInField(String s) {
		for (int i = 0; i < field.length; i++) {
			if (field[i] != null && field[i].equals(team.get(s))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if a given player is playing in the field.
	 * 
	 * @param s
	 *            String name of the player being checked.
	 * @return int representing the position the player is playing, -1 if they
	 *         aren't in the field.
	 */
	public int playerInLineup(String s) {
		for (int i = 0; i < lineup.length; i++) {
			if (lineup[i] != null && lineup[i].equals(team.get(s))) {
				return i;
			}
		}
		return 10;
	}

	/**
	 * Gets the fielding value of the player playing the passed position.
	 * 
	 * @param i
	 *            int representing the position being investigated.
	 * @return int representing the field value of the player at the position.
	 */
	public int getFielding(int i) {
		return field[i].getFielding(i);
	}

	/**
	 * Adds a new player to the LineupManager
	 * 
	 * @param p
	 *            The PlayerData being added to the LineupManager
	 */
	public void addPlayer(PlayerData p) {
		team.put(p.toString(), p);
	}

	/**
	 * Substitutes in a new pitcher as the current pitcher for this lineup.
	 * 
	 * @param p
	 *            String representing the name of the pitcher being substituted
	 *            in.
	 * @throws IllegalArgumentException
	 *             if the passed player is not a pitcher.
	 */
	public void subPitcher(String p) {
		if (team.get(p).getClass() != PitcherData.class) { // Don't want phantom
															// HitterData by
															// accident
			throw new IllegalArgumentException("Passed player is not a pitcher");
		}
		if (currentPitcher != null) {
			discarded.add(currentPitcher);
		}
		currentPitcher = (PitcherData) team.get(p);
		field[1] = currentPitcher;
	}

	/**
	 * Inserts a player into the field at a given position.
	 * 
	 * @param s
	 *            String name of the player being inserted into the field.
	 * @param field
	 *            int representing the position being inserted into.
	 * @return PlayerData of any player who was currently at that position.
	 * @throws IllegalArgumentException
	 *             if the passed int isn't a real position.
	 */
	public PlayerData playTheField(String s, int field) {
		if (field > 9 || field < 0) {
			throw new IllegalArgumentException("Field position must be between 0 and 9");
		}
		PlayerData temp = this.field[field];
		this.field[field] = team.get(s);
		return temp;
	}

	/**
	 * Inserts a player into the batting order at a given position.
	 * 
	 * @param s
	 *            String name of the player being inserted into the batting
	 *            order.
	 * @param lineup
	 *            int representing the position being inserted into.
	 * @return PlayerData of any player currently at that position in the order.
	 * @throws IllegalArgumentException
	 *             if lineup position passed isn't a real position.
	 */
	public PlayerData hitInOrder(String s, int lineup) {
		if (lineup > 9 || lineup < 1) {
			throw new IllegalArgumentException("Lineup position must be between 1 and 9");
		}
		lineup = lineup - 1;
		PlayerData temp = this.lineup[lineup];
		PlayerData player = team.get(s);
		if (player.isPitcher()) {
			this.lineup[lineup] = new HitterData((PitcherData) player);
		} else {
			this.lineup[lineup] = (HitterData) team.get(s);
		}
		return temp;
	}

	/**
	 * Gets batter currently batting in batting order.
	 * 
	 * @return HitterData of player currently batting.
	 */
	public HitterData getCurrentBatter() {
		return lineup[currentSpot];
	}

	/**
	 * Gets current pitcher.
	 * 
	 * @return PitcherData of player currently pitching.
	 */
	public PitcherData getCurrentPitcher() {
		return currentPitcher;
	}

	/**
	 * Advances current batter to next person in order.
	 */
	public void nextBatter() {
		currentSpot = (currentSpot + 1) % 9;
	}

	/**
	 * Discards strategy card at certain spot.
	 * 
	 * @param i
	 *            int representing the hand spot being discarded from.
	 * @return true is card successfully discarded, false otherwise.
	 */
	public boolean discardCard(int i) {
		if (i < sCards.size()) {
			sCards.remove(i);
			return true;
		}
		return false;
	}

	/**
	 * Removes all players from field.
	 */
	public void clearField() {
		for (int i = 0; i < field.length; i++) {
			field[i] = null;
		}
	}

	/**
	 * Removes all players from lineup.
	 */
	public void clearLineup() {
		for (int i = 0; i < lineup.length; i++) {
			lineup[i] = null;
		}
	}

	/**
	 * Draws random strategy card.
	 * 
	 * @return true if card successfully drawn.
	 */
	public boolean drawCard() {
		sCards.add(StrategyCard.getRandomCard());
		return true;
	}

	/**
	 * Puts a used player into the inactive pile.
	 * 
	 * @param p
	 *            The PlayerData who is now inactive.
	 */
	public void insertTrash(PlayerData p) {
		discarded.add(p);
	}

	/**
	 * Gets the strategy card at a certain hand position.
	 * 
	 * @param i
	 *            int representing the hand position.
	 * @return The selected strategy card at said position.
	 */
	private StrategyCard selectCard(int i) {
		return sCards.get(i);
	}

	/**
	 * This method is using the strategy card at position 0. I don't really know
	 * if I need it or why it's returning String.
	 */
	// TODO: Maybe delete this.
	public String useCard() {
		StrategyCard s = selectCard(0);
		if (StrategyCard.parsePrecondition(s.getPre())) {
			StrategyCard.emit(s.getUID());
			return s.getPost();
		}
		return null;
	}

	/**
	 * Testing method don't use. Adds a random card to the hand a whole bunch of
	 * dummy cards.
	 */
	public void populateSCards() {
		sCards.add(0, StrategyCard.getRandomCard());
		for (int i = sCards.size(); i < 100; i++) {
			sCards.add(StrategyCard.maker(-1, null, null, null, null, null)); // dummy
		}
	}

	/**
	 * Testing Method don't use. Gets the randomly selected card from
	 * populateSCards()
	 */
	public StrategyCard getSCard() {
		return sCards.get(0);
	}

	/**
	 * Converts the LineupManager into a save file.
	 * 
	 * @param fileName
	 *            The name of the file the lineupManager is being written to.
	 */
	public void export(String fileName) {
		try {
			FileWriter writer = new FileWriter("SaveData/" + fileName);
			for (String s : team.keySet()) {
				writer.write(s);
				String field = "_";
				String lineup = "_";
				for (int i = 0; i < 10; i++) {
					if (this.field[i] != null && this.field[i].equals(team.get(s))) {
						field = Integer.toString(i);
					}
					if (i < 9) {
						if (this.lineup[i] != null && this.lineup[i].equals(team.get(s))) {
							lineup = Integer.toString(i);
						}
					}
				}
				writer.write("\t" + field);
				writer.write("\t" + lineup);
				writer.write("\n");
			}
			for (PlayerData p : discarded) {
				writer.write(p.toString() + " D\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Imports a save file to a new LineupManager.
	 * 
	 * @param fileName
	 *            the name of the file being imported.
	 * @param pool
	 *            the overall pool of players that the LineupManager was
	 *            imported from.
	 * @return a new LineupManager with all attributes as specified in the save
	 *         file.
	 */
	public static LineupManager teamImport(String fileName, Map<String, PlayerData> pool) {
		try {
			Scanner s = new Scanner(new File("SaveData/" + fileName));
			LineupManager loaded = new LineupManager(new HashMap<String, PlayerData>());
			s.useDelimiter("\\n|\\r\\n|\\t");
			while (s.hasNext()) {
				String player = s.next();
				PlayerData playerD = pool.get(player);
				loaded.addPlayer(playerD);
				String str = s.next();
				if (!str.equals("_")) {
					loaded.field[Integer.parseInt(str)] = playerD;
				}
				str = s.next();
				if (!str.equals("_")) {
					if (!playerD.isPitcher()) {
						loaded.lineup[Integer.parseInt(str)] = (HitterData) playerD;
					} else {
						loaded.lineup[Integer.parseInt(str)] = new HitterData((PitcherData) playerD);
					}
				}
				/*
				 * String discarded = s.next(); if (!discarded.equals("D")) {
				 * loaded.addPlayer(pool.get(player)); if (s.hasNextLine()) {
				 * s.nextLine(); } } else {
				 * loaded.insertTrash(pool.get(player)); }
				 */
			}
			s.close();
			return loaded;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Bad file name");
		}
	}
}