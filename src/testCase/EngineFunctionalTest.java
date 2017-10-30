package testCase;

import gameData.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EngineFunctionalTest {

	public static void main(String[] args) throws FileNotFoundException {
		String[] team1 = {"Frank Thomas", "Tim Hudson", "Jorge Posada", "Todd Helton", "Jose Vidro", "Morgan Ensberg", "Nomar Garciaparra", "Manny Ramirez", "Scott Podsednik", "Reggie Sanders"};
		String[] team2 = {"Edgar Martinez", "Roy Halladay", "Mike Piazza", "Albert Pujols", "Bret Boone", "Bill Mueller", "Alex Rodriguez", "Vladimir Guerrero", "Carlos Beltran", "Aubrey Huff"};
		File hitters = new File("DataFiles/2004 hitters.txt");
		File pitchers = new File("DataFiles/2004 pitchers.txt");
		DraftManager dm = DraftManager.initializePool(pitchers, hitters);
		LineupManager teamOne = new LineupManager();
		LineupManager teamTwo = new LineupManager();
		for (int i = 0; i < 10; i++) {
			teamOne = dm.draftPlayer(teamOne, team1[i]);
			teamTwo = dm.draftPlayer(teamTwo, team2[i]);
		}
		for (int i = 0; i < 10; i++) {
			if (i == 0) {
				teamOne.playTheField(team1[i], i);
				teamOne.hitInOrder(team1[i], 4);
				teamTwo.playTheField(team2[i], i);
				teamTwo.hitInOrder(team2[i], 4);
			} else if (i > 1) {
				teamOne.playTheField(team1[i], i);
				teamOne.hitInOrder(team1[i], ((i + 2) % 9) + 1);
				teamTwo.playTheField(team2[i], i);
				teamTwo.hitInOrder(team2[i], ((i + 2) % 9) + 1);
			}
		}
		teamOne.subPitcher(team1[1]);
		teamTwo.subPitcher(team2[1]);
		StrategyCard scMan = new StrategyCard();
		GameManager gm = new GameManager(teamOne, teamTwo, scMan);
		int i = 0;
		List<Integer> scores = new ArrayList<Integer>();
		teamOne.populateSCards();
		teamTwo.populateSCards();
		while (i < 1) {
			gm.advanceProgram();
			if (gm.getInning() % 10 == 0) {
				scores.add(gm.getTotalRuns());
				gm.resetGameTrack();
				teamOne.populateSCards();
				teamTwo.populateSCards();
				i++;
			}
		}
		double totalScore = 0.0;
		for (Integer j : scores) {
			totalScore += j;
		}
		StrategyCard.printLog();
		System.out.println("Average score: " + totalScore / scores.size());
	}
}
