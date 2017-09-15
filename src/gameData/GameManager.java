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
		String oCard = offense.useCard();
		String dCard = defense.useCard();
		if (oCard != null) {
			this.parsePostcondition(oCard, offense, defense, "offense");
		}
		if (dCard != null) {
			this.parsePostcondition(dCard, offense, defense, "defense");
		}
		oCard= dCard = null;
		PitcherData pitcher = defense.getCurrentPitcher();
		HitterData hitter = offense.getCurrentBatter();
		int pitch = pitcher.getBaseMod() + roll() + pitcher.checkInnings(gamestat.inning) + pitchMod;
		if (pitch >= hitter.getBaseMod()) {
			StrategyCard.emit("PC");
			pitcher.checkCard(roll() + swingMod);
		} else {
			StrategyCard.emit("HC");
			hitter.checkCard(roll() + swingMod);
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

	public void parsePostcondition(String s, LineupManager offense, LineupManager defense, String team) {
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
			switch (pre[0]) {
			case "SW": // Change swingMod
				swingMod += parseIntCondition(pre[1]);
				break;
			case "PI": // Change pitchMod
				pitchMod += parseIntCondition(pre[1]);
				break;
			case "P": // Status related to the pitcher
				if (pre[1].equals("TI")) { // Pitcher is tired
					//defense.getCurrentPitcher().checkInnings(defense.g)
				} else if (pre[1].equals("CL")) { // Pitcher is a closer
					if (!defense.getCurrentPitcher().getRole().equals("Closer")) {
						throw new IllegalArgumentException(defense.getCurrentPitcher() + " is not a closer.");
					}
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
						user.discardCard(1);
					} else {
						enemy.discardCard(1);
					}
				}
				break;
			case "BB": // Change result to walk
				break;
			default:
				throw new IllegalArgumentException(pre[0] + ": not a valid postcondition");
			}
		}
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
