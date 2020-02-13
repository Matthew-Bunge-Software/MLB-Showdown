package com.cook.showdown.graphicsinterface;

import javax.swing.DefaultListModel;

import com.cook.showdown.gamedata.LineupManager;
import com.cook.showdown.gamedata.StrategyCard;

import org.jdesktop.swingx.JXList;

public class InterfaceUtility {
	
	public static void populateList(JXList strat, LineupManager lm) {
		DefaultListModel<StrategyCard> tm = new DefaultListModel<>();
		for (StrategyCard s : lm.getSCards()) {
			tm.addElement(s);
		}
		strat.setModel(tm);
	}
}
