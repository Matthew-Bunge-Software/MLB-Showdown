package gameData;

import java.io.FileNotFoundException;
import java.util.*;
import players.*;

public class GameManager {
	Field grass;
	LineupManager homeTeam;
	LineupManager awayTeam;
	LineupManager defense;
	LineupManager offense;
	GameStat gamestat;
	StrategyCard scMan;
	Random dice;
	private int swingMod;
	private int pitchMod;

	public GameManager(LineupManager home, LineupManager away) {
		try {
			scMan = new StrategyCard();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Specified file does not exist");
		}
		grass = new Field();
		dice = new Random();
		gamestat = new GameStat();
		homeTeam = home;
		awayTeam = away;
		offense = away;
		defense = home;
		swingMod = 0;
		pitchMod = 0;
	}

	public void pitch() {
		swingMod = 0;
		pitchMod = 0;
		StrategyCard.emit("BP");
		useStrategy(offense, "offense");
		useStrategy(defense, "defense");
		PitcherData pitcher = defense.getCurrentPitcher();
		HitterData hitter = offense.getCurrentBatter();
		int pitch = pitcher.getBaseMod() + roll() + pitcher.checkInnings(gamestat.inning) + pitchMod;
		swing(pitch, hitter, pitcher);
		useStrategy(offense, "offense");
		useStrategy(defense, "defense");
		List<String> tokens = StrategyCard.getTokens();
		while (tokens.get(tokens.size() - 1).equals("RRS")) {
			useStrategy(offense, "offense");
			useStrategy(defense, "defense");
			swing(pitch, hitter, pitcher);
			useStrategy(offense, "offense");
			useStrategy(defense, "defense");
		}
		processResult();
		offense.nextBatter();
		gamestat.update();
		if (gamestat.inningEnd()) {
			StrategyCard.emit("IO");
			grass.clear();
			LineupManager temp = offense;
			offense = defense;
			defense = temp;
		}

	}
	
	public void swing(int pitch, HitterData hitter, PitcherData pitcher) {
		if (pitch >= hitter.getBaseMod()) {
			StrategyCard.emit("PC");
			pitcher.checkCard(roll() + swingMod);
		} else {
			StrategyCard.emit("HC");
			hitter.checkCard(roll() + swingMod);
		}
		swingMod = 0;
	}
	
	public void useStrategy(LineupManager user, String team) {
		String s = user.useCard();
		if (s != null) {
			this.parsePostcondition(s, offense, defense, team);
		}
	}

	private int roll() {
		return dice.nextInt(20) + 1;
	}

	public int getInning() {
		return gamestat.inning;
	}

	public void printScore() {
		System.out.println("Home: " + gamestat.homeRuns);
		System.out.println("Away: " + gamestat.awayRuns);
	}
	
	public GameStat processResult() {
		List<String> tokens = StrategyCard.getTokens();
		String token = tokens.get(tokens.size() - 1);
		int i = 1;
		while (token.startsWith("'")) {
			i++;
			token = tokens.get(tokens.size() - i);
		}
		switch (token) {
		case "PU":
			return grass.popout(gamestat);
		case "SO":
			return grass.strikeout(gamestat);
		case "GO":
			return grass.groundout(defense, offense.getCurrentBatter(), gamestat);
		case "FO":
			return grass.flyout(gamestat);
		case "BB":
			return grass.walk(offense.getCurrentBatter(), gamestat);
		case "1B":
			return grass.single(offense.getCurrentBatter(), gamestat);
		case "1B+":
			return grass.singlePlus(offense.getCurrentBatter(), gamestat);
		case "2B":
			return grass.twoBase(offense.getCurrentBatter(), gamestat);
		case "3B":
			return grass.triple(offense.getCurrentBatter(), gamestat);
		case "HR":
			return grass.homer(gamestat);
		default:
			StrategyCard.printLog();
			throw new IllegalArgumentException("This method was called at a bad time");
		}
	}

	public boolean parsePostcondition(String s, LineupManager offense, LineupManager defense, String team) {
		LineupManager user;
		LineupManager enemy;
		if (team.equals("offense")) {
			user = offense;
			enemy = defense;
		} else {
			user = defense;
			enemy = offense;
		}
		String[] all = s.split(" ");
		for (int i = 0; i < all.length; i++) {
			String[] pre = all[i].split("\\+");
			if (pre.length > 1) {
				String[] post = pre[1].split("\\^");
				if (post.length > 1) {
					for (String p : post) {
						if (parsePostcondition(pre[0] + "+" + p, offense, defense, team)) {
							break;
						}
					}
				}
			}
			switch (pre[0]) {
			case "SW": // Change swingMod
				swingMod += parseIntCondition(pre[1]);
				break;
			case "PI": // Change pitchMod
				pitchMod += parseIntCondition(pre[1]);
				break;
			case "P": // Status related to the pitcher
				PitcherData pitcher = defense.getCurrentPitcher();
				if (pre[1].equals("TI")) { // Pitcher is tired
					//defense.getCurrentPitcher().checkInnings(defense.g)
				} else if (pre[1].equals("CL") && !pitcher.getRole().equals("Closer")) { // Pitcher is a closer
					return false;
				} else if (pre[1].equals("L") && !pitcher.getHand().equals("L")) {
					return false;
				} else if (pre[1].equals("R") && !pitcher.getHand().equals("R")) {
					return false;
				}
				break;
			case "B":	// Status related to the batter
				HitterData batter = offense.getCurrentBatter();
				if (pre[1].equals("R") && !batter.getBattingSide().equals("R")) {
					return false;
				} else if (pre[1].equals("S") && batter.getBattingSide().equals("S")) {
					return false;
				} else if (pre[1].equals("L") && batter.getBattingSide().equals("L")) {
					return false;
				}
				break;
			case "DI": // Discard a card
				for (int j = 0; j < Integer.parseInt(pre[2]); j++) {
					if (pre[1].equals("SE")) {
						user.discardCard(1);
					} else {
						enemy.discardCard(1);
					}
				}
				break;
			case "DR": // Draw a card
				for (int j = 0; j < Integer.parseInt(pre[2]); j++) {
					if (pre[1].equals("SE")) {
						user.drawCard();
					} else {
						enemy.drawCard();
					}
				}
				break;
			case "PU":	// Change result to pop out
				StrategyCard.emit("PU");
				break;
			case "BB":	// Change result to walk
				StrategyCard.emit("BB");
				break;
			case "RRS": // Reroll a swing
				StrategyCard.emit("RRS");
				break;
			default:
				throw new IllegalArgumentException(pre[0] + ": not a valid postcondition");
			}
		}
		return true;
	}

	private int parseIntCondition(String s) {
		switch (s) {
		case "AR": // All runners
			return grass.getState();
		default:
			return Integer.parseInt(s);
		}
	}

	/*
	 * Mostly a debugging method to run a bunch of real games without pitcher
	 * substitution
	 */
	public void resetGameTrack() {
		gamestat = new GameStat();
	}

	public int getTotalRuns() {
		return gamestat.awayRuns + gamestat.homeRuns;
	}
}
