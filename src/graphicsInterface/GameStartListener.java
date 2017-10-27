package graphicsInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import org.jdesktop.swingx.JXList;

import gameData.LineupManager;
import gameData.StrategyCard;

public class GameStartListener implements ActionListener {
	
	JButton button1, button2, button3, button4;
	JXList strat1, strat2;
	JMenuItem start;
	LineupTable team1, team2;
	LineupManager lineup1, lineup2;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		start.setEnabled(false);
		button1.setVisible(false);
		button2.setVisible(false);
		button3.setVisible(false);
		button4.setVisible(false);
		team1.stopEdit();
		team2.stopEdit();
		lineup1.populateSCards();
		lineup2.populateSCards();
		DefaultListModel<StrategyCard> tm = new DefaultListModel<>();
		for (StrategyCard s : lineup1.getSCards()) {
			tm.addElement(s);
		}
		strat1.setModel(tm);
		tm = new DefaultListModel<>();
		for (StrategyCard s : lineup2.getSCards()) {
			tm.addElement(s);
		}
		strat2.setModel(tm);
	}
	
	public boolean registerItem(Object c) {
		if (c instanceof JTable) {
			LineupTable t = (LineupTable) c;
			if (team1 == null) 		{ team1 = t; return true; }
			else if (team2 == null) { team2 = t; return true; }
		} else if (c instanceof JXList) {
			JXList j = (JXList) c;
			if (strat1 == null) 		{ strat1 = j; return true; }
			else if (strat2 == null) 	{ strat2 = j; return true; }
		} else if (c instanceof JButton) {
			JButton b = (JButton) c;
			if (button1 == null) 		{ button1 = b; return true; }
			else if (button2 == null) 	{ button2 = b; return true; }
			else if (button3 == null) 	{ button3 = b; return true; }
			else if (button4 == null) 	{ button4 = b; return true; }
		} else if (c instanceof JMenuItem) {
			JMenuItem m = (JMenuItem) c;
			if (start == null) { start = m; return true; }
		} else if (c instanceof LineupManager) {
			LineupManager lm = (LineupManager) c;
			if (lineup1 == null) 		{ lineup1 = lm; return true; }
			else if (lineup2 == null) 	{ lineup2 = lm; return true; }
		}
		return false;
	}

}
