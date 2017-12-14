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
	private Map<StrategyCard, Integer> sCards;
	private List<StrategyCard> discardMe;
	private List<StrategyCard> useMe;

	/**
	 * Creates new empty LineupManager.
	 */
	public LineupManager() {
		team = new HashMap<>();
		field = new PlayerData[10];
		lineup = new HitterData[9];
		currentPitcher = null;
		discarded = new HashSet<>();
		sCards = new HashMap<>();
		discardMe = new ArrayList<>();
		useMe = new ArrayList<>();
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
	 * Sets the current pitcher to the pitcher in the field
	 * This call is mostly useful after constructing an initial lineup
	 * 
	 * @return True if there is a pitcher in the field, false otherwise
	 */
	public boolean setPitcher() {
		if (field[1] != null) {
			currentPitcher = (PitcherData) field[1];
			return true;
		}
		return false;
	}

	/**
	 * Advances current batter to next person in order.
	 */
	public void nextBatter() {
		currentSpot = (currentSpot + 1) % 9;
	}

	/**
	 * Discards a certain strategy card
	 * 
	 * @param i
	 *            The card being discarded
	 * @return true is card successfully discarded, false otherwise.
	 */
	public boolean discardCard(StrategyCard sc) {
		if (sCards.containsKey(sc)) {
			if (sCards.get(sc) == 1) {
				sCards.remove(sc);
			} else {
				sCards.put(sc, sCards.get(sc) - 1);
			}
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
		StrategyCard s = StrategyCard.getRandomCard();
		if (sCards.containsKey(s)) {
			sCards.put(s, sCards.get(s) + 1);
		} else {
			sCards.put(s,  1);
		}
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
	 * This method is using the strategy card at position 0. I don't really know
	 * if I need it or why it's returning String.
	 */
	// TODO: Maybe delete this.
	/* public String useCard() {
		StrategyCard s = selectCard(0);
		if (StrategyCard.parsePrecondition(s.getPre())) {
			StrategyCard.emit(s.getUID());
			return s.getPost();
		}
		return null;
	} */

	/**
	 * Sets up initial hand of 4 cards
	 */
	public void populateSCards() {
		for (int i = 0; i < 4; i++) {
			drawCard();
		}
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
	 * Gets an instance of the list of all StrategyCards in the LineupManager
	 * 
	 * @return List of all StrategyCards
	 */
	public List<StrategyCard> getSCards() {
		List<StrategyCard> listCards = new ArrayList<>();
		for (StrategyCard s : sCards.keySet()) {
			for (int i = 0; i < sCards.get(s); i++) {
				listCards.add(s);
			}
		}
		return listCards;
	}
	
	public PlayerData getPlayerAtPosition(int i) {
		return field[i];
	}
	
	public void readyDiscard(StrategyCard sc) {
		discardMe.add(sc);
	}
	
	public void readyUse(StrategyCard sc) {
		useMe.add(sc);
	}
	
	public List<StrategyCard> getUseCards() {
		return useMe;
	}
	
	public List<StrategyCard> undoDiscard() {
		List<StrategyCard> undo = new ArrayList<>();
		undo.addAll(discardMe);
		discardMe.clear();
		return undo;
	}
	
	public List<StrategyCard> undoUse() {
		List<StrategyCard> undo = new ArrayList<>();
		undo.addAll(useMe);
		useMe.clear();
		return undo;
	}
	
	public void processDiscard() {
		for (StrategyCard sc : discardMe) {
			discardCard(sc);
		}
		discardMe.clear();
	}
	
	public void processUse() {
		for (StrategyCard sc : useMe) {
			discardCard(sc);
		}
		useMe.clear();
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