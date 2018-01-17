package testCase;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gameData.GameStat;

public class GameStatTest {

	private GameStat gamestat;
	
	@Before
	public void setUp() {
		gamestat = new GameStat();
	}
	
	@Test
	public void testInitialState() {
		assertEquals(0, gamestat.homeRuns);
		assertEquals(0, gamestat.awayRuns);
		assertEquals(0, gamestat.homeHits);
		assertEquals(0, gamestat.awayHits);
		assertEquals(1, gamestat.inning);
		assertEquals(0, gamestat.getOuts());
		assertTrue(gamestat.top);
		assertEquals(1, gamestat.getAwaySpread().size());
		int initialAwayRuns = gamestat.getAwaySpread().get(0);
		assertEquals(0, initialAwayRuns);
		assertTrue(gamestat.getHomeSpread().isEmpty());
	}
	
	@Test
	public void testYerOut() {
		assertEquals(0, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(1, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(2, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(3, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(3, gamestat.getOuts());
		gamestat.yerOut();
		assertEquals(3, gamestat.getOuts());
	}
}
