package com.cook.showdown.players;

import java.util.Comparator;

/**
 * A simple player comparator for the purposes of sorting PlayerDatas by
 * whatever implementation of compareTo PlayerData is given.
 * 
 * @author Matthew Bunge
 */

public class PlayerComparator implements Comparator<PlayerData> {
	@Override
	public int compare(PlayerData arg0, PlayerData arg1) {
		return arg0.compareTo(arg1);
	}
}