package com.cook.showdown.utilities.comparators;

import java.util.Comparator;

import com.cook.showdown.models.players.PlayerData;

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