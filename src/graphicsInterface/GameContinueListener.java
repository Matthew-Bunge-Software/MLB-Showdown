package graphicsInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

import org.jdesktop.swingx.JXList;

import gameData.GameManager;
import gameData.StrategyCard;

public class GameContinueListener implements ActionListener {
	
	JTextArea text1;
	JXList stratHome, stratAway;
	GameManager game;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		game.advanceProgram();
		GameManager.ProgramState ps = game.getProgramState();
		if (ps == GameManager.ProgramState.AfterSwing) {
			text1.append("At the plate is " + game.getHitter() + "\n");
			text1.append("The Pitcher is " + game.getPitcher() + "\n");
			text1.append("Advantage is with the " + game.getPrevAdv() + "\n");
			text1.append("The result is " + StrategyCard.getTokens().get(StrategyCard.getTokens().size() - 1) + "\n");
		} else if (ps == GameManager.ProgramState.BeforePitch) {
			text1.append("The score is " + game.getGameStat().awayRuns + " to " + game.getGameStat().homeRuns + "\n");
		} else if (ps == GameManager.ProgramState.BeforeReroll) {
			text1.append("A reconsideration incoming\n");
		} else if (ps == GameManager.ProgramState.AfterReroll) {
			text1.append("The new result is " + StrategyCard.getTokens().get(StrategyCard.getTokens().size() - 1) + "\n");
		}
		if (game.inningSwitched()) {
			text1.append("The inning is over.\n");
		}
		InterfaceUtility.populateList(stratHome, game.getHome());
		InterfaceUtility.populateList(stratAway, game.getAway());
		text1.setCaretPosition(text1.getDocument().getLength());
	}
	
	public boolean registerItem(Object c) {
		if (c instanceof JTextArea) {
			JTextArea jta = (JTextArea) c;
			if (text1 == null) { text1 = jta; return true; }
		} else if (c instanceof GameManager) {
			GameManager gm = (GameManager) c;
			if (game == null) { game = gm; return true; }
		} else if (c instanceof JXList) {
			JXList j = (JXList) c;
			if (stratHome == null) 		{ stratHome = j; return true; }
			else if (stratAway == null) 	{ stratAway = j; return true; }
		}
		return false;
	}
}
