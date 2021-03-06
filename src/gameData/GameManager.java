package gameData;

import java.util.*;
import players.*;

public class GameManager {
	
	public enum ProgramState {
		BeforePitch, AfterSwing, BeforeReroll, AfterReroll, BatterResolved, 
		ProcessResult, BeforeDoublePlay, AfterDoublePlay;
	}
	
	private Field grass;
	private LineupManager homeTeam;
	private LineupManager awayTeam;
	private LineupManager defense;
	private LineupManager offense;
	private GameStat gamestat;
	StrategyCard scMan;
	Random dice;
	private int swingMod;
	private int pitchMod;
	private int fieldMod;
	private ProgramState state;
	private HitterData hitter;
	private PitcherData pitcher;
	private int pitch;
	private String adv;
	private boolean checkPassed;

	public GameManager(LineupManager home, LineupManager away, StrategyCard scMan) {
		this.scMan = scMan;
		grass = new Field();
		dice = new Random();
		gamestat = new GameStat();
		homeTeam = home;
		awayTeam = away;
		offense = away;
		defense = home;
		swingMod = 0;
		pitchMod = 0;
		state = ProgramState.BeforePitch;
		StrategyCard.emit("BP");
	}
	
	public void advanceProgram() {
		for (StrategyCard sc : offense.getUseCards()) {
			useStrategy(offense, "offense", sc);
		}
		for (StrategyCard sc : defense.getUseCards()) {
			useStrategy(defense, "defense", sc);
		}
		offense.processDiscard(); offense.processUse();
		defense.processDiscard(); defense.processUse();
		List<String> tokens = StrategyCard.getTokens();
		switch (state) {
		case BeforePitch:
			pitcher = defense.getCurrentPitcher();
			hitter = offense.getCurrentBatter();
			pitch = pitcher.getBaseMod() + roll() + pitcher.checkInnings(gamestat.getInning()) + pitchMod;
			adv = swing(pitch, hitter, pitcher);
			state = ProgramState.AfterSwing;
			break;
		case AfterSwing:
			if (tokens.get(tokens.size() - 1).equals("RRS")) {
				state = ProgramState.BeforeReroll;
			} else {
				state = ProgramState.ProcessResult;
				advanceProgram();
			}
			break;
		case BeforeReroll:
			adv = swing(pitch, hitter, pitcher);
			state = ProgramState.AfterReroll;
			break;
		case AfterReroll:
			if (tokens.get(tokens.size() - 1).equals("RRS")) {
				state = ProgramState.BeforeReroll;
			} else {
				state = ProgramState.ProcessResult;
				advanceProgram();
			}
			break;	
		case ProcessResult:
			processResult();
			if (tokens.get(tokens.size() - 1).equals("BDP")) {
				state = ProgramState.BeforeDoublePlay;
			} else if (tokens.get(tokens.size() - 1).equals("ADP")) {
				state = ProgramState.AfterDoublePlay;
			} else {
				state = ProgramState.BatterResolved;
				advanceProgram();
			}
			break;
		case BeforeDoublePlay:
			state = ProgramState.ProcessResult;
			advanceProgram();
			break;
		case AfterDoublePlay:
			if (tokens.get(tokens.size() - 1).equals("RRDP")) {
				state = ProgramState.BeforeDoublePlay;
				advanceProgram();
				break;
			}
			state = ProgramState.ProcessResult;
			advanceProgram();
			break;
		case BatterResolved:
			offense.nextBatter();
			gamestat.update();
			swingMod = 0;
			pitchMod = 0;
			if (gamestat.inningEnd()) {
				StrategyCard.emit("IO");
				grass.clear();
				LineupManager temp = offense;
				offense = defense;
				defense = temp;
				offense.drawCard();
				defense.drawCard();
			}
			state = ProgramState.BeforePitch;
			StrategyCard.emit("BP");
			break;
		}
	}

	/* public void pitch() {
		StrategyCard.emit("BP");
		//Prior to pitch
		useStrategy(offense, "offense");
		useStrategy(defense, "defense");
		//After swing
		useStrategy(offense, "offense");
		useStrategy(defense, "defense");
		List<String> tokens = StrategyCard.getTokens();
		while (tokens.get(tokens.size() - 1).equals("RRS")) {
			//Prior to reroll
			useStrategy(offense, "offense");
			useStrategy(defense, "defense");
			swing(pitch, hitter, pitcher);
			//After reroll
			useStrategy(offense, "offense");
			useStrategy(defense, "defense");
		}
	} */
	
	public String swing(int pitch, HitterData hitter, PitcherData pitcher) {
		String adv;
		if (pitch >= hitter.getBaseMod()) {
			StrategyCard.emit("PC");
			pitcher.checkCard(roll() + swingMod);
			adv = "Pitcher";
		} else {
			StrategyCard.emit("HC");
			hitter.checkCard(roll() + swingMod);
			adv = "Hitter";
		}
		swingMod = 0;
		return adv;
	}
	
	public void useStrategy(LineupManager user, String team, StrategyCard s) {
		if (user.getSCards().contains(s)) {
			this.parsePostcondition(s.getPost(), offense, defense, team);
		}
		
	}

	private int roll() {
		return dice.nextInt(20) + 1;
	}

	public int getInning() {
		return gamestat.getInning();
	}

	public void printScore() {
		System.out.println("Home: " + gamestat.getHomeRuns());
		System.out.println("Away: " + gamestat.getAwayRuns());
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
		case "RRDP":
		case "BDP":
			checkPassed = infieldCheck(defense, offense.getCurrentBatter());
			StrategyCard.emit("ADP");
			return gamestat;
		case "ADP":
			fieldMod = 0;
			return grass.doublePlay(offense.getCurrentBatter(), gamestat, checkPassed);
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
			case "D": // Change fieldMod
				fieldMod += parseIntCondition(pre[1]);
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
						user.processDiscard();
					} else {
						enemy.processDiscard();
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
			case "RRDP": // Reroll a doubleplay
				StrategyCard.emit("RRDP");
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
		return gamestat.getAwayRuns() + gamestat.getHomeRuns();
	}
	
	public ProgramState getProgramState() {
		return state;
	}
	
	public HitterData getHitter() {
		return hitter;
	}
	
	public PitcherData getPitcher() {
		return pitcher;
	}
	
	public GameStat getGameStat() {
		return gamestat;
	}
	
	public boolean inningSwitched() {
		return StrategyCard.getTokens().get(StrategyCard.getTokens().size() - 2).equals("IO");
	}
	
	public LineupManager getAway() {
		return awayTeam;
	}
	
	public LineupManager getHome() {
		return homeTeam;
	}
	
	public LineupManager getDefense() {
		return defense;
	}
	
	public String getPrevAdv() {
		return adv;
	}
	
	public PlayerData getRunnerOn(int base) {
		return grass.getRunner(base);
	}
	
	public boolean checkPassed() {
		return checkPassed;
	}
	
	/**
	 * Executes an infield check, for example in the case of attempting a double
	 * play
	 * 
	 * @param teamOne
	 *            The lineup of the team currently in the field
	 * @param runner
	 *            The lead runner who the fielding team is attempting to throw
	 *            out
	 * @return true in the case that the infield check passed, false otherwise
	 */
	private boolean infieldCheck(LineupManager teamOne, HitterData runner) {
		int total = dice.nextInt(20) + 1 + fieldMod;
		for (int i = 3; i <= 6; i++) {
			total += teamOne.getFielding(i);
		}
		return total > runner.getSpeed();
	}
}
