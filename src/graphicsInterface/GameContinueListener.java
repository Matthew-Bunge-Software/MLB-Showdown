package graphicsInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

import gameData.GameManager;
import gameData.StrategyCard;

public class GameContinueListener implements ActionListener {
	
	JTextArea text1;
	GameManager game;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		game.advanceProgram();
		GameManager.ProgramState ps = game.getProgramState();
		if (ps == GameManager.ProgramState.AfterSwing) {
			text1.append("At the plate is " + game.getHitter() + "\n");
			text1.append("The Pitcher is " + game.getPitcher() + "\n");
			text1.append("The result is " + StrategyCard.getTokens().get(StrategyCard.getTokens().size() - 1) + "\n");
		} else if (ps == GameManager.ProgramState.BeforePitch) {
			text1.append("The score is " + game.getGameStat().awayRuns + " to " + game.getGameStat().homeRuns + "\n");
		}
		text1.setCaretPosition(text1.getDocument().getLength());
	}
	
	public boolean registerItem(Object c) {
		if (c instanceof JTextArea) {
			JTextArea jta = (JTextArea) c;
			if (text1 == null) { text1 = jta; return true; }
		} else if (c instanceof GameManager) {
			GameManager gm = (GameManager) c;
			if (game == null) { game = gm; return true; }
		}
		return false;
	}

}
