package testCase;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import gameData.DraftManager;
import players.HitterData;
import players.PitcherData;
import players.PlayerData;

public class PlayerEquivalenceTest {
	
	static Map<String, PlayerData> mainPool;
	
	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException {
		mainPool = DraftManager.initializePool(new File("2004 pitchers.txt"),
				new File("2004 hitters.txt")).getPool();
	}
	
	@Test
	public void testPitcherGhosting() {
		PlayerData moyer = mainPool.get("Jamie Moyer");
		HitterData hitter = new HitterData((PitcherData) moyer);
		assertTrue(hitter.equals(moyer));
	}
}
