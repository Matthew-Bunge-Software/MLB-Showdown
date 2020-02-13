package com.cook.showdown.graphicsinterface;

import javax.swing.JTable;

public class LineupTable extends JTable {
	private boolean editable = true;
	
	public boolean isCellEditable(int row, int column) {
		if (editable) {
			if (column != 1) {
				return true;
			}
		}
		return false;
	}
	
	public void stopEdit() {
		editable = false;
	}
}
