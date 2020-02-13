package com.cook.showdown.testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import com.cook.showdown.gamedata.DraftManager;
import com.cook.showdown.players.HitterData;
import com.cook.showdown.players.PitcherData;
import com.cook.showdown.players.PlayerData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerEquivalenceTest {

	private static Map<String, PlayerData> mainPool;

	@BeforeAll
	public static void setUpBeforeClass() throws FileNotFoundException {
		mainPool = DraftManager.initializePool(new File("DataFiles/2004 pitchers.txt"),
				new File("DataFiles/2004 hitters.txt")).getPool();
	}
	
	@Test
	public void testPitcherGhosting() {
		PlayerData moyer = mainPool.get("Jamie Moyer");
		HitterData hitter = new HitterData((PitcherData) moyer);
		assertTrue(hitter.equals(moyer));
	}
}
