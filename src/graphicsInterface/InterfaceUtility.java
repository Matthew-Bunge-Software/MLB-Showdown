package graphicsInterface;

import javax.swing.DefaultListModel;

import org.jdesktop.swingx.JXList;

import gameData.LineupManager;
import gameData.StrategyCard;

public class InterfaceUtility {
	
	public static void populateList(JXList strat, LineupManager lm) {
		DefaultListModel<StrategyCard> tm = new DefaultListModel<>();
		for (StrategyCard s : lm.getSCards()) {
			tm.addElement(s);
		}
		strat.setModel(tm);
	}
}
