package com.cook.showdown.graphicsinterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTable;

import com.cook.showdown.gamedata.LineupManager;
import com.cook.showdown.players.PlayerData;
import com.cook.showdown.players.Position;

import org.jdesktop.swingx.JXList;

public class GameStartListener implements ActionListener {
	
	JButton button1, button2, button3, button4, buttonNext;
	JXList strat1, strat2;
	LineupTable team1, team2;
	LineupManager lineup1, lineup2;
	Map<String, PlayerData> pool;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		buttonNext.setText("Next");
		button1.setVisible(false);
		button2.setVisible(false);
		button3.setVisible(false);
		button4.setVisible(false);
		team1.stopEdit();
		team2.stopEdit();
		lineup1.populateSCards();
		lineup2.populateSCards();
		for (int i = 0; i < Math.max(team1.getRowCount(),  team2.getRowCount()); i++) {
			if (i < team1.getRowCount()) {
				PlayerData p = pool.get((String)team1.getValueAt(i,  1));
				lineup1.addPlayer(p);
				if (team1.getValueAt(i, 0) != null) {
					lineup1.hitInOrder(p.toString(), (int) team1.getValueAt(i,  0));
				}
				if (team1.getValueAt(i, 2) != null) {
					lineup1.playTheField(p.toString(), Position.intFromAbbr((String) team1.getValueAt(i, 2)));
				}
			}
			if (i < team2.getRowCount()) {
				PlayerData p = pool.get((String)team2.getValueAt(i,  1));
				lineup2.addPlayer(p);
				if (team2.getValueAt(i, 0) != null) {
					lineup2.hitInOrder(p.toString(), (int) team2.getValueAt(i,  0));
				}
				if (team2.getValueAt(i, 2) != null) {
					lineup2.playTheField(p.toString(), Position.intFromAbbr((String) team2.getValueAt(i, 2)));
				}
			}
		}
		lineup1.setPitcher();
		lineup2.setPitcher();
		InterfaceUtility.populateList(strat1, lineup1);
		InterfaceUtility.populateList(strat2, lineup2);
	}
	
	public boolean registerItem(Object c, String...s) {
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
			if (Arrays.asList(s).contains("next")) { buttonNext = b; return true; }
			else if (button1 == null) 		{ button1 = b; return true; }
			else if (button2 == null) 	{ button2 = b; return true; }
			else if (button3 == null) 	{ button3 = b; return true; }
			else if (button4 == null) 	{ button4 = b; return true; }
		} else if (c instanceof LineupManager) {
			LineupManager lm = (LineupManager) c;
			if (lineup1 == null) 		{ lineup1 = lm; return true; }
			else if (lineup2 == null) 	{ lineup2 = lm; return true; }
		} else if (c instanceof Map) {
			Map<String, PlayerData> spd = (HashMap<String, PlayerData>) c;
			if (pool == null) { pool = spd; return true; }
		}
		return false;
	}

}
