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
	
	public LineupManager() {
		team = new HashMap<String, PlayerData>();
		field = new PlayerData[10];
		lineup = new HitterData[9];
		currentPitcher = null;
		discarded = new HashSet<PlayerData>();
		sCards = new ArrayList<StrategyCard>();
	}
	
	public LineupManager(Map<String, PlayerData> team) {
		this();
		this.team = team;
	}
	
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
	
	public int playerInLineup(String s) {
		for (int i = 0; i < lineup.length; i++) {
			if (lineup[i] != null && lineup[i].equals(team.get(s))) {
				return i;
			}
		}
		return 10;
	}
	
	public int getFielding(int i) {
		return field[i].getFielding(i);
	}
	
	public void addPlayer(PlayerData p) {
		team.put(p.toString(), p);
	}
	
	public void subPitcher(String p) {
		if (team.get(p).getClass() != PitcherData.class) {
			throw new IllegalArgumentException("Passed player is not a pitcher");
		}
		if (currentPitcher != null) {
			discarded.add(currentPitcher);
		}
		currentPitcher = (PitcherData) team.get(p);
		field[1] = currentPitcher;
	}
	
	public PlayerData playTheField(String s, int field) {
		if (field > 9 || field < 0) {
			throw new IllegalArgumentException("Field position must be between 0 and 9");
		}
		PlayerData temp = this.field[field];
		this.field[field] = team.get(s);
		return temp;
	}
	
	public PlayerData hitInOrder(String s, int lineup) {
		lineup = lineup - 1;
		if (lineup > 8 || lineup < 0) {
			throw new IllegalArgumentException("Lineup position must be between 1 and 9");
		}
		PlayerData temp = this.lineup[lineup];
		PlayerData player = team.get(s);
		if (player.isPitcher()) {
			this.lineup[lineup] = new HitterData((PitcherData) player);
		} else {
			this.lineup[lineup] = (HitterData) team.get(s);
		}
		return temp;
	}
	
	public HitterData getCurrentBatter() {
		return lineup[currentSpot];
	}
	
	public PitcherData getCurrentPitcher() {
		return currentPitcher;
	}
	
	public void nextBatter() {
		currentSpot = (currentSpot + 1) % 9;
	}
	
	public boolean discardCard(int i) {
		if (i < sCards.size()) {
			sCards.remove(i);
			return true;
		}
		return false;
	}
	
	public boolean drawCard() {
		sCards.add(StrategyCard.getRandomCard());
		return true;
	}
	
	public void insertTrash(PlayerData p) {
		discarded.add(p);
	}
	
	private StrategyCard selectCard(int i) {
		return sCards.get(i);
	}
	
	public String useCard() {
		StrategyCard s = selectCard(0);
		if (StrategyCard.parsePrecondition(s.getPre())) {
			StrategyCard.emit(s.getUID());
			return s.getPost();
		}
		return null;
	}
	
	/*
	 * Testing method
	 */
	public void populateSCards() {
		sCards.add(0, StrategyCard.getRandomCard());
		for (int i = sCards.size(); i < 100; i ++) {
			sCards.add(StrategyCard.maker(-1, null, null, null, null, null)); //dummy
		}
	}
	
	/**
	 * Testing Method
	 */
	public StrategyCard getSCard() {
		return sCards.get(0);
	}
	
	public void export(String fileName) {
		try {
			FileWriter writer = new FileWriter("SaveData/K");
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
	
	public static LineupManager teamImport(String fileName, Map<String, PlayerData> pool) {
		try {
			Scanner s = new Scanner(new File("SaveData/" + fileName));
			LineupManager loaded = new LineupManager(new HashMap<String, PlayerData>());
			s.useDelimiter("\\n|\\r\\n|\\t");
			while(s.hasNext()) {
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
				/*String discarded = s.next();
				if (!discarded.equals("D")) {
					loaded.addPlayer(pool.get(player));
					if (s.hasNextLine()) {
						s.nextLine();
					}
				} else {
					loaded.insertTrash(pool.get(player));
				} */
			}
			s.close();
			return loaded;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Bad file name");
		}
	}
}